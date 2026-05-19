package com.example._7.item.effect;

import com.example._7.character.Combatant;
import com.example._7.item.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * ItemEffect 執行時的上下文。
 *
 * owner：觸發道具的人。
 * enemy：owner 的對手。
 * item：目前觸發的道具。
 * phase：目前是開戰前套用被動，還是戰鬥中主動觸發。
 *
 * 這個類別也集中保存「本場戰鬥中的 buff / debuff / 被動能力加成」。
 */
public class EffectContext {
    public enum EffectPhase {
        PASSIVE_SETUP,
        ACTIVE_TRIGGER
    }

    private static final Map<Combatant, BonusStats> BONUS_STATS =
            Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * 角色本身持有的正面效果，例如：吸血、尖刺、技能疾速。
     */
    private static final Map<Combatant, Map<String, Integer>> BUFF_LAYERS =
            Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * 角色受到的負面效果，例如：中毒、燃燒、致盲、技能緩速。
     */
    private static final Map<Combatant, Map<String, Integer>> DEBUFF_LAYERS =
            Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * 會影響所有道具的正面效果，例如：所有道具獲得技能疾速、強化、精準。
     */
    private static final Map<Combatant, Map<String, Integer>> GLOBAL_ITEM_BUFF_LAYERS =
            Collections.synchronizedMap(new WeakHashMap<>());

    private final Combatant owner;
    private final Combatant enemy;
    private final Item item;
    private final EffectPhase phase;

    private EffectContext(Combatant owner, Combatant enemy, Item item, EffectPhase phase) {
        if (owner == null) throw new IllegalArgumentException("owner 不能是 null");
        if (enemy == null) throw new IllegalArgumentException("enemy 不能是 null");
        if (item == null) throw new IllegalArgumentException("item 不能是 null");
        if (phase == null) throw new IllegalArgumentException("phase 不能是 null");

        this.owner = owner;
        this.enemy = enemy;
        this.item = item;
        this.phase = phase;
    }

    public static EffectContext activeTrigger(Combatant owner, Combatant enemy, Item item) {
        return new EffectContext(owner, enemy, item, EffectPhase.ACTIVE_TRIGGER);
    }

    public static EffectContext passiveSetup(Combatant owner, Combatant enemy, Item item) {
        return new EffectContext(owner, enemy, item, EffectPhase.PASSIVE_SETUP);
    }

    /**
     * 每場戰鬥開始前呼叫一次，避免上一場戰鬥的 buff/debuff/被動加成殘留。
     */
    public static void resetBattleData(Combatant... combatants) {
        if (combatants == null) return;

        for (Combatant combatant : combatants) {
            if (combatant == null) continue;
            BONUS_STATS.remove(combatant);
            BUFF_LAYERS.remove(combatant);
            DEBUFF_LAYERS.remove(combatant);
            GLOBAL_ITEM_BUFF_LAYERS.remove(combatant);
        }
    }

    public Combatant getOwner() {
        return owner;
    }

    public Combatant getEnemy() {
        return enemy;
    }

    public Item getItem() {
        return item;
    }

    public EffectPhase getPhase() {
        return phase;
    }

    public boolean isPassiveSetup() {
        return phase == EffectPhase.PASSIVE_SETUP;
    }

    public boolean isActiveTrigger() {
        return phase == EffectPhase.ACTIVE_TRIGGER;
    }

    // ---------------------------------------------------------------------
    // 立即型效果：傷害、回血、護盾、回復資源
    // ---------------------------------------------------------------------

    /**
     * 對敵人造成原始傷害，不套用強化與吸血。
     */
    public int damageEnemyRaw(int amount) {
        return damageCombatantAndReturnHpDamage(enemy, amount);
    }

    /**
     * 對自己造成原始傷害，不套用任何攻擊加成。
     */
    public int damageOwnerRaw(int amount) {
        return damageCombatantAndReturnHpDamage(owner, amount);
    }

    /**
     * 舊版相容方法：對敵人造成傷害。建議 DamageEffect 使用 damageEnemyWithOwnerModifiers。
     */
    public void damageEnemy(int amount) {
        damageEnemyRaw(amount);
    }

    /**
     * 舊版相容方法：對自己造成傷害。
     */
    public void damageOwner(int amount) {
        damageOwnerRaw(amount);
    }

    /**
     * 主動傷害：套用「強化」後造成傷害，若實際扣到 HP，則依「吸血」回血。
     *
     * @return 實際扣到敵人 HP 的數值，不包含被護盾吸收的部分。
     */
    public int damageEnemyWithOwnerModifiers(int baseDamage) {
        int finalDamage = calculateOwnerActiveDamage(baseDamage);
        int hpDamage = damageEnemyRaw(finalDamage);

        int lifestealHealing = getOwnerLifestealHealing(hpDamage);
        if (lifestealHealing > 0) {
            healOwner(lifestealHealing);
        }

        return hpDamage;
    }

    public void healOwner(int amount) {
        owner.getBattleState().heal(amount, getEffectiveMaxHp(owner));
    }

    public void addShieldToOwner(int amount) {
        owner.getBattleState().addShield(amount);
    }

    public void recoverOwnerStamina(int amount) {
        owner.getBattleState().recoverStamina(amount, getEffectiveMaxStamina(owner));
    }

    public void recoverOwnerMana(int amount) {
        owner.getBattleState().recoverMana(amount, getEffectiveMaxMana(owner));
    }

    private static int damageCombatantAndReturnHpDamage(Combatant combatant, int amount) {
        if (combatant == null || amount <= 0) return 0;

        int hpBefore = combatant.getBattleState().getCurrentHp();
        combatant.getBattleState().takeDamage(amount);
        int hpAfter = combatant.getBattleState().getCurrentHp();

        return Math.max(0, hpBefore - hpAfter);
    }

    // ---------------------------------------------------------------------
    // 被動數值加成：最大 HP、最大耐力、最大魔力、恢復速度
    // ---------------------------------------------------------------------

    public void addOwnerMaxHp(int amount) {
        if (amount <= 0) return;
        bonus(owner).maxHp += amount;
        owner.getBattleState().heal(amount, getEffectiveMaxHp(owner));
    }

    public void addOwnerMaxStamina(int amount) {
        if (amount <= 0) return;
        bonus(owner).maxStamina += amount;
        owner.getBattleState().recoverStamina(amount, getEffectiveMaxStamina(owner));
    }

    public void addOwnerMaxMana(int amount) {
        if (amount <= 0) return;
        bonus(owner).maxMana += amount;
        owner.getBattleState().recoverMana(amount, getEffectiveMaxMana(owner));
    }

    public void addOwnerStaminaRecovery(double amountPerSecond) {
        if (amountPerSecond <= 0) return;
        bonus(owner).staminaRecoveryRate += amountPerSecond;
    }

    public void addOwnerManaRecovery(double amountPerSecond) {
        if (amountPerSecond <= 0) return;
        bonus(owner).manaRecoveryRate += amountPerSecond;
    }

    // ---------------------------------------------------------------------
    // buff / debuff 層數操作
    // ---------------------------------------------------------------------

    public void addBuffToOwner(String buffName, int layers) {
        addLayer(BUFF_LAYERS, owner, buffName, layers);
    }

    public void applyDebuffToEnemy(String debuffName, int layers) {
        addLayer(DEBUFF_LAYERS, enemy, debuffName, layers);
    }

    public void addGlobalItemBuffToOwner(String buffName, int layers) {
        addLayer(GLOBAL_ITEM_BUFF_LAYERS, owner, buffName, layers);
    }

    public static int getBuffLayers(Combatant combatant, String buffName) {
        return getLayer(BUFF_LAYERS, combatant, buffName);
    }

    public static int getDebuffLayers(Combatant combatant, String debuffName) {
        return getLayer(DEBUFF_LAYERS, combatant, debuffName);
    }

    public static int getGlobalItemBuffLayers(Combatant combatant, String buffName) {
        return getLayer(GLOBAL_ITEM_BUFF_LAYERS, combatant, buffName);
    }

    public static int getPositiveEffectLayers(Combatant combatant, String effectName) {
        return getBuffLayers(combatant, effectName) + getGlobalItemBuffLayers(combatant, effectName);
    }

    public static int getNegativeEffectLayers(Combatant combatant, String effectName) {
        return getDebuffLayers(combatant, effectName);
    }

    public static void reduceBuffLayers(Combatant combatant, String buffName, int layers) {
        reduceLayer(BUFF_LAYERS, combatant, buffName, layers);
    }

    public static void reduceDebuffLayers(Combatant combatant, String debuffName, int layers) {
        reduceLayer(DEBUFF_LAYERS, combatant, debuffName, layers);
    }

    public static void clearBuff(Combatant combatant, String buffName) {
        clearLayer(BUFF_LAYERS, combatant, buffName);
    }

    public static void clearDebuff(Combatant combatant, String debuffName) {
        clearLayer(DEBUFF_LAYERS, combatant, debuffName);
    }

    // ---------------------------------------------------------------------
    // 正面效果規則
    // ---------------------------------------------------------------------

    /**
     * 技能疾速：每層冷卻 -5%，最多 -50%。
     * 技能緩速：每層冷卻 +5%，最多 +50%。
     *
     * 回傳值可用在冷卻計算：finalCooldown = baseCooldown * multiplier。
     */
    public static double getCooldownMultiplier(Combatant combatant) {
        int hasteLayers = getPositiveEffectLayers(combatant, EffectRules.SKILL_HASTE);
        int slowLayers = getNegativeEffectLayers(combatant, EffectRules.SKILL_SLOW);

        double hasteReduction = EffectRules.cappedRate(
                hasteLayers,
                EffectRules.SKILL_HASTE_COOLDOWN_REDUCTION_PER_LAYER,
                EffectRules.SKILL_HASTE_MAX_REDUCTION
        );

        double slowIncrease = EffectRules.cappedRate(
                slowLayers,
                EffectRules.SKILL_SLOW_COOLDOWN_INCREASE_PER_LAYER,
                EffectRules.SKILL_SLOW_MAX_INCREASE
        );

        return Math.max(0.1, 1.0 - hasteReduction + slowIncrease);
    }

    public double getOwnerCooldownMultiplier() {
        return getCooldownMultiplier(owner);
    }

    public double getEnemyCooldownMultiplier() {
        return getCooldownMultiplier(enemy);
    }

    /**
     * 精準：每層命中率 +5%，最多 +40%。
     * 致盲：每層命中率 -5%，最多 -40%。
     */
    public static double getFinalHitRate(Combatant attacker, Combatant defender, double baseHitRate) {
        int precisionLayers = getPositiveEffectLayers(attacker, EffectRules.PRECISION);
        int blindLayers = getNegativeEffectLayers(attacker, EffectRules.BLIND);

        double precisionBonus = EffectRules.cappedRate(
                precisionLayers,
                EffectRules.PRECISION_HIT_RATE_BONUS_PER_LAYER,
                EffectRules.PRECISION_MAX_BONUS
        );

        double blindPenalty = EffectRules.cappedRate(
                blindLayers,
                EffectRules.BLIND_HIT_RATE_PENALTY_PER_LAYER,
                EffectRules.BLIND_MAX_PENALTY
        );

        return EffectRules.clamp(baseHitRate + precisionBonus - blindPenalty, 0.0, 1.0);
    }

    public double getOwnerFinalHitRate(double baseHitRate) {
        return getFinalHitRate(owner, enemy, baseHitRate);
    }

    public double getEnemyFinalHitRate(double baseHitRate) {
        return getFinalHitRate(enemy, owner, baseHitRate);
    }

    /**
     * 強化：每層主動傷害 +1。
     */
    public static int getActiveDamageBonus(Combatant combatant) {
        int empowerLayers = getPositiveEffectLayers(combatant, EffectRules.EMPOWER);
        return EffectRules.positiveLayers(empowerLayers) * EffectRules.EMPOWER_DAMAGE_BONUS_PER_LAYER;
    }

    public int getOwnerDamageBonus() {
        return getActiveDamageBonus(owner);
    }

    public int calculateOwnerActiveDamage(int baseDamage) {
        return Math.max(0, baseDamage + getOwnerDamageBonus());
    }

    /**
     * 吸血：每層在造成傷害後回復 2 HP。
     * 只有實際扣到敵人 HP 時才回復。
     */
    public static int getLifestealHealing(Combatant combatant, int dealtHpDamage) {
        if (dealtHpDamage <= 0) return 0;
        int lifestealLayers = getPositiveEffectLayers(combatant, EffectRules.LIFESTEAL);
        return EffectRules.positiveLayers(lifestealLayers) * EffectRules.LIFESTEAL_HEAL_PER_LAYER;
    }

    public int getOwnerLifestealHealing(int dealtHpDamage) {
        return getLifestealHealing(owner, dealtHpDamage);
    }

    /**
     * 尖刺：每層受到攻擊時反彈 2 傷害。
     * 這個方法只計算數值；真正何時反彈，之後可由 BattleEngine 在「受到攻擊後」呼叫。
     */
    public static int getThornsDamage(Combatant defender) {
        int thornsLayers = getPositiveEffectLayers(defender, EffectRules.THORNS);
        return EffectRules.positiveLayers(thornsLayers) * EffectRules.THORNS_DAMAGE_PER_LAYER;
    }

    public static int applyThornsCounterDamage(Combatant defender, Combatant attacker) {
        int damage = getThornsDamage(defender);
        if (damage <= 0 || attacker == null) return 0;
        return damageCombatantAndReturnHpDamage(attacker, damage);
    }

    // ---------------------------------------------------------------------
    // 負面效果規則
    // ---------------------------------------------------------------------

    /**
     * 中毒：每次狀態更新造成層數傷害。
     */
    public static int getPoisonTickDamage(Combatant combatant) {
        int layers = getNegativeEffectLayers(combatant, EffectRules.POISON);
        return EffectRules.positiveLayers(layers) * EffectRules.POISON_DAMAGE_PER_LAYER;
    }

    public static int applyPoisonTickDamage(Combatant combatant) {
        int damage = getPoisonTickDamage(combatant);
        if (damage <= 0) return 0;
        return damageCombatantAndReturnHpDamage(combatant, damage);
    }

    /**
     * 燃燒：每 1 秒造成層數 x 1 傷害，觸發後層數 -1。
     */
    public static int getBurnTickDamage(Combatant combatant) {
        int layers = getNegativeEffectLayers(combatant, EffectRules.BURN);
        return EffectRules.positiveLayers(layers) * EffectRules.BURN_DAMAGE_PER_LAYER;
    }

    public static int applyBurnTickDamageAndDecay(Combatant combatant) {
        int damage = getBurnTickDamage(combatant);
        if (damage <= 0) return 0;

        int hpDamage = damageCombatantAndReturnHpDamage(combatant, damage);
        reduceDebuffLayers(combatant, EffectRules.BURN, EffectRules.BURN_LAYER_DECREASE_PER_TICK);
        return hpDamage;
    }

    /**
     * 一次處理持續傷害。
     * 注意：燃燒的規則是每 1 秒觸發一次；中毒是每次狀態更新觸發一次。
     * 如果你之後在 BattleEngine 分開計時，可以改用 applyPoisonTickDamage / applyBurnTickDamageAndDecay。
     */
    public static int applyDamageOverTimeTick(Combatant combatant) {
        int poisonDamage = applyPoisonTickDamage(combatant);
        int burnDamage = applyBurnTickDamageAndDecay(combatant);
        return poisonDamage + burnDamage;
    }

    // ---------------------------------------------------------------------
    // 有效角色數值
    // ---------------------------------------------------------------------

    public static int getEffectiveMaxHp(Combatant combatant) {
        return combatant.getCharacterStats().getMaxHp() + bonus(combatant).maxHp;
    }

    public static int getEffectiveMaxStamina(Combatant combatant) {
        return combatant.getCharacterStats().getMaxStamina() + bonus(combatant).maxStamina;
    }

    public static int getEffectiveMaxMana(Combatant combatant) {
        return combatant.getCharacterStats().getMaxMana() + bonus(combatant).maxMana;
    }

    public static double getEffectiveStaminaRecoveryRate(Combatant combatant) {
        return combatant.getCharacterStats().getStaminaRecoveryRate() + bonus(combatant).staminaRecoveryRate;
    }

    public static double getEffectiveManaRecoveryRate(Combatant combatant) {
        return combatant.getCharacterStats().getManaRecoveryRate() + bonus(combatant).manaRecoveryRate;
    }

    private static BonusStats bonus(Combatant combatant) {
        return BONUS_STATS.computeIfAbsent(combatant, key -> new BonusStats());
    }

    private static void addLayer(Map<Combatant, Map<String, Integer>> map,
                                 Combatant combatant,
                                 String name,
                                 int layers) {
        if (combatant == null || name == null || name.isBlank() || layers <= 0) return;
        map.computeIfAbsent(combatant, key -> new HashMap<>())
                .merge(name, layers, Integer::sum);
    }

    private static void reduceLayer(Map<Combatant, Map<String, Integer>> map,
                                    Combatant combatant,
                                    String name,
                                    int layers) {
        if (combatant == null || name == null || name.isBlank() || layers <= 0) return;
        Map<String, Integer> layersByName = map.get(combatant);
        if (layersByName == null) return;

        int current = layersByName.getOrDefault(name, 0);
        int next = Math.max(0, current - layers);

        if (next == 0) {
            layersByName.remove(name);
        } else {
            layersByName.put(name, next);
        }
    }

    private static void clearLayer(Map<Combatant, Map<String, Integer>> map,
                                   Combatant combatant,
                                   String name) {
        if (combatant == null || name == null || name.isBlank()) return;
        Map<String, Integer> layersByName = map.get(combatant);
        if (layersByName != null) {
            layersByName.remove(name);
        }
    }

    private static int getLayer(Map<Combatant, Map<String, Integer>> map,
                                Combatant combatant,
                                String name) {
        if (combatant == null || name == null) return 0;
        Map<String, Integer> layersByName = map.get(combatant);
        if (layersByName == null) return 0;
        return layersByName.getOrDefault(name, 0);
    }

    private static class BonusStats {
        private int maxHp;
        private int maxStamina;
        private int maxMana;
        private double staminaRecoveryRate;
        private double manaRecoveryRate;
    }
}
