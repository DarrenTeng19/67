package com.example._7.ui.util;

import com.example._7.item.Item;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class ItemImageCache {
    private static final int MAX_IMAGE_SIZE = 320;
    private static final Map<String, Image> CACHE = new HashMap<>();
    private static final Map<String, Boolean> MISSING = new HashMap<>();

    private ItemImageCache() {
    }

    public static Image get(Item item) {
        if (item == null || item.getId() == null) {
            return null;
        }

        String id = item.getId();
        if (CACHE.containsKey(id)) {
            return CACHE.get(id);
        }
        if (MISSING.containsKey(id)) {
            return null;
        }

        Image image = load(id);
        if (image == null) {
            MISSING.put(id, Boolean.TRUE);
        } else {
            CACHE.put(id, image);
        }
        return image;
    }

    private static Image load(String itemId) {
        String base = "/com/example/_7/images/items/" + itemId;
        for (String extension : new String[]{".png", ".jpg", ".jpeg"}) {
            URL resource = ItemImageCache.class.getResource(base + extension);
            if (resource != null) {
                return new Image(
                        resource.toExternalForm(),
                        MAX_IMAGE_SIZE,
                        MAX_IMAGE_SIZE,
                        true,
                        true,
                        false
                );
            }
        }
        return null;
    }
}
