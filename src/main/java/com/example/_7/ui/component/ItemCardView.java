package com.example._7.ui.component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ItemCardView extends VBox {
    private final Label lblName = new Label("Item Name");
    private final Label lblPrice = new Label("Price: 0");

    public ItemCardView() {
        setSpacing(4);
        setPadding(new Insets(6));
        getChildren().addAll(lblName, lblPrice);
        setStyle("-fx-border-color: #bbb; -fx-background-color: #fafafa;");
    }

    public void setName(String name) { lblName.setText(name); }
    public void setPrice(int p) { lblPrice.setText("Price: " + p); }
}