package com.example._7.ui.component;

import com.example._7.item.Item;
import com.example._7.shop.Shop;
import com.example._7.shop.ShopOffer;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * 商店面板：顯示每個 ShopOffer 的縮圖、名稱、價格與尺寸資訊。
 * 圖片檔案請放在 resources/com/example/_7/images/items/<itemId>.png (或 .jpg)
 */
public class ShopPanel extends VBox {
    private final ListView<ShopOffer> list = new ListView<>(FXCollections.observableArrayList());
    private final Button btnBuy = new Button("購買選取商品");

    private Consumer<ShopOffer> onBuyOffer;
    private Consumer<Item> onItemSelected;

    public ShopPanel() {
        setSpacing(6);
        setPadding(new Insets(4, 0, 0, 0));
        getStyleClass().add("shop-panel");
        list.setMinHeight(150);
        list.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(list, Priority.ALWAYS);
        list.setCellFactory(view -> new ShopOfferCell());

        list.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (onItemSelected != null && newValue != null) {
                onItemSelected.accept(newValue.getItem());
            }
        });

        list.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
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
        private final HBox root = new HBox(8);
        private final ImageView imageView = new ImageView();
        private final VBox textBox = new VBox(2);
        private final Label nameLabel = new Label();
        private final Label metaLabel = new Label();

        ShopOfferCell() {
            root.setAlignment(Pos.CENTER_LEFT);
            imageView.setFitWidth(48);
            imageView.setFitHeight(48);
            imageView.setPreserveRatio(true);
            imageView.getStyleClass().add("shop-item-image");
            textBox.getChildren().addAll(nameLabel, metaLabel);
            root.getChildren().addAll(imageView, textBox);
            setPadding(new Insets(6));
        }

        @Override
        protected void updateItem(ShopOffer offer, boolean empty) {
            super.updateItem(offer, empty);
            if (empty || offer == null || offer.getItem() == null) {
                setText(null);
                setGraphic(null);
                setStyle("");
                return;
            }

            Item item = offer.getItem();
            String prefix = offer.isSold() ? "[已售出] " : "";
            nameLabel.setText(prefix + item.getName());
            metaLabel.setText("$" + item.getPrice() + "    " + item.getShape().width() + "x" + item.getShape().height());

            Image img = loadItemImage(item);
            if (img != null) {
                imageView.setImage(img);
            } else {
                imageView.setImage(null);
            }

            setGraphic(root);
            setStyle(offer.isSold()
                    ? "-fx-opacity: 0.38; -fx-text-fill: #785f46; -fx-font-weight: 700;"
                    : "");
        }

        private Image loadItemImage(Item item) {
            if (item == null || item.getId() == null) return null;
            String path = "/com/example/_7/images/items/" + item.getId() + ".png";
            var res = getClass().getResource(path);
            if (res == null) {
                // try jpg
                res = getClass().getResource("/com/example/_7/images/items/" + item.getId() + ".jpg");
                if (res == null) return null;
            }
            try {
                return new Image(res.toExternalForm(), 48, 48, true, true);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
