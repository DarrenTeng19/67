package com.example._7.item;

/*
* 裝備資訊原型
* 名稱
* 所屬職業
* 類型: 組件 / 裝備
* 價格
* CD
* 命中率
* 耐力消耗
* 魔力校號
* 效果說明
* */

public abstract class Item {
    private String name;
    private CharacterClass ownerClass;
    private int price;
    private double triggerInterval;
    private double hitRate;
    private int staminaCost;
    private int manaCost;
    private ItemEffect effect;
}
