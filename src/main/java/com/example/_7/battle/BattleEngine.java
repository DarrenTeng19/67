package com.example._7.battle;

/*
* 戰鬥邏輯
* 初始化
* 固定時間更新
* 檢查道具冷卻
* 檢查資源
* 命中判定
* 套用效果
* 檢查死亡
* */

import com.example._7.character.Combatant;

public class BattleEngine {
    private final Combatant player;
    private final Combatant enemy;

    private boolean battleStarted;
    private boolean battleEnded;

    // Constructor
    public BattleEngine(Combatant player, Combatant enemy) {
        this.player = player;
        this.enemy = enemy;
        this.battleStarted = false;
        this.battleEnded = false;
    }

    // Methods
    public void startBattle() {
        player.getBattleState().resetFrom(player.getCharacterStats());
        enemy.getBattleState().resetFrom(enemy.getCharacterStats());

        // 之後補:
        // initializeItemRuntimeStates(player);
        // initializeItemRuntimeStates(enemy);

        this.battleStarted = true;
        this.battleEnded = false;
    }

    public void update(double deltaTime) {
        if (!battleStarted || battleEnded) {
            return;
        }

        regenerateResources(player, deltaTime);
        regenerateResources(enemy, deltaTime);

        updateStatusEffects(player, deltaTime);
        updateStatusEffects(enemy, deltaTime);

        triggerItems(player, enemy, deltaTime);
        triggerItems(enemy, player, deltaTime);

        checkBattleEnd();
    }
}
