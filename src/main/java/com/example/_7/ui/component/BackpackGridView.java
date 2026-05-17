package com.example._7.ui.component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class BackpackGridView extends VBox {
    private final GridPane grid = new GridPane();

    public BackpackGridView() {
        setSpacing(6);
        setPadding(new Insets(6));
        getChildren().add(new Label("Backpack"));
        grid.setHgap(4);
        grid.setVgap(4);

        // simple 5x5 placeholder
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                Rectangle rect = new Rectangle(30, 30);
                rect.getStyleClass().add("backpack-cell");
                rect.setStyle("-fx-fill: #ddd; -fx-stroke: #999;");
                grid.add(rect, c, r);
            }
        }
        getChildren().add(grid);
    }
}