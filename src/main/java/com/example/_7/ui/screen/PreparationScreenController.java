package com.example._7.ui.screen;

import com.example._7.app.GameApp;
import com.example._7.battle.BattleEngine;
import com.example._7.character.Player;
import com.example._7.game.GameSession;
import com.example._7.game.RoundManager;
import com.example._7.shop.Shop;
import com.example._7.ui.component.BackpackGridView;
import com.example._7.ui.component.CharacterInfoPanel;
import com.example._7.ui.component.HealthBarView;
import com.example._7.ui.component.ItemCardView;
import com.example._7.ui.component.ShopPanel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class PreparationScreenController {

    private GameSession session;
    private RoundManager roundManager;
    private GameApp app;

    @FXML private AnchorPane root;

    // containers in FXML
    @FXML private AnchorPane charInfoContainer;
    @FXML private AnchorPane healthBarContainer;
    @FXML private AnchorPane shopPanelContainer;
    @FXML private AnchorPane backpackContainer;
    @FXML private AnchorPane itemCardContainer;

    // controls
    @FXML private Button btnStartBattle;
    @FXML private Button btnRefreshShop;
    @FXML private Button btnOpenStorage;

    // created components
    private CharacterInfoPanel charInfo;
    private HealthBarView healthBar;
    private ShopPanel shopPanel;
    private BackpackGridView backpackView;
    private ItemCardView itemCard;

    // init 由載入方呼叫注入依賴
    public void init(GameSession session, RoundManager roundManager, GameApp app) {
        this.session = session;
        this.roundManager = roundManager;
        this.app = app;

        // 初始化當前回合（如果尚未）
        roundManager.initializeCurrentRound(session);
        refreshUI();
    }

    @FXML
    private void initialize() {
        // instantiate components and add to containers
        try {
            charInfo = new CharacterInfoPanel();
            healthBar = new HealthBarView();
            shopPanel = new ShopPanel();
            backpackView = new BackpackGridView();
            itemCard = new ItemCardView();

            if (charInfoContainer != null) {
                charInfoContainer.getChildren().add(charInfo);
                AnchorPane.setTopAnchor(charInfo, 0.0);
                AnchorPane.setLeftAnchor(charInfo, 0.0);
            }
            if (healthBarContainer != null) {
                healthBarContainer.getChildren().add(healthBar);
                AnchorPane.setTopAnchor(healthBar, 0.0);
                AnchorPane.setLeftAnchor(healthBar, 0.0);
            }
            if (shopPanelContainer != null) {
                shopPanelContainer.getChildren().add(shopPanel);
                AnchorPane.setTopAnchor(shopPanel, 0.0);
                AnchorPane.setLeftAnchor(shopPanel, 0.0);
            }
            if (backpackContainer != null) {
                backpackContainer.getChildren().add(backpackView);
                AnchorPane.setTopAnchor(backpackView, 0.0);
                AnchorPane.setLeftAnchor(backpackView, 0.0);
            }
            if (itemCardContainer != null) {
                itemCardContainer.getChildren().add(itemCard);
                AnchorPane.setTopAnchor(itemCard, 0.0);
                AnchorPane.setLeftAnchor(itemCard, 0.0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshUI() {
        // 更新 character info
        if (session != null && session.getPlayer() != null && charInfo != null) {
            Player p = session.getPlayer();
            charInfo.setName(p.getName());
            charInfo.setCharacterClass(p.getCharacterClass().name());
        }

        // 更新 shop panel
        if (shopPanel != null && session != null) {
            Shop shop = session.getCurrentShop();
            shopPanel.setShop(shop);
        }
    }

    @FXML
    private void onStartBattle() {
        try {
            BattleEngine engine = new BattleEngine(session.getPlayer(), session.getCurrentEnemy());

            // Priority 4：把玩家與敵人「背包中實際參戰的道具」交給 BattleEngine。
            // BattleEngine 不直接讀取 Backpack，這樣可以維持 battle 和 inventory 的低耦合。
            engine.setItemsFor(
                    session.getPlayer(),
                    session.getPlayer().getBackpack().getBattleItems()
            );
            engine.setItemsFor(
                    session.getCurrentEnemy(),
                    session.getCurrentEnemy().getBackpack().getBattleItems()
            );

            engine.startBattle();

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/_7/battle.fxml"));
            javafx.scene.Parent root = loader.load();
            BattleScreenController battleCtrl = loader.getController();
            battleCtrl.init(session, engine, roundManager, app);

            javafx.stage.Stage stage = (javafx.stage.Stage) btnStartBattle.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRefreshShop() {
        if (roundManager != null && session != null) {
            roundManager.initializeCurrentRound(session);
            refreshUI();
        }
    }

    @FXML
    private void onOpenStorage() {
        System.out.println("Open storage (not implemented)");
    }
}