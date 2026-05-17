package com.example._7.battle;

import com.example._7.character.CharacterStats;
import com.example._7.status.Buff;
import com.example._7.status.Debuff;

import java.util.ArrayList;
import java.util.List;

public class BattleState {
    private int currentHp;
    private int currentStamina;
    private int currentMana;
    private int currentShield;

    // 儲存最大值，供 UI 或其他邏輯查詢
    private int maxHp;
    private int maxStamina;
    private int maxMana;

    private List<Buff> buffs;
    private List<Debuff> debuffs;

    // Constructor: 先將所有數值設為 0，透過 resetFrom 將角色最大數值填入
    public BattleState() {
        this.currentHp = 0;
        this.currentStamina = 0;
        this.currentMana = 0;
        this.currentShield = 0;

        this.maxHp = 0;
        this.maxStamina = 0;
        this.maxMana = 0;

        this.buffs = new ArrayList<>();
        this.debuffs = new ArrayList<>();
    }

    /**
     * 根據角色屬性初始化戰鬥狀態（設定最大值與當前值）
     */
    public void resetFrom(CharacterStats characterStats) {
        if (characterStats == null) return;

        this.maxHp = characterStats.getMaxHp();
        this.maxStamina = characterStats.getMaxStamina();
        this.maxMana = characterStats.getMaxMana();

        this.currentHp = this.maxHp;
        this.currentStamina = this.maxStamina;
        this.currentMana = this.maxMana;
        this.currentShield = 0;

        // 清空/初始化狀態效果列表
        this.buffs.clear();
        this.debuffs.clear();
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    // current getters / setters
    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int hp) {
        this.currentHp = Math.max(hp, Integer.MIN_VALUE); // 可依需求加上上限/下限檢查
    }

    public int getCurrentStamina() {
        return currentStamina;
    }

    public void setCurrentStamina(int stamina) {
        this.currentStamina = stamina;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(int mana) {
        this.currentMana = mana;
    }

    public int getCurrentShield() {
        return currentShield;
    }

    public void setCurrentShield(int shield) {
        this.currentShield = shield;
    }

    // max getters（新增，解決 UI 呼叫 getMaxHp() 的問題）
    public int getMaxHp() {
        return maxHp;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public int getMaxMana() {
        return maxMana;
    }

    // buffs / debuffs 存取（簡單方法）
    public List<Buff> getBuffs() {
        return buffs;
    }

    public List<Debuff> getDebuffs() {
        return debuffs;
    }
}