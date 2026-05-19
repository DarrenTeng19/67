package com.example._7.item.effect;

public class RecoverManaEffect implements ItemEffect {
    private final int amount;

    public RecoverManaEffect(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void apply(EffectContext context) {
        if (!context.isActiveTrigger()) return;
        context.recoverOwnerMana(amount);
    }

    @Override
    public String getDescription() {
        return "回復 " + amount + " 魔力";
    }
}
