package com.example._7.ui.component;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class StorageView extends VBox {
    private final ListView<String> list = new ListView<>(FXCollections.observableArrayList());

    public StorageView() {
        setSpacing(6);
        setPadding(new Insets(6));
        getChildren().add(list);
        list.getItems().addAll("Stored A", "Stored B");
    }
}