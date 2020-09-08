package com.example.foodhunter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.foodhunter.R;
import com.example.foodhunter.models.Cart;
import com.example.foodhunter.models.Item;
import com.example.foodhunter.sessions.CartSessionStore;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartItemHolder> {
    public interface CartListener {
        void onQuantityChange(Item item, int value);
    }
    private Context context;
    private CartSessionStore cartSessionStore;
    private CartListener cartListener;
    public CartAdapter(Context context, CartListener cartListener) {
        this.context = context;
        cartSessionStore = new CartSessionStore(context);
        this.cartListener = cartListener;
    }

    @NonNull
    @Override
    public CartAdapter.CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //View cartItemsView = layoutInflater.inflate(R.layout.cart_items,parent,false);
        View cartItemsView = layoutInflater.inflate(R.layout.cart_items,parent,false);
        CartItemHolder holder = new CartItemHolder(cartItemsView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartItemHolder holder, int position) {
        //final Item item = cart.getItemList().get(position);
        final Item item = cartSessionStore.getCartDetails().getItemList().get(position);
        final String id = item.getId();
        final String name = item.getName();
        final String category = item.getCategory();
        final int price = item.getPrice();
        final String image = item.getImage();
        holder.item_name.setText(name);
        holder.item_price.setText("Rs. ₹ " + price);
        holder.item_category.setText("Category: " + category);
        holder.item_quantity.setNumber(String.valueOf(item.getQuantity()));
        if(item.getImage() != null) {
            Glide.with(context).load(image).into(holder.item_image);
        }
        holder.item_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                cartListener.onQuantityChange(item,newValue);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartSessionStore.getCartDetails().getItemList().size();
    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        ImageView item_image;
        TextView item_name,item_category,item_price;
        ElegantNumberButton item_quantity;
        public CartItemHolder(@NonNull View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.imgProduct);
            item_name = itemView.findViewById(R.id.item_name_text_view);
            item_category = itemView.findViewById(R.id.item_category_text_view);
            item_price = itemView.findViewById(R.id.item_price_text_view);
            item_quantity = itemView.findViewById(R.id.item_quantity);
        }
    }
}
