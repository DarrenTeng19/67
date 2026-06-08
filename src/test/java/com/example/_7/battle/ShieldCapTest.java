package com.example._7.battle;

import com.example._7.character.CharacterClass;
import com.example._7.character.Enemy;
import com.example._7.character.Player;
import com.example._7.inventory.Backpack;
import com.example._7.inventory.Storage;
import com.example._7.item.Item;
import com.example._7.item.ItemCatalog;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShieldCapTest {
    @Test
    void repeatedShieldTriggersCannotExceedQuarterOfEffectiveMaxHp() {
        ItemCatalog itemCatalog = new ItemCatalog();
        Item shield = itemCatalog.getById("steel_shield");
        Player player = new Player(
                "Player",
                CharacterClass.WARRIOR,
                0,
                CharacterClass.WARRIOR.createInitialStats(),
                new Backpack(),
                new Storage()
        );
        Enemy enemy = new Enemy(
                "Enemy",
                CharacterClass.WARRIOR.createInitialStats(),
                new BattleState(),
                new Backpack()
        );
        BattleEngine engine = new BattleEngine(player, enemy, List.of(shield), List.of());

        engine.startBattle();
        for (int i = 0; i < 20; i++) {
            engine.update(3.0);
        }

        // Steel shield raises effective max HP from 120 to 132.
        assertEquals(33, player.getBattleState().getCurrentShield());
    }
}
