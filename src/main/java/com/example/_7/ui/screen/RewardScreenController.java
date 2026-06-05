package com.example._7.ui.screen;

import com.example._7.app.GameApp;
import com.example._7.game.GamePhase;
import com.example._7.game.GameSession;
import com.example._7.game.RoundManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RewardScreenController {

    private GameSession session;
    private RoundManager roundManager;
    private GameApp app;

    @FXML private Label lblTitle;
    @FXML private Label lblGoldReward;
    @FXML private Label lblCurrentGold;

    public void init(GameSession session, RoundManager roundManager, GameApp app) {
        this.session = session;
        this.roundManager = roundManager;
        this.app = app;

        updateUI();
    }

    private void updateUI() {
        if (lblTitle != null) {
            lblTitle.setText("Victory - Round " + session.getLastClearedRound() + " Cleared");
        }

        if (lblGoldReward != null) {
            lblGoldReward.setText("Gold +" + session.getLastGoldReward());
        }

        if (lblCurrentGold != null) {
            lblCurrentGold.setText("Current Gold: " + session.getPlayer().getGold());
        }
    }

    @FXML
    private void onContinue() {
        roundManager.continueAfterReward(session);

        if (session.getCurrentPhase() == GamePhase.PREPARATION) {
            app.getSceneManager().showPreparation(session, roundManager);
        } else if (session.getCurrentPhase() == GamePhase.GAME_OVER) {
            // 之後補 showGameOver
            app.getSceneManager().showPreparation(session, roundManager);
        }
    }
}