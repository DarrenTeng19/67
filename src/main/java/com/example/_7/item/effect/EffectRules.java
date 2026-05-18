package com.example._7.item.effect;

/**
 * 集中管理正面效果與負面效果的數值規則。
 *
 * 這個類別只負責「規則數值」，不直接修改戰鬥狀態。
 * 真正套用效果的時機，之後可由 BattleEngine 呼叫 EffectContext 的方法處理。
 */
public final class EffectRules {
    private EffectRules() {
    }

    // 正面效果名稱
    public static final String SHIELD = "護盾";
    public static final String SKILL_HASTE = "技能疾速";
    public static final String LIFESTEAL = "吸血";
    public static final String EMPOWER = "強化";
    public static final String PRECISION = "精準";
    public static final String THORNS = "尖刺";

    // 負面效果名稱
    public static final String POISON = "中毒";
    public static final String BURN = "燃燒";
    public static final String BLIND = "致盲";
    public static final String SKILL_SLOW = "技能緩速";

    // 正面效果規則
    public static final double SKILL_HASTE_COOLDOWN_REDUCTION_PER_LAYER = 0.05;
    public static final double SKILL_HASTE_MAX_REDUCTION = 0.50;

    public static final int LIFESTEAL_HEAL_PER_LAYER = 2;

    public static final int EMPOWER_DAMAGE_BONUS_PER_LAYER = 1;

    public static final double PRECISION_HIT_RATE_BONUS_PER_LAYER = 0.05;
    public static final double PRECISION_MAX_BONUS = 0.40;

    public static final int THORNS_DAMAGE_PER_LAYER = 2;

    // 負面效果規則
    public static final int POISON_DAMAGE_PER_LAYER = 1;

    public static final int BURN_DAMAGE_PER_LAYER = 1;
    public static final int BURN_LAYER_DECREASE_PER_TICK = 1;

    public static final double BLIND_HIT_RATE_PENALTY_PER_LAYER = 0.05;
    public static final double BLIND_MAX_PENALTY = 0.40;

    public static final double SKILL_SLOW_COOLDOWN_INCREASE_PER_LAYER = 0.05;
    public static final double SKILL_SLOW_MAX_INCREASE = 0.50;

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int positiveLayers(int layers) {
        return Math.max(0, layers);
    }

    public static double cappedRate(int layers, double perLayer, double maxAbsValue) {
        return Math.min(positiveLayers(layers) * perLayer, maxAbsValue);
    }
}
