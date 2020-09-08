package com.example.foodhunter.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Item> itemList = new ArrayList<>();

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
