package com.example.foodhunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodhunter.adapters.CartAdapter;
import com.example.foodhunter.keys.StaticData;
import com.example.foodhunter.models.Cart;
import com.example.foodhunter.models.Item;
import com.example.foodhunter.sessions.CartSessionStore;
import com.example.foodhunter.sessions.LocalSessionStore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivitiy extends AppCompatActivity implements CartAdapter.CartListener {
    private CartSessionStore cartSessionStore;
    private LocalSessionStore localSessionStore;
    private CartAdapter cartAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView cartValue;
    private Button placeOrderBtn;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartSessionStore = new CartSessionStore(this);
        cartAdapter = new CartAdapter(this,CartActivitiy.this);
        localSessionStore = new LocalSessionStore(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Placing Order");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerCart);
        cartValue = findViewById(R.id.cartValue);
        placeOrderBtn = findViewById(R.id.btnPlaceOrder);
        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CartActivity","Placing Order");
                progressDialog.show();
                //processing the cart data
                Map<Object, Object> order = new HashMap<>();
                List<Map<String,Integer>> items = new ArrayList();
                order.put("userId", localSessionStore.getData(StaticData.USER_ID));
                for(int i=0;i<cartSessionStore.getCartDetails().getItemList().size();i++)
                {
                    Map<String,Integer> eachItem = new HashMap();
                    eachItem.put(cartSessionStore.getCartDetails().getItemList().get(i).getId(),cartSessionStore.getCartDetails().getItemList().get(i).getQuantity());
                    items.add(eachItem);
                }
                order.put("items",items);
                //entering to the db
                db.collection("orders").add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Log.d("CartActivity", "Document added with id: " + documentReference.getId());
                        cartSessionStore.addToCart(new Cart());
                        Log.d("CartActivity", "Scammed");
                        startActivity(new Intent(CartActivitiy.this,WelcomeBottomActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CartActivitiy.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cartAdapter);
        Log.d("CartActivity",new Gson().toJson(cartSessionStore.getCartDetails()));
        resetCartValue();
    }

    public void resetCartValue() {
        float cartValueNum = 0;
        for(Item i : cartSessionStore.getCartDetails().getItemList())
            cartValueNum+= i.getPrice() * i.getQuantity();
        cartValue.setText("Cart Value: Rs. ₹" + String.valueOf(cartValueNum));
    }

    @Override
    public void onQuantityChange(Item item, int value) {
        Cart cart = cartSessionStore.getCartDetails();
        List<Item> cartListItems = cart.getItemList();
        for(int j = 0;j<cartListItems.size();j++) {
            Item i = cartListItems.get(j);
            if (i.getId().equals(item.getId())) {
                cartListItems.get(j).setQuantity(value);
                break;
            }
        }
        cart.setItemList(cartListItems);
        cartSessionStore.addToCart(cart);
        cartAdapter.notifyDataSetChanged();
        resetCartValue();
    }
}