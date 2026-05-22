package com.example._7.ui.component;

import com.example._7.item.Item;
import com.example._7.shop.Shop;
import com.example._7.shop.ShopOffer;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class ShopPanel extends VBox {
    private final ListView<String> list = new ListView<>();

    public ShopPanel() {
        setSpacing(6);
        setPadding(new Insets(6));
        getChildren().add(new Label("Shop"));
        list.setPrefHeight(120);
        getChildren().add(list);

        // placeholder content
        list.getItems().addAll("Offer A", "Offer B", "Offer C");
    }

    public void setShop(Shop shop) {
        list.getItems().clear();
        if (shop == null) return;
        for (ShopOffer o : shop.getOffers()) {
            Item i = o.getItem();
            list.getItems().add(i == null ? "(null)" : i.getName());
        }
    }
}