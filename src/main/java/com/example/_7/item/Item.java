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

import com.example._7.item.effect.ItemEffect;
import com.example._7.item.shape.ItemShape;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Item {
    private final String id;
    private final String name;
    private final ItemAffinity affinity;
    private final Set<ItemRole> roles;

    private final ItemShape shape;
    private final int price;
    private final int rarity;

    private final ItemTriggerType triggerType;
    private final double cooldownSeconds;

    private final double hitRate;
    private final int staminaCost;
    private final int manaCost;

    private final List<ItemEffect> effects; // 給程式執行效果用的

    protected Item(
            String id,
            String name,
            ItemAffinity affinity,
            Set<ItemRole> roles,
            ItemShape shape,
            int price,
            int rarity,
            ItemTriggerType triggerType,
            double cooldownSeconds,
            double hitRate,
            int staminaCost,
            int manaCost,
            String description,
            List<ItemEffect> effects
    ) {
        this.id = id;
        this.name = name;
        this.affinity = affinity;
        this.roles = EnumSet.copyOf(roles);
        this.shape = shape;
        this.price = price;
        this.rarity = rarity;
        this.triggerType = triggerType;
        this.cooldownSeconds = cooldownSeconds;
        this.hitRate = hitRate;
        this.staminaCost = staminaCost;
        this.manaCost = manaCost;
        this.effects = List.copyOf(effects);
    }

    public boolean isComponent() {
        return roles.contains(ItemRole.COMPONENT);
    }

    public boolean isEquipment() {
        return roles.contains(ItemRole.EQUIPMENT);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemAffinity getAffinity(){
        return affinity;
    }

    public ItemShape getShape() {
        return shape;
    }

    public int getPrice() {
        return price;
    }

    public int getRarity() {
        return rarity;
    }

    public ItemTriggerType getTriggerType() {
        return triggerType;
    }

    public double getCooldownSeconds() {
        return cooldownSeconds;
    }

    public double getHitRate() {
        return hitRate;
    }

    public int getStaminaCost() {
        return staminaCost;
    }

    public int getManaCost() {
        return manaCost;
    }

    public List<ItemEffect> getEffects() {
        return effects;
    }

    public boolean isActive() {
        return triggerType == ItemTriggerType.ACTIVE;
    }

    public boolean isPassive() {
        return triggerType == ItemTriggerType.PASSIVE;
    }

    public String getDescription() {
        return effects.stream()
                .map(ItemEffect::getDescription)
                .collect(Collectors.joining(","));
    }
}
