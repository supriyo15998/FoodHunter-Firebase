package com.example.foodhunter.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodhunter.R;
import com.example.foodhunter.adapters.RecyclerItemAdapter;
import com.example.foodhunter.keys.StaticData;
import com.example.foodhunter.models.Cart;
import com.example.foodhunter.models.Item;
import com.example.foodhunter.sessions.CartSessionStore;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ItemDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ItemDetailsActivity";
    private TextView itemName,itemCategory,itemPrice;
    private ImageView img;
    private Button btnAvailability;
    private CartSessionStore cartSessionStore;
    private RecyclerItemAdapter.CartButtonClickedListener cartButtonClickedListener;
    boolean inCart = false;
    private Item item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        cartSessionStore = new CartSessionStore(this);
        cartButtonClickedListener = new RecyclerItemAdapter.CartButtonClickedListener() {
            @Override
            public void onAddToCartClicked(Item item) {
                Cart cart = cartSessionStore.getCartDetails();
                List<Item> cartList = cart.getItemList();
                cartList.add(item);
                cart.setItemList(cartList);
                cartSessionStore.addToCart(cart);
                inCart = true;
                paintView();
            }

            @Override
            public void onRemoveCartClicked(Item item) {
                Cart cart = cartSessionStore.getCartDetails();
                List<Item> cartList = cart.getItemList();
                for(Item i:cartList) {
                    if(i.getId().equals(item.getId())) {
                        cartList.remove(i);
                        break;
                    }
                }
                cart.setItemList(cartList);
                cartSessionStore.addToCart(cart);
                inCart = false;
                paintView();
            }
        };
        itemName = findViewById(R.id.itemName);
        itemPrice = findViewById(R.id.itemPrice);
        itemCategory = findViewById(R.id.itemCategory);
        btnAvailability = findViewById(R.id.btnAvailability);
        img = findViewById(R.id.itemImage);
        Bundle bundle = getIntent().getBundleExtra("item_data");
        String item_details = bundle.getString("ITEM_DETAILS");
        Type type = new TypeToken<Map<String,String>>(){}.getType();
        Map<String,String> itemMap = new Gson().fromJson(item_details,type);
        item = new Item();
        item.setId(itemMap.get("id"));
        item.setName(itemMap.get("name"));
        item.setPrice(Integer.parseInt(itemMap.get("price")));
        item.setImage(itemMap.get("image"));
        item.setCategory("category");
        if(itemMap.get("isAvailable").equals("true"))
            item.setAvailable(true);
        else
            item.setAvailable(false);
        Log.d(TAG, item.getName());
        //Log.d(TAG,new Gson().toJson(bundle.getString("ITEM_DETAILS")));
        paintView();
    }

    private void paintView() {
        itemName.setText(item.getName());
        itemCategory.setText("Category: " + item.getCategory());
        itemPrice.setText("Price: " + String.valueOf(item.getPrice()));
        Glide.with(this).load(item.getImage()).centerCrop().into(img);
        if (item.isAvailable()) {
            //check if present in cart or not
            for(int i=0;i<cartSessionStore.getCartDetails().getItemList().size();i++)
            {
                if(item.getId().equals(cartSessionStore.getCartDetails().getItemList().get(i).getId()))
                {
                    inCart = true;
                    break;
                }
            }
            if(inCart) {
                btnAvailability.setText("Remove from Cart");
                btnAvailability.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG,"removing from cart");
                        cartButtonClickedListener.onRemoveCartClicked(item);
                    }
                });
            }else {
                btnAvailability.setText("Add to Cart");
                item.setQuantity(1);
                btnAvailability.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG,"adding to cart");
                        cartButtonClickedListener.onAddToCartClicked(item);
                    }
                });
            }
            btnAvailability.setBackgroundColor(Color.parseColor("#2F9C3A"));
        }else {
            btnAvailability.setText("Currently Unavailable");
            btnAvailability.setBackgroundColor(Color.parseColor("#FFD62020"));
        }
    }
}