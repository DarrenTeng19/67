package com.example._7.character;

/*
* 角色當前狀態
* 血量
* 魔量
* 防禦
* buffs, debuffs
* */

public class BattleStats {
    private int currentHp;
    private int currentStamina;
    private int currentMana;
    private int shield;
    private List<Buff> buffs;
    private List<Debuff> debuffs;
}
