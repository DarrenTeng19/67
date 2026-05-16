package com.example._7.character;

/*
 * 定義角色數值上限
 * 最大血量
 * 最大魔量
 * 最大防禦
 * buffs, debuffs
 * */

import com.example._7.status.Buff;
import com.example._7.status.Debuff;

import java.util.List;

public class CharacterStats {
    private int maxHp;
    private int maxStamina;
    private int maxMana;

    // 回復率與回魔率
    private double staminaRecoveryRate;
    private double manaRecoveryRate;

    private List<Buff> buffs;
    private List<Debuff> debuffs;

    // Constructor
    public CharacterStats(
            int maxHp,
            int maxStamina,
            double staminaRecoveryRate,
            int maxMana,
            double manaRecoveryRate
    ) {
        this.maxHp = maxHp;
        this.maxStamina = maxStamina;
        this.staminaRecoveryRate = staminaRecoveryRate;
        this.maxMana = maxMana;
        this.manaRecoveryRate = manaRecoveryRate;
    }

    // Methods
    public int getMaxHp() {
        return maxHp;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public double getStaminaRecoveryRate() {
        return staminaRecoveryRate;
    }

    public double getManaRecoveryRate() {
        return manaRecoveryRate;
    }
}
