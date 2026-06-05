package com.example._7.recipe;

import java.util.List;
import java.util.Optional;

/**
 * 合成配方目錄。
 *
 * 注意：這裡的 item id 必須和 ItemCatalog 裡建立道具時使用的 id 完全一致。
 */
public class RecipeCatalog {
    private final List<Recipe> recipes;

    public RecipeCatalog() {
        this.recipes = List.of(
                // 通用
                recipe("red_green_to_magic_stone", "red_crystal", "green_crystal", "magic_stone"),

                // 戰士
                recipe("wooden_sword_iron_plate_to_iron_sword", "wooden_sword", "iron_plate", "iron_sword"),
                recipe("small_round_shield_iron_plate_to_steel_shield", "small_round_shield", "iron_plate", "steel_shield"),
                recipe("hammer_iron_plate_to_big_hammer", "hammer", "iron_plate", "big_hammer"),
                recipe("football_helmet_mana_shard_to_adaptive_helmet", "football_helmet", "mana_shard", "adaptive_helmet"),
                recipe("chainmail_thorn_spread_to_thorn_armor", "chainmail", "thorn_spread", "thorn_armor"),
                recipe("iron_sword_himmel_note_to_hero_himmel_greatsword", "iron_sword", "himmel_note", "hero_himmel_greatsword"),

                // 遊俠
                recipe("dagger_green_crystal_to_deadly_dagger", "dagger", "green_crystal", "deadly_dagger"),
                recipe("fisherman_hat_iron_plate_to_adventurer_helmet", "fisherman_hat", "iron_plate", "adventurer_helmet"),
                recipe("hunter_vest_ranger_patience_to_ranger_vest_equipment", "hunter_vest", "ranger_patience", "ranger_vest_equipment"),
                recipe("recurve_bow_poison_ivy_to_poison_bow", "recurve_bow", "poison_ivy", "poison_bow"),
                recipe("poison_bow_teemo_poison_needle_to_death_sentence", "poison_bow", "teemo_poison_needle", "death_sentence"),

                // 魔法師
                recipe("broom_flame_to_burning_broom", "broom", "flame", "burning_broom"),
                recipe("broom_fern_bracelet_to_fern_wooden_stick", "broom", "fern_bracelet", "fern_wooden_stick"),
                recipe("burning_broom_basic_staff_to_burning_staff", "burning_broom", "basic_staff", "burning_staff"),
                recipe("basic_staff_grandma_crystal_ball_to_advanced_wand", "basic_staff", "grandma_crystal_ball", "advanced_wand"),
                recipe("beret_intro_magic_guide_to_dark_mage_hat", "beret", "intro_magic_guide", "dark_mage_hat"),
                recipe("leather_cloak_tear_of_goddess_to_mana_cloak", "leather_cloak", "tear_of_goddess", "mana_cloak"),
                recipe("advanced_wand_frieren_ice_magic_book_to_frieren_staff", "advanced_wand", "frieren_ice_magic_book", "frieren_staff")
        );
    }

    private Recipe recipe(String id, String a, String b, String result) {
        return new Recipe(id, a, b, result, false);
    }

    public List<Recipe> getAllRecipes() {
        return recipes;
    }

    public Optional<Recipe> findRecipe(String itemAId, String itemBId) {
        return recipes.stream()
                .filter(recipe -> recipe.matches(itemAId, itemBId))
                .findFirst();
    }
}
