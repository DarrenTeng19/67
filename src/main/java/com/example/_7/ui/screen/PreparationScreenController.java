package com.example._7.ui.screen;

import com.example._7.app.GameApp;
import com.example._7.battle.BattleEngine;
import com.example._7.character.EffectiveStatsCalculator;
import com.example._7.character.Player;
import com.example._7.game.GameSession;
import com.example._7.game.RoundManager;
import com.example._7.inventory.Backpack;
import com.example._7.inventory.GridPosition;
import com.example._7.inventory.PlacedItem;
import com.example._7.inventory.Rotation;
import com.example._7.inventory.Storage;
import com.example._7.item.Item;
import com.example._7.item.ItemCatalog;
import com.example._7.recipe.RecipeCatalog;
import com.example._7.recipe.RecipeResult;
import com.example._7.recipe.RecipeService;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class PreparationScreenController {

    private static final int SHOP_REFRESH_COST = 1;

    private GameSession session;
    private RoundManager roundManager;
    private GameApp app;

    @FXML
    private BorderPane root;

    @FXML
    private AnchorPane charInfoContainer;
    @FXML
    private AnchorPane healthBarContainer;
    @FXML
    private AnchorPane shopPanelContainer;
    @FXML
    private AnchorPane storageContainer;
    @FXML
    private AnchorPane backpackContainer;
    @FXML
    private AnchorPane itemCardContainer;
    @FXML
    private ImageView characterPortrait;
    @FXML
    private Label lblRoundBadge;
    @FXML
    private Label lblShopCount;
    @FXML
    private Label lblStorageCount;
    @FXML
    private Label lblBackpackCount;

    @FXML
    private Button btnStartBattle;
    @FXML
    private Button btnRefreshShop;
    @FXML
    private Button btnAutoCraft;
    @FXML
    private Button btnOpenStorage;

    private CharacterInfoPanel charInfo;
    private HealthBarView healthBar;
    private ShopPanel shopPanel;
    private StorageView storageView;
    private BackpackGridView backpackView;
    private ItemCardView itemCard;
    private RecycleBinView recycleBinView;
    private final RecipeService recipeService = new RecipeService(new RecipeCatalog(), new ItemCatalog());

    private Item draggedStorageItem;
    private Rotation draggedStorageRotation = Rotation.DEGREE_0;
    private PlacedItem selectedPlacedItem;
    private Item selectedStorageItem;

    public void init(GameSession session, RoundManager roundManager, GameApp app) {
        this.session = session;
        this.roundManager = roundManager;
        this.app = app;

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
            itemCard.setMessage("可按「自動放入背包」，或拖曳到背包格子。按住/選取時可用 Q / R 旋轉。");
            if (backpackView != null) backpackView.clearSelection();
        });

        storageView.setOnAutoPlaceRequested(this::autoPlaceStorageItem);
        storageView.setOnRotationChanged(rotation -> {
            draggedStorageRotation = rotation == null ? Rotation.DEGREE_0 : rotation;
            if (selectedStorageItem != null) {
                showMessage("「" + selectedStorageItem.getName() + "」目前旋轉角度：" + draggedStorageRotation.getDisplayName());
            }
        });
        storageView.setOnDragStarted(item -> {
            draggedStorageItem = item;
            draggedStorageRotation = storageView.getCurrentDragRotation();
        });
        storageView.setOnDragDone(() -> {
            draggedStorageItem = null;
            draggedStorageRotation = Rotation.DEGREE_0;
        });
        storageView.setDraggedBackpackItemSupplier(() -> selectedPlacedItem);
        storageView.setOnBackpackItemDropped(this::moveBackpackItemToStorage);

        backpackView.setDraggedStorageItemSupplier(() -> draggedStorageItem);
        backpackView.setDraggedStorageRotationSupplier(() -> draggedStorageRotation);
        backpackView.setOnStorageItemDropped(this::placeStorageItemAt);
        backpackView.setOnPlacedItemMoved(this::movePlacedItemTo);
        backpackView.setOnPlacedItemRotationRequested(this::rotatePlacedItem);
        backpackView.setOnDraggedStorageItemRotationRequested(this::rotateDraggedStorageItem);
        backpackView.setOnPlacedItemSelected(placedItem -> {
            selectedPlacedItem = placedItem;
            selectedStorageItem = null;
            if (placedItem != null) {
                itemCard.setItem(placedItem.getItem());
                itemCard.setMessage("背包物品可拖曳移動；按住/選取時按 R 順時針旋轉、Q 逆時針旋轉，也可用回收箱賣出。");
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
        if (lblRoundBadge != null) {
            lblRoundBadge.setText("ROUND " + session.getCurrentRound());
        }
        if (characterPortrait != null) {
            characterPortrait.setImage(loadCharacterImage(player));
        }
        if (charInfo != null) {
            charInfo.setName(player.getName());
            charInfo.setCharacterClass(player.getCharacterClass().getDisplayName());
            charInfo.setGold(player.getGold());
            charInfo.setRound(session.getCurrentRound());
        }

        if (healthBar != null) {
            var effectiveStats = EffectiveStatsCalculator.calculate(
                    player.getCharacterStats(),
                    player.getBackpack().getBattleItems()
            );
            healthBar.update(
                    effectiveStats.maxHp(),
                    effectiveStats.maxHp(),
                    effectiveStats.maxStamina(),
                    effectiveStats.staminaRecoveryRate(),
                    effectiveStats.maxMana(),
                    effectiveStats.manaRecoveryRate()
            );
        }

        if (shopPanel != null) {
            Shop shop = session.getCurrentShop();
            shopPanel.setShop(shop);
            if (lblShopCount != null) {
                long available = shop == null
                        ? 0
                        : shop.getOffers().stream().filter(offer -> !offer.isSold()).count();
                lblShopCount.setText(String.valueOf(available));
            }
        }

        if (storageView != null) {
            storageView.setStorage(player.getStorage());
            if (lblStorageCount != null) {
                lblStorageCount.setText(String.valueOf(player.getStorage().getItems().size()));
            }
        }

        if (backpackView != null) {
            backpackView.setBackpack(player.getBackpack());
            backpackView.setSelectedPlacedItem(selectedPlacedItem);  // ← 新增這行
            if (lblBackpackCount != null) {
                int occupiedCellCount = player.getBackpack().getOccupiedCellCount();
                int cellCount = player.getBackpack().getRows() * player.getBackpack().getCols();
                lblBackpackCount.setText(occupiedCellCount + " / " + cellCount);
            }
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

        Player player = session.getPlayer();
        Storage storage = player.getStorage();
        Backpack backpack = player.getBackpack();

        if (!storage.contains(item)) {
            showMessage("儲物箱中找不到「" + item.getName() + "」。");
            return;
        }

        // 自動放入時不要求玩家先轉到正確角度。
        // 會依序嘗試目前角度、0°、90°、180°、270°，找到第一個可放位置就放入。
        for (Rotation rotation : getAutoPlaceRotationOrder(draggedStorageRotation)) {
            for (int row = 0; row < backpack.getRows(); row++) {
                for (int col = 0; col < backpack.getCols(); col++) {
                    GridPosition position = new GridPosition(row, col);
                    PlacedItem candidate = new PlacedItem(item, position);
                    candidate.setRotation(rotation);

                    if (backpack.tryPlaceItem(candidate)) {
                        storage.removeItem(item);
                        selectedPlacedItem = candidate;
                        selectedStorageItem = null;
                        draggedStorageRotation = Rotation.DEGREE_0;
                        if (storageView != null) storageView.resetDragRotation();
                        itemCard.setItem(item);
                        showMessage("已自動將「" + item.getName() + "」以 "
                                + rotation.getDisplayName()
                                + " 放入背包。位置：第 " + (row + 1) + " 列，第 " + (col + 1) + " 欄。");
                        refreshUI();
                        return;
                    }
                }
            }
        }

        showMessage("背包沒有足夠空間放入「" + item.getName() + "」，即使旋轉後也放不下。");
    }

    private Rotation[] getAutoPlaceRotationOrder(Rotation preferredRotation) {
        Rotation preferred = preferredRotation == null ? Rotation.DEGREE_0 : preferredRotation;
        java.util.List<Rotation> ordered = new java.util.ArrayList<>();
        ordered.add(preferred);
        for (Rotation rotation : Rotation.values()) {
            if (rotation != preferred) {
                ordered.add(rotation);
            }
        }
        return ordered.toArray(new Rotation[0]);
    }

    private boolean placeStorageItemAt(Item item, GridPosition position, Rotation rotation) {
        if (item == null || position == null) return false;
        if (rotation == null) rotation = Rotation.DEGREE_0;

        Player player = session.getPlayer();
        Storage storage = player.getStorage();
        Backpack backpack = player.getBackpack();

        if (!storage.contains(item)) {
            showMessage("儲物箱中找不到「" + item.getName() + "」。");
            return false;
        }

        PlacedItem placedItem = new PlacedItem(item, position);
        placedItem.setRotation(rotation);
        if (!backpack.tryPlaceItem(placedItem)) {
            showMessage("這個位置放不下「" + item.getName() + "」；目前旋轉角度：" + rotation.getDisplayName());
            return false;
        }

        storage.removeItem(item);
        selectedPlacedItem = placedItem;
        selectedStorageItem = null;
        draggedStorageRotation = Rotation.DEGREE_0;
        if (storageView != null) storageView.resetDragRotation();
        itemCard.setItem(item);
        showMessage("已將「" + item.getName() + "」以 " + rotation.getDisplayName() + " 放入背包。");
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

    private boolean moveBackpackItemToStorage(PlacedItem placedItem) {
        if (placedItem == null) return false;

        Player player = session.getPlayer();
        Backpack backpack = player.getBackpack();
        Storage storage = player.getStorage();
        Item item = placedItem.getItem();
        Rotation previousRotation = placedItem.getRotation();

        if (!backpack.removeItem(placedItem)) {
            showMessage("背包中找不到「" + item.getName() + "」。");
            return false;
        }

        storage.addItem(item);
        selectedPlacedItem = null;
        selectedStorageItem = item;
        draggedStorageItem = null;
        draggedStorageRotation = previousRotation == null ? Rotation.DEGREE_0 : previousRotation;

        if (storageView != null) {
            storageView.setCurrentDragRotation(draggedStorageRotation);
        }
        if (backpackView != null) {
            backpackView.clearSelection();
        }
        itemCard.setItem(item);
        showMessage("已將「" + item.getName() + "」從背包放回儲物箱。旋轉角度保留為 "
                + draggedStorageRotation.getDisplayName() + "。");
        refreshUI();
        if (storageView != null) {
            storageView.selectItem(item);
        }
        return true;
    }

    private void rotateDraggedStorageItem(boolean clockwise) {
        draggedStorageRotation = clockwise
                ? draggedStorageRotation.nextClockwise()
                : draggedStorageRotation.nextCounterClockwise();

        if (draggedStorageItem != null) {
            showMessage("「" + draggedStorageItem.getName() + "」目前旋轉角度：" + draggedStorageRotation.getDisplayName());
        } else {
            showMessage("目前旋轉角度：" + draggedStorageRotation.getDisplayName());
        }
    }

    private boolean rotatePlacedItem(PlacedItem placedItem, boolean clockwise) {
        if (placedItem == null) return false;

        Backpack backpack = session.getPlayer().getBackpack();
        boolean success = clockwise
                ? backpack.tryRotateItemClockwise(placedItem)
                : backpack.tryRotateItemCounterClockwise(placedItem);

        selectedPlacedItem = placedItem;
        itemCard.setItem(placedItem.getItem());

        if (success) {
            showMessage("已旋轉「" + placedItem.getItem().getName() + "」到 " + placedItem.getRotation().getDisplayName() + "。");
        } else {
            showMessage("「" + placedItem.getItem().getName() + "」旋轉後會超出背包或重疊，因此無法旋轉。");
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

            app.getSceneManager().showBattle(session, engine, roundManager);
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
    private void onAutoCraft() {
        if (session == null || session.getPlayer() == null) {
            showMessage("找不到玩家資料，無法合成。");
            return;
        }

        RecipeResult result = recipeService.autoCraftFirst(session.getPlayer());
        showMessage(result.getMessage());

        if (result.isSuccess()) {
            selectedPlacedItem = null;
            selectedStorageItem = result.getResultItem();
            itemCard.setItem(result.getResultItem());
            refreshUI();
            if (storageView != null) {
                storageView.selectItem(result.getResultItem());
            }
        } else {
            refreshUI();
        }
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

    private Image loadCharacterImage(Player player) {
        if (player == null || player.getCharacterClass() == null) {
            return null;
        }

        String fileName = switch (player.getCharacterClass()) {
            case WARRIOR -> "warrior.png";
            case RANGER -> "ranger.png";
            case MAGE -> "mage-transparent.png";
        };
        var resource = getClass().getResource("/com/example/_7/images/characters/" + fileName);
        return resource == null ? null : new Image(resource.toExternalForm());
    }
}
