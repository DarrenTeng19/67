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
    private Combatant player;
    private Combatant enemy;
    private BattleState state;

    public void update(double deltaTime) {
        regenerateResources(player, deltaTime);
        regenerateResources(enemy, deltaTime);

        updateStatusEffects(player, deltaTime);
        updateStatusEffects(enemy, deltaTime);

        processItems(player, enemy, deltaTime);
        processItems(enemy, player, deltaTime);

        checkBattleEnd();
    }
}
