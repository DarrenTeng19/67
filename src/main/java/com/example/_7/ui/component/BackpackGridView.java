package com.example._7.ui.component;

import com.example._7.inventory.Backpack;
import com.example._7.inventory.GridPosition;
import com.example._7.inventory.PlacedItem;
import com.example._7.inventory.Rotation;
import com.example._7.item.Item;
import com.example._7.item.shape.ItemShape;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 背包格子視圖（支援 item 以圖片跨格顯示）
 * - 圖片檔案放在 resources/com/example/_7/images/items/<itemId>.png
 * - 每個 item 的圖片會自動依 shape 長寬伸縮 (CELL_SIZE * width, CELL_SIZE * height)
 * - 顯示行為為 cover（填滿格子），旋轉時允許圖片超出邊界（不用 clip）。
 */
public class BackpackGridView extends VBox {
    private static final int CELL_SIZE = 48;

    @FunctionalInterface
    public interface StorageItemDropHandler {
        boolean drop(Item item, GridPosition position, Rotation rotation);
    }

    @FunctionalInterface
    public interface PlacedItemRotationHandler {
        boolean rotate(PlacedItem placedItem, boolean clockwise);
    }

    private final GridPane grid = new GridPane();
    private final Label helpLabel = new Label("Q / R  旋轉");
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
        setPadding(new Insets(4, 0, 0, 0));
        setFocusTraversable(true);
        getStyleClass().add("backpack-panel");
        helpLabel.getStyleClass().add("component-hint");
        grid.setFocusTraversable(true);

        grid.setHgap(4);
        grid.setVgap(4);
        getChildren().add(grid);
        getChildren().add(helpLabel);

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

    /**
     * 由外部(通常是 controller) 指定目前被選取的 PlacedItem。
     * 會更新內部狀態並重新繪製，使 redraw 後仍能看到選取樣式。
     */
    public void setSelectedPlacedItem(PlacedItem placedItem) {
        this.selectedPlacedItem = placedItem;
        redraw();
    }

    public void redraw() {
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        if (backpack == null) {
            return;
        }

        int rows = backpack.getRows();
        int cols = backpack.getCols();

        // 1) 背景格子（可接收拖放）
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                GridPosition pos = new GridPosition(r, c);
                StackPane cell = createCell(pos);
                grid.add(cell, c, r);
            }
        }

        // 2) 加上 placed items（以圖片跨格顯示）
        for (PlacedItem placed : backpack.getPlacedItems()) {
            if (placed == null) continue;
            GridPosition base = placed.getPosition();
            if (base == null) continue;
            ItemShape shape = placed.getCurrentShape();
            if (shape == null) continue;

            int width = Math.max(1, shape.width());
            int height = Math.max(1, shape.height());

            StackPane itemNode = createItemNode(placed, width, height);
            try {
                grid.add(itemNode, base.col(), base.row(), width, height);
            } catch (Exception ex) {
                System.err.println("Failed to add item node to grid: " + ex.getMessage());
                grid.add(itemNode, 0, 0);
            }
        }
    }

    private StackPane createCell(GridPosition position) {
        StackPane cell = new StackPane();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setMinSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);
        cell.setFocusTraversable(true);
        cell.getStyleClass().add("backpack-cell");
        cell.getStyleClass().add((position.row() + position.col()) % 2 == 0
                ? "backpack-cell-light"
                : "backpack-cell-dark");

        cell.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString() && (db.getString().startsWith("STORAGE_ITEM:") || db.getString().startsWith("BACKPACK_ITEM:"))) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        cell.setOnDragDropped(event -> {
            boolean success = false;
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                String payload = db.getString();
                if (payload.startsWith("STORAGE_ITEM:")) {
                    Item draggedStorageItem = draggedStorageItemSupplier == null ? null : draggedStorageItemSupplier.get();
                    Rotation draggedRotation = draggedStorageRotationSupplier == null ? Rotation.DEGREE_0 : draggedStorageRotationSupplier.get();
                    if (draggedRotation == null) draggedRotation = Rotation.DEGREE_0;
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

        return cell;
    }

    private StackPane createItemNode(PlacedItem placedItem, int colspan, int rowspan) {
        if (colspan <= 0) colspan = 1;
        if (rowspan <= 0) rowspan = 1;

        StackPane node = new StackPane();
        node.setAlignment(Pos.CENTER);
        node.setFocusTraversable(true);
        node.getStyleClass().add("backpack-item-node");
        double nodeWidth = CELL_SIZE * colspan + (colspan - 1) * grid.getHgap();
        double nodeHeight = CELL_SIZE * rowspan + (rowspan - 1) * grid.getVgap();
        node.setPrefWidth(nodeWidth);
        node.setPrefHeight(nodeHeight);
        node.setMinWidth(nodeWidth);
        node.setMinHeight(nodeHeight);
        node.setMaxWidth(nodeWidth);
        node.setMaxHeight(nodeHeight);

        try {
            Image img = findItemImage(placedItem.getItem());

            if (img != null) {
                double angle = 0.0;
                try {
                    Rotation rot = placedItem.getRotation();
                    if (rot != null) angle = rotationToAngle(rot);
                } catch (Exception ignored) {}

                ImageView iv = createCoveringImageView(img, (int) nodeWidth, (int) nodeHeight, angle);
                if (iv != null) {
                    StackPane.setAlignment(iv, Pos.CENTER);
                    iv.setMouseTransparent(true);
                    node.getChildren().add(iv);
                } else {
                    node.getChildren().add(new Label(placedItem.getItem().getName()));
                }
            } else {
                node.getChildren().add(new Label(placedItem.getItem().getName()));
            }
        } catch (Exception ex) {
            System.err.println("Error creating item node image: " + ex.getMessage());
            node.getChildren().add(new Label(placedItem.getItem().getName()));
        }

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(nodeWidth, nodeHeight);
        clip.setArcWidth(0);
        clip.setArcHeight(0);
        node.setClip(clip);

        // 根據選取狀態應用樣式與邊框
        updateNodeSelectionStyle(node, placedItem);

        node.setOnMousePressed(evt -> {
            node.requestFocus();
            selectedPlacedItem = placedItem;
            heldPlacedItem = placedItem;
            if (onPlacedItemSelected != null) onPlacedItemSelected.accept(placedItem);
            evt.consume();
        });

        node.setOnMouseClicked(evt -> {
            node.requestFocus();
            selectedPlacedItem = placedItem;
            if (onPlacedItemSelected != null) onPlacedItemSelected.accept(placedItem);
            evt.consume();
        });

        node.setOnDragDetected(evt -> {
            node.requestFocus();
            selectedPlacedItem = placedItem;
            heldPlacedItem = placedItem;
            draggingPlacedItem = placedItem;
            if (onPlacedItemSelected != null) onPlacedItemSelected.accept(placedItem);

            Dragboard dragboard = node.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("BACKPACK_ITEM:" + placedItem.getItem().getId());
            dragboard.setContent(content);
            evt.consume();
        });

        node.setOnDragDone(evt -> {
            draggingPlacedItem = null;
            heldPlacedItem = null;
            evt.consume();
            redraw();
        });

        node.setOnMouseReleased(evt -> heldPlacedItem = null);

        return node;
    }

    private void updateNodeSelectionStyle(StackPane node, PlacedItem placedItem) {
        if (selectedPlacedItem != null &&
                placedItem.getItem().getId().equals(selectedPlacedItem.getItem().getId())) {
            node.setStyle(
                    "-fx-border-color: #d4af37; " +
                            "-fx-border-width: 2; " +
                            "-fx-padding: -2;"
            );
        } else {
            node.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
        }
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
                Platform.runLater(this::requestFocus);
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
        Platform.runLater(this::requestFocus);
        event.consume();
    }

    private Image findItemImage(Item item) {
        if (item == null || item.getId() == null) return null;
        String base = "/com/example/_7/images/items/" + item.getId();
        String[] exts = {".png", ".jpg", ".jpeg"};
        for (String ext : exts) {
            URL res = getClass().getResource(base + ext);
            if (res != null) {
                try {
                    return new Image(res.toExternalForm(), false);
                } catch (Exception ignored) {}
            }
        }
        return null;
    }

    private ImageView createCoveringImageView(Image img, int targetWidth, int targetHeight, double rotateAngle) {
        if (img == null) return null;
        double imgW = img.getWidth();
        double imgH = img.getHeight();
        if (imgW <= 0 || imgH <= 0) {
            ImageView fallback = new ImageView(img);
            fallback.setPreserveRatio(true);
            fallback.setFitWidth(targetWidth);
            fallback.setFitHeight(targetHeight);
            fallback.setRotate(rotateAngle);
            return fallback;
        }

        // 旋轉 90/270° 時互換寬高以計算正確的 scale
        double displayWidth = targetWidth;
        double displayHeight = targetHeight;
        if (rotateAngle == 90.0 || rotateAngle == 270.0) {
            displayWidth = targetHeight;  // ← 互換
            displayHeight = targetWidth;  // ← 互換
        }

        // 以旋轉後的顯示區域計算 contain scale
        double scale = Math.min((double) displayWidth / imgW, (double) displayHeight / imgH);
        double finalW = imgW * scale;
        double finalH = imgH * scale;

        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setFitWidth(finalW);
        iv.setFitHeight(finalH);
        iv.setRotate(rotateAngle);

        return iv;
    }

    private double rotationToAngle(Rotation rotation) {
        if (rotation == null) return 0.0;
        return switch (rotation) {
            case DEGREE_0 -> 0.0;
            case DEGREE_90 -> 90.0;
            case DEGREE_180 -> 180.0;
            case DEGREE_270 -> 270.0;
        };
    }

    private String shortName(String name) {
        if (name == null) return "?";
        if (name.length() <= 4) return name;
        return name.substring(0, 4);
    }
}
