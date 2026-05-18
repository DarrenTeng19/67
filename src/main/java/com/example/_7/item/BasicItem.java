package com.example._7.item;

import com.example._7.item.effect.ItemEffect;
import com.example._7.item.shape.ItemShape;

import java.util.List;
import java.util.Set;

public class BasicItem extends Item {
    public BasicItem(
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
        super(
                id,
                name,
                affinity,
                roles,
                shape,
                price,
                rarity,
                triggerType,
                cooldownSeconds,
                hitRate,
                staminaCost,
                manaCost,
                description,
                effects
        );
    }
}
