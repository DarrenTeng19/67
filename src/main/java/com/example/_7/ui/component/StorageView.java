package com.example._7.ui.component;

import com.example._7.inventory.Rotation;
import com.example._7.inventory.Storage;
import com.example._7.item.Item;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class StorageView extends VBox {
    private final ListView<Item> list = new ListView<>(FXCollections.observableArrayList());
    private final Button btnAutoPlace = new Button("自動放入背包");
    private final Button btnSell = new Button("賣出選取物品");
    private final Label rotationHint = new Label("旋轉：0°");

    private Consumer<Item> onItemSelected;
    private Consumer<Item> onAutoPlaceRequested;
    private Consumer<Item> onSellRequested;
    private Consumer<Item> onDragStarted;
    private Runnable onDragDone;
    private Consumer<Rotation> onRotationChanged;

    private Item heldItem;
    private Rotation currentDragRotation = Rotation.DEGREE_0;

    public StorageView() {
        setSpacing(6);
        setPadding(new Insets(6));
        setFocusTraversable(true);
        list.setFocusTraversable(true);

        getChildren().add(new Label("Storage 儲物箱"));
        list.setPrefHeight(260);
        list.setCellFactory(view -> new StorageItemCell());

        list.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                heldItem = newValue;
                if (onItemSelected != null) {
                    onItemSelected.accept(newValue);
                }
            }
        });

        list.addEventFilter(KeyEvent.KEY_PRESSED, this::handleRotationKeyPressed);
        addEventFilter(KeyEvent.KEY_PRESSED, this::handleRotationKeyPressed);

        // 備援：如果事件落在 ListView 本身，也可以開始拖曳。
        list.setOnDragDetected(event -> {
            Item selected = getSelectedItem();
            if (selected == null) {
                return;
            }
            beginDrag(selected, list);
            event.consume();
        });

        list.setOnDragDone(event -> {
            finishDrag();
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
        getChildren().addAll(
                list,
                rotationHint,
                buttons,
                new Label("提示：拖曳儲物箱物品到背包；按住/選取物品時按 R 順時針旋轉、Q 逆時針旋轉。")
        );
    }

    public void setStorage(Storage storage) {
        Item selected = getSelectedItem();
        list.getItems().clear();
        if (storage != null) {
            list.getItems().addAll(storage.getItems());
        }
        if (selected != null && list.getItems().contains(selected)) {
            list.getSelectionModel().select(selected);
        }
    }

    public Item getSelectedItem() {
        return list.getSelectionModel().getSelectedItem();
    }

    public void selectItem(Item item) {
        list.getSelectionModel().select(item);
        heldItem = item;
    }

    public Rotation getCurrentDragRotation() {
        return currentDragRotation;
    }

    public void resetDragRotation() {
        currentDragRotation = Rotation.DEGREE_0;
        updateRotationHint();
        if (onRotationChanged != null) {
            onRotationChanged.accept(currentDragRotation);
        }
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

    public void setOnRotationChanged(Consumer<Rotation> onRotationChanged) {
        this.onRotationChanged = onRotationChanged;
    }

    private void beginDrag(Item item, Node source) {
        if (item == null || source == null) {
            return;
        }

        list.getSelectionModel().select(item);
        heldItem = item;
        list.requestFocus();

        Dragboard dragboard = source.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString("STORAGE_ITEM:" + item.getId());
        dragboard.setContent(content);

        if (onDragStarted != null) {
            onDragStarted.accept(item);
        }
    }

    private void finishDrag() {
        heldItem = null;
        resetDragRotation();
        if (onDragDone != null) {
            onDragDone.run();
        }
    }

    private void handleRotationKeyPressed(KeyEvent event) {
        if (event.getCode() != KeyCode.R && event.getCode() != KeyCode.Q) {
            return;
        }

        Item target = heldItem != null ? heldItem : getSelectedItem();
        if (target == null) {
            return;
        }

        currentDragRotation = event.getCode() == KeyCode.R
                ? currentDragRotation.nextClockwise()
                : currentDragRotation.nextCounterClockwise();

        updateRotationHint();
        if (onRotationChanged != null) {
            onRotationChanged.accept(currentDragRotation);
        }
        event.consume();
    }

    private void updateRotationHint() {
        rotationHint.setText("旋轉：" + currentDragRotation.getDisplayName());
    }

    private class StorageItemCell extends ListCell<Item> {
        StorageItemCell() {
            setOnMousePressed(event -> {
                Item item = getItem();
                if (item != null) {
                    heldItem = item;
                    currentDragRotation = Rotation.DEGREE_0;
                    updateRotationHint();
                    list.getSelectionModel().select(item);
                    list.requestFocus();
                    if (onRotationChanged != null) {
                        onRotationChanged.accept(currentDragRotation);
                    }
                }
                // 不 consume，否則 ListView / Cell 可能無法產生 drag detected。
            });

            setOnMouseReleased(event -> {
                if (getScene() == null) {
                    heldItem = null;
                }
            });

            setOnDragDetected(event -> {
                Item item = getItem();
                if (item == null) {
                    return;
                }
                beginDrag(item, this);
                event.consume();
            });

            setOnDragDone(event -> {
                finishDrag();
                event.consume();
            });
        }

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
