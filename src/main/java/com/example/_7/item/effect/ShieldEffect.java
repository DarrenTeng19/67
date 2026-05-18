package com.example._7.item.effect;

public class ShieldEffect implements ItemEffect {
    private final int amount;

    public ShieldEffect(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void apply(EffectContext context) {
        if (!context.isActiveTrigger()) return;
        context.addShieldToOwner(amount);
    }

    @Override
    public String getDescription() {
        return "獲得 " + amount + " 護盾";
    }
}
