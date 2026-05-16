package com.example._7.game;

/*
* 關卡控制 包含
* 目前關卡
* 勝利場數
* 使用職業
* 每回合的商店
* 當前的敵人
* */


public class GameSession {
    private Player player;
    private int currentRound;          // 1 ~ 5
    private int wins;
    private Enemy currentEnemy;
    private Shop currentShop;
    private GamePhase currentPhase;
}
