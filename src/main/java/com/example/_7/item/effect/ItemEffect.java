package com.example._7.item.effect;

public interface ItemEffect {
    /**
     * 實際套用效果。
     *
     * ACTIVE_TRIGGER：戰鬥中道具冷卻完成且命中後呼叫。
     * PASSIVE_SETUP：戰鬥開始前，對被動道具呼叫一次。
     */
    void apply(EffectContext context);

    String getDescription();
}
