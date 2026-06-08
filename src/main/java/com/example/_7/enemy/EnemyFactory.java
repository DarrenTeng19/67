package com.example._7.enemy;

import com.example._7.battle.BattleState;
import com.example._7.character.CharacterClass;
import com.example._7.character.Enemy;
import com.example._7.inventory.Backpack;
import com.example._7.inventory.GridPosition;
import com.example._7.inventory.PlacedItem;
import com.example._7.item.Item;
import com.example._7.item.ItemCatalog;

import java.util.List;
import java.util.Random;

public class EnemyFactory {
    private static final int ENEMIES_PER_ROUND = 5;

    private static final List<List<EnemyTemplate>> ENEMY_POOLS = List.of(
            List.of(
                    enemy("Warrior Vanguard", CharacterClass.WARRIOR,
                            "wooden_sword", "small_round_shield", "hammer", "banana", "energy_drink"),
                    enemy("Warrior Duelist", CharacterClass.WARRIOR,
                            "iron_sword", "football_helmet", "hammer"),
                    enemy("Ranger Scout", CharacterClass.RANGER,
                            "recurve_bow", "hunter_vest", "dagger"),
                    enemy("Ranger Skirmisher", CharacterClass.RANGER,
                            "short_bow", "quiver", "dagger", "hunter_vest"),
                    enemy("Mage Apprentice", CharacterClass.MAGE,
                            "basic_staff", "intro_magic_guide", "burning_broom")
            ),
            List.of(
                    enemy("Warrior Guard", CharacterClass.WARRIOR,
                            "iron_sword", "steel_shield", "hammer", "banana", "wooden_sword", "energy_drink"),
                    enemy("Warrior Breaker", CharacterClass.WARRIOR,
                            "big_hammer", "steel_shield", "iron_sword", "wooden_sword"),
                    enemy("Ranger Venomguard", CharacterClass.RANGER,
                            "poison_bow", "ranger_vest_equipment"),
                    enemy("Ranger Trapper", CharacterClass.RANGER,
                            "recurve_bow", "poison_ivy", "world_tree_leaf", "teemo_blowdart"),
                    enemy("Mage Adept", CharacterClass.MAGE,
                            "advanced_wand", "basic_staff", "intro_magic_guide")
            ),
            List.of(
                    enemy("Warrior Champion", CharacterClass.WARRIOR,
                            "big_hammer", "adaptive_helmet", "himmel_note", "banana",
                            "energy_drink", "iron_sword", "iron_sword"),
                    enemy("Ranger Executioner", CharacterClass.RANGER,
                            "death_sentence", "poison_bow", "ranger_vest_equipment"),
                    enemy("Ranger Shadow", CharacterClass.RANGER,
                            "death_sentence", "adventurer_helmet", "deadly_dagger",
                            "world_tree_leaf"),
                    enemy("Mage Archmage", CharacterClass.MAGE,
                            "frieren_staff", "advanced_wand", "mana_cloak"),
                    enemy("Mage Hexer", CharacterClass.MAGE,
                            "frieren_staff", "dark_mage_hat", "mana_cloak")
            ),
            List.of(
                    enemy("Warrior Warlord", CharacterClass.WARRIOR,
                            "hero_himmel_greatsword", "thorn_armor", "himmel_note", "banana",
                            "iron_sword", "energy_drink", "iron_sword"),
                    enemy("Ranger Reaper", CharacterClass.RANGER,
                            "death_sentence", "death_sentence", "ranger_vest_equipment"),
                    enemy("Ranger Plague Hunter", CharacterClass.RANGER,
                            "death_sentence", "poison_bow", "ranger_vest_equipment",
                            "world_tree_leaf", "teemo_blowdart"),
                    enemy("Mage Grand Sorcerer", CharacterClass.MAGE,
                            "frieren_staff", "frieren_staff", "advanced_wand", "basic_staff"),
                    enemy("Mage Spellbinder", CharacterClass.MAGE,
                            "frieren_staff", "advanced_wand", "advanced_wand", "mana_cloak")
            ),
            List.of(
                    enemy("Warrior Final Champion", CharacterClass.WARRIOR,
                            "hero_himmel_greatsword", "hero_himmel_greatsword", "thorn_armor",
                            "banana", "energy_drink", "himmel_note", "thorn_spread", "big_hammer"),
                    enemy("Ranger Death Squad", CharacterClass.RANGER,
                            "death_sentence", "death_sentence", "death_sentence",
                            "ranger_vest_equipment", "dagger"),
                    enemy("Ranger Venom King", CharacterClass.RANGER,
                            "death_sentence", "death_sentence", "poison_bow",
                            "poison_bow", "ranger_vest_equipment"),
                    enemy("Mage Ancient One", CharacterClass.MAGE,
                            "frieren_staff", "frieren_staff", "frieren_staff",
                            "advanced_wand", "grandma_crystal_ball"),
                    enemy("Mage Staff Master", CharacterClass.MAGE,
                            "frieren_staff", "frieren_staff", "advanced_wand",
                            "advanced_wand", "advanced_wand")
            )
    );

    private final ItemCatalog itemCatalog;
    private final Random random;

    public EnemyFactory(ItemCatalog itemCatalog) {
        this(itemCatalog, new Random());
    }

    EnemyFactory(ItemCatalog itemCatalog, Random random) {
        this.itemCatalog = itemCatalog;
        this.random = random;
    }

    public Enemy createEnemyForRound(int round) {
        List<Enemy> pool = createEnemyPoolForRound(round);
        return pool.get(random.nextInt(pool.size()));
    }

    public List<Enemy> createEnemyPoolForRound(int round) {
        int roundIndex = Math.max(1, Math.min(ENEMY_POOLS.size(), round)) - 1;
        List<Enemy> enemies = ENEMY_POOLS.get(roundIndex).stream()
                .map(template -> createEnemy(template, roundIndex + 1))
                .toList();

        if (enemies.size() != ENEMIES_PER_ROUND) {
            throw new IllegalStateException("Each round must contain exactly five enemies.");
        }
        return enemies;
    }

    private Enemy createEnemy(EnemyTemplate template, int round) {
        Backpack backpack = new Backpack();
        for (String itemId : template.itemIds()) {
            placeFirstAvailable(backpack, itemId);
        }

        return new Enemy(
                template.name() + " - Round " + round,
                template.characterClass(),
                template.characterClass().createInitialStats(),
                new BattleState(),
                backpack
        );
    }

    private void placeFirstAvailable(Backpack backpack, String itemId) {
        Item item = itemCatalog.getById(itemId);
        GridPosition position = backpack.findFirstAvailablePosition(item)
                .orElseThrow(() -> new IllegalStateException(
                        "Enemy loadout does not fit in backpack: " + itemId
                ));
        backpack.tryPlaceItem(new PlacedItem(item, position));
    }

    private static EnemyTemplate enemy(
            String name,
            CharacterClass characterClass,
            String... itemIds
    ) {
        return new EnemyTemplate(name, characterClass, List.of(itemIds));
    }

    private record EnemyTemplate(
            String name,
            CharacterClass characterClass,
            List<String> itemIds
    ) {
    }
}
