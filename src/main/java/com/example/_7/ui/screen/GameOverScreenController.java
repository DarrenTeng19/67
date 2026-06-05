package com.example._7.ui.screen;

import com.example._7.app.GameApp;
import com.example._7.game.GameResult;
import com.example._7.game.GameSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameOverScreenController {

    private GameSession session;
    private GameApp app;

    @FXML
    private Label lblTitle;
    @FXML
    private Label lblSummary;
    @FXML
    private Label lblGold;
    @FXML
    private Label lblDefeatedEnemies;

    public void init(GameSession session, GameApp app) {
        this.session = session;
        this.app = app;
        updateUI();
    }

    private void updateUI() {
        if (session == null) return;

        boolean cleared = session.getGameResult() == GameResult.CLEARED;

        if (lblTitle != null) {
            lblTitle.setText(cleared ? "Game Cleared!" : "Defeated");
        }

        if (lblSummary != null) {
            lblSummary.setText(cleared
                    ? "你成功通過所有 5 場戰鬥。"
                    : "你在第 " + session.getCurrentRound() + " 回合戰敗。");
        }

        if (lblGold != null) {
            lblGold.setText("Final Gold: " + session.getPlayer().getGold());
        }

        if (lblDefeatedEnemies != null) {
            lblDefeatedEnemies.setText("Defeated Enemies: " + session.getDefeatedEnemies());
        }
    }

    @FXML
    private void onBackToMainMenu() {
        app.getSceneManager().showMainMenu();
    }
}