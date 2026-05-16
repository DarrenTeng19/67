package com.example._7.shop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shop {
    private final List<ShopOffer> offers;

    public Shop(List<ShopOffer> offers) {
        this.offers = new ArrayList<>(offers);
    }

    public List<ShopOffer> getOffers() {
        return Collections.unmodifiableList(offers);
    }
}
