package com.example._7.ui.component;

import com.example._7.inventory.Storage;
import com.example._7.item.Item;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class StorageView extends VBox {
    private final ListView<Item> list = new ListView<>(FXCollections.observableArrayList());
    private final Button btnAutoPlace = new Button("自動放入背包");
    private final Button btnSell = new Button("賣出選取物品");

    private Consumer<Item> onItemSelected;
    private Consumer<Item> onAutoPlaceRequested;
    private Consumer<Item> onSellRequested;
    private Consumer<Item> onDragStarted;
    private Runnable onDragDone;

    public StorageView() {
        setSpacing(6);
        setPadding(new Insets(6));
        getChildren().add(new Label("Storage 儲物箱"));
        list.setPrefHeight(260);
        list.setCellFactory(view -> new StorageItemCell());

        list.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (onItemSelected != null && newValue != null) {
                onItemSelected.accept(newValue);
            }
        });

        list.setOnDragDetected(event -> {
            Item selected = getSelectedItem();
            if (selected == null) {
                return;
            }
            Dragboard dragboard = list.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("STORAGE_ITEM:" + selected.getId());
            dragboard.setContent(content);
            if (onDragStarted != null) {
                onDragStarted.accept(selected);
            }
            event.consume();
        });

        list.setOnDragDone(event -> {
            if (onDragDone != null) {
                onDragDone.run();
            }
            event.consume();
        });

        btnAutoPlace.setMaxWidth(Double.MAX_VALUE);
        btnAutoPlace.setOnAction(event -> {
            Item selected = getSelectedItem();
            if (selected != null && onAutoPlaceRequested != null) {
                onAutoPlaceRequested.accept(selected);
            }
        });

        btnSell.setMaxWidth(Double.MAX_VALUE);
        btnSell.setOnAction(event -> {
            Item selected = getSelectedItem();
            if (selected != null && onSellRequested != null) {
                onSellRequested.accept(selected);
            }
        });

        HBox buttons = new HBox(6, btnAutoPlace, btnSell);
        getChildren().addAll(list, buttons, new Label("提示：也可以把儲物箱物品拖曳到背包格子。"));
    }

    public void setStorage(Storage storage) {
        list.getItems().clear();
        if (storage != null) {
            list.getItems().addAll(storage.getItems());
        }
    }

    public Item getSelectedItem() {
        return list.getSelectionModel().getSelectedItem();
    }

    public void selectItem(Item item) {
        list.getSelectionModel().select(item);
    }

    public void setOnItemSelected(Consumer<Item> onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    public void setOnAutoPlaceRequested(Consumer<Item> onAutoPlaceRequested) {
        this.onAutoPlaceRequested = onAutoPlaceRequested;
    }

    public void setOnSellRequested(Consumer<Item> onSellRequested) {
        this.onSellRequested = onSellRequested;
    }

    public void setOnDragStarted(Consumer<Item> onDragStarted) {
        this.onDragStarted = onDragStarted;
    }

    public void setOnDragDone(Runnable onDragDone) {
        this.onDragDone = onDragDone;
    }

    private static class StorageItemCell extends ListCell<Item> {
        @Override
        protected void updateItem(Item item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                return;
            }
            setText(item.getName()
                    + "  $" + item.getPrice()
                    + "  " + item.getShape().width() + "x" + item.getShape().height());
        }
    }
}
