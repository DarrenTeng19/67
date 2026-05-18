package com.example._7.item.effect;

public class AddStaminaRecoveryEffect implements ItemEffect {
    private final double amountPerSecond;

    public AddStaminaRecoveryEffect(double amountPerSecond) {
        this.amountPerSecond = amountPerSecond;
    }

    public double getAmountPerSecond() {
        return amountPerSecond;
    }

    @Override
    public void apply(EffectContext context) {
        if (!context.isPassiveSetup()) return;
        context.addOwnerStaminaRecovery(amountPerSecond);
    }

    @Override
    public String getDescription() {
        return "耐力恢復 +" + amountPerSecond + "/s";
    }
}
