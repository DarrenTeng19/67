package com.example._7.battle;

/*
 * 戰鬥邏輯
 *
 * 目前這版主要把 Item / ItemEffect 接起來：
 * 1. 開戰時重置 BattleState 與 EffectContext
 * 2. 套用所有道具的 PASSIVE_SETUP 效果
 * 3. 每次 update 時回復耐力/魔力
 * 4. 每 1 秒處理中毒與燃燒
 * 5. 檢查主動道具冷卻、資源、命中率
 * 6. 命中後呼叫 effect.apply(context)
 * 7. 處理尖刺反傷與勝負判定
 *
 * 注意：BattleEngine 不直接知道你的背包類別長什麼樣。
 * 請在 startBattle() 前呼叫 setItemsFor(player, playerItems) 與
 * setItemsFor(enemy, enemyItems)，把雙方真正參戰的道具傳進來。
 */

import com.example._7.character.Combatant;
import com.example._7.item.Item;
import com.example._7.item.effect.EffectContext;
import com.example._7.item.effect.ItemEffect;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BattleEngine {
    private static final double MIN_COOLDOWN_SECONDS = 0.1;
    private static final double STATUS_TICK_SECONDS = 1.0;

    private final Combatant player;
    private final Combatant enemy;
    private final Random random;

    /** 由外部傳入：每個角色背包中真正參戰的道具。 */
    private final Map<Combatant, List<Item>> registeredItems;

    /** 戰鬥中使用：每個道具的冷卻累積狀態。 */
    private final Map<Combatant, List<ItemRuntimeState>> runtimeStates;

    /** 因為恢復速度是 double，但 BattleState 目前是 int，所以用 remainder 累積小數。 */
    private final Map<Combatant, Double> staminaRecoveryRemainders;
    private final Map<Combatant, Double> manaRecoveryRemainders;

    /** 狀態更新計時器：中毒與燃燒每 1 秒結算一次。 */
    private final Map<Combatant, Double> statusTimers;
    private final Deque<BattleEvent> pendingEvents;

    private boolean battleStarted;
    private boolean battleEnded;
    private Combatant winner;

    public BattleEngine(Combatant player, Combatant enemy) {
        this(player, enemy, List.of(), List.of());
    }

    public BattleEngine(Combatant player, Combatant enemy, List<Item> playerItems, List<Item> enemyItems) {
        if (player == null) throw new IllegalArgumentException("player 不能是 null");
        if (enemy == null) throw new IllegalArgumentException("enemy 不能是 null");

        this.player = player;
        this.enemy = enemy;
        this.random = new Random();
        this.registeredItems = new IdentityHashMap<>();
        this.runtimeStates = new IdentityHashMap<>();
        this.staminaRecoveryRemainders = new IdentityHashMap<>();
        this.manaRecoveryRemainders = new IdentityHashMap<>();
        this.statusTimers = new IdentityHashMap<>();
        this.pendingEvents = new ArrayDeque<>();

        setItemsFor(player, playerItems);
        setItemsFor(enemy, enemyItems);

        this.battleStarted = false;
        this.battleEnded = false;
        this.winner = null;
        this.pendingEvents.clear();
    }

    /**
     * 設定某個角色這場戰鬥要使用的道具。
     *
     * 建議使用方式：
     * BattleEngine engine = new BattleEngine(player, enemy);
     * engine.setItemsFor(player, playerBackpackItems);
     * engine.setItemsFor(enemy, enemyBackpackItems);
     * engine.startBattle();
     */
    public void setItemsFor(Combatant combatant, List<Item> items) {
        if (combatant == null) return;
        if (items == null) {
            registeredItems.put(combatant, List.of());
        } else {
            registeredItems.put(combatant, List.copyOf(items));
        }

        // 如果戰鬥還沒開始，先不建立 runtime；startBattle 時會統一建立。
        // 如果戰鬥中換裝，則立即重建該角色道具冷卻狀態。
        if (battleStarted && !battleEnded) {
            initializeItemRuntimeStates(combatant);
        }
    }

    public List<Item> getItemsFor(Combatant combatant) {
        return registeredItems.getOrDefault(combatant, List.of());
    }

    public List<ItemRuntimeState> getRuntimeStatesFor(Combatant combatant) {
        return runtimeStates.getOrDefault(combatant, List.of());
    }

    public void startBattle() {
        player.getBattleState().resetFrom(player.getCharacterStats());
        enemy.getBattleState().resetFrom(enemy.getCharacterStats());

        EffectContext.resetBattleData(player, enemy);

        initializeItemRuntimeStates(player);
        initializeItemRuntimeStates(enemy);

        staminaRecoveryRemainders.clear();
        manaRecoveryRemainders.clear();
        statusTimers.clear();
        staminaRecoveryRemainders.put(player, 0.0);
        staminaRecoveryRemainders.put(enemy, 0.0);
        manaRecoveryRemainders.put(player, 0.0);
        manaRecoveryRemainders.put(enemy, 0.0);
        statusTimers.put(player, 0.0);
        statusTimers.put(enemy, 0.0);

        // 注意：所有道具都呼叫 PASSIVE_SETUP。
        // 由每個 effect 自己決定要不要在 passive 階段生效。
        // 這樣 ACTIVE 裝備上的 AddMaxHpEffect / AddBuffEffect 也能在開戰前套用一次。
        applyPassiveEffects(player, enemy);
        applyPassiveEffects(enemy, player);

        this.battleStarted = true;
        this.battleEnded = false;
        this.winner = null;

        checkBattleEnd();
    }

    public void update(double deltaTime) {
        if (!battleStarted || battleEnded) {
            return;
        }
        if (deltaTime <= 0) {
            return;
        }

        regenerateResources(player, deltaTime);
        regenerateResources(enemy, deltaTime);

        updateStatusEffects(player, deltaTime);
        updateStatusEffects(enemy, deltaTime);
        if (battleEnded) return;

        triggerItems(player, enemy, deltaTime);
        if (battleEnded) return;

        triggerItems(enemy, player, deltaTime);

        checkBattleEnd();
    }

    private void initializeItemRuntimeStates(Combatant combatant) {
        List<ItemRuntimeState> states = new ArrayList<>();
        for (Item item : getItemsFor(combatant)) {
            if (item == null) continue;
            states.add(new ItemRuntimeState(item));
        }
        runtimeStates.put(combatant, states);
    }

    private void applyPassiveEffects(Combatant owner, Combatant target) {
        for (Item item : getItemsFor(owner)) {
            if (item == null) continue;

            EffectContext context = EffectContext.passiveSetup(owner, target, item);
            for (ItemEffect effect : item.getEffects()) {
                effect.apply(context);
            }
        }
    }

    private void regenerateResources(Combatant combatant, double deltaTime) {
        if (combatant.getBattleState().isDead()) return;

        double staminaGain = EffectContext.getEffectiveStaminaRecoveryRate(combatant) * deltaTime
                + staminaRecoveryRemainders.getOrDefault(combatant, 0.0);
        int staminaWhole = (int) staminaGain;
        staminaRecoveryRemainders.put(combatant, staminaGain - staminaWhole);

        if (staminaWhole > 0) {
            combatant.getBattleState().recoverStamina(
                    staminaWhole,
                    EffectContext.getEffectiveMaxStamina(combatant)
            );
        }

        double manaGain = EffectContext.getEffectiveManaRecoveryRate(combatant) * deltaTime
                + manaRecoveryRemainders.getOrDefault(combatant, 0.0);
        int manaWhole = (int) manaGain;
        manaRecoveryRemainders.put(combatant, manaGain - manaWhole);

        if (manaWhole > 0) {
            combatant.getBattleState().recoverMana(
                    manaWhole,
                    EffectContext.getEffectiveMaxMana(combatant)
            );
        }
    }

    private void updateStatusEffects(Combatant combatant, double deltaTime) {
        if (combatant.getBattleState().isDead()) return;

        double timer = statusTimers.getOrDefault(combatant, 0.0) + deltaTime;

        while (timer >= STATUS_TICK_SECONDS && !combatant.getBattleState().isDead()) {
            timer -= STATUS_TICK_SECONDS;

            // 中毒：每次狀態更新造成層數傷害。
            EffectContext.applyPoisonTickDamage(combatant);
            if (combatant.getBattleState().isDead()) break;

            // 燃燒：每 1 秒造成層數 x 1 傷害，觸發後層數 -1。
            EffectContext.applyBurnTickDamageAndDecay(combatant);
        }

        statusTimers.put(combatant, timer);
        checkBattleEnd();
    }

    private void triggerItems(Combatant owner, Combatant target, double deltaTime) {
        if (owner.getBattleState().isDead() || target.getBattleState().isDead()) return;

        List<ItemRuntimeState> states = runtimeStates.getOrDefault(owner, List.of());

        for (ItemRuntimeState state : states) {
            if (owner.getBattleState().isDead() || target.getBattleState().isDead()) break;

            Item item = state.getItem();
            if (item == null || !item.isActive()) {
                continue;
            }

            state.update(deltaTime);

            double effectiveCooldown = getEffectiveCooldownSeconds(owner, item);
            if (!state.isReady(effectiveCooldown)) {
                continue;
            }

            // 冷卻已經完成，但資源不足時，不重置冷卻；等資源恢復後下一次 update 再嘗試觸發。
            if (!hasEnoughResources(owner, item)) {
                continue;
            }

            payResourceCosts(owner, item);
            pendingEvents.addLast(new BattleEvent(
                    BattleEvent.Type.ATTACK, owner, target, item, 0
            ));

            double finalHitRate = EffectContext.getFinalHitRate(owner, target, item.getHitRate());
            boolean hit = random.nextDouble() <= finalHitRate;

            if (hit) {
                pendingEvents.addLast(new BattleEvent(
                        BattleEvent.Type.HIT, owner, target, item, 0
                ));
                int targetTotalBefore = target.getBattleState().getCurrentHp()
                        + target.getBattleState().getCurrentShield();

                EffectContext context = EffectContext.activeTrigger(owner, target, item);
                for (ItemEffect effect : item.getEffects()) {
                    effect.apply(context);
                }

                int targetTotalAfter = target.getBattleState().getCurrentHp()
                        + target.getBattleState().getCurrentShield();

                boolean targetTookAttackDamage = targetTotalAfter < targetTotalBefore;
                if (targetTookAttackDamage) {
                    pendingEvents.addLast(new BattleEvent(
                            BattleEvent.Type.DAMAGE,
                            owner,
                            target,
                            item,
                            targetTotalBefore - targetTotalAfter
                    ));
                    EffectContext.applyThornsCounterDamage(target, owner);
                }
            } else {
                pendingEvents.addLast(new BattleEvent(
                        BattleEvent.Type.MISS, owner, target, item, 0
                ));
            }

            // 不管命中或未命中，只要嘗試觸發並消耗資源，就重新計算冷卻。
            state.resetCooldown();
            checkBattleEnd();
        }
    }

    private double getEffectiveCooldownSeconds(Combatant owner, Item item) {
        double baseCooldown = Math.max(MIN_COOLDOWN_SECONDS, item.getCooldownSeconds());
        double multiplier = EffectContext.getCooldownMultiplier(owner);
        return Math.max(MIN_COOLDOWN_SECONDS, baseCooldown * multiplier);
    }

    private boolean hasEnoughResources(Combatant owner, Item item) {
        return owner.getBattleState().hasEnoughStamina(item.getStaminaCost())
                && owner.getBattleState().hasEnoughMana(item.getManaCost());
    }

    private void payResourceCosts(Combatant owner, Item item) {
        owner.getBattleState().useStamina(item.getStaminaCost());
        owner.getBattleState().useMana(item.getManaCost());
    }

    private void checkBattleEnd() {
        boolean playerDead = player.getBattleState().isDead();
        boolean enemyDead = enemy.getBattleState().isDead();

        if (!playerDead && !enemyDead) {
            return;
        }

        battleEnded = true;

        if (playerDead && enemyDead) {
            winner = null;
        } else if (enemyDead) {
            winner = player;
        } else {
            winner = enemy;
        }
    }

    public boolean isBattleStarted() {
        return battleStarted;
    }

    public boolean isBattleEnded() {
        return battleEnded;
    }

    public Combatant getWinner() {
        return winner;
    }

    public Combatant getPlayer() {
        return player;
    }

    public Combatant getEnemy() {
        return enemy;
    }

    public List<BattleEvent> drainEvents() {
        List<BattleEvent> events = new ArrayList<>(pendingEvents);
        pendingEvents.clear();
        return events;
    }
}
