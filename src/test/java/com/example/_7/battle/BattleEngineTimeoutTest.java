package com.example._7.battle;

import com.example._7.character.CharacterClass;
import com.example._7.character.CharacterStats;
import com.example._7.character.Enemy;
import com.example._7.character.Player;
import com.example._7.inventory.Backpack;
import com.example._7.inventory.Storage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattleEngineTimeoutTest {

    @Test
    void defeatsPlayerWhenBattleReachesTwoMinutes() {
        Player player = createPlayer();
        Enemy enemy = createEnemy();
        BattleEngine engine = new BattleEngine(player, enemy);
        engine.startBattle();

        engine.update(BattleEngine.BATTLE_TIME_LIMIT_SECONDS);

        assertTrue(engine.isBattleEnded());
        assertTrue(engine.hasTimedOut());
        assertTrue(player.isDead());
        assertFalse(enemy.isDead());
        assertSame(enemy, engine.getWinner());
        assertEquals(0.0, engine.getRemainingBattleSeconds());
    }

    @Test
    void doesNotTimeoutBeforeTwoMinutes() {
        Player player = createPlayer();
        Enemy enemy = createEnemy();
        BattleEngine engine = new BattleEngine(player, enemy);
        engine.startBattle();

        engine.update(BattleEngine.BATTLE_TIME_LIMIT_SECONDS - 0.01);

        assertFalse(engine.isBattleEnded());
        assertFalse(engine.hasTimedOut());
        assertFalse(player.isDead());
        assertEquals(0.01, engine.getRemainingBattleSeconds(), 0.0001);
    }

    @Test
    void resetsTimerWhenBattleStartsAgain() {
        BattleEngine engine = new BattleEngine(createPlayer(), createEnemy());
        engine.startBattle();
        engine.update(30.0);

        engine.startBattle();

        assertEquals(0.0, engine.getElapsedBattleSeconds());
        assertEquals(BattleEngine.BATTLE_TIME_LIMIT_SECONDS, engine.getRemainingBattleSeconds());
        assertFalse(engine.hasTimedOut());
    }

    private Player createPlayer() {
        return new Player(
                "Player",
                CharacterClass.WARRIOR,
                0,
                createStats(),
                new Backpack(),
                new Storage()
        );
    }

    private Enemy createEnemy() {
        return new Enemy(
                "Enemy",
                createStats(),
                new BattleState(),
                new Backpack()
        );
    }

    private CharacterStats createStats() {
        return new CharacterStats(100, 10, 0, 10, 0);
    }
}
