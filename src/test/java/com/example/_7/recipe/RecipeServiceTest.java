package com.example._7.recipe;

import com.example._7.character.CharacterClass;
import com.example._7.character.Player;
import com.example._7.inventory.Backpack;
import com.example._7.inventory.GridPosition;
import com.example._7.inventory.PlacedItem;
import com.example._7.inventory.Storage;
import com.example._7.item.Item;
import com.example._7.item.ItemCatalog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeServiceTest {
    private final ItemCatalog itemCatalog = new ItemCatalog();
    private final RecipeService recipeService =
            new RecipeService(new RecipeCatalog(), itemCatalog);

    @Test
    void craftsDeathSentenceFromEquipmentAndComponentInStorage() {
        Player player = createPlayer();
        Item poisonBow = itemCatalog.getById("poison_bow");
        Item poisonNeedle = itemCatalog.getById("teemo_poison_needle");
        player.getStorage().addItem(poisonBow);
        player.getStorage().addItem(poisonNeedle);

        RecipeResult result = recipeService.autoCraftFirst(player);

        assertTrue(result.isSuccess());
        assertEquals("death_sentence", result.getResultItem().getId());
        assertFalse(player.getStorage().contains(poisonBow));
        assertFalse(player.getStorage().contains(poisonNeedle));
        assertTrue(player.getStorage().contains(result.getResultItem()));
    }

    @Test
    void craftsDeathSentenceWhenPoisonBowIsEquippedInBackpack() {
        Player player = createPlayer();
        Item poisonBow = itemCatalog.getById("poison_bow");
        Item poisonNeedle = itemCatalog.getById("teemo_poison_needle");
        PlacedItem placedBow = new PlacedItem(poisonBow, new GridPosition(0, 0));
        assertTrue(player.getBackpack().tryPlaceItem(placedBow));
        player.getStorage().addItem(poisonNeedle);

        RecipeResult result = recipeService.autoCraftFirst(player);

        assertTrue(result.isSuccess());
        assertEquals("death_sentence", result.getResultItem().getId());
        assertFalse(player.getBackpack().getPlacedItems().contains(placedBow));
        assertFalse(player.getStorage().contains(poisonNeedle));
        assertTrue(player.getStorage().contains(result.getResultItem()));
    }

    @Test
    void craftsHeroHimmelGreatswordFromIronSwordAndHimmelNote() {
        Player player = createPlayer();
        Item ironSword = itemCatalog.getById("iron_sword");
        Item himmelNote = itemCatalog.getById("himmel_note");
        player.getStorage().addItem(ironSword);
        player.getStorage().addItem(himmelNote);

        RecipeResult result = recipeService.autoCraftFirst(player);

        assertTrue(result.isSuccess());
        assertEquals("hero_himmel_greatsword", result.getResultItem().getId());
        assertFalse(player.getStorage().contains(ironSword));
        assertFalse(player.getStorage().contains(himmelNote));
        assertTrue(player.getStorage().contains(result.getResultItem()));
    }

    private Player createPlayer() {
        return new Player(
                "Test",
                CharacterClass.RANGER,
                0,
                CharacterClass.RANGER.createInitialStats(),
                new Backpack(),
                new Storage()
        );
    }
}
