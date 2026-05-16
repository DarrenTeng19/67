package com.example._7.character;

import com.example._7.battle.BattleState;
import com.example._7.inventory.Backpack;

public class Enemy implements Combatant {
    private final String name;

    // 對手最大數值跟對戰當前數值
    private final CharacterStats characterStats;
    private final BattleState battleState;

    private final Backpack backpack;

    // 對手生成邏輯 (Constructor): 未知
    public Enemy(
            String name,
            CharacterStats characterStats,
            BattleState battleState,
            Backpack backpack
    ) {
        this.name = name;
        this.characterStats = characterStats;
        this.battleState = battleState;
        this.backpack = backpack;
    }

    @Override
    public String getName() {
        return name;
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

    @Override
    public boolean isDead() {
        return battleState.isDead();
    }
}
