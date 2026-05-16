package com.example._7.inventory;

import com.example._7.item.Item;
import com.example._7.item.shape.ItemShape;

public class PlacedItem {
    private final Item item;
    private GridPosition position;
    private Rotation rotation;

    public PlacedItem(Item item, GridPosition position) {
        this.item = item;
        this.position = position;
        this.rotation = Rotation.DEGREE_0;
    }

    public Item getItem(){
        return item;
    }

    public GridPosition getPosition() {
        return position;
    }

    public void RotateClockwise() {
        this.rotation = rotation.nextClockwise();
    }

    public ItemShape getCurrentShape() {
        return item.getShape().rotated(rotation);
    }
}
