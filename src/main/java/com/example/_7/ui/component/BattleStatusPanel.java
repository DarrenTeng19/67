package com.example._7.ui.component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.ProgressBar;

public class BattleStatusPanel extends VBox {
    private final ProgressBar playerHp = new ProgressBar(1.0);
    private final ProgressBar enemyHp = new ProgressBar(1.0);

    public BattleStatusPanel() {
        setSpacing(6);
        setPadding(new Insets(6));
        getChildren().addAll(new Label("Battle Status"), new Label("Player HP"), playerHp, new Label("Enemy HP"), enemyHp);
        setPrefWidth(200);
    }

    public void update(double playerRatio, double enemyRatio) {
        playerHp.setProgress(playerRatio);
        enemyHp.setProgress(enemyRatio);
    }
}