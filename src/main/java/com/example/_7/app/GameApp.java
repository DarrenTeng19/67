package com.example._7.app;

import com.example._7.enemy.EnemyFactory;
import com.example._7.game.GameSession;
import com.example._7.game.RoundManager;
import com.example._7.item.ItemCatalog;
import com.example._7.shop.ShopGenerator;
import com.example._7.character.CharacterClass;
import com.example._7.character.Player;
import com.example._7.inventory.Backpack;
import com.example._7.inventory.Storage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example._7.ui.screen.PreparationScreenController;

public class GameApp extends Application {

    private RoundManager roundManager;
    private GameSession session;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 建立共享服務
        ItemCatalog itemCatalog = new ItemCatalog();
        ShopGenerator shopGenerator = new ShopGenerator(itemCatalog);
        EnemyFactory enemyFactory = new EnemyFactory();
        roundManager = new RoundManager(enemyFactory, shopGenerator);

        // 建立玩家與 session - 改為使用 Backpack 與 Storage
        Player player = new Player(
                "Player",
                CharacterClass.WARRIOR,
                50,
                CharacterClass.WARRIOR.createInitialStats(),
                new Backpack(),
                new Storage()
        );
        session = new GameSession(player);

        // 載入 preparation 畫面
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/_7/preparation.fxml"));
        Parent root = loader.load();

        // 取得 controller 並注入依賴
        PreparationScreenController controller = loader.getController();
        controller.init(session, roundManager, this);

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setTitle("Game - Preparation");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}