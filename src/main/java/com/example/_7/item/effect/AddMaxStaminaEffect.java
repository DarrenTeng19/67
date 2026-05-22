package com.example._7.item.effect;

public class AddMaxStaminaEffect implements ItemEffect {
    private final int amount;

    public AddMaxStaminaEffect(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void apply(EffectContext context) {
        if (!context.isPassiveSetup()) return;
        context.addOwnerMaxStamina(amount);
    }

    @Override
    public String getDescription() {
        return "最大耐力 +" + amount;
    }
}
