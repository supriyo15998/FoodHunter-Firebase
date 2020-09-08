package com.example.foodhunter.sessions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.foodhunter.keys.StaticData;
import com.example.foodhunter.models.Cart;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class CartSessionStore {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public CartSessionStore(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public void addToCart(Cart cart) {
        editor = preferences.edit();
        String cartString = new Gson().toJson(cart);
        editor.putString(StaticData.CART_KEY,cartString);
        editor.commit();
        Log.d("CartSessionStore", new Gson().toJson(getCartDetails()));
    }
    public Cart getCartDetails() {
        String cartString = preferences.getString(StaticData.CART_KEY, new Gson().toJson(new Cart()));
        Type type = new TypeToken<Cart>(){}.getType();
        Cart cart = new Gson().fromJson(cartString,type);
        return cart;
    }
    public void clearCart() {
        editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
