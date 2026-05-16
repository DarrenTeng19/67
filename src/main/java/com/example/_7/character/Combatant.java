package com.example._7.character;

/*
* 定義玩家與對手的共同方法
*
* */

import com.example._7.battle.BattleState;
import com.example._7.inventory.Backpack;

public interface Combatant {
    String getName();

    CharacterStats getCharacterStats();

    BattleState getBattleState();

    Backpack getBackpack();

    boolean isDead();
}
