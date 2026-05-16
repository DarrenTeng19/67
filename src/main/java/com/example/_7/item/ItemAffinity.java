package com.example._7.item;

import com.example._7.character.CharacterClass;

public enum ItemAffinity {
    COMMON,
    WARRIOR,
    RANGER,
    MAGE;

    public boolean matches(CharacterClass characterClass) {
        return switch(this) {
            case COMMON -> true;
            case WARRIOR -> characterClass == CharacterClass.WARRIOR;
            case RANGER ->  characterClass == CharacterClass.RANGER;
            case MAGE -> characterClass == CharacterClass.MAGE;
        };
    }
}
