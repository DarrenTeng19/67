package com.example._7.ui.screen;

import com.example._7.app.GameApp;
import com.example._7.battle.BattleEngine;
import com.example._7.character.Player;
import com.example._7.game.GameSession;
import com.example._7.game.RoundManager;
import com.example._7.inventory.Backpack;
import com.example._7.inventory.GridPosition;
import com.example._7.inventory.PlacedItem;
import com.example._7.inventory.Storage;
import com.example._7.item.Item;
import com.example._7.shop.Shop;
import com.example._7.shop.ShopOffer;
import com.example._7.ui.component.BackpackGridView;
import com.example._7.ui.component.CharacterInfoPanel;
import com.example._7.ui.component.HealthBarView;
import com.example._7.ui.component.ItemCardView;
import com.example._7.ui.component.RecycleBinView;
import com.example._7.ui.component.ShopPanel;
import com.example._7.ui.component.StorageView;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class PreparationScreenController {

    private static final int SHOP_REFRESH_COST = 1;

    private GameSession session;
    private RoundManager roundManager;
    private GameApp app;

    @FXML private AnchorPane root;

    @FXML private AnchorPane charInfoContainer;
    @FXML private AnchorPane healthBarContainer;
    @FXML private AnchorPane shopPanelContainer;
    @FXML private AnchorPane storageContainer;
    @FXML private AnchorPane backpackContainer;
    @FXML private AnchorPane itemCardContainer;

    @FXML private Button btnStartBattle;
    @FXML private Button btnRefreshShop;
    @FXML private Button btnOpenStorage;

    private CharacterInfoPanel charInfo;
    private HealthBarView healthBar;
    private ShopPanel shopPanel;
    private StorageView storageView;
    private BackpackGridView backpackView;
    private ItemCardView itemCard;
    private RecycleBinView recycleBinView;

    private Item draggedStorageItem;
    private PlacedItem selectedPlacedItem;
    private Item selectedStorageItem;

    public void init(GameSession session, RoundManager roundManager, GameApp app) {
        this.session = session;
        this.roundManager = roundManager;
        this.app = app;

        roundManager.initializeCurrentRound(session);
        refreshUI();
    }

    @FXML
    private void initialize() {
        try {
            charInfo = new CharacterInfoPanel();
            healthBar = new HealthBarView();
            shopPanel = new ShopPanel();
            storageView = new StorageView();
            backpackView = new BackpackGridView();
            itemCard = new ItemCardView();
            recycleBinView = new RecycleBinView();

            attach(charInfoContainer, charInfo);
            attach(healthBarContainer, healthBar);
            attach(shopPanelContainer, shopPanel);
            attach(storageContainer, storageView);
            attach(backpackContainer, backpackView);

            VBox detailBox = new VBox(8, itemCard, recycleBinView);
            attach(itemCardContainer, detailBox);

            setupCallbacks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void attach(AnchorPane container, javafx.scene.Node node) {
        if (container == null || node == null) return;
        container.getChildren().clear();
        container.getChildren().add(node);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
    }

    private void setupCallbacks() {
        shopPanel.setOnItemSelected(item -> {
            itemCard.setItem(item);
            itemCard.setMessage("點擊購買，商品會進入儲物箱。");
        });

        shopPanel.setOnBuyOffer(this::buyOffer);

        storageView.setOnItemSelected(item -> {
            selectedStorageItem = item;
            selectedPlacedItem = null;
            itemCard.setItem(item);
            itemCard.setMessage("可按「自動放入背包」，或拖曳到背包格子。");
            if (backpackView != null) backpackView.clearSelection();
        });

        storageView.setOnAutoPlaceRequested(this::autoPlaceStorageItem);
        storageView.setOnSellRequested(this::sellStorageItem);
        storageView.setOnDragStarted(item -> draggedStorageItem = item);
        storageView.setOnDragDone(() -> draggedStorageItem = null);

        backpackView.setDraggedStorageItemSupplier(() -> draggedStorageItem);
        backpackView.setOnStorageItemDropped(this::placeStorageItemAt);
        backpackView.setOnPlacedItemMoved(this::movePlacedItemTo);
        backpackView.setOnPlacedItemSelected(placedItem -> {
            selectedPlacedItem = placedItem;
            selectedStorageItem = null;
            if (placedItem != null) {
                itemCard.setItem(placedItem.getItem());
                itemCard.setMessage("背包物品可拖曳移動，也可用回收箱賣出。");
            } else {
                itemCard.clear();
                itemCard.setMessage("");
            }
        });

        recycleBinView.getSellButton().setOnAction(event -> sellSelectedItem());
    }

    private void refreshUI() {
        if (session == null || session.getPlayer() == null) {
            return;
        }

        Player player = session.getPlayer();
        if (charInfo != null) {
            charInfo.setName(player.getName());
            charInfo.setCharacterClass(player.getCharacterClass().name());
            charInfo.setGold(player.getGold());
            charInfo.setRound(session.getCurrentRound());
        }

        if (healthBar != null) {
            int maxHp = player.getCharacterStats().getMaxHp();
            healthBar.update(maxHp, maxHp);
        }

        if (shopPanel != null) {
            Shop shop = session.getCurrentShop();
            shopPanel.setShop(shop);
        }

        if (storageView != null) {
            storageView.setStorage(player.getStorage());
        }

        if (backpackView != null) {
            backpackView.setBackpack(player.getBackpack());
        }
    }

    private void buyOffer(ShopOffer offer) {
        if (offer == null || offer.getItem() == null) return;

        if (offer.isSold()) {
            showMessage("這個商品已經賣出了。");
            return;
        }

        Player player = session.getPlayer();
        Item item = offer.getItem();

        if (!player.spendGold(item.getPrice())) {
            showMessage("金幣不足，無法購買「" + item.getName() + "」。");
            return;
        }

        player.getStorage().addItem(item);
        offer.markAsSold();
        selectedStorageItem = item;
        selectedPlacedItem = null;
        itemCard.setItem(item);
        showMessage("已購買「" + item.getName() + "」，放入儲物箱。");
        refreshUI();
        shopPanel.refresh();
        storageView.selectItem(item);
    }

    private void autoPlaceStorageItem(Item item) {
        if (item == null) return;
        Backpack backpack = session.getPlayer().getBackpack();
        backpack.findFirstAvailablePosition(item).ifPresentOrElse(
                position -> placeStorageItemAt(item, position),
                () -> showMessage("背包沒有足夠空間放入「" + item.getName() + "」。")
        );
    }

    private boolean placeStorageItemAt(Item item, GridPosition position) {
        if (item == null || position == null) return false;

        Player player = session.getPlayer();
        Storage storage = player.getStorage();
        Backpack backpack = player.getBackpack();

        if (!storage.contains(item)) {
            showMessage("儲物箱中找不到「" + item.getName() + "」。");
            return false;
        }

        PlacedItem placedItem = new PlacedItem(item, position);
        if (!backpack.tryPlaceItem(placedItem)) {
            showMessage("這個位置放不下「" + item.getName() + "」。");
            return false;
        }

        storage.removeItem(item);
        selectedPlacedItem = placedItem;
        selectedStorageItem = null;
        itemCard.setItem(item);
        showMessage("已將「" + item.getName() + "」放入背包。");
        refreshUI();
        return true;
    }

    private boolean movePlacedItemTo(PlacedItem placedItem, GridPosition position) {
        if (placedItem == null || position == null) return false;
        boolean success = session.getPlayer().getBackpack().tryMoveItem(placedItem, position);
        if (success) {
            selectedPlacedItem = placedItem;
            itemCard.setItem(placedItem.getItem());
            showMessage("已移動「" + placedItem.getItem().getName() + "」。");
        } else {
            showMessage("這個位置放不下「" + placedItem.getItem().getName() + "」。");
        }
        refreshUI();
        return success;
    }

    private void sellStorageItem(Item item) {
        if (item == null) return;
        Player player = session.getPlayer();
        if (!player.getStorage().removeItem(item)) {
            showMessage("儲物箱中找不到這個物品。");
            return;
        }
        int gold = Math.max(1, item.getPrice() / 2);
        player.addGold(gold);
        selectedStorageItem = null;
        itemCard.clear();
        showMessage("已賣出「" + item.getName() + "」，獲得 " + gold + " 金幣。");
        refreshUI();
    }

    private void sellSelectedItem() {
        if (selectedPlacedItem != null) {
            sellBackpackItem(selectedPlacedItem);
        } else if (selectedStorageItem != null) {
            sellStorageItem(selectedStorageItem);
        } else {
            showMessage("請先選取儲物箱或背包中的物品。");
        }
    }

    private void sellBackpackItem(PlacedItem placedItem) {
        if (placedItem == null) return;
        Player player = session.getPlayer();
        if (!player.getBackpack().removeItem(placedItem)) {
            showMessage("背包中找不到這個物品。");
            return;
        }
        Item item = placedItem.getItem();
        int gold = Math.max(1, item.getPrice() / 2);
        player.addGold(gold);
        selectedPlacedItem = null;
        itemCard.clear();
        showMessage("已賣出「" + item.getName() + "」，獲得 " + gold + " 金幣。");
        refreshUI();
    }

    @FXML
    private void onStartBattle() {
        try {
            BattleEngine engine = new BattleEngine(session.getPlayer(), session.getCurrentEnemy());
            engine.setItemsFor(session.getPlayer(), session.getPlayer().getBackpack().getBattleItems());
            engine.setItemsFor(session.getCurrentEnemy(), session.getCurrentEnemy().getBackpack().getBattleItems());
            engine.startBattle();

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/_7/battle.fxml"));
            Parent root = loader.load();
            BattleScreenController battleCtrl = loader.getController();
            battleCtrl.init(session, engine, roundManager, app);

            javafx.stage.Stage stage = (javafx.stage.Stage) btnStartBattle.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("開始戰鬥失敗：" + e.getMessage());
        }
    }

    @FXML
    private void onRefreshShop() {
        if (roundManager == null || session == null) return;

        Player player = session.getPlayer();
        if (!player.spendGold(SHOP_REFRESH_COST)) {
            showMessage("金幣不足，刷新商店需要 " + SHOP_REFRESH_COST + " 金幣。");
            refreshUI();
            return;
        }

        roundManager.refreshShop(session);
        showMessage("已花費 " + SHOP_REFRESH_COST + " 金幣刷新商店。");
        refreshUI();
    }

    @FXML
    private void onOpenStorage() {
        if (storageView != null) {
            storageView.requestFocus();
        }
        showMessage("儲物箱已顯示在畫面中央，可拖曳物品到背包。");
    }

    private void showMessage(String message) {
        if (itemCard != null) {
            itemCard.setMessage(message);
        }
    }
}
