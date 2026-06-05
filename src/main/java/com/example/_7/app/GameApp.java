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
import com.example._7.ui.SceneManager;

public class GameApp extends Application {

    private static final int WINDOW_WIDTH = 1100;
    private static final int WINDOW_HEIGHT = 700;
    private static final int STARTING_GOLD = 30;

    private Stage primaryStage;
    private RoundManager roundManager;
    private GameSession session;

    private SceneManager sceneManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.sceneManager = new SceneManager(primaryStage, this);

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
        sceneManager.showMainMenu();
    }

    public void startNewGame(CharacterClass characterClass) {
        CharacterClass selectedClass = characterClass == null ? CharacterClass.WARRIOR : characterClass;
        Player player = new Player(
                "玩家",
                selectedClass,
                STARTING_GOLD,
                selectedClass.createInitialStats(),
                new Backpack(),
                new Storage()
        );
        session = new GameSession(player);

       roundManager.initializeCurrentRound(session);
       sceneManager.showPreparation(session, roundManager);
    }

    private void setRoot(Parent root, String title) {
        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        } else {
            primaryStage.getScene().setRoot(root);
        }
        primaryStage.setTitle(title);
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
