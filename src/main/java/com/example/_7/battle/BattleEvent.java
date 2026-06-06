package com.example._7.battle;

import com.example._7.character.Combatant;
import com.example._7.item.Item;

public record BattleEvent(
        Type type,
        Combatant actor,
        Combatant target,
        Item item,
        int amount
) {
    public enum Type {
        ATTACK,
        HIT,
        MISS,
        DAMAGE
    }
}
