package com.example._7.ui.component;

import com.example._7.inventory.Backpack;
import com.example._7.inventory.GridPosition;
import com.example._7.inventory.PlacedItem;
import com.example._7.inventory.Rotation;
import com.example._7.item.Item;
import com.example._7.item.shape.ItemShape;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BackpackGridView extends VBox {
    private static final int CELL_SIZE = 54;

    @FunctionalInterface
    public interface StorageItemDropHandler {
        boolean drop(Item item, GridPosition position, Rotation rotation);
    }

    @FunctionalInterface
    public interface PlacedItemRotationHandler {
        boolean rotate(PlacedItem placedItem, boolean clockwise);
    }

    private final GridPane grid = new GridPane();
    private Backpack backpack;
    private PlacedItem selectedPlacedItem;
    private PlacedItem draggingPlacedItem;
    private PlacedItem heldPlacedItem;

    private Supplier<Item> draggedStorageItemSupplier;
    private Supplier<Rotation> draggedStorageRotationSupplier;
    private StorageItemDropHandler onStorageItemDropped;
    private BiFunction<PlacedItem, GridPosition, Boolean> onPlacedItemMoved;
    private PlacedItemRotationHandler onPlacedItemRotationRequested;
    private Consumer<Boolean> onDraggedStorageItemRotationRequested;
    private Consumer<PlacedItem> onPlacedItemSelected;

    public BackpackGridView() {
        setSpacing(6);
        setPadding(new Insets(6));
        setFocusTraversable(true);
        grid.setFocusTraversable(true);

        getChildren().add(new Label("Backpack 背包"));
        grid.setHgap(4);
        grid.setVgap(4);
        getChildren().add(grid);
        getChildren().add(new Label("提示：拖曳物品到格子；按住/選取背包物品時按 R 順時針旋轉、Q 逆時針旋轉。"));

        addEventFilter(KeyEvent.KEY_PRESSED, this::handleRotationKeyPressed);
        grid.addEventFilter(KeyEvent.KEY_PRESSED, this::handleRotationKeyPressed);
    }

    public void setBackpack(Backpack backpack) {
        this.backpack = backpack;
        redraw();
    }

    public PlacedItem getSelectedPlacedItem() {
        return selectedPlacedItem;
    }

    public void clearSelection() {
        selectedPlacedItem = null;
        heldPlacedItem = null;
        redraw();
    }

    public void setDraggedStorageItemSupplier(Supplier<Item> draggedStorageItemSupplier) {
        this.draggedStorageItemSupplier = draggedStorageItemSupplier;
    }

    public void setDraggedStorageRotationSupplier(Supplier<Rotation> draggedStorageRotationSupplier) {
        this.draggedStorageRotationSupplier = draggedStorageRotationSupplier;
    }

    public void setOnStorageItemDropped(StorageItemDropHandler onStorageItemDropped) {
        this.onStorageItemDropped = onStorageItemDropped;
    }

    public void setOnPlacedItemMoved(BiFunction<PlacedItem, GridPosition, Boolean> onPlacedItemMoved) {
        this.onPlacedItemMoved = onPlacedItemMoved;
    }

    public void setOnPlacedItemRotationRequested(PlacedItemRotationHandler onPlacedItemRotationRequested) {
        this.onPlacedItemRotationRequested = onPlacedItemRotationRequested;
    }

    public void setOnDraggedStorageItemRotationRequested(Consumer<Boolean> onDraggedStorageItemRotationRequested) {
        this.onDraggedStorageItemRotationRequested = onDraggedStorageItemRotationRequested;
    }

    public void setOnPlacedItemSelected(Consumer<PlacedItem> onPlacedItemSelected) {
        this.onPlacedItemSelected = onPlacedItemSelected;
    }

    public void redraw() {
        grid.getChildren().clear();
        if (backpack == null) {
            return;
        }

        for (int row = 0; row < backpack.getRows(); row++) {
            for (int col = 0; col < backpack.getCols(); col++) {
                GridPosition position = new GridPosition(row, col);
                StackPane cell = createCell(position);
                grid.add(cell, col, row);
            }
        }
    }

    private StackPane createCell(GridPosition position) {
        StackPane cell = new StackPane();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setMinSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);
        cell.setFocusTraversable(true);

        Optional<PlacedItem> placedOptional = backpack.getPlacedItemAt(position);
        PlacedItem placedItem = placedOptional.orElse(null);

        String baseStyle = "-fx-border-color: #999; -fx-background-color: #eeeeee;";
        if (placedItem != null) {
            baseStyle = "-fx-border-color: #555; -fx-background-color: #cfe8ff;";
            Label label = new Label(cellText(placedItem));
            label.setWrapText(true);
            cell.getChildren().add(label);
        }
        if (placedItem != null && placedItem == selectedPlacedItem) {
            baseStyle = "-fx-border-color: #ff9900; -fx-border-width: 3; -fx-background-color: #ffe2b6;";
        }
        cell.setStyle(baseStyle);

        cell.setOnMousePressed(event -> {
            requestFocus();
            grid.requestFocus();
            if (placedItem != null) {
                heldPlacedItem = placedItem;
                selectedPlacedItem = placedItem;
                if (onPlacedItemSelected != null) {
                    onPlacedItemSelected.accept(placedItem);
                }
                // 不要在 mouse pressed 時 redraw，否則原本的 cell 會被刪掉，JavaFX 會偵測不到 drag gesture。
            }
        });

        cell.setOnMouseReleased(event -> {
            // 如果正在拖曳，不要在這裡清掉 heldPlacedItem；等 DragDone / Drop 再清。
            if (draggingPlacedItem == null) {
                heldPlacedItem = null;
            }
        });

        cell.setOnMouseClicked(event -> {
            requestFocus();
            grid.requestFocus();
            if (placedItem != null) {
                selectedPlacedItem = placedItem;
                if (onPlacedItemSelected != null) {
                    onPlacedItemSelected.accept(placedItem);
                }
            } else {
                selectedPlacedItem = null;
                if (onPlacedItemSelected != null) {
                    onPlacedItemSelected.accept(null);
                }
            }
            redraw();
            event.consume();
        });

        cell.setOnDragDetected(event -> {
            if (placedItem == null) {
                return;
            }
            requestFocus();
            grid.requestFocus();
            selectedPlacedItem = placedItem;
            heldPlacedItem = placedItem;
            draggingPlacedItem = placedItem;
            if (onPlacedItemSelected != null) {
                onPlacedItemSelected.accept(placedItem);
            }

            Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("BACKPACK_ITEM:" + placedItem.getItem().getId());
            dragboard.setContent(content);
            event.consume();
        });

        cell.setOnDragOver(event -> {
            requestFocus();
            grid.requestFocus();
            cell.requestFocus();
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()
                    && (dragboard.getString().startsWith("STORAGE_ITEM:")
                    || dragboard.getString().startsWith("BACKPACK_ITEM:"))) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        cell.setOnDragDropped(event -> {
            boolean success = false;
            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasString()) {
                String payload = dragboard.getString();
                if (payload.startsWith("STORAGE_ITEM:")) {
                    Item draggedStorageItem = draggedStorageItemSupplier == null ? null : draggedStorageItemSupplier.get();
                    Rotation draggedRotation = draggedStorageRotationSupplier == null ? Rotation.DEGREE_0 : draggedStorageRotationSupplier.get();
                    if (draggedRotation == null) {
                        draggedRotation = Rotation.DEGREE_0;
                    }
                    if (draggedStorageItem != null && onStorageItemDropped != null) {
                        success = onStorageItemDropped.drop(draggedStorageItem, position, draggedRotation);
                    }
                } else if (payload.startsWith("BACKPACK_ITEM:")) {
                    if (draggingPlacedItem != null && onPlacedItemMoved != null) {
                        success = onPlacedItemMoved.apply(draggingPlacedItem, position);
                    }
                }
            }

            draggingPlacedItem = null;
            heldPlacedItem = null;
            event.setDropCompleted(success);
            event.consume();
            redraw();
        });

        cell.setOnDragDone(event -> {
            draggingPlacedItem = null;
            heldPlacedItem = null;
            event.consume();
        });

        return cell;
    }

    private void handleRotationKeyPressed(KeyEvent event) {
        if (event.getCode() != KeyCode.R && event.getCode() != KeyCode.Q) {
            return;
        }

        PlacedItem target = draggingPlacedItem != null
                ? draggingPlacedItem
                : heldPlacedItem != null ? heldPlacedItem : selectedPlacedItem;

        boolean clockwise = event.getCode() == KeyCode.R;

        if (target == null) {
            Item draggedStorageItem = draggedStorageItemSupplier == null ? null : draggedStorageItemSupplier.get();
            if (draggedStorageItem != null && onDraggedStorageItemRotationRequested != null) {
                onDraggedStorageItemRotationRequested.accept(clockwise);
                event.consume();
            }
            return;
        }

        if (onPlacedItemRotationRequested == null) {
            return;
        }

        boolean success = onPlacedItemRotationRequested.rotate(target, clockwise);
        if (success) {
            selectedPlacedItem = target;
            redraw();
        }
        event.consume();
    }

    private String cellText(PlacedItem placedItem) {
        if (placedItem == null) {
            return "?";
        }
        ItemShape shape = placedItem.getCurrentShape();
        return shortName(placedItem.getItem().getName()) + "\n" + shape.width() + "x" + shape.height();
    }

    private String shortName(String name) {
        if (name == null) return "?";
        if (name.length() <= 4) return name;
        return name.substring(0, 4);
    }
}
