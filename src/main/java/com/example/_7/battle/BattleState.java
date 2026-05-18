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

    private final List<Buff> buffs;
    private final List<Debuff> debuffs;

    public BattleState() {
        this.currentHp = 0;
        this.currentStamina = 0;
        this.currentMana = 0;
        this.currentShield = 0;
        this.buffs = new ArrayList<>();
        this.debuffs = new ArrayList<>();
    }

    public void resetFrom(CharacterStats characterStats) {
        this.currentHp = characterStats.getMaxHp();
        this.currentStamina = characterStats.getMaxStamina();
        this.currentMana = characterStats.getMaxMana();
        this.currentShield = 0;
        this.buffs.clear();
        this.debuffs.clear();
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

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

    public List<Buff> getBuffs() {
        return List.copyOf(buffs);
    }

    public List<Debuff> getDebuffs() {
        return List.copyOf(debuffs);
    }

    public void takeDamage(int damage) {
        if (damage <= 0) return;

        int remainingDamage = damage;

        if (currentShield > 0) {
            int blocked = Math.min(currentShield, remainingDamage);
            currentShield -= blocked;
            remainingDamage -= blocked;
        }

        if (remainingDamage > 0) {
            currentHp = Math.max(0, currentHp - remainingDamage);
        }
    }

    public void heal(int amount, int maxHp) {
        if (amount <= 0) return;
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public void addShield(int amount) {
        if (amount <= 0) return;
        currentShield += amount;
    }

    public void recoverStamina(int amount, int maxStamina) {
        if (amount <= 0) return;
        currentStamina = Math.min(maxStamina, currentStamina + amount);
    }

    public void recoverMana(int amount, int maxMana) {
        if (amount <= 0) return;
        currentMana = Math.min(maxMana, currentMana + amount);
    }

    public boolean hasEnoughStamina(int cost) {
        return currentStamina >= cost;
    }

    public boolean hasEnoughMana(int cost) {
        return currentMana >= cost;
    }

    public void useStamina(int amount) {
        if (amount <= 0) return;
        currentStamina = Math.max(0, currentStamina - amount);
    }

    public void useMana(int amount) {
        if (amount <= 0) return;
        currentMana = Math.max(0, currentMana - amount);
    }
}
