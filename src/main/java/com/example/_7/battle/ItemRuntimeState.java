package com.example._7.battle;

import com.example._7.item.Item;

/*
 * 道具在單場戰鬥中的即時狀態。
 *
 * Item 本身只保存固定資料，例如冷卻、命中率、消耗與效果；
 * ItemRuntimeState 則保存「這場戰鬥中這個道具已經累積多少冷卻時間」。
 */
public class ItemRuntimeState {
    private final Item item;
    private double elapsedSeconds;

    public ItemRuntimeState(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("item 不能是 null");
        }
        this.item = item;
        this.elapsedSeconds = 0.0;
    }

    public Item getItem() {
        return item;
    }

    public double getElapsedSeconds() {
        return elapsedSeconds;
    }

    public void update(double deltaTime) {
        if (deltaTime <= 0) return;
        elapsedSeconds += deltaTime;
    }

    public boolean isReady(double effectiveCooldownSeconds) {
        if (effectiveCooldownSeconds <= 0) {
            return true;
        }
        return elapsedSeconds >= effectiveCooldownSeconds;
    }

    public void resetCooldown() {
        elapsedSeconds = 0.0;
    }
}
