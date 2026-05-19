package com.example._7.item.effect;

public class AddMaxHpEffect implements ItemEffect {
    private final int amount;

    public AddMaxHpEffect(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void apply(EffectContext context) {
        if (!context.isPassiveSetup()) return;
        context.addOwnerMaxHp(amount);
    }

    @Override
    public String getDescription() {
        return "最大 HP +" + amount;
    }
}
