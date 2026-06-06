package com.example._7.ui.component;

import com.example._7.inventory.Rotation;
import com.example._7.inventory.Storage;
import com.example._7.inventory.PlacedItem;
import com.example._7.item.Item;
import com.example._7.item.shape.ItemShape;
import javafx.application.Platform;
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
import javafx.scene.layout.VBox;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class StorageView extends VBox {
    private final ListView<Item> list = new ListView<>(FXCollections.observableArrayList());
    private final Button btnAutoPlace = new Button("自動放入背包");
    private final Label rotationHint = new Label("旋轉：0°");
    private final Label helpLabel = new Label("Q / R  旋轉");

    private Consumer<Item> onItemSelected;
    private Consumer<Item> onAutoPlaceRequested;
    private Consumer<Item> onDragStarted;
    private Runnable onDragDone;
    private Consumer<Rotation> onRotationChanged;
    private Supplier<PlacedItem> draggedBackpackItemSupplier;
    private Function<PlacedItem, Boolean> onBackpackItemDropped;

    private Item heldItem;
    private Rotation currentDragRotation = Rotation.DEGREE_0;

    public StorageView() {
        setSpacing(6);
        setPadding(new Insets(4, 0, 0, 0));
        setFocusTraversable(true);
        getStyleClass().add("storage-panel");
        rotationHint.getStyleClass().add("component-hint");
        helpLabel.getStyleClass().add("component-hint");
        list.setFocusTraversable(true);

        list.setPrefHeight(215);
        list.setMinHeight(140);
        list.setCellFactory(view -> new StorageItemCell());

        list.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                heldItem = newValue;
                if (onItemSelected != null) {
                    onItemSelected.accept(newValue);
                }
            } else {
                heldItem = null;
            }
            list.refresh();
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
            finishDrag(event.getTransferMode() != null);
            event.consume();
        });

        setupBackpackDropTarget(this);
        setupBackpackDropTarget(list);

        btnAutoPlace.setMaxWidth(Double.MAX_VALUE);
        btnAutoPlace.setOnAction(event -> {
            Item selected = getSelectedItem();
            if (selected != null && onAutoPlaceRequested != null) {
                onAutoPlaceRequested.accept(selected);
            }
        });

        getChildren().addAll(
                list,
                rotationHint,
                btnAutoPlace,
                helpLabel
        );
    }

    public void setStorage(Storage storage) {
        var items = storage == null ? java.util.List.<Item>of() : storage.getItems();
        if (list.getItems().equals(items)) {
            return;
        }

        Item selected = getSelectedItem();
        int selectedIndex = list.getSelectionModel().getSelectedIndex();
        list.getItems().setAll(items);

        if (selected != null && list.getItems().contains(selected)) {
            list.getSelectionModel().select(selected);
        } else if (selectedIndex >= 0 && !list.getItems().isEmpty()) {
            int nextIndex = Math.min(selectedIndex, list.getItems().size() - 1);
            list.getSelectionModel().select(nextIndex);
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
        setCurrentDragRotation(Rotation.DEGREE_0, true);
    }

    public void setCurrentDragRotation(Rotation rotation) {
        setCurrentDragRotation(rotation, true);
    }

    public void rotateCurrentDragRotation(boolean clockwise) {
        Rotation next = clockwise
                ? currentDragRotation.nextClockwise()
                : currentDragRotation.nextCounterClockwise();
        setCurrentDragRotation(next, true);
    }

    private void setCurrentDragRotation(Rotation rotation, boolean notify) {
        currentDragRotation = rotation == null ? Rotation.DEGREE_0 : rotation;
        updateRotationHint();
        list.refresh();
        if (notify && onRotationChanged != null) {
            onRotationChanged.accept(currentDragRotation);
        }
    }

    public void setOnItemSelected(Consumer<Item> onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    public void setOnAutoPlaceRequested(Consumer<Item> onAutoPlaceRequested) {
        this.onAutoPlaceRequested = onAutoPlaceRequested;
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

    public void setDraggedBackpackItemSupplier(Supplier<PlacedItem> draggedBackpackItemSupplier) {
        this.draggedBackpackItemSupplier = draggedBackpackItemSupplier;
    }

    public void setOnBackpackItemDropped(Function<PlacedItem, Boolean> onBackpackItemDropped) {
        this.onBackpackItemDropped = onBackpackItemDropped;
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

    private void finishDrag(boolean dropSucceeded) {
        heldItem = null;

        // 如果拖曳成功，旋轉角度回到 0°，方便下一個物品從預設角度開始。
        // 如果拖曳失敗，保留目前角度，讓玩家可以換位置再拖一次，不需要重新按 Q/R。
        if (dropSucceeded) {
            resetDragRotation();
        }

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

        rotateCurrentDragRotation(event.getCode() == KeyCode.R);
        Platform.runLater(list::requestFocus);
        event.consume();
    }


    private void setupBackpackDropTarget(Node target) {
        if (target == null) {
            return;
        }

        target.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString() && dragboard.getString().startsWith("BACKPACK_ITEM:")) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        target.setOnDragDropped(event -> {
            boolean success = false;
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString() && dragboard.getString().startsWith("BACKPACK_ITEM:")) {
                PlacedItem placedItem = draggedBackpackItemSupplier == null ? null : draggedBackpackItemSupplier.get();
                if (placedItem != null && onBackpackItemDropped != null) {
                    success = Boolean.TRUE.equals(onBackpackItemDropped.apply(placedItem));
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void updateRotationHint() {
        rotationHint.setText("旋轉：" + currentDragRotation.getDisplayName());
    }

    private class StorageItemCell extends ListCell<Item> {
        StorageItemCell() {
            setOnMousePressed(event -> {
                Item item = getItem();
                if (item != null) {
                    Item previouslySelected = getSelectedItem();
                    boolean isSameSelectedItem = previouslySelected == item;

                    heldItem = item;

                    // 重要：如果玩家已經選取這個物品並按 Q/R 轉好角度，
                    // 再按住同一個 cell 開始拖曳時，不可以把角度重設成 0°。
                    // 否則像木劍 1x2 必須橫放時，拖曳一開始就會被重置成直放，導致放不進背包。
                    if (!isSameSelectedItem) {
                        setCurrentDragRotation(Rotation.DEGREE_0, true);
                    } else {
                        updateRotationHint();
                    }

                    list.getSelectionModel().select(item);
                    list.requestFocus();
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
                finishDrag(event.getTransferMode() != null);
                event.consume();
            });

            setupBackpackDropTarget(this);
        }

        @Override
        protected void updateItem(Item item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                return;
            }
            Rotation displayRotation = item == getSelectedItem()
                    ? currentDragRotation
                    : Rotation.DEGREE_0;
            ItemShape displayShape = item.getShape().rotated(displayRotation);
            setText(item.getName()
                    + "  $" + item.getPrice()
                    + "  " + displayShape.width() + "x" + displayShape.height());
        }
    }
}
