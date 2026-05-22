package com.example._7.ui.component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

public class HealthBarView extends HBox {
    private final ProgressBar bar = new ProgressBar(1.0);
    private final Label lbl = new Label("100/100");

    public HealthBarView() {
        setSpacing(6);
        setPadding(new Insets(4));
        getChildren().addAll(bar, lbl);
        bar.setPrefWidth(120);
    }

    public void update(int cur, int max) {
        bar.setProgress(max <= 0 ? 0 : (double) cur / max);
        lbl.setText(cur + "/" + max);
    }
}