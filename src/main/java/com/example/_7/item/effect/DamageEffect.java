package com.example._7.item.effect;

public class DamageEffect implements ItemEffect {
    private final int amount;

    public DamageEffect(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void apply(EffectContext context) {
        if (!context.isActiveTrigger()) return;

        // 主動傷害會套用：
        // 1. 強化：每層主動傷害 +1
        // 2. 吸血：若實際扣到敵人 HP，每層回復 2 HP
        context.damageEnemyWithOwnerModifiers(amount);
    }

    @Override
    public String getDescription() {
        return "造成 " + amount + " 傷害";
    }
}
