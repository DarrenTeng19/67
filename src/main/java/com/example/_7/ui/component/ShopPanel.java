package com.example._7.ui.component;

import com.example._7.item.Item;
import com.example._7.shop.Shop;
import com.example._7.shop.ShopOffer;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class ShopPanel extends VBox {
    private final ListView<ShopOffer> list = new ListView<>(FXCollections.observableArrayList());
    private final Button btnBuy = new Button("購買選取商品");

    private Consumer<ShopOffer> onBuyOffer;
    private Consumer<Item> onItemSelected;

    public ShopPanel() {
        setSpacing(6);
        setPadding(new Insets(4, 0, 0, 0));
        getStyleClass().add("shop-panel");
        list.setPrefHeight(230);
        list.setMinHeight(150);
        list.setCellFactory(view -> new ShopOfferCell());

        list.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (onItemSelected != null && newValue != null) {
                onItemSelected.accept(newValue.getItem());
            }
        });

        list.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                buySelected();
            }
        });

        btnBuy.setMaxWidth(Double.MAX_VALUE);
        btnBuy.setOnAction(event -> buySelected());

        getChildren().addAll(list, btnBuy);
    }

    public void setShop(Shop shop) {
        list.getItems().clear();
        if (shop == null) return;
        list.getItems().addAll(shop.getOffers());
    }

    public void setOnBuyOffer(Consumer<ShopOffer> onBuyOffer) {
        this.onBuyOffer = onBuyOffer;
    }

    public void setOnItemSelected(Consumer<Item> onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    public ShopOffer getSelectedOffer() {
        return list.getSelectionModel().getSelectedItem();
    }

    public void refresh() {
        list.refresh();
    }

    private void buySelected() {
        ShopOffer offer = getSelectedOffer();
        if (offer != null && onBuyOffer != null) {
            onBuyOffer.accept(offer);
        }
    }

    private static class ShopOfferCell extends ListCell<ShopOffer> {
        @Override
        protected void updateItem(ShopOffer offer, boolean empty) {
            super.updateItem(offer, empty);
            if (empty || offer == null || offer.getItem() == null) {
                setText(null);
                setStyle("");
                return;
            }

            Item item = offer.getItem();
            String prefix = offer.isSold() ? "[已售出] " : "";
            setText(prefix + item.getName()
                    + "  $" + item.getPrice()
                    + "  " + item.getShape().width() + "x" + item.getShape().height());
            setStyle(offer.isSold()
                    ? "-fx-opacity: 0.45; -fx-text-fill: #777;"
                    : "");
        }
    }
}
