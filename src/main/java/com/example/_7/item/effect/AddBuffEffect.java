package com.example._7.item.effect;

public class AddBuffEffect implements ItemEffect {
    private final String buffName;
    private final int layers;

    public AddBuffEffect(String buffName, int layers) {
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
        // 目前 ItemCatalog 裡的「吸血、尖刺」比較像開戰前獲得的角色狀態，
        // 所以只在 PASSIVE_SETUP 套用，避免道具每次主動觸發都重複疊加。
        if (!context.isPassiveSetup()) return;
        context.addBuffToOwner(buffName, layers);
    }

    @Override
    public String getDescription() {
        return "角色附加 " + layers + " 層" + buffName;
    }
}
