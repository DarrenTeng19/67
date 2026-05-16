package com.example._7.character;

/*
* 定義玩家與對手的共同方法
*
* */

public interface Combatant {
    String getName();
    BattleStats getBattleStats();
    Backpack getBackpack();
    boolean isDead();
}
