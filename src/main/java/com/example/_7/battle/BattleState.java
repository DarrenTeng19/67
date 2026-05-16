package com.example._7.battle;

import com.example._7.character.CharacterStats;
import com.example._7.status.Buff;
import com.example._7.status.Debuff;

import java.util.List;

public class BattleState {
    private int currentHp;
    private int currentStamina;
    private int currentMana;
    private int currentShield;

    private List<Buff> buffs;
    private List<Debuff> debuffs;

    // Constructor: 先將所有數值設為 0 透過 resetFrom 將角色最大數值填入
    public BattleState() {
        this.currentHp = 0;
        this.currentStamina = 0;
        this.currentMana = 0;
        this.currentShield = 0;
    }

    // Methods
    public void resetFrom(CharacterStats characterStats) {
        this.currentHp = characterStats.getMaxHp();
        this.currentStamina = characterStats.getMaxStamina();
        this.currentMana = characterStats.getMaxMana();
        this.currentShield = 0;

        // 之後有 buff, debuff 的狀態
        // this.buffs.clear();
        // this.buffs.clear();
    }

    public boolean isDead(){
        return currentHp <= 0;
    }

    // getters
    public int getCurrentHp() {
        return currentHp;
    }

    public int getCurrentStamina() {
        return currentStamina;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public int getCurrentShield() {
        return currentShield;
    }
    // setters

}
