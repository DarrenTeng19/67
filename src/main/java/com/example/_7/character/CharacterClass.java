package com.example._7.character;

public enum CharacterClass {
    WARRIOR(
            "戰士", // 職業
            120, // HP
            12, // 最大耐力
            3.0, // 耐力恢復
            4, // 最大魔力
            0.5 // 魔力回復速率
    ),

    RANGER(
        "遊俠",
            100,
            10,
            2.2,
            8,
            1.0
    ),
    MAGE(
            "魔法師",
            95,
            5,
            1.0,
            15,
            3.0
    );

    private final String displayName;

    private final int initialMaxHp;
    private final int initialMaxStamina;
    private final double initialStaminaRecoveryRate;
    private final int initialMaxMana;
    private final double initialManaRecoveryRate;

    CharacterClass(
            String displayName,
            int initialMaxHp,
            int initialMaxStamina,
            double initialStaminaRecoveryRate,
            int initialMaxMana,
            double initialManaRecoveryRate
    ) {
        this.displayName = displayName;
        this.initialMaxHp = initialMaxHp;
        this.initialMaxMana = initialMaxMana;
        this.initialManaRecoveryRate = initialManaRecoveryRate;
        this.initialStaminaRecoveryRate = initialStaminaRecoveryRate;
        this.initialMaxStamina = initialMaxStamina;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getInitialMaxHp() {
        return initialMaxHp;
    }

    public int getInitialMaxMana() {
        return initialMaxMana;
    }

    public int getInitialMaxStamina(){
        return initialMaxStamina;
    }

    public double getInitialStaminaRecoveryRate() {
        return initialStaminaRecoveryRate;
    }

    public double getInitialManaRecoveryRate() {
        return initialManaRecoveryRate;
    }

    public CharacterStats createInitialStats() {
        return new CharacterStats(
                initialMaxHp,
                initialMaxStamina,
                initialStaminaRecoveryRate,
                initialMaxMana,
                initialManaRecoveryRate
        );
    }
}
