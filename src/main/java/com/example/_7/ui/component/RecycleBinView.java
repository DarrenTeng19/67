package com.example._7.ui.component;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class RecycleBinView extends VBox {
    private final Button btn = new Button("Recycle");

    public RecycleBinView() {
        setPadding(new Insets(6));
        getChildren().add(btn);
    }

    public Button getButton() { return btn; }
}