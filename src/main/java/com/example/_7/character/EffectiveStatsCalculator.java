package com.example._7.character;

import com.example._7.item.Item;
import com.example._7.item.effect.AddManaRecoveryEffect;
import com.example._7.item.effect.AddMaxHpEffect;
import com.example._7.item.effect.AddMaxManaEffect;
import com.example._7.item.effect.AddMaxStaminaEffect;
import com.example._7.item.effect.AddStaminaRecoveryEffect;
import com.example._7.item.effect.ItemEffect;

import java.util.List;

public final class EffectiveStatsCalculator {
    private EffectiveStatsCalculator() {
    }

    public static EffectiveStats calculate(CharacterStats baseStats, List<Item> equippedItems) {
        if (baseStats == null) {
            throw new IllegalArgumentException("baseStats 不能是 null");
        }

        int maxHp = baseStats.getMaxHp();
        int maxStamina = baseStats.getMaxStamina();
        int maxMana = baseStats.getMaxMana();
        double staminaRecovery = baseStats.getStaminaRecoveryRate();
        double manaRecovery = baseStats.getManaRecoveryRate();

        if (equippedItems != null) {
            for (Item item : equippedItems) {
                if (item == null) continue;

                for (ItemEffect effect : item.getEffects()) {
                    if (effect instanceof AddMaxHpEffect addMaxHp) {
                        maxHp += addMaxHp.getAmount();
                    } else if (effect instanceof AddMaxStaminaEffect addMaxStamina) {
                        maxStamina += addMaxStamina.getAmount();
                    } else if (effect instanceof AddMaxManaEffect addMaxMana) {
                        maxMana += addMaxMana.getAmount();
                    } else if (effect instanceof AddStaminaRecoveryEffect addStaminaRecovery) {
                        staminaRecovery += addStaminaRecovery.getAmountPerSecond();
                    } else if (effect instanceof AddManaRecoveryEffect addManaRecovery) {
                        manaRecovery += addManaRecovery.getAmountPerSecond();
                    }
                }
            }
        }

        return new EffectiveStats(maxHp, maxStamina, staminaRecovery, maxMana, manaRecovery);
    }

    public record EffectiveStats(
            int maxHp,
            int maxStamina,
            double staminaRecoveryRate,
            int maxMana,
            double manaRecoveryRate
    ) {
    }
}
