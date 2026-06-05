package com.example._7.ui.component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CharacterInfoPanel extends VBox {
    private final Label lblName = new Label("玩家");
    private final Label lblClass = new Label("Class: -");
    private final Label lblGold = new Label("Gold: 0");
    private final Label lblRound = new Label("Round: 1");

    public CharacterInfoPanel() {
        setSpacing(4);
        setPadding(new Insets(13));
        getStyleClass().add("character-info-panel");
        lblName.getStyleClass().add("character-name");
        lblClass.getStyleClass().add("character-meta");
        lblGold.getStyleClass().add("character-meta");
        lblRound.getStyleClass().add("character-meta");
        getChildren().addAll(lblName, lblClass, lblGold, lblRound);
    }

    public void setName(String n) {
        lblName.setText(n == null ? "玩家" : n);
    }

    public void setCharacterClass(String c) {
        lblClass.setText("職業：" + (c == null ? "-" : c));
    }

    public void setGold(int gold) {
        lblGold.setText("金幣：" + gold);
    }

    public void setRound(int round) {
        lblRound.setText("回合：" + round);
    }
}
