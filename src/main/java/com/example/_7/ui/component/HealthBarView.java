package com.example._7.ui.component;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class HealthBarView extends VBox {
    private final ProgressBar bar = new ProgressBar(1.0);
    private final Label hpValue = new Label("100 / 100");
    private final Label staminaValue = new Label("0");
    private final Label staminaRecovery = new Label("+0 / 秒");
    private final Label manaValue = new Label("0");
    private final Label manaRecovery = new Label("+0 / 秒");
    private final Label title = new Label("生命值");

    public HealthBarView() {
        setSpacing(5);
        setPadding(new Insets(11, 14, 9, 14));
        getStyleClass().add("health-panel");
        title.getStyleClass().add("health-title");
        hpValue.getStyleClass().add("health-value");
        staminaValue.getStyleClass().add("resource-value");
        staminaRecovery.getStyleClass().add("resource-recovery");
        manaValue.getStyleClass().add("resource-value");
        manaRecovery.getStyleClass().add("resource-recovery");

        HBox hpHeader = new HBox(title, spacer(), hpValue);
        HBox resources = new HBox(
                8,
                resourceBox("耐力", staminaValue, staminaRecovery, "stamina-stat"),
                resourceBox("魔力", manaValue, manaRecovery, "mana-stat")
        );
        getChildren().addAll(hpHeader, bar, resources);
        bar.setMaxWidth(Double.MAX_VALUE);
    }

    public void update(
            int currentHp,
            int maxHp,
            int maxStamina,
            double staminaRecoveryRate,
            int maxMana,
            double manaRecoveryRate
    ) {
        bar.setProgress(maxHp <= 0 ? 0 : (double) currentHp / maxHp);
        hpValue.setText(currentHp + " / " + maxHp);
        staminaValue.setText(String.valueOf(maxStamina));
        staminaRecovery.setText("+" + formatRate(staminaRecoveryRate) + " / 秒");
        manaValue.setText(String.valueOf(maxMana));
        manaRecovery.setText("+" + formatRate(manaRecoveryRate) + " / 秒");
    }

    public void update(int currentHp, int maxHp) {
        bar.setProgress(maxHp <= 0 ? 0 : (double) currentHp / maxHp);
        hpValue.setText(currentHp + " / " + maxHp);
    }

    private VBox resourceBox(String name, Label value, Label recovery, String styleClass) {
        Label label = new Label(name);
        label.getStyleClass().add("resource-label");
        HBox values = new HBox(6, value, recovery);
        VBox box = new VBox(1, label, values);
        box.getStyleClass().addAll("resource-stat", styleClass);
        HBox.setHgrow(box, javafx.scene.layout.Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        return spacer;
    }

    private String formatRate(double value) {
        if (value == Math.rint(value)) {
            return String.format("%.0f", value);
        }
        return String.format("%.1f", value);
    }
}
