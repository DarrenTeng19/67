package com.example._7.ui.component;

import com.example._7.inventory.Backpack;
import com.example._7.inventory.GridPosition;
import com.example._7.inventory.PlacedItem;
import com.example._7.item.Item;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
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

    private final GridPane grid = new GridPane();
    private Backpack backpack;
    private PlacedItem selectedPlacedItem;
    private PlacedItem draggingPlacedItem;

    private Supplier<Item> draggedStorageItemSupplier;
    private BiFunction<Item, GridPosition, Boolean> onStorageItemDropped;
    private BiFunction<PlacedItem, GridPosition, Boolean> onPlacedItemMoved;
    private Consumer<PlacedItem> onPlacedItemSelected;

    public BackpackGridView() {
        setSpacing(6);
        setPadding(new Insets(6));
        getChildren().add(new Label("Backpack 背包"));
        grid.setHgap(4);
        grid.setVgap(4);
        getChildren().add(grid);
        getChildren().add(new Label("提示：從儲物箱拖曳到格子；背包內物品也可拖曳移動。"));
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
        redraw();
    }

    public void setDraggedStorageItemSupplier(Supplier<Item> draggedStorageItemSupplier) {
        this.draggedStorageItemSupplier = draggedStorageItemSupplier;
    }

    public void setOnStorageItemDropped(BiFunction<Item, GridPosition, Boolean> onStorageItemDropped) {
        this.onStorageItemDropped = onStorageItemDropped;
    }

    public void setOnPlacedItemMoved(BiFunction<PlacedItem, GridPosition, Boolean> onPlacedItemMoved) {
        this.onPlacedItemMoved = onPlacedItemMoved;
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

        Optional<PlacedItem> placedOptional = backpack.getPlacedItemAt(position);
        PlacedItem placedItem = placedOptional.orElse(null);

        String baseStyle = "-fx-border-color: #999; -fx-background-color: #eeeeee;";
        if (placedItem != null) {
            baseStyle = "-fx-border-color: #555; -fx-background-color: #cfe8ff;";
            Label label = new Label(shortName(placedItem.getItem().getName()));
            label.setWrapText(true);
            cell.getChildren().add(label);
        }
        if (placedItem != null && placedItem == selectedPlacedItem) {
            baseStyle = "-fx-border-color: #ff9900; -fx-border-width: 3; -fx-background-color: #ffe2b6;";
        }
        cell.setStyle(baseStyle);

        cell.setOnMouseClicked(event -> {
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
        });

        cell.setOnDragDetected(event -> {
            if (placedItem == null) {
                return;
            }
            selectedPlacedItem = placedItem;
            draggingPlacedItem = placedItem;
            Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("BACKPACK_ITEM:" + placedItem.getItem().getId());
            dragboard.setContent(content);
            event.consume();
        });

        cell.setOnDragOver(event -> {
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
                    if (draggedStorageItem != null && onStorageItemDropped != null) {
                        success = onStorageItemDropped.apply(draggedStorageItem, position);
                    }
                } else if (payload.startsWith("BACKPACK_ITEM:")) {
                    if (draggingPlacedItem != null && onPlacedItemMoved != null) {
                        success = onPlacedItemMoved.apply(draggingPlacedItem, position);
                    }
                }
            }

            draggingPlacedItem = null;
            event.setDropCompleted(success);
            event.consume();
            redraw();
        });

        cell.setOnDragDone(event -> {
            draggingPlacedItem = null;
            event.consume();
        });

        return cell;
    }

    private String shortName(String name) {
        if (name == null) return "?";
        if (name.length() <= 4) return name;
        return name.substring(0, 4);
    }
}
