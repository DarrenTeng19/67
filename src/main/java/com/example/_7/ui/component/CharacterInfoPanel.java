package com.example._7.ui.component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CharacterInfoPanel extends VBox {
    private final Label lblName = new Label("Player");
    private final Label lblClass = new Label("Class: -");

    public CharacterInfoPanel() {
        setSpacing(4);
        setPadding(new Insets(6));
        getChildren().addAll(lblName, lblClass);
    }

    public void setName(String n) { lblName.setText(n); }
    public void setCharacterClass(String c) { lblClass.setText("Class: " + c); }
}