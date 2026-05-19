package com.example._7.item;

/*
 * 根據 HackMD 道具系統產生的道具目錄。
 * 新增/修改道具時，建議集中改這個類別。
 */

import com.example._7.character.CharacterClass;
import com.example._7.item.effect.*;
import com.example._7.item.shape.ItemShapes;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ItemCatalog {
    private final List<Item> allItems;

    public ItemCatalog() {
        this.allItems = List.of(
                createBanana(),
                createEnergyDrink(),
                createIronPlate(),
                createManaShard(),
                createFairyAmulet(),
                createRedCrystal(),
                createGreenCrystal(),
                createWoodenSword(),
                createSmallRoundShield(),
                createHammer(),
                createChainmail(),
                createFootballHelmet(),
                createArmorShard(),
                createHimmelNote(),
                createThornSpread(),
                createShortBow(),
                createQuiver(),
                createDagger(),
                createHunterVest(),
                createTeemoBlowdart(),
                createFishermanHat(),
                createRangerPatience(),
                createRecurveBow(),
                createPoisonIvy(),
                createWorldTreeLeaf(),
                createTeemoPoisonNeedle(),
                createBroom(),
                createFlame(),
                createCottonGloves(),
                createIntroMagicGuide(),
                createLeatherCloak(),
                createLeatherGloves(),
                createBeret(),
                createBasicStaff(),
                createFrierenIceBook(),
                createNatureChronicle(),
                createFernBracelet(),
                createGrandmaCrystalBall(),
                createTearOfGoddess(),
                createMagicStone(),
                createIronSword(),
                createSteelShield(),
                createBigHammer(),
                createAdaptiveHelmet(),
                createThornArmor(),
                createHeroHimmelSword(),
                createDeadlyDagger(),
                createAdventurerHelmet(),
                createRangerVestEquipment(),
                createPoisonBow(),
                createDeathSentence(),
                createBurningBroom(),
                createFernWoodenStick(),
                createBurningStaff(),
                createAdvancedWand(),
                createDarkMageHat(),
                createManaCloak(),
                createFrierenStaff()
        );
    }

    public List<Item> getAllItems() {
        return allItems;
    }

    public Optional<Item> findById(String id) {
        return allItems.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    public Item getById(String id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到道具 id: " + id));
    }

    public List<Item> getShopPool(CharacterClass characterClass, int round) {
        return allItems.stream()
                .filter(item -> isAvailableForClass(item, characterClass))
                .filter(item -> isAvailableInRound(item, round))
                .toList();
    }

    private boolean isAvailableForClass(Item item, CharacterClass characterClass) {
        return item.getAffinity() == ItemAffinity.COMMON
                || item.getAffinity().matches(characterClass);
    }

    private boolean isAvailableInRound(Item item, int round) {
        // HackMD 目前沒有獨立的「出現回合」欄位，先用稀有度控制商店解鎖。
        return item.getRarity() <= Math.max(1, round);
    }

    private Item item(
            String id,
            String name,
            ItemAffinity affinity,
            Set<ItemRole> roles,
            int width,
            int height,
            int price,
            int rarity,
            ItemTriggerType triggerType,
            double cooldownSeconds,
            double hitRate,
            int staminaCost,
            int manaCost,
            List<ItemEffect> effects
    ) {
        return new BasicItem(
                id,
                name,
                affinity,
                roles,
                ItemShapes.rectangle(width, height),
                price,
                rarity,
                triggerType,
                cooldownSeconds,
                hitRate,
                staminaCost,
                manaCost,
                "",
                effects
        );
    }

    private Item createBanana() {
        return item(
                "banana",
                "香蕉",
                ItemAffinity.COMMON,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                3,
                1,
                ItemTriggerType.ACTIVE,
                4.0,
                1.00,
                1,
                0,
                List.of(new HealEffect(4))
        );
    }

    private Item createEnergyDrink() {
        return item(
                "energy_drink",
                "能量飲料",
                ItemAffinity.COMMON,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                3,
                1,
                ItemTriggerType.ACTIVE,
                2.0,
                1.00,
                0,
                0,
                List.of(new RecoverStaminaEffect(3))
        );
    }

    private Item createIronPlate() {
        return item(
                "iron_plate",
                "鐵片",
                ItemAffinity.COMMON,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                5,
                2,
                ItemTriggerType.ACTIVE,
                3.5,
                1.00,
                1,
                0,
                List.of(new ShieldEffect(4))
        );
    }

    private Item createManaShard() {
        return item(
                "mana_shard",
                "魔力碎片",
                ItemAffinity.COMMON,
                Set.of(ItemRole.COMPONENT),
                1,
                1,
                3,
                1,
                ItemTriggerType.ACTIVE,
                2.0,
                1.00,
                0,
                0,
                List.of(new RecoverManaEffect(1))
        );
    }

    private Item createFairyAmulet() {
        return item(
                "fairy_amulet",
                "仙女護符",
                ItemAffinity.COMMON,
                Set.of(ItemRole.COMPONENT),
                1,
                1,
                5,
                2,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddManaRecoveryEffect(1.0))
        );
    }

    private Item createRedCrystal() {
        return item(
                "red_crystal",
                "紅水晶",
                ItemAffinity.COMMON,
                Set.of(ItemRole.COMPONENT),
                1,
                1,
                5,
                2,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddMaxHpEffect(8))
        );
    }

    private Item createGreenCrystal() {
        return item(
                "green_crystal",
                "綠水晶",
                ItemAffinity.COMMON,
                Set.of(ItemRole.COMPONENT),
                1,
                1,
                5,
                2,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new MaterialOnlyEffect("僅供合成裝備用"))
        );
    }

    private Item createWoodenSword() {
        return item(
                "wooden_sword",
                "木劍",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                2,
                1,
                ItemTriggerType.ACTIVE,
                1.8,
                0.85,
                2,
                0,
                List.of(new DamageEffect(7))
        );
    }

    private Item createSmallRoundShield() {
        return item(
                "small_round_shield",
                "小圓盾",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.COMPONENT),
                2,
                2,
                3,
                1,
                ItemTriggerType.ACTIVE,
                2.2,
                1.00,
                2,
                0,
                List.of(new AddMaxHpEffect(12), new ShieldEffect(8))
        );
    }

    private Item createHammer() {
        return item(
                "hammer",
                "鐵鎚",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                4,
                2,
                ItemTriggerType.ACTIVE,
                4.0,
                0.65,
                4,
                0,
                List.of(new DamageEffect(11))
        );
    }

    private Item createChainmail() {
        return item(
                "chainmail",
                "鎖子甲",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.COMPONENT),
                2,
                2,
                5,
                2,
                ItemTriggerType.ACTIVE,
                3.5,
                1.00,
                3,
                0,
                List.of(new AddMaxHpEffect(20), new AddStaminaRecoveryEffect(1.0), new ShieldEffect(12))
        );
    }

    private Item createFootballHelmet() {
        return item(
                "football_helmet",
                "橄欖球頭盔",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                4,
                2,
                ItemTriggerType.ACTIVE,
                3.0,
                1.00,
                2,
                0,
                List.of(new AddMaxHpEffect(12), new AddMaxStaminaEffect(3), new ShieldEffect(9))
        );
    }

    private Item createArmorShard() {
        return item(
                "armor_shard",
                "盔甲片碎片",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.COMPONENT),
                1,
                1,
                6,
                3,
                ItemTriggerType.ACTIVE,
                4.0,
                1.00,
                2,
                0,
                List.of(new ShieldEffect(10))
        );
    }

    private Item createHimmelNote() {
        return item(
                "himmel_note",
                "欣梅爾的筆記",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                11,
                4,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                3,
                0,
                List.of(new AddMaxStaminaEffect(5), new AddStaminaRecoveryEffect(1.0), new AddBuffEffect("吸血", 1))
        );
    }

    private Item createThornSpread() {
        return item(
                "thorn_spread",
                "荊棘蔓延",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                12,
                4,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                3,
                0,
                List.of(new AddMaxHpEffect(20), new AddBuffEffect("尖刺", 1))
        );
    }

    private Item createShortBow() {
        return item(
                "short_bow",
                "短弓",
                ItemAffinity.RANGER,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                2,
                1,
                ItemTriggerType.ACTIVE,
                1.9,
                0.90,
                2,
                0,
                List.of(new DamageEffect(6))
        );
    }

    private Item createQuiver() {
        return item(
                "quiver",
                "箭袋",
                ItemAffinity.RANGER,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                3,
                1,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddMaxHpEffect(5), new AddGlobalItemBuffEffect("精準", 1))
        );
    }

    private Item createDagger() {
        return item(
                "dagger",
                "匕首",
                ItemAffinity.RANGER,
                Set.of(ItemRole.COMPONENT),
                1,
                1,
                3,
                1,
                ItemTriggerType.ACTIVE,
                1.8,
                0.90,
                1,
                0,
                List.of(new DamageEffect(2))
        );
    }

    private Item createHunterVest() {
        return item(
                "hunter_vest",
                "獵人背心",
                ItemAffinity.RANGER,
                Set.of(ItemRole.COMPONENT),
                2,
                2,
                6,
                2,
                ItemTriggerType.ACTIVE,
                4.0,
                1.00,
                1,
                1,
                List.of(new AddMaxHpEffect(15), new ShieldEffect(5), new AddBuffEffect("技能疾速", 1))
        );
    }

    private Item createTeemoBlowdart() {
        return item(
                "teemo_blowdart",
                "提摩的吹箭",
                ItemAffinity.RANGER,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                5,
                2,
                ItemTriggerType.ACTIVE,
                3.5,
                0.80,
                2,
                1,
                List.of(new DamageEffect(2), new ApplyDebuffEffect("致盲", 1))
        );
    }

    private Item createFishermanHat() {
        return item(
                "fisherman_hat",
                "漁夫帽",
                ItemAffinity.RANGER,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                4,
                2,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddMaxHpEffect(10), new AddGlobalItemBuffEffect("技能疾速", 1))
        );
    }

    private Item createRangerPatience() {
        return item(
                "ranger_patience",
                "遊俠的耐心",
                ItemAffinity.RANGER,
                Set.of(ItemRole.COMPONENT),
                1,
                1,
                4,
                2,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddMaxStaminaEffect(2), new AddStaminaRecoveryEffect(1.0))
        );
    }

    private Item createRecurveBow() {
        return item(
                "recurve_bow",
                "反曲弓",
                ItemAffinity.RANGER,
                Set.of(ItemRole.COMPONENT),
                1,
                3,
                6,
                2,
                ItemTriggerType.ACTIVE,
                3.2,
                0.85,
                4,
                0,
                List.of(new DamageEffect(13))
        );
    }

    private Item createPoisonIvy() {
        return item(
                "poison_ivy",
                "劇毒常春藤",
                ItemAffinity.RANGER,
                Set.of(ItemRole.COMPONENT),
                1,
                3,
                8,
                3,
                ItemTriggerType.ACTIVE,
                3.0,
                0.90,
                3,
                0,
                List.of(new DamageEffect(10), new ApplyDebuffEffect("中毒", 1))
        );
    }

    private Item createWorldTreeLeaf() {
        return item(
                "world_tree_leaf",
                "世界樹樹葉",
                ItemAffinity.RANGER,
                Set.of(ItemRole.COMPONENT),
                1,
                1,
                8,
                3,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddMaxHpEffect(14), new AddMaxStaminaEffect(4), new AddStaminaRecoveryEffect(2.0))
        );
    }

    private Item createTeemoPoisonNeedle() {
        return item(
                "teemo_poison_needle",
                "提摩的毒針",
                ItemAffinity.RANGER,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                11,
                4,
                ItemTriggerType.ACTIVE,
                3.5,
                0.80,
                4,
                0,
                List.of(new DamageEffect(3), new ApplyDebuffEffect("中毒", 1), new ApplyDebuffEffect("致盲", 1))
        );
    }

    private Item createBroom() {
        return item(
                "broom",
                "掃把",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                2,
                1,
                ItemTriggerType.ACTIVE,
                2.2,
                1.00,
                1,
                0,
                List.of(new DamageEffect(4))
        );
    }

    private Item createFlame() {
        return item(
                "flame",
                "一團火焰",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                1,
                1,
                3,
                1,
                ItemTriggerType.ACTIVE,
                3.0,
                0.88,
                0,
                2,
                List.of(new DamageEffect(2), new ApplyDebuffEffect("燃燒", 1))
        );
    }

    private Item createCottonGloves() {
        return item(
                "cotton_gloves",
                "棉布手套",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                3,
                1,
                ItemTriggerType.ACTIVE,
                3.0,
                1.00,
                0,
                3,
                List.of(new AddMaxHpEffect(3), new ShieldEffect(2))
        );
    }

    private Item createIntroMagicGuide() {
        return item(
                "intro_magic_guide",
                "魔法入門指南",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                5,
                2,
                ItemTriggerType.ACTIVE,
                3.2,
                0.85,
                0,
                3,
                List.of(new AddManaRecoveryEffect(1.0), new AddMaxHpEffect(10), new DamageEffect(7))
        );
    }

    private Item createLeatherCloak() {
        return item(
                "leather_cloak",
                "皮製斗篷",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                2,
                2,
                6,
                2,
                ItemTriggerType.ACTIVE,
                4.5,
                1.00,
                0,
                3,
                List.of(new AddMaxHpEffect(12), new ShieldEffect(5))
        );
    }

    private Item createLeatherGloves() {
        return item(
                "leather_gloves",
                "皮製手套",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                4,
                2,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddMaxHpEffect(4), new AddGlobalItemBuffEffect("技能疾速", 1))
        );
    }

    private Item createBeret() {
        return item(
                "beret",
                "貝雷帽",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                4,
                2,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddMaxHpEffect(6), new AddGlobalItemBuffEffect("強化", 1))
        );
    }

    private Item createBasicStaff() {
        return item(
                "basic_staff",
                "基礎法杖",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                1,
                3,
                6,
                2,
                ItemTriggerType.ACTIVE,
                3.5,
                0.90,
                0,
                4,
                List.of(new AddMaxManaEffect(3), new DamageEffect(11))
        );
    }

    private Item createFrierenIceBook() {
        return item(
                "frieren_ice_magic_book",
                "芙莉蓮的冰魔法手冊",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                9,
                3,
                ItemTriggerType.ACTIVE,
                2.5,
                0.85,
                0,
                5,
                List.of(new AddMaxManaEffect(4), new DamageEffect(7), new ApplyDebuffEffect("技能緩速", 1))
        );
    }

    private Item createNatureChronicle() {
        return item(
                "nature_chronicle",
                "大自然的編年史",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                1,
                2,
                8,
                3,
                ItemTriggerType.ACTIVE,
                4.0,
                0.80,
                0,
                5,
                List.of(new ShieldEffect(3), new ApplyDebuffEffect("中毒", 1))
        );
    }

    private Item createFernBracelet() {
        return item(
                "fern_bracelet",
                "費倫的手環",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                1,
                1,
                7,
                3,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddGlobalItemBuffEffect("精準", 1), new AddManaRecoveryEffect(1.5), new AddMaxManaEffect(2))
        );
    }

    private Item createGrandmaCrystalBall() {
        return item(
                "grandma_crystal_ball",
                "阿罵的水晶球",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                1,
                1,
                10,
                3,
                ItemTriggerType.ACTIVE,
                4.0,
                0.40,
                0,
                0,
                List.of(new AddMaxHpEffect(8), new DamageEffect(30), new ApplyDebuffEffect("技能緩速", 2))
        );
    }

    private Item createTearOfGoddess() {
        return item(
                "tear_of_goddess",
                "女神之淚",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT),
                1,
                1,
                12,
                4,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddMaxManaEffect(3), new AddManaRecoveryEffect(2.0))
        );
    }

    private Item createMagicStone() {
        return item(
                "magic_stone",
                "魔法石",
                ItemAffinity.COMMON,
                Set.of(ItemRole.COMPONENT, ItemRole.EQUIPMENT),
                1,
                2,
                8,
                3,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddMaxManaEffect(3), new AddManaRecoveryEffect(1.0))
        );
    }

    private Item createIronSword() {
        return item(
                "iron_sword",
                "鐵劍",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.COMPONENT, ItemRole.EQUIPMENT),
                1,
                2,
                7,
                2,
                ItemTriggerType.ACTIVE,
                2.0,
                0.88,
                4,
                0,
                List.of(new DamageEffect(11))
        );
    }

    private Item createSteelShield() {
        return item(
                "steel_shield",
                "鋼盾",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.EQUIPMENT),
                2,
                2,
                8,
                2,
                ItemTriggerType.ACTIVE,
                2.8,
                1.00,
                3,
                0,
                List.of(new AddMaxHpEffect(15), new ShieldEffect(12))
        );
    }

    private Item createBigHammer() {
        return item(
                "big_hammer",
                "大棒槌",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.EQUIPMENT),
                1,
                2,
                10,
                3,
                ItemTriggerType.ACTIVE,
                4.2,
                0.70,
                6,
                0,
                List.of(new DamageEffect(25))
        );
    }

    private Item createAdaptiveHelmet() {
        return item(
                "adaptive_helmet",
                "適性之盔",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.EQUIPMENT),
                1,
                2,
                10,
                3,
                ItemTriggerType.ACTIVE,
                3.0,
                1.00,
                6,
                0,
                List.of(new AddMaxHpEffect(20), new AddMaxStaminaEffect(5), new ShieldEffect(12))
        );
    }

    private Item createThornArmor() {
        return item(
                "thorn_armor",
                "荊棘反甲",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.EQUIPMENT),
                2,
                3,
                17,
                4,
                ItemTriggerType.ACTIVE,
                4.0,
                1.00,
                2,
                0,
                List.of(new AddMaxHpEffect(35), new AddStaminaRecoveryEffect(1.5), new ShieldEffect(20), new AddBuffEffect("尖刺", 1))
        );
    }

    private Item createHeroHimmelSword() {
        return item(
                "hero_himmel_greatsword",
                "勇者欣梅爾的大寶劍",
                ItemAffinity.WARRIOR,
                Set.of(ItemRole.EQUIPMENT),
                1,
                3,
                18,
                4,
                ItemTriggerType.ACTIVE,
                3.2,
                0.85,
                5,
                0,
                List.of(new AddMaxStaminaEffect(5), new AddStaminaRecoveryEffect(1.0), new DamageEffect(20), new AddBuffEffect("吸血", 1))
        );
    }

    private Item createDeadlyDagger() {
        return item(
                "deadly_dagger",
                "致命匕首",
                ItemAffinity.RANGER,
                Set.of(ItemRole.EQUIPMENT),
                1,
                3,
                8,
                2,
                ItemTriggerType.ACTIVE,
                2.2,
                0.90,
                3,
                0,
                List.of(new DamageEffect(5), new ApplyDebuffEffect("中毒", 1))
        );
    }

    private Item createAdventurerHelmet() {
        return item(
                "adventurer_helmet",
                "冒險家頭盔",
                ItemAffinity.RANGER,
                Set.of(ItemRole.EQUIPMENT),
                1,
                2,
                9,
                3,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddMaxHpEffect(20), new AddGlobalItemBuffEffect("技能疾速", 2))
        );
    }

    private Item createRangerVestEquipment() {
        return item(
                "ranger_vest_equipment",
                "遊俠的背心",
                ItemAffinity.RANGER,
                Set.of(ItemRole.EQUIPMENT),
                2,
                2,
                14,
                3,
                ItemTriggerType.PASSIVE,
                0.0,
                1.00,
                0,
                0,
                List.of(new AddMaxHpEffect(25), new AddMaxStaminaEffect(7), new AddStaminaRecoveryEffect(2.0), new AddGlobalItemBuffEffect("技能疾速", 1))
        );
    }

    private Item createPoisonBow() {
        return item(
                "poison_bow",
                "有毒的弓",
                ItemAffinity.RANGER,
                Set.of(ItemRole.EQUIPMENT),
                1,
                3,
                14,
                4,
                ItemTriggerType.ACTIVE,
                3.0,
                0.85,
                4,
                2,
                List.of(new DamageEffect(20), new ApplyDebuffEffect("中毒", 1))
        );
    }

    private Item createDeathSentence() {
        return item(
                "death_sentence",
                "致死宣告",
                ItemAffinity.RANGER,
                Set.of(ItemRole.EQUIPMENT),
                1,
                3,
                25,
                5,
                ItemTriggerType.ACTIVE,
                3.5,
                0.90,
                6,
                3,
                List.of(new DamageEffect(25), new ApplyDebuffEffect("中毒", 2), new ApplyDebuffEffect("致盲", 1))
        );
    }

    private Item createBurningBroom() {
        return item(
                "burning_broom",
                "著火的掃把",
                ItemAffinity.MAGE,
                Set.of(ItemRole.EQUIPMENT),
                1,
                2,
                4,
                1,
                ItemTriggerType.ACTIVE,
                2.5,
                0.70,
                1,
                2,
                List.of(new DamageEffect(6), new ApplyDebuffEffect("燃燒", 1))
        );
    }

    private Item createFernWoodenStick() {
        return item(
                "fern_wooden_stick",
                "費倫的木棍",
                ItemAffinity.MAGE,
                Set.of(ItemRole.EQUIPMENT),
                1,
                3,
                8,
                2,
                ItemTriggerType.ACTIVE,
                3.0,
                0.80,
                2,
                4,
                List.of(new AddGlobalItemBuffEffect("精準", 1), new AddMaxManaEffect(2), new DamageEffect(10))
        );
    }

    private Item createBurningStaff() {
        return item(
                "burning_staff",
                "著火的法杖",
                ItemAffinity.MAGE,
                Set.of(ItemRole.EQUIPMENT),
                1,
                3,
                9,
                3,
                ItemTriggerType.ACTIVE,
                4.0,
                0.85,
                0,
                5,
                List.of(new AddMaxManaEffect(3), new DamageEffect(15), new ApplyDebuffEffect("燃燒", 1))
        );
    }

    private Item createAdvancedWand() {
        return item(
                "advanced_wand",
                "進階魔杖",
                ItemAffinity.MAGE,
                Set.of(ItemRole.COMPONENT, ItemRole.EQUIPMENT),
                1,
                3,
                16,
                4,
                ItemTriggerType.ACTIVE,
                3.5,
                0.75,
                0,
                6,
                List.of(new AddMaxHpEffect(10), new AddMaxManaEffect(5), new DamageEffect(20))
        );
    }

    private Item createDarkMageHat() {
        return item(
                "dark_mage_hat",
                "黑暗魔法師的魔法帽",
                ItemAffinity.MAGE,
                Set.of(ItemRole.EQUIPMENT),
                2,
                2,
                14,
                4,
                ItemTriggerType.ACTIVE,
                8.0,
                0.90,
                0,
                10,
                List.of(new AddMaxHpEffect(15), new DamageEffect(10), new AddGlobalItemBuffEffect("強化", 2))
        );
    }

    private Item createManaCloak() {
        return item(
                "mana_cloak",
                "魔力斗篷",
                ItemAffinity.MAGE,
                Set.of(ItemRole.EQUIPMENT),
                2,
                2,
                14,
                4,
                ItemTriggerType.ACTIVE,
                4.5,
                1.00,
                0,
                0,
                List.of(new AddMaxHpEffect(20), new AddMaxManaEffect(3), new AddManaRecoveryEffect(1.0), new ShieldEffect(10))
        );
    }

    private Item createFrierenStaff() {
        return item(
                "frieren_staff",
                "芙莉蓮的法杖",
                ItemAffinity.MAGE,
                Set.of(ItemRole.EQUIPMENT),
                1,
                3,
                22,
                5,
                ItemTriggerType.ACTIVE,
                4.5,
                0.85,
                0,
                10,
                List.of(new AddMaxHpEffect(13), new AddMaxManaEffect(5), new DamageEffect(30), new ApplyDebuffEffect("技能緩速", 2))
        );
    }

}
