package com.example._7.app;

import com.example._7.character.CharacterClass;
import com.example._7.character.Player;
import com.example._7.enemy.EnemyFactory;
import com.example._7.game.GameSession;
import com.example._7.game.RoundManager;
import com.example._7.inventory.Backpack;
import com.example._7.inventory.Storage;
import com.example._7.item.ItemCatalog;
import com.example._7.shop.ShopGenerator;
import com.example._7.ui.screen.MainMenuScreen;
import com.example._7.ui.screen.PreparationScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApp extends Application {

    private static final int WINDOW_WIDTH = 1100;
    private static final int WINDOW_HEIGHT = 700;
    private static final int STARTING_GOLD = 10000;

    private Stage primaryStage;
    private RoundManager roundManager;
    private GameSession session;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        ItemCatalog itemCatalog = new ItemCatalog();
        ShopGenerator shopGenerator = new ShopGenerator(itemCatalog);
        EnemyFactory enemyFactory = new EnemyFactory(itemCatalog);
        roundManager = new RoundManager(enemyFactory, shopGenerator);

        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(640);
        showMainMenu();
        primaryStage.show();
    }

    public void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/_7/main-menu.fxml"));
            Parent root = loader.load();

            MainMenuScreen controller = loader.getController();
            controller.init(this);

            setRoot(root, "Backpack Battles - 主選單");
        } catch (Exception e) {
            throw new IllegalStateException("無法載入主選單", e);
        }
    }

    public void startNewGame(CharacterClass characterClass) {
        CharacterClass selectedClass = characterClass == null ? CharacterClass.WARRIOR : characterClass;
        Player player = new Player(
                "Player",
                selectedClass,
                STARTING_GOLD,
                selectedClass.createInitialStats(),
                new Backpack(),
                new Storage()
        );
        session = new GameSession(player);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/_7/preparation.fxml"));
            Parent root = loader.load();

            PreparationScreenController controller = loader.getController();
            controller.init(session, roundManager, this);

            setRoot(root, "Backpack Battles - 準備階段");
        } catch (Exception e) {
            throw new IllegalStateException("無法開始新遊戲", e);
        }
    }

    private void setRoot(Parent root, String title) {
        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        } else {
            primaryStage.getScene().setRoot(root);
        }
        primaryStage.setTitle(title);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
