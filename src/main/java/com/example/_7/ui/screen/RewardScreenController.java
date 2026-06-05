package com.example._7.ui.screen;

import com.example._7.app.GameApp;
import com.example._7.character.CharacterClass;
import com.example._7.game.GamePhase;
import com.example._7.game.GameSession;
import com.example._7.game.RoundManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

public class RewardScreenController {
    private GameSession session;
    private RoundManager roundManager;
    private GameApp app;

    @FXML private Label lblTitle;
    @FXML private Label lblSummary;
    @FXML private Label lblRound;
    @FXML private Label lblGoldReward;
    @FXML private Label lblPlayerName;
    @FXML private Label lblPlayerClass;
    @FXML private ImageView characterPortrait;

    public void init(GameSession session, RoundManager roundManager, GameApp app) {
        this.session = session;
        this.roundManager = roundManager;
        this.app = app;
        updateUI();
    }

    private void updateUI() {
        CharacterClass characterClass = session.getPlayer().getCharacterClass();
        int clearedRound = session.getLastClearedRound();

        lblTitle.setText("戰鬥勝利");
        lblSummary.setText("你成功通過第 " + clearedRound + " 回合");
        lblRound.setText(clearedRound + " / 5");
        lblGoldReward.setText("+" + session.getLastGoldReward() + " G");
        lblPlayerName.setText(session.getPlayer().getName());
        lblPlayerClass.setText(classDisplayName(characterClass));
        characterPortrait.setImage(loadCharacterImage(fileNameFor(characterClass)));
    }

    @FXML
    private void onContinue() {
        roundManager.continueAfterReward(session);

        if (session.getCurrentPhase() == GamePhase.PREPARATION) {
            app.getSceneManager().showPreparation(session, roundManager);
        } else if (session.getCurrentPhase() == GamePhase.GAME_OVER) {
            app.getSceneManager().showGameOver(session);
        }
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
