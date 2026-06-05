package com.example._7.recipe;

import java.util.Objects;

/**
 * 兩個道具合成一個道具的配方。
 * 第一版先採用「不要求相鄰」的合成規則。
 */
public class Recipe {
    private final String id;
    private final String ingredientAId;
    private final String ingredientBId;
    private final String resultItemId;
    private final boolean requireAdjacent;

    public Recipe(
            String id,
            String ingredientAId,
            String ingredientBId,
            String resultItemId,
            boolean requireAdjacent
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Recipe id must not be blank.");
        }
        if (ingredientAId == null || ingredientAId.isBlank()) {
            throw new IllegalArgumentException("Ingredient A id must not be blank.");
        }
        if (ingredientBId == null || ingredientBId.isBlank()) {
            throw new IllegalArgumentException("Ingredient B id must not be blank.");
        }
        if (resultItemId == null || resultItemId.isBlank()) {
            throw new IllegalArgumentException("Result item id must not be blank.");
        }

        this.id = id;
        this.ingredientAId = ingredientAId;
        this.ingredientBId = ingredientBId;
        this.resultItemId = resultItemId;
        this.requireAdjacent = requireAdjacent;
    }

    public String getId() {
        return id;
    }

    public String getIngredientAId() {
        return ingredientAId;
    }

    public String getIngredientBId() {
        return ingredientBId;
    }

    public String getResultItemId() {
        return resultItemId;
    }

    public boolean isRequireAdjacent() {
        return requireAdjacent;
    }

    /**
     * 配方不分順序：A+B 與 B+A 都可以合成。
     */
    public boolean matches(String itemAId, String itemBId) {
        return (Objects.equals(ingredientAId, itemAId) && Objects.equals(ingredientBId, itemBId))
                || (Objects.equals(ingredientAId, itemBId) && Objects.equals(ingredientBId, itemAId));
    }
}
