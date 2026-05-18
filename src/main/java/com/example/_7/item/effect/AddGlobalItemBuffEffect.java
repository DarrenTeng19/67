package com.example._7.item.effect;

public class AddGlobalItemBuffEffect implements ItemEffect {
    private final String buffName;
    private final int layers;

    public AddGlobalItemBuffEffect(String buffName, int layers) {
        this.buffName = buffName;
        this.layers = layers;
    }

    public String getBuffName() {
        return buffName;
    }

    public int getLayers() {
        return layers;
    }

    @Override
    public void apply(EffectContext context) {
        if (!context.isPassiveSetup()) return;
        context.addGlobalItemBuffToOwner(buffName, layers);
    }

    @Override
    public String getDescription() {
        return "所有道具獲得 " + layers + " 層" + buffName;
    }
}
