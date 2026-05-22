package com.example._7.item.effect;

public class MaterialOnlyEffect implements ItemEffect {
    private final String purpose;

    public MaterialOnlyEffect(String purpose) {
        this.purpose = purpose;
    }

    public String getPurpose() {
        return purpose;
    }

    @Override
    public void apply(EffectContext context) {
        // 純合成材料沒有戰鬥效果。
    }

    @Override
    public String getDescription() {
        return purpose;
    }
}
