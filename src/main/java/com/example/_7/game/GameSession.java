package com.example._7.game;

/*
 * 遊戲的總狀態容器 (目前的遊戲狀態)
 * 主畫面開始新遊戲
 * 選擇職業後建立玩家
 * 目前第幾回合
 * 當前敵人是誰
 * 當前商店內容
 * 戰鬥結束後推進流程
 * 等等的串接
 * */


import com.example._7.character.Enemy;
import com.example._7.character.Player;
import com.example._7.shop.Shop;

public class GameSession {
    private static final int TOTAL_ROUNDS = 5;

    private final Player player;

    private int currentRound;          // 1 ~ 5
    private int defeatedEnemies;

    private Enemy currentEnemy;
    private Shop currentShop;

    private GamePhase currentPhase;
    private GameResult gameResult;

    // 供每一個 battle 結算時顯示獎勵金額
    private int lastGoldReward;
    private int lastClearedRound;

    public GameSession(Player player) {
        this.player = player;
        this.currentRound = 1;
        this.defeatedEnemies = 0;
        this.currentPhase = GamePhase.PREPARATION;
        this.gameResult = GameResult.IN_PROGRESS;
        this.lastGoldReward = 0;
        this.lastClearedRound = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getDefeatedEnemies() {
        return defeatedEnemies;
    }

    public Enemy getCurrentEnemy() {
        return currentEnemy;
    }

    public void setCurrentEnemy(Enemy currentEnemy) {
        this.currentEnemy = currentEnemy;
    }

    public Shop getCurrentShop() {
        return currentShop;
    }

    public void setCurrentShop(Shop currentShop) {
        this.currentShop = currentShop;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(GamePhase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    public void setGameResult(GameResult gameResult) {
        this.gameResult = gameResult;
    }

    public boolean isFinalRound() {
        return currentRound >= TOTAL_ROUNDS;
    }

    public void advanceToNextRound() {
        if (!isFinalRound()) {
            currentRound++;
        }
    }

    public void increaseDefeatedEnemies() {
        defeatedEnemies++;
    }

    // 金幣獎勵金額 getter
    public int getLastGoldReward() {
        return lastGoldReward;
    }

    public void setLastGoldReward(int lastGoldReward) {
        this.lastGoldReward = Math.max(0, lastGoldReward);
    }

    public int getLastClearedRound() {
        return lastClearedRound;
    }

    public void setLastClearedRound(int lastClearedRound) {
        this.lastClearedRound = lastClearedRound;
    }
}
