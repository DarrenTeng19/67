package com.example._7.ui.component;

import com.example._7.item.Item;
import com.example._7.item.ItemAffinity;
import com.example._7.item.ItemRole;
import com.example._7.item.ItemTriggerType;
import com.example._7.item.shape.ItemShape;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;

public class ItemCardView extends VBox {
    private final Label lblTitle = new Label("尚未選擇物品");
    private final Label lblBasic = new Label("-");
    private final Label lblBattle = new Label("-");
    private final Label lblEffect = new Label("-");
    private final Label lblMessage = new Label("");

    public ItemCardView() {
        setSpacing(6);
        setPadding(new Insets(4, 2, 8, 2));
        getStyleClass().add("item-card");
        lblTitle.getStyleClass().add("item-card-title");
        lblBasic.getStyleClass().add("item-card-text");
        lblBattle.getStyleClass().add("item-card-text");
        lblEffect.getStyleClass().add("item-card-text");
        lblMessage.getStyleClass().add("item-message");
        lblBasic.setWrapText(true);
        lblBattle.setWrapText(true);
        lblEffect.setWrapText(true);
        lblMessage.setWrapText(true);
        getChildren().addAll(lblTitle, lblBasic, lblBattle, lblEffect, lblMessage);
    }

    public void setItem(Item item) {
        if (item == null) {
            clear();
            return;
        }

        ItemShape shape = item.getShape();
        String roles = item.getRoles().stream()
                .map(this::roleName)
                .collect(Collectors.joining(" / "));

        lblTitle.setText(item.getName());
        lblBasic.setText("職業：" + affinityName(item.getAffinity())
                + "\n類型：" + roles
                + "\n價格：" + item.getPrice()
                + "｜稀有度：" + item.getRarity()
                + "\n尺寸：" + shape.width() + " x " + shape.height());
        lblBattle.setText("觸發：" + triggerName(item.getTriggerType())
                + "\n冷卻：" + item.getCooldownSeconds() + " 秒"
                + "｜命中率：" + Math.round(item.getHitRate() * 100) + "%"
                + "\n消耗：耐力 " + item.getStaminaCost() + " / 魔力 " + item.getManaCost());
        lblEffect.setText("效果：" + item.getDescription());
    }

    public void clear() {
        lblTitle.setText("尚未選擇物品");
        lblBasic.setText("-");
        lblBattle.setText("-");
        lblEffect.setText("-");
    }

    public void setMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }

    private String affinityName(ItemAffinity affinity) {
        return switch (affinity) {
            case COMMON -> "通用";
            case WARRIOR -> "戰士";
            case RANGER -> "遊俠";
            case MAGE -> "魔法師";
        };
    }

    private String roleName(ItemRole role) {
        return switch (role) {
            case COMPONENT -> "組件";
            case EQUIPMENT -> "裝備";
        };
    }

    private String triggerName(ItemTriggerType triggerType) {
        return switch (triggerType) {
            case ACTIVE -> "主動";
            case PASSIVE -> "被動";
        };
    }
}
