package com.example.foodhunter.models;

import java.util.List;
import java.util.Map;

public class Order {
    private String userId;
    private List<Item> items;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
