package com.example._7.recipe;

import com.example._7.character.Player;
import com.example._7.inventory.Backpack;
import com.example._7.inventory.GridPosition;
import com.example._7.inventory.PlacedItem;
import com.example._7.inventory.Storage;
import com.example._7.item.Item;
import com.example._7.item.ItemCatalog;
import com.example._7.item.ItemRole;
import com.example._7.item.shape.GridOffset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 第一版合成服務：
 * 1. 掃描玩家的儲物箱與背包。
 * 2. 找到第一組符合配方的兩個道具。
 * 3. 移除材料。
 * 4. 將合成結果放回儲物箱。
 */
public class RecipeService {
    private final RecipeCatalog recipeCatalog;
    private final ItemCatalog itemCatalog;

    public RecipeService(RecipeCatalog recipeCatalog, ItemCatalog itemCatalog) {
        this.recipeCatalog = recipeCatalog;
        this.itemCatalog = itemCatalog;
    }

    public RecipeResult autoCraftFirst(Player player) {
        if (player == null) {
            return RecipeResult.failure("找不到玩家，無法合成。");
        }

        Storage storage = player.getStorage();
        Backpack backpack = player.getBackpack();

        List<CraftCandidate> candidates = collectCandidates(storage, backpack);
        if (candidates.size() < 2) {
            return RecipeResult.failure("可合成材料不足。需要至少兩個可作為組件的道具。");
        }

        for (int i = 0; i < candidates.size(); i++) {
            for (int j = i + 1; j < candidates.size(); j++) {
                CraftCandidate first = candidates.get(i);
                CraftCandidate second = candidates.get(j);

                Optional<Recipe> recipeOptional = recipeCatalog.findRecipe(
                        first.item.getId(),
                        second.item.getId()
                );

                if (recipeOptional.isEmpty()) {
                    continue;
                }

                Recipe recipe = recipeOptional.get();
                if (recipe.isRequireAdjacent() && !areAdjacent(first, second)) {
                    continue;
                }

                Item resultItem = itemCatalog.getById(recipe.getResultItemId());

                if (!removeCandidate(storage, backpack, first)) {
                    return RecipeResult.failure("合成失敗：無法移除第一個材料。");
                }
                if (!removeCandidate(storage, backpack, second)) {
                    // 理論上很少發生。若發生，至少把第一個材料放回儲物箱，避免材料直接消失。
                    storage.addItem(first.item);
                    return RecipeResult.failure("合成失敗：無法移除第二個材料。");
                }

                storage.addItem(resultItem);
                return RecipeResult.success(recipe, resultItem, first.item.getName(), second.item.getName());
            }
        }

        return RecipeResult.failure("目前沒有找到可以合成的配方。");
    }

    private List<CraftCandidate> collectCandidates(Storage storage, Backpack backpack) {
        List<CraftCandidate> candidates = new ArrayList<>();

        if (storage != null) {
            for (Item item : storage.getItems()) {
                if (isCraftMaterial(item)) {
                    candidates.add(CraftCandidate.fromStorage(item));
                }
            }
        }

        if (backpack != null) {
            for (PlacedItem placedItem : backpack.getPlacedItems()) {
                if (placedItem != null && isCraftMaterial(placedItem.getItem())) {
                    candidates.add(CraftCandidate.fromBackpack(placedItem));
                }
            }
        }

        return candidates;
    }

    private boolean isCraftMaterial(Item item) {
        return item != null && item.getRoles().contains(ItemRole.COMPONENT);
    }

    private boolean removeCandidate(Storage storage, Backpack backpack, CraftCandidate candidate) {
        if (candidate == null) {
            return false;
        }

        if (candidate.isFromBackpack()) {
            return backpack != null && backpack.removeItem(candidate.placedItem);
        }

        return storage != null && storage.removeItem(candidate.item);
    }

    private boolean areAdjacent(CraftCandidate first, CraftCandidate second) {
        if (!first.isFromBackpack() || !second.isFromBackpack()) {
            return false;
        }

        Set<GridPosition> firstCells = getOccupiedCells(first.placedItem);
        Set<GridPosition> secondCells = getOccupiedCells(second.placedItem);

        for (GridPosition a : firstCells) {
            for (GridPosition b : secondCells) {
                int rowDiff = Math.abs(a.row() - b.row());
                int colDiff = Math.abs(a.col() - b.col());
                if (rowDiff + colDiff == 1) {
                    return true;
                }
            }
        }

        return false;
    }

    private Set<GridPosition> getOccupiedCells(PlacedItem placedItem) {
        Set<GridPosition> cells = new HashSet<>();
        if (placedItem == null) {
            return cells;
        }

        GridPosition base = placedItem.getPosition();
        for (GridOffset offset : placedItem.getCurrentShape().occupiedCells()) {
            cells.add(new GridPosition(base.row() + offset.row(), base.col() + offset.col()));
        }
        return cells;
    }

    private static class CraftCandidate {
        private final Item item;
        private final PlacedItem placedItem;

        private CraftCandidate(Item item, PlacedItem placedItem) {
            this.item = item;
            this.placedItem = placedItem;
        }

        static CraftCandidate fromStorage(Item item) {
            return new CraftCandidate(item, null);
        }

        static CraftCandidate fromBackpack(PlacedItem placedItem) {
            return new CraftCandidate(placedItem.getItem(), placedItem);
        }

        boolean isFromBackpack() {
            return placedItem != null;
        }
    }
}
