package com.example._7.ui.component;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RecycleBinView extends VBox {
    private final Label label = new Label("Recycle 回收箱");
    private final Button btnSellSelected = new Button("賣出目前選取物品");

    public RecycleBinView() {
        setSpacing(6);
        setPadding(new Insets(8));
        setStyle("-fx-border-color: #c88; -fx-background-color: #fff5f5; -fx-border-radius: 6; -fx-background-radius: 6;");
        btnSellSelected.setMaxWidth(Double.MAX_VALUE);
        getChildren().addAll(label, btnSellSelected);
    }

    public Button getSellButton() {
        return btnSellSelected;
    }
}
