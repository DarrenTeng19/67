package com.example._7.item.effect;

public class HealEffect implements ItemEffect {
    private final int amount;

    public HealEffect(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void apply(EffectContext context) {
        if (!context.isActiveTrigger()) return;
        context.healOwner(amount);
    }

    @Override
    public String getDescription() {
        return "回復 " + amount + " 生命";
    }
}
