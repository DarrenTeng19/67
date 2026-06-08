package com.example._7.item;

import com.example._7.item.effect.AddBuffEffect;
import com.example._7.item.effect.AddMaxHpEffect;
import com.example._7.item.effect.AddMaxStaminaEffect;
import com.example._7.item.effect.AddStaminaRecoveryEffect;
import com.example._7.item.effect.DamageEffect;
import com.example._7.item.effect.ItemEffect;
import com.example._7.item.effect.ShieldEffect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WarriorItemBalanceTest {
    private final ItemCatalog itemCatalog = new ItemCatalog();

    @Test
    void highTierWarriorItemsUseNerfedValues() {
        Item armor = itemCatalog.getById("thorn_armor");
        Item sword = itemCatalog.getById("hero_himmel_greatsword");

        assertEquals(30, amount(armor, AddMaxHpEffect.class));
        assertEquals(17, amount(armor, ShieldEffect.class));
        assertEquals(12, amount(sword, DamageEffect.class));
        assertEquals(4, amount(sword, AddMaxStaminaEffect.class));
    }

    @Test
    void warriorWeaponDamageUsesLatestNerfedValues() {
        assertEquals(4, amount(itemCatalog.getById("wooden_sword"), DamageEffect.class));
        assertEquals(7, amount(itemCatalog.getById("hammer"), DamageEffect.class));
        assertEquals(7, amount(itemCatalog.getById("iron_sword"), DamageEffect.class));
        assertEquals(15, amount(itemCatalog.getById("big_hammer"), DamageEffect.class));
        assertEquals(12, amount(itemCatalog.getById("hero_himmel_greatsword"), DamageEffect.class));
    }

    @Test
    void nerfKeepsSpecialEffectTypes() {
        Item armor = itemCatalog.getById("thorn_armor");
        Item sword = itemCatalog.getById("hero_himmel_greatsword");

        assertTrue(hasEffect(armor, AddMaxHpEffect.class));
        assertTrue(hasEffect(armor, AddStaminaRecoveryEffect.class));
        assertTrue(hasEffect(armor, ShieldEffect.class));
        assertTrue(hasEffect(armor, AddBuffEffect.class));

        assertTrue(hasEffect(sword, AddMaxStaminaEffect.class));
        assertTrue(hasEffect(sword, AddStaminaRecoveryEffect.class));
        assertTrue(hasEffect(sword, DamageEffect.class));
        assertTrue(hasEffect(sword, AddBuffEffect.class));
    }

    private boolean hasEffect(Item item, Class<? extends ItemEffect> effectType) {
        return item.getEffects().stream().anyMatch(effectType::isInstance);
    }

    private int amount(Item item, Class<? extends ItemEffect> effectType) {
        return item.getEffects().stream()
                .filter(effectType::isInstance)
                .mapToInt(effect -> effectAmount(effectType.cast(effect)))
                .sum();
    }

    private int effectAmount(ItemEffect effect) {
        if (effect instanceof AddMaxHpEffect value) return value.getAmount();
        if (effect instanceof AddMaxStaminaEffect value) return value.getAmount();
        if (effect instanceof DamageEffect value) return value.getAmount();
        if (effect instanceof ShieldEffect value) return value.getAmount();
        throw new IllegalArgumentException("Unsupported numeric effect: " + effect.getClass().getSimpleName());
    }
}
