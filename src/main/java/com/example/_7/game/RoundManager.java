package com.example._7.game;

/*
 * 負責初始化當前回合
 * 處理戰鬥勝利
 * 處理戰鬥失敗
 * */

import com.example._7.character.Enemy;
import com.example._7.enemy.EnemyFactory;
import com.example._7.shop.Shop;
import com.example._7.shop.ShopGenerator;

public class RoundManager {
    private final EnemyFactory enemyFactory;
    private final ShopGenerator shopGenerator;

    // 獎勵設定
    private static final int[] ROUND_CLEAR_GOLD_REWARDS = {
            10, 20, 40, 100, 0
    };

    public RoundManager(
            EnemyFactory enemyFactory,
            ShopGenerator shopGenerator
    ) {
        this.enemyFactory = enemyFactory;
        this.shopGenerator = shopGenerator;
    }

    public void initializeCurrentRound(GameSession session) {
        int round = session.getCurrentRound();

        Enemy enemy = enemyFactory.createEnemyForRound(round);
        Shop shop = shopGenerator.generateShop(
                session.getPlayer().getCharacterClass(),
                round
        );

        session.setCurrentEnemy(enemy);
        session.setCurrentShop(shop);
        session.setCurrentPhase(GamePhase.PREPARATION);
    }


    public void refreshShop(GameSession session) {
        if (session == null || session.getPlayer() == null) {
            return;
        }
        Shop shop = shopGenerator.generateShop(
                session.getPlayer().getCharacterClass(),
                session.getCurrentRound()
        );
        session.setCurrentShop(shop);
    }

    public void handleBattleVictory(GameSession session) {
        int clearedRound = session.getCurrentRound();
        int goldReward = getGoldRewardForRound(clearedRound);

        session.increaseDefeatedEnemies();
        session.setLastGoldReward(goldReward);
        session.setLastClearedRound(clearedRound);
        session.getPlayer().addGold(goldReward);

        if (session.isFinalRound()) {
            session.setCurrentPhase(GamePhase.GAME_OVER);
            session.setGameResult(GameResult.CLEARED);
            return;
        }

        session.setCurrentPhase(GamePhase.REWARD);
    }

    public void continueAfterReward(GameSession session) {
        if (session == null) {
            return;
        }

        session.advanceToNextRound();
        initializeCurrentRound(session);
    }

    public int getGoldRewardForRound(int round) {
        if (round <= 0 || round > ROUND_CLEAR_GOLD_REWARDS.length) {
            return 0;
        }

        return ROUND_CLEAR_GOLD_REWARDS[round - 1];
    }

    public void handleBattleDefeat(GameSession session) {
        session.setCurrentPhase(GamePhase.GAME_OVER);
        session.setGameResult(GameResult.DEFEATED);
    }

    public boolean isFinalRound(int round) {
        return round >= 5;
    }
}
