package com.example._7.character;

import com.example._7.item.ItemCatalog;
import com.example._7.item.effect.ShieldEffect;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EffectiveStatsCalculatorTest {
    @Test
    void mageStartsWithThreeManaRecoveryPerSecond() {
        CharacterStats stats = CharacterClass.MAGE.createInitialStats();

        assertEquals(95, stats.getMaxHp());
        assertEquals(3.0, stats.getManaRecoveryRate());
    }

    @Test
    void includesBackpackItemStatBonuses() {
        CharacterStats baseStats = CharacterClass.WARRIOR.createInitialStats();
        var shield = new ItemCatalog().getById("small_round_shield");

        var result = EffectiveStatsCalculator.calculate(baseStats, List.of(shield));

        assertEquals(130, result.maxHp());
        assertEquals(12, result.maxStamina());
        assertEquals(3.0, result.staminaRecoveryRate());
        assertEquals(4, result.maxMana());
        assertEquals(0.5, result.manaRecoveryRate());
    }

    @Test
    void smallRoundShieldProvidesFourShield() {
        var shield = new ItemCatalog().getById("small_round_shield");

        int shieldAmount = shield.getEffects().stream()
                .filter(ShieldEffect.class::isInstance)
                .map(ShieldEffect.class::cast)
                .mapToInt(ShieldEffect::getAmount)
                .sum();

        assertEquals(4, shieldAmount);
    }
}
