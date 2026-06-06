package com.example._7.inventory;

import com.example._7.item.Item;
import com.example._7.item.ItemCatalog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackpackOccupiedCellCountTest {

    @Test
    void countsOccupiedCellsInsteadOfNumberOfItems() {
        Backpack backpack = new Backpack(5, 5);
        Item oneByTwoItem = new ItemCatalog().getById("banana");

        assertTrue(backpack.tryPlaceItem(
                new PlacedItem(oneByTwoItem, new GridPosition(0, 0))
        ));

        assertEquals(2, backpack.getOccupiedCellCount());
    }
}
