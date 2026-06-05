package com.example._7.recipe;

import com.example._7.item.Item;

public class RecipeResult {
    private final boolean success;
    private final String message;
    private final Recipe recipe;
    private final Item resultItem;

    private RecipeResult(boolean success, String message, Recipe recipe, Item resultItem) {
        this.success = success;
        this.message = message;
        this.recipe = recipe;
        this.resultItem = resultItem;
    }

    public static RecipeResult success(Recipe recipe, Item resultItem, String ingredientAName, String ingredientBName) {
        String message = "合成成功：「" + ingredientAName + "」+「" + ingredientBName + "」→「" + resultItem.getName() + "」。";
        return new RecipeResult(true, message, recipe, resultItem);
    }

    public static RecipeResult failure(String message) {
        return new RecipeResult(false, message, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Item getResultItem() {
        return resultItem;
    }
}
