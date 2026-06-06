package com.example._7.ui.screen;

import com.example._7.app.GameApp;
import com.example._7.character.CharacterClass;
import com.example._7.game.GameResult;
import com.example._7.game.GameSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;

public class GameOverScreenController {
    private GameSession session;
    private GameApp app;

    @FXML private VBox resultCard;
    @FXML private Label lblResultBadge;
    @FXML private Label lblTitle;
    @FXML private Label lblSummary;
    @FXML private Label lblRound;
    @FXML private Label lblPlayerName;
    @FXML private Label lblPlayerClass;
    @FXML private Label lblPortraitMark;
    @FXML private ImageView characterPortrait;

    public void init(GameSession session, GameApp app) {
        this.session = session;
        this.app = app;
        updateUI();
    }

    private void updateUI() {
        if (session == null || session.getPlayer() == null) {
            return;
        }

        boolean cleared = session.getGameResult() == GameResult.CLEARED;
        CharacterClass characterClass = session.getPlayer().getCharacterClass();

        lblTitle.setText(cleared ? "冒險完成" : "戰敗");
        lblSummary.setText(cleared
                ? "你成功通過五個回合，完成了這次冒險。"
                : "你在第 " + session.getCurrentRound() + " 回合倒下");
        lblResultBadge.setText(cleared ? "CLEARED" : "DEFEATED");
        lblPortraitMark.setText(cleared ? "CLEARED" : "DEFEATED");

        lblPlayerName.setText(session.getPlayer().getName());
        lblPlayerClass.setText(classDisplayName(characterClass));
        lblRound.setText(session.getCurrentRound() + " / 5");
        characterPortrait.setImage(loadCharacterImage(fileNameFor(characterClass)));

        if (cleared) {
            resultCard.getStyleClass().add("cleared-card");
            lblTitle.getStyleClass().add("cleared-title");
            lblResultBadge.getStyleClass().add("cleared-badge");
        }
    }

    @FXML
    private void onBackToMainMenu() {
        app.getSceneManager().showMainMenu();
    }

    private Image loadCharacterImage(String fileName) {
        URL resource = getClass().getResource("/com/example/_7/images/characters/" + fileName);
        return resource == null ? null : new Image(resource.toExternalForm(), true);
    }

    private String fileNameFor(CharacterClass characterClass) {
        return switch (characterClass) {
            case WARRIOR -> "warrior.png";
            case RANGER -> "ranger.png";
            case MAGE -> "mage-transparent.png";
        };
    }

    private String classDisplayName(CharacterClass characterClass) {
        return switch (characterClass) {
            case WARRIOR -> "戰士";
            case RANGER -> "遊俠";
            case MAGE -> "魔法師";
        };
    }
}
