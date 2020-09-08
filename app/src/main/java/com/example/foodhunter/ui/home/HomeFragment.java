package com.example.foodhunter.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhunter.R;
import com.example.foodhunter.adapters.RecyclerItemAdapter;
import com.example.foodhunter.keys.StaticData;
import com.example.foodhunter.models.Cart;
import com.example.foodhunter.models.Item;
import com.example.foodhunter.sessions.CartSessionStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements RecyclerItemAdapter.CartButtonClickedListener {

    private HomeViewModel homeViewModel;
    RecyclerItemAdapter recyclerItemAdapter;
    private RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    FirebaseFirestore db;
    CartSessionStore cartSessionStore;
    private List<Item> itemList = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        db = FirebaseFirestore.getInstance();
        cartSessionStore = new CartSessionStore(getContext());
        fetchItems();
        return root;
    }
    private void fetchItems() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Fetching data..");
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        db.collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        Map<String,Object> map = document.getData();
                        Item item = new Item();
                        item.setId(document.getId());
                        item.setName(map.get(StaticData.ITEM_NAME).toString());
                        item.setAvailable((boolean)map.get(StaticData.ITEM_AVAILABILITY));
                        item.setCategory(map.get(StaticData.ITEM_CATEGORY).toString());
                        item.setPrice(Integer.parseInt(map.get(StaticData.ITEM_PRICE).toString()));
                        item.setImage(map.get(StaticData.ITEM_IMAGE).toString());
                        //Log.d("HomeFragment",item.getName() + item.getCategory() + item.getPrice() + item.isAvailable());
                        itemList.add(item);
                        recyclerItemAdapter = new RecyclerItemAdapter(getContext(),itemList, HomeFragment.this);
                        recyclerView.setAdapter(recyclerItemAdapter);
                    }
                }
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        if(recyclerItemAdapter != null)
            recyclerItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAddToCartClicked(Item item) {
        Cart cart  = cartSessionStore.getCartDetails();
        List<Item> cartList = cart.getItemList();
        cartList.add(item);
        cart.setItemList(cartList);
        cartSessionStore.addToCart(cart);
        recyclerItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRemoveCartClicked(Item item) {
        Cart cart = cartSessionStore.getCartDetails();
        List<Item> cartList = cart.getItemList();
        for(Item i : cartList) {
            if (i.getId().equals(item.getId())) {
                cartList.remove(i);
                break;
            }
        }
        cart.setItemList(cartList);
        cartSessionStore.addToCart(cart);
        recyclerItemAdapter.notifyDataSetChanged();
    }
}