package com.example._7.item.effect;

public class RecoverStaminaEffect implements ItemEffect {
    private final int amount;

    public RecoverStaminaEffect(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void apply(EffectContext context) {
        if (!context.isActiveTrigger()) return;
        context.recoverOwnerStamina(amount);
    }

    @Override
    public String getDescription() {
        return "回復 " + amount + " 耐力值";
    }
}
