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
import com.example._7.ui.screen.PreparationScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApp extends Application {

    private RoundManager roundManager;
    private GameSession session;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ItemCatalog itemCatalog = new ItemCatalog();
        ShopGenerator shopGenerator = new ShopGenerator(itemCatalog);
        EnemyFactory enemyFactory = new EnemyFactory(itemCatalog);
        roundManager = new RoundManager(enemyFactory, shopGenerator);

        Player player = new Player(
                "Player",
                CharacterClass.WARRIOR,
                10000,
                CharacterClass.WARRIOR.createInitialStats(),
                new Backpack(),
                new Storage()
        );
        session = new GameSession(player);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/_7/preparation.fxml"));
        Parent root = loader.load();

        PreparationScreenController controller = loader.getController();
        controller.init(session, roundManager, this);

        primaryStage.setScene(new Scene(root, 1000, 650));
        primaryStage.setTitle("Backpack Auto Battler - Preparation");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
