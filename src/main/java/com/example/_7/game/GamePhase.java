package com.example._7.game;

/*
 * 標註遊戲進程
 *
 */

public enum GamePhase {
    PREPARATION,
    BATTLE,
    REWARD,
    RESULT, // 可以表示 1. 單場 battle 剛打完 2. 顯示 Victory / defeat 3. 準備轉下一階段
    GAME_OVER
}
