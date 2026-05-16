package com.example._7.game;

/*
* 每回合遊戲的流程控制 包含
* 怪物生成
* 判斷是否進入第五回合
* 產生回合間的商店
* */

public class RoundManager {
    public Enemy createEnemyForRound(int round) {
        return EnemyFactory.createEnemy(round);
    }

    public boolean isFinalRound(int round) {
        return round >= 5;
    }
}
