package com.example._7.ui.screen;

import com.example._7.app.GameApp;
import com.example._7.battle.BattleEngine;
import com.example._7.battle.BattleEvent;
import com.example._7.battle.BattleState;
import com.example._7.character.CharacterClass;
import com.example._7.character.Combatant;
import com.example._7.character.Player;
import com.example._7.game.GamePhase;
import com.example._7.game.GameSession;
import com.example._7.game.RoundManager;
import com.example._7.item.effect.EffectContext;
import com.example._7.item.effect.EffectRules;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class BattleScreenController {
    private static final int MAX_LOG_ENTRIES = 5;

    private GameSession session;
    private BattleEngine engine;
    private RoundManager roundManager;
    private GameApp app;
    private AnimationTimer timer;
    private long lastTimeNs = -1;
    private boolean paused;
    private double speedMultiplier = 1.0;
    private int previousPlayerTotal;
    private int previousEnemyTotal;
    private boolean playerDamageReportedThisFrame;
    private boolean enemyDamageReportedThisFrame;

    @FXML private Label lblRound;
    @FXML private Label lblBattleState;
    @FXML private Label lblPlayerName;
    @FXML private Label lblPlayerClass;
    @FXML private Label lblEnemyName;
    @FXML private Label lblEnemyClass;
    @FXML private Label lblPlayerHp;
    @FXML private Label lblPlayerStamina;
    @FXML private Label lblPlayerMana;
    @FXML private Label lblPlayerShield;
    @FXML private Label lblEnemyHp;
    @FXML private Label lblEnemyStamina;
    @FXML private Label lblEnemyMana;
    @FXML private Label lblEnemyShield;
    @FXML private Label lblLastAction;
    @FXML private Label lblPlayerDamage;
    @FXML private Label lblEnemyDamage;
    @FXML private ProgressBar playerHpBar;
    @FXML private ProgressBar playerStaminaBar;
    @FXML private ProgressBar playerManaBar;
    @FXML private ProgressBar playerShieldBar;
    @FXML private ProgressBar enemyHpBar;
    @FXML private ProgressBar enemyStaminaBar;
    @FXML private ProgressBar enemyManaBar;
    @FXML private ProgressBar enemyShieldBar;
    @FXML private FlowPane playerEffects;
    @FXML private FlowPane enemyEffects;
    @FXML private ImageView playerPortrait;
    @FXML private ImageView enemyPortrait;
    @FXML private StackPane playerPortraitPane;
    @FXML private StackPane enemyPortraitPane;
    @FXML private VBox battleLog;
    @FXML private Button btnPause;
    @FXML private Button btnResume;
    @FXML private Button btnSpeed1x;
    @FXML private Button btnSpeed2x;

    public void init(GameSession session, BattleEngine engine, RoundManager roundManager, GameApp app) {
        this.session = session;
        this.engine = engine;
        this.roundManager = roundManager;
        this.app = app;

        Player player = session.getPlayer();
        lblRound.setText("ROUND " + session.getCurrentRound() + " / 5");
        lblPlayerName.setText(player.getName());
        lblPlayerClass.setText(classDisplayName(player.getCharacterClass()));
        lblEnemyName.setText("Enemy");
        lblEnemyClass.setText("戰士");

        playerPortrait.setImage(loadCharacterImage(fileNameFor(player.getCharacterClass())));
        enemyPortrait.setImage(loadCharacterImage("warrior.png"));

        previousPlayerTotal = totalDurability(player);
        previousEnemyTotal = totalDurability(session.getCurrentEnemy());
        updateUI();
        addLog("第 " + session.getCurrentRound() + " 回合，戰鬥開始。");
        startLoop();
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
                if (!paused) {
                    engine.update(deltaSeconds * speedMultiplier);
                }

                processBattleEvents();
                animateUnreportedDamage();
                updateUI();
                finishBattleIfNeeded();
            }
        };
        timer.start();
    }

    private void processBattleEvents() {
        playerDamageReportedThisFrame = false;
        enemyDamageReportedThisFrame = false;

        for (BattleEvent event : engine.drainEvents()) {
            boolean actorIsPlayer = event.actor() == session.getPlayer();
            String actorName = actorIsPlayer ? "我方" : "敵方";
            String itemName = event.item() == null ? "攻擊" : event.item().getName();

            switch (event.type()) {
                case ATTACK -> {
                    playAttackAnimation(actorIsPlayer);
                    setLastAction(actorName + "使用「" + itemName + "」");
                }
                case MISS -> {
                    String message = actorName + "的「" + itemName + "」未命中";
                    setLastAction(message);
                    addLog(message + "。");
                }
                case DAMAGE -> {
                    boolean targetIsPlayer = event.target() == session.getPlayer();
                    if (targetIsPlayer) {
                        playerDamageReportedThisFrame = true;
                    } else {
                        enemyDamageReportedThisFrame = true;
                    }
                    playDamageAnimation(targetIsPlayer, event.amount());
                    String message = (targetIsPlayer ? "我方" : "敵方")
                            + "受到 " + event.amount() + " 點傷害";
                    setLastAction(message);
                    addLog(message + "。");
                }
                case HIT -> {
                    // DAMAGE or effect changes provide the visible feedback.
                }
            }
        }
    }

    private void animateUnreportedDamage() {
        int playerTotal = totalDurability(session.getPlayer());
        int enemyTotal = totalDurability(session.getCurrentEnemy());

        if (playerTotal < previousPlayerTotal && !playerDamageReportedThisFrame) {
            playDamageAnimation(true, previousPlayerTotal - playerTotal);
        }
        if (enemyTotal < previousEnemyTotal && !enemyDamageReportedThisFrame) {
            playDamageAnimation(false, previousEnemyTotal - enemyTotal);
        }

        previousPlayerTotal = playerTotal;
        previousEnemyTotal = enemyTotal;
    }

    private void updateUI() {
        updateCombatant(
                session.getPlayer(),
                playerHpBar, playerStaminaBar, playerManaBar, playerShieldBar,
                lblPlayerHp, lblPlayerStamina, lblPlayerMana, lblPlayerShield,
                playerEffects
        );
        updateCombatant(
                session.getCurrentEnemy(),
                enemyHpBar, enemyStaminaBar, enemyManaBar, enemyShieldBar,
                lblEnemyHp, lblEnemyStamina, lblEnemyMana, lblEnemyShield,
                enemyEffects
        );
    }

    private void updateCombatant(
            Combatant combatant,
            ProgressBar hpBar,
            ProgressBar staminaBar,
            ProgressBar manaBar,
            ProgressBar shieldBar,
            Label hpLabel,
            Label staminaLabel,
            Label manaLabel,
            Label shieldLabel,
            FlowPane effectsPane
    ) {
        BattleState state = combatant.getBattleState();
        int maxHp = EffectContext.getEffectiveMaxHp(combatant);
        int maxStamina = EffectContext.getEffectiveMaxStamina(combatant);
        int maxMana = EffectContext.getEffectiveMaxMana(combatant);

        hpBar.setProgress(ratio(state.getCurrentHp(), maxHp));
        staminaBar.setProgress(ratio(state.getCurrentStamina(), maxStamina));
        manaBar.setProgress(ratio(state.getCurrentMana(), maxMana));
        shieldBar.setProgress(ratio(state.getCurrentShield(), maxHp));
        hpLabel.setText(state.getCurrentHp() + " / " + maxHp);
        staminaLabel.setText(state.getCurrentStamina() + " / " + maxStamina);
        manaLabel.setText(state.getCurrentMana() + " / " + maxMana);
        shieldLabel.setText(String.valueOf(state.getCurrentShield()));
        updateEffects(combatant, effectsPane);
    }

    private void updateEffects(Combatant combatant, FlowPane pane) {
        Map<String, Integer> positive = new LinkedHashMap<>();
        positive.put("加速", EffectContext.getPositiveEffectLayers(combatant, EffectRules.SKILL_HASTE));
        positive.put("吸血", EffectContext.getPositiveEffectLayers(combatant, EffectRules.LIFESTEAL));
        positive.put("強化", EffectContext.getPositiveEffectLayers(combatant, EffectRules.EMPOWER));
        positive.put("精準", EffectContext.getPositiveEffectLayers(combatant, EffectRules.PRECISION));
        positive.put("尖刺", EffectContext.getPositiveEffectLayers(combatant, EffectRules.THORNS));

        Map<String, Integer> negative = new LinkedHashMap<>();
        negative.put("中毒", EffectContext.getNegativeEffectLayers(combatant, EffectRules.POISON));
        negative.put("燃燒", EffectContext.getNegativeEffectLayers(combatant, EffectRules.BURN));
        negative.put("致盲", EffectContext.getNegativeEffectLayers(combatant, EffectRules.BLIND));
        negative.put("緩速", EffectContext.getNegativeEffectLayers(combatant, EffectRules.SKILL_SLOW));

        String signature = positive.toString() + negative;
        if (signature.equals(pane.getUserData())) {
            return;
        }
        pane.setUserData(signature);
        pane.getChildren().clear();
        positive.forEach((name, layers) -> addEffectChip(pane, name, layers, true));
        negative.forEach((name, layers) -> addEffectChip(pane, name, layers, false));
        if (pane.getChildren().isEmpty()) {
            Label empty = new Label("無");
            empty.getStyleClass().addAll("effect-chip", "empty-effect");
            pane.getChildren().add(empty);
        }
    }

    private void addEffectChip(FlowPane pane, String name, int layers, boolean positive) {
        if (layers <= 0) {
            return;
        }
        Label chip = new Label(name + " x" + layers);
        chip.getStyleClass().addAll(
                "effect-chip",
                positive ? "positive-effect" : "negative-effect"
        );
        pane.getChildren().add(chip);
    }

    private void playAttackAnimation(boolean playerAttacks) {
        StackPane portrait = playerAttacks ? playerPortraitPane : enemyPortraitPane;
        double direction = playerAttacks ? 1.0 : -1.0;

        TranslateTransition lunge = new TranslateTransition(Duration.millis(115), portrait);
        lunge.setByX(34 * direction);
        ScaleTransition grow = new ScaleTransition(Duration.millis(115), portrait);
        grow.setToX(1.05);
        grow.setToY(1.05);

        TranslateTransition returnMove = new TranslateTransition(Duration.millis(170), portrait);
        returnMove.setToX(0);
        ScaleTransition shrink = new ScaleTransition(Duration.millis(170), portrait);
        shrink.setToX(1);
        shrink.setToY(1);

        new SequentialTransition(
                new ParallelTransition(lunge, grow),
                new ParallelTransition(returnMove, shrink)
        ).play();
    }

    private void playDamageAnimation(boolean playerDamaged, int damage) {
        StackPane portrait = playerDamaged ? playerPortraitPane : enemyPortraitPane;
        Label popup = playerDamaged ? lblPlayerDamage : lblEnemyDamage;
        popup.setText("-" + damage);

        RotateTransition left = new RotateTransition(Duration.millis(55), portrait);
        left.setToAngle(-4);
        RotateTransition right = new RotateTransition(Duration.millis(80), portrait);
        right.setToAngle(4);
        right.setCycleCount(3);
        right.setAutoReverse(true);
        RotateTransition center = new RotateTransition(Duration.millis(55), portrait);
        center.setToAngle(0);

        FadeTransition show = new FadeTransition(Duration.millis(80), popup);
        show.setToValue(1);
        TranslateTransition floatUp = new TranslateTransition(Duration.millis(480), popup);
        floatUp.setFromY(18);
        floatUp.setToY(-30);
        FadeTransition fade = new FadeTransition(Duration.millis(320), popup);
        fade.setDelay(Duration.millis(180));
        fade.setToValue(0);

        new SequentialTransition(left, right, center).play();
        new ParallelTransition(show, floatUp, fade).play();
    }

    private void finishBattleIfNeeded() {
        boolean playerDead = session.getPlayer().isDead();
        boolean enemyDead = session.getCurrentEnemy().isDead();
        if (!playerDead && !enemyDead) {
            return;
        }

        timer.stop();
        updateUI();
        lblBattleState.setText(enemyDead && !playerDead ? "戰鬥勝利" : "戰鬥失敗");

        PauseTransition delay = new PauseTransition(Duration.millis(650));
        delay.setOnFinished(event -> {
            if (enemyDead && !playerDead) {
                roundManager.handleBattleVictory(session);
                if (session.getCurrentPhase() == GamePhase.REWARD) {
                    app.getSceneManager().showReward(session, roundManager);
                } else {
                    app.getSceneManager().showGameOver(session);
                }
            } else {
                roundManager.handleBattleDefeat(session);
                app.getSceneManager().showGameOver(session);
            }
        });
        delay.play();
    }

    private void setLastAction(String message) {
        lblLastAction.setText(message);
    }

    private void addLog(String message) {
        Label entry = new Label("• " + message);
        entry.getStyleClass().add("log-entry");
        battleLog.getChildren().add(0, entry);
        while (battleLog.getChildren().size() > MAX_LOG_ENTRIES) {
            battleLog.getChildren().remove(battleLog.getChildren().size() - 1);
        }
    }

    private Image loadCharacterImage(String fileName) {
        URL resource = getClass().getResource("/com/example/_7/images/characters/" + fileName);
        return resource == null ? null : new Image(resource.toExternalForm(), true);
    }

    private String fileNameFor(CharacterClass characterClass) {
        return switch (characterClass) {
            case WARRIOR -> "warrior.png";
            case RANGER -> "ranger.png";
            case MAGE -> "mage-transparent.png";
        };
    }

    private String classDisplayName(CharacterClass characterClass) {
        return switch (characterClass) {
            case WARRIOR -> "戰士";
            case RANGER -> "遊俠";
            case MAGE -> "魔法師";
        };
    }

    private int totalDurability(Combatant combatant) {
        return combatant.getBattleState().getCurrentHp()
                + combatant.getBattleState().getCurrentShield();
    }

    private double ratio(int current, int maximum) {
        if (maximum <= 0) {
            return 0;
        }
        return Math.max(0, Math.min(1, (double) current / maximum));
    }

    @FXML
    private void onPause() {
        paused = true;
        lblBattleState.setText("戰鬥暫停");
        lblBattleState.getStyleClass().add("paused-badge");
        btnPause.setVisible(false);
        btnPause.setManaged(false);
        btnResume.setVisible(true);
        btnResume.setManaged(true);
    }

    @FXML
    private void onResume() {
        paused = false;
        lastTimeNs = -1;
        lblBattleState.setText("戰鬥進行中");
        lblBattleState.getStyleClass().remove("paused-badge");
        btnPause.setVisible(true);
        btnPause.setManaged(true);
        btnResume.setVisible(false);
        btnResume.setManaged(false);
    }

    @FXML
    private void onSpeed1x() {
        speedMultiplier = 1.0;
        setActiveSpeedButton(btnSpeed1x, btnSpeed2x);
    }

    @FXML
    private void onSpeed2x() {
        speedMultiplier = 2.0;
        setActiveSpeedButton(btnSpeed2x, btnSpeed1x);
    }

    private void setActiveSpeedButton(Button active, Button inactive) {
        if (!active.getStyleClass().contains("active-control")) {
            active.getStyleClass().add("active-control");
        }
        inactive.getStyleClass().remove("active-control");
    }
}
