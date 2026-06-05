package com.example._7.ui.screen;

import com.example._7.app.GameApp;
import com.example._7.character.CharacterClass;
import com.example._7.character.CharacterStats;
import com.example._7.item.Item;
import com.example._7.item.ItemCatalog;
import com.example._7.recipe.Recipe;
import com.example._7.recipe.RecipeCatalog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.util.Comparator;
import java.util.stream.Collectors;

public class MainMenuScreen {
    @FXML private ToggleButton btnWarrior;
    @FXML private ToggleButton btnRanger;
    @FXML private ToggleButton btnMage;

    @FXML private Label lblClassName;
    @FXML private Label lblClassDescription;
    @FXML private Label lblHp;
    @FXML private Label lblStamina;
    @FXML private Label lblStaminaRecovery;
    @FXML private Label lblMana;
    @FXML private Label lblManaRecovery;
    @FXML private ImageView characterImage;

    private final ToggleGroup classToggleGroup = new ToggleGroup();
    private final Image warriorImage = loadCharacterImage("warrior.png");
    private final Image rangerImage = loadCharacterImage("ranger.png");
    private final Image mageImage = loadCharacterImage("mage-transparent.png");
    private GameApp app;
    private CharacterClass selectedClass = CharacterClass.WARRIOR;

    public void init(GameApp app) {
        this.app = app;
    }

    @FXML
    private void initialize() {
        btnWarrior.setToggleGroup(classToggleGroup);
        btnRanger.setToggleGroup(classToggleGroup);
        btnMage.setToggleGroup(classToggleGroup);

        btnWarrior.setUserData(CharacterClass.WARRIOR);
        btnRanger.setUserData(CharacterClass.RANGER);
        btnMage.setUserData(CharacterClass.MAGE);

        classToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == null) {
                classToggleGroup.selectToggle(oldToggle);
                return;
            }
            selectedClass = (CharacterClass) newToggle.getUserData();
            updateClassPreview();
        });

        btnWarrior.setSelected(true);
        updateClassPreview();
    }

    @FXML
    private void onStartGame() {
        if (app != null) {
            app.startNewGame(selectedClass);
        }
    }

    @FXML
    private void onShowItems() {
        ItemCatalog catalog = new ItemCatalog();
        String content = catalog.getAllItems().stream()
                .sorted(Comparator.comparing(Item::getAffinity).thenComparing(Item::getRarity))
                .map(item -> String.format(
                        "[%s] %s  |  $%d  |  稀有度 %d  |  %dx%d%n%s",
                        item.getAffinity(),
                        item.getName(),
                        item.getPrice(),
                        item.getRarity(),
                        item.getShape().width(),
                        item.getShape().height(),
                        item.getDescription()
                ))
                .collect(Collectors.joining("\n\n"));

        showScrollableInformation("組件與裝備總覽", content);
    }

    @FXML
    private void onShowRecipes() {
        ItemCatalog itemCatalog = new ItemCatalog();
        RecipeCatalog recipeCatalog = new RecipeCatalog();
        String content = recipeCatalog.getAllRecipes().stream()
                .map(recipe -> formatRecipe(recipe, itemCatalog))
                .collect(Collectors.joining("\n"));

        showScrollableInformation("合成配方", content);
    }

    @FXML
    private void onExit() {
        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "確定要離開遊戲嗎？",
                ButtonType.CANCEL,
                ButtonType.OK
        );
        confirmation.setTitle("離開遊戲");
        confirmation.setHeaderText(null);
        confirmation.showAndWait()
                .filter(ButtonType.OK::equals)
                .ifPresent(button -> Platform.exit());
    }

    private void updateClassPreview() {
        CharacterStats stats = selectedClass.createInitialStats();
        lblClassName.setText(selectedClass.getDisplayName());
        lblHp.setText(String.valueOf(stats.getMaxHp()));
        lblStamina.setText(String.valueOf(stats.getMaxStamina()));
        lblStaminaRecovery.setText(formatRate(stats.getStaminaRecoveryRate()));
        lblMana.setText(String.valueOf(stats.getMaxMana()));
        lblManaRecovery.setText(formatRate(stats.getManaRecoveryRate()));

        switch (selectedClass) {
            case WARRIOR -> {
                showCharacterImage(warriorImage);
                lblClassDescription.setText("高生命與高耐力，擅長使用武器和護甲正面壓制敵人。");
            }
            case RANGER -> {
                showCharacterImage(rangerImage);
                lblClassDescription.setText("攻守均衡，利用精準攻擊、毒素與致盲削弱敵人。");
            }
            case MAGE -> {
                showCharacterImage(mageImage);
                lblClassDescription.setText("擁有充沛魔力，以法術傷害和持續性負面效果取勝。");
            }
        }
    }

    private Image loadCharacterImage(String fileName) {
        String path = "/com/example/_7/images/characters/" + fileName;
        var resource = getClass().getResource(path);
        return resource == null ? null : new Image(resource.toExternalForm());
    }

    private void showCharacterImage(Image image) {
        if (image == null) {
            hideCharacterImage();
            return;
        }
        characterImage.setImage(image);
        characterImage.setManaged(true);
        characterImage.setVisible(true);
    }

    private void hideCharacterImage() {
        characterImage.setVisible(false);
        characterImage.setManaged(false);
        characterImage.setImage(null);
    }

    private String formatRecipe(Recipe recipe, ItemCatalog catalog) {
        return itemName(catalog, recipe.getIngredientAId())
                + " + "
                + itemName(catalog, recipe.getIngredientBId())
                + "  →  "
                + itemName(catalog, recipe.getResultItemId());
    }

    private String itemName(ItemCatalog catalog, String itemId) {
        return catalog.findById(itemId)
                .map(Item::getName)
                .orElse(itemId);
    }

    private String formatRate(double value) {
        if (value == Math.rint(value)) {
            return String.format("%.0f / 秒", value);
        }
        return String.format("%.1f / 秒", value);
    }

    private void showScrollableInformation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(720, 480);
        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}
