package com.example._7.character;

import com.example._7.battle.BattleState;
import com.example._7.inventory.Backpack;
import com.example._7.inventory.Storage;

public class Player implements Combatant {
    private final String name;
    private final CharacterClass characterClass;

    private int gold;
    private int wins;

    // 角色最大數值與當前數值
    private final CharacterStats characterStats;
    private final BattleState battleState;


    private final Backpack backpack;
    private final Storage storage;

    public Player(
            String name,
            CharacterClass characterClass,
            int gold,
            CharacterStats characterStats,
            Backpack backpack,
            Storage storage
    ) {
        this.name = name;
        this.characterClass = characterClass;
        this.gold = gold;
        this.wins = 0;

        this.characterStats = characterStats;
        this.battleState = new BattleState();

        this.backpack = backpack;
        this.storage = storage;
    }

    @Override
    public String getName(){
        return name;
    }

    public CharacterClass getCharacterClass(){
        return characterClass;
    }

    public int getGold(){
        return gold;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public boolean spendGold(int amount) {
        if (gold < amount) {
            return false;
        }
        gold -= amount;
        return true;
    }

    public int getWins() {
        return wins;
    }

    public void addWin() {
        wins++;
    }

    @Override
    public CharacterStats getCharacterStats() {
        return characterStats;
    }

    @Override
    public BattleState getBattleState() {
        return battleState;
    }

    @Override
    public Backpack getBackpack() {
        return backpack;
    }

    public Storage getStorage(){
        return storage;
    }

    @Override
    public boolean isDead() {
        return battleState.isDead();
    }
}
