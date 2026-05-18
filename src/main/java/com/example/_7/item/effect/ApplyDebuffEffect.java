package com.example._7.item.effect;

public class ApplyDebuffEffect implements ItemEffect {
    private final String debuffName;
    private final int layers;

    public ApplyDebuffEffect(String debuffName, int layers) {
        this.debuffName = debuffName;
        this.layers = layers;
    }

    public String getDebuffName() {
        return debuffName;
    }

    public int getLayers() {
        return layers;
    }

    @Override
    public void apply(EffectContext context) {
        if (!context.isActiveTrigger()) return;
        context.applyDebuffToEnemy(debuffName, layers);
    }

    @Override
    public String getDescription() {
        return "附加 " + layers + " 層" + debuffName;
    }
}
