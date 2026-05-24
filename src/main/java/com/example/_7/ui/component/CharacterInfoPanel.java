package com.example._7.ui.component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CharacterInfoPanel extends VBox {
    private final Label lblName = new Label("Player");
    private final Label lblClass = new Label("Class: -");
    private final Label lblGold = new Label("Gold: 0");
    private final Label lblRound = new Label("Round: 1");

    public CharacterInfoPanel() {
        setSpacing(4);
        setPadding(new Insets(6));
        getChildren().addAll(lblName, lblClass, lblGold, lblRound);
    }

    public void setName(String n) {
        lblName.setText(n == null ? "Player" : n);
    }

    public void setCharacterClass(String c) {
        lblClass.setText("Class: " + (c == null ? "-" : c));
    }

    public void setGold(int gold) {
        lblGold.setText("Gold: " + gold);
    }

    public void setRound(int round) {
        lblRound.setText("Round: " + round);
    }
}
