package com.example._7.item.effect;

public class AddMaxManaEffect implements ItemEffect {
    private final int amount;

    public AddMaxManaEffect(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void apply(EffectContext context) {
        if (!context.isPassiveSetup()) return;
        context.addOwnerMaxMana(amount);
    }

    @Override
    public String getDescription() {
        return "最大魔力 +" + amount;
    }
}
