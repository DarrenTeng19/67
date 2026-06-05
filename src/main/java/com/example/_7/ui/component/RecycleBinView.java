package com.example._7.ui.component;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RecycleBinView extends VBox {
    private final Label label = new Label("回收箱");
    private final Button btnSellSelected = new Button("賣出目前選取物品");

    public RecycleBinView() {
        setSpacing(6);
        setPadding(new Insets(8));
        getStyleClass().add("recycle-panel");
        label.getStyleClass().add("component-title");
        btnSellSelected.setMaxWidth(Double.MAX_VALUE);
        getChildren().addAll(label, btnSellSelected);
    }

    public Button getSellButton() {
        return btnSellSelected;
    }
}
