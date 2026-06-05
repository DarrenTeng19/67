package com.example._7.ui;

import com.example._7.app.GameApp;
import com.example._7.battle.BattleEngine;
import com.example._7.game.GameSession;
import com.example._7.game.RoundManager;
import com.example._7.ui.screen.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
* 負責畫面管理 各種畫面的切換請呼叫 sceneManager
* */

public class SceneManager {
    private static final int WINDOW_WIDTH = 1100;
    private static final int WINDOW_HEIGHT = 700;

    private final Stage stage;
    private final GameApp app;

    public SceneManager(Stage stage, GameApp app) {
        this.stage = stage;
        this.app = app;
    }

    // 準備畫面
    public void showPreparation(GameSession session, RoundManager roundManager) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/_7/preparation.fxml"));
            Parent root = loader.load();

            PreparationScreenController controller = loader.getController();
            controller.init(session, roundManager, app);

            setRoot(root, "Backpack Battles - 準備階段");
        } catch (Exception e) {
            throw new IllegalStateException("無法載入準備階段", e);
        }
    }

    // 戰鬥畫面
    public void showBattle(GameSession session, BattleEngine engine, RoundManager roundManager) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/_7/battle.fxml"));
            Parent root = loader.load();

            BattleScreenController controller = loader.getController();
            controller.init(session, engine, roundManager, app);

            setRoot(root, "Backpack Battles - 戰鬥階段");
        } catch (Exception e) {
            throw new IllegalStateException("無法載入戰鬥畫面", e);
        }
    }

    // 獎勵畫面
    public void showReward(GameSession session, RoundManager roundManager) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/_7/reward.fxml"));
            Parent root = loader.load();

            RewardScreenController controller = loader.getController();
            controller.init(session, roundManager, app);

            setRoot(root, "Backpack Battles - 獎勵");
        } catch (Exception e) {
            throw new IllegalStateException("無法載入獎勵畫面", e);
        }
    }

    // 遊戲結束畫面
    public void showGameOver(GameSession session) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/_7/game-over.fxml"));
            Parent root = loader.load();

            GameOverScreenController controller = loader.getController();
            controller.init(session, app);

            setRoot(root, "Backpack Battles - 結算畫面");
        } catch (Exception e) {
            throw new IllegalStateException("無法載入結算畫面", e);
        }
    }

    // showMainMenu()
    public void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/_7/main-menu.fxml"));
            Parent root = loader.load();

            MainMenuScreen controller = loader.getController();
            controller.init(app);

            setRoot(root, "Backpack Battles - 主選單");
        } catch (Exception e) {
            throw new IllegalStateException("無法載入主選單", e);
        }
    }

    // root
    private void setRoot(Parent root, String title) {
        if (stage.getScene() == null) {
            stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        } else {
            stage.getScene().setRoot(root);
        }
        stage.setTitle(title);
    }
}
