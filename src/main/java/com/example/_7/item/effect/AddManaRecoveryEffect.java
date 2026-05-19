package com.example._7.item.effect;

public class AddManaRecoveryEffect implements ItemEffect {
    private final double amountPerSecond;

    public AddManaRecoveryEffect(double amountPerSecond) {
        this.amountPerSecond = amountPerSecond;
    }

    public double getAmountPerSecond() {
        return amountPerSecond;
    }

    @Override
    public void apply(EffectContext context) {
        if (!context.isPassiveSetup()) return;
        context.addOwnerManaRecovery(amountPerSecond);
    }

    @Override
    public String getDescription() {
        return "魔力恢復 +" + amountPerSecond + "/s";
    }
}
