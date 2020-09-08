package com.example.foodhunter.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodhunter.R;
import com.example.foodhunter.keys.StaticData;
import com.example.foodhunter.models.Cart;
import com.example.foodhunter.models.Item;
import com.example.foodhunter.sessions.CartSessionStore;
import com.example.foodhunter.ui.home.ItemDetailsActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RecyclerItemAdapter extends RecyclerView.Adapter<RecyclerItemAdapter.ItemHolder> {

    public interface CartButtonClickedListener {
        void onAddToCartClicked(Item item);
        void onRemoveCartClicked(Item item);
    }

    private Context context;
    private List<Item> itemList;
    private CartSessionStore cartSessionStore;
    private Cart cart;
    private List<Item> cartItemList ;
    private CartButtonClickedListener cartButtonClickedListener;
    public RecyclerItemAdapter(Context context, List<Item> itemList, CartButtonClickedListener cartButtonClickedListener) {
        this.context = context;
        this.itemList = itemList;
        cartSessionStore = new CartSessionStore(context);
        cartItemList = cartSessionStore.getCartDetails() != null ? cartSessionStore.getCartDetails().getItemList() : new ArrayList<Item>();
        cart = cartSessionStore.getCartDetails() != null ? cartSessionStore.getCartDetails() : new Cart();
        this.cartButtonClickedListener = cartButtonClickedListener;
    }

    @NonNull
    @Override
    public RecyclerItemAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemsView = layoutInflater.inflate(R.layout.recycler_items,parent,false);
        ItemHolder holder = new ItemHolder(itemsView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerItemAdapter.ItemHolder holder, int position) {
        /*
        * set the data into design view
        * */
        final Item item = itemList.get(position);
        final String id = item.getId();
        final String name = item.getName();
        final String category = item.getCategory();
        final boolean isAvailable = item.isAvailable();
        final int price = item.getPrice();
        final String image = item.getImage();
//        final String title = book.getBookTitle();
//        final String price = book.getBookPrice();
//        final String author = book.getBookAuthorName();
//        final String image = book.getBookImageUri();
//        final String description = book.getBookDesc();
        holder.itemName.setText(name);
        holder.itemPrice.setText("Rs. ₹" + String.valueOf(price));
        if (item.isAvailable()) {
            //Log.d("RecyclerItemAdapter",cartSessionStore.getCartDetails().toString());
            //check if already in cart or not
            holder.btnAction.setBackgroundColor(Color.parseColor("#2F9C3A"));
            holder.btnAction.setText("Add to Cart");
            holder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("RecyclerItemAdapter", "Adding to cart");
                    item.setQuantity(1);
                    cartButtonClickedListener.onAddToCartClicked(item);
//                    cartItemList.add(item);
//                    cart.setItemList(cartItemList);
//                    cartSessionStore.addToCart(cart);

                }
            });
            for(int i=0;i<cartSessionStore.getCartDetails().getItemList().size();i++)
            {
                //Log.d("HomeFragment", String.valueOf(cartSessionStore.getCartDetails().getItemList().get(i).getId().equals(item.getId())));
                if(cartSessionStore.getCartDetails().getItemList().get(i).getId().equals(item.getId()))
                {
                    //exists in cart
                    holder.btnAction.setBackgroundColor(Color.parseColor("#2F9C3A"));
                    holder.btnAction.setText("Remove From Cart");
                    holder.btnAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cartButtonClickedListener.onRemoveCartClicked(item);
                        }
                    });

                }
            }
        }
        else {
            holder.btnAction.setBackgroundColor(Color.parseColor("#FFD62020"));
            holder.btnAction.setText("Currently Unavailable");
        }
        if(item.getImage() != null) {
            Glide.with(context).load(item.getImage()).placeholder(android.R.drawable.ic_menu_rotate).into(holder.itemImage);
        }
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("ITEM_DETAILS",new Gson().toJson(item));
//                bundle.putString(StaticData.ITEM_ID,id);
//                bundle.putString(StaticData.ITEM_NAME,name);
//                bundle.putString(StaticData.ITEM_CATEGORY,category);
//                bundle.putBoolean(StaticData.ITEM_AVAILABILITY,isAvailable);
//                bundle.putInt(StaticData.ITEM_PRICE,price);
//                bundle.putString(StaticData.ITEM_IMAGE,image);
                Intent intent = new Intent(context, ItemDetailsActivity.class);
                intent.putExtra("item_data",bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemName,itemPrice;
        private Button btnAction;
        private LinearLayout itemLayout;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemLayout = itemView.findViewById(R.id.item_layout);
            itemName = itemView.findViewById(R.id.item_title);
            itemPrice = itemView.findViewById(R.id.item_price);
            btnAction = itemView.findViewById(R.id.actionBtn);
        }
    }


}
