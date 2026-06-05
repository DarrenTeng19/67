package com.example._7.ui.screen;

import com.example._7.app.GameApp;
import com.example._7.battle.BattleEngine;
import com.example._7.game.GamePhase;
import com.example._7.game.GameSession;
import com.example._7.game.RoundManager;
import com.example._7.item.effect.EffectContext;
import com.example._7.ui.component.BattleStatusPanel;
import com.example._7.ui.component.CharacterInfoPanel;
import com.example._7.ui.component.HealthBarView;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class BattleScreenController {

    private GameSession session;
    private BattleEngine engine;
    private RoundManager roundManager;
    private GameApp app;

    // containers
    @FXML private AnchorPane statusPanelContainer;
    @FXML private AnchorPane charInfoContainer;
    @FXML private AnchorPane playerHealthContainer;
    @FXML private AnchorPane enemyHealthContainer;

    @FXML private Button btnAbort;

    // created components
    private BattleStatusPanel statusPanel;
    private CharacterInfoPanel charInfo;
    private HealthBarView playerHealth;
    private HealthBarView enemyHealth;

    private AnimationTimer timer;
    private long lastTimeNs = -1;

    public void init(GameSession session, BattleEngine engine, RoundManager roundManager, GameApp app) {
        this.session = session;
        this.engine = engine;
        this.roundManager = roundManager;
        this.app = app;

        // create components here (also possible to do in initialize)
        createAndAttachComponents();

        if (session != null && session.getPlayer() != null && charInfo != null) {
            charInfo.setName(session.getPlayer().getName());
            charInfo.setCharacterClass(session.getPlayer().getCharacterClass().name());
        }

        startLoop();
    }

    @FXML
    private void initialize() {
        // no-op (we create components in init to ensure session available)
    }

    private void createAndAttachComponents() {
        try {
            statusPanel = new BattleStatusPanel();
            charInfo = new CharacterInfoPanel();
            playerHealth = new HealthBarView();
            enemyHealth = new HealthBarView();

            if (statusPanelContainer != null) {
                statusPanelContainer.getChildren().add(statusPanel);
                AnchorPane.setTopAnchor(statusPanel, 0.0);
                AnchorPane.setLeftAnchor(statusPanel, 0.0);
            }
            if (charInfoContainer != null) {
                charInfoContainer.getChildren().add(charInfo);
                AnchorPane.setTopAnchor(charInfo, 0.0);
                AnchorPane.setLeftAnchor(charInfo, 0.0);
            }
            if (playerHealthContainer != null) {
                playerHealthContainer.getChildren().add(playerHealth);
                AnchorPane.setTopAnchor(playerHealth, 0.0);
                AnchorPane.setLeftAnchor(playerHealth, 0.0);
            }
            if (enemyHealthContainer != null) {
                enemyHealthContainer.getChildren().add(enemyHealth);
                AnchorPane.setTopAnchor(enemyHealth, 0.0);
                AnchorPane.setLeftAnchor(enemyHealth, 0.0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startLoop() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTimeNs < 0) {
                    lastTimeNs = now;
                    return;
                }
                double deltaSeconds = (now - lastTimeNs) / 1_000_000_000.0;
                lastTimeNs = now;

                engine.update(deltaSeconds);
                updateUI();

                boolean playerDead = session.getPlayer().isDead();
                boolean enemyDead = session.getCurrentEnemy().isDead();

                if (playerDead || enemyDead) {
                    stop();
                    if (enemyDead && !playerDead) roundManager.handleBattleVictory(session);
                    else roundManager.handleBattleDefeat(session);

                    try {
                        if (session.getCurrentPhase() == GamePhase.PREPARATION) {
                            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/_7/preparation.fxml"));
                            javafx.scene.Parent root = loader.load();
                            PreparationScreenController prep = loader.getController();
                            prep.init(session, roundManager, app);
                            javafx.stage.Stage stage = (javafx.stage.Stage) btnAbort.getScene().getWindow();
                            stage.getScene().setRoot(root);
                        } else {
                            // game over
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        timer.start();
    }

    private void updateUI() {
        try {
            int playerCur = session.getPlayer().getBattleState().getCurrentHp();
            int playerMax = EffectContext.getEffectiveMaxHp(session.getPlayer());

            int enemyCur = session.getCurrentEnemy().getBattleState().getCurrentHp();
            int enemyMax = EffectContext.getEffectiveMaxHp(session.getCurrentEnemy());

            if (playerHealth != null) {
                playerHealth.update(playerCur, playerMax);
            }

            if (enemyHealth != null) {
                enemyHealth.update(enemyCur, enemyMax);
            }

            if (statusPanel != null) {
                double pRatio = playerMax > 0 ? (double) playerCur / playerMax : 0;
                double eRatio = enemyMax > 0 ? (double) enemyCur / enemyMax : 0;
                statusPanel.update(pRatio, eRatio);
            }
        } catch (Exception ignored) {
        }
    }

    @FXML
    private void onAbort() {
        if (timer != null) timer.stop();
    }
}