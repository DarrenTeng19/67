package com.example._7.enemy;

import com.example._7.character.CharacterClass;
import com.example._7.character.CharacterStats;
import com.example._7.character.Enemy;
import com.example._7.battle.BattleState;
import com.example._7.inventory.Backpack;

public class EnemyFactory {

    public Enemy createEnemyForRound(int round) {
        // 暫時實現：根據回合數產生不同難度的敵人
        String name = "Enemy Round " + round;
        CharacterStats stats = CharacterClass.WARRIOR.createInitialStats();
        BattleState battleState = new BattleState();
        Backpack backpack = new Backpack();

        return new Enemy(name, stats, battleState, backpack);
    }
}