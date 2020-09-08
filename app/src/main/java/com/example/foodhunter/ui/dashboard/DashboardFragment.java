package com.example.foodhunter.ui.dashboard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhunter.R;
import com.example.foodhunter.adapters.OrderAdapter;
import com.example.foodhunter.keys.StaticData;
import com.example.foodhunter.models.Item;
import com.example.foodhunter.models.Order;
import com.example.foodhunter.models.User;
import com.example.foodhunter.sessions.LocalSessionStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private RecyclerView myOrdersRecyclerView;
    FirebaseFirestore db;
    LinearLayoutManager linearLayoutManager;
    OrderAdapter orderAdapter;
    ProgressDialog progressDialog;
    private List<Order> orderList;
    private LocalSessionStore localSessionStore;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        myOrdersRecyclerView = root.findViewById(R.id.myOrdersRecyclerView);
        orderList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        myOrdersRecyclerView.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getContext());
        localSessionStore = new LocalSessionStore(getContext());
        orderAdapter = new OrderAdapter(getContext(),orderList);
        myOrdersRecyclerView.setAdapter(orderAdapter);
        progressDialog.setTitle("Fetching orders...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        fetchMyOrders();
        Log.d("DashboardFragment", new Gson().toJson(orderList));
        return root;
    }

    private void fetchMyOrders() {
        progressDialog.show();
        db.collection("orders").whereEqualTo("userId", localSessionStore.getData(StaticData.USER_ID)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().size() > 0) {
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            final Order order = new Order();
                            final List<Item> orderItems = new ArrayList<>();
                            order.setUserId(localSessionStore.getData(StaticData.USER_ID));
                            for (final HashMap<String, Integer> o : (ArrayList<HashMap>) document.getData().get("items")) {
                                for (final String key : o.keySet()) {
                                    db.collection("items").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot doc = task.getResult();
                                                if (doc.exists()) {
                                                    Item i = new Item();
                                                    i.setName(doc.getData().get("name").toString());
                                                    i.setImage(doc.getData().get("image").toString());
                                                    i.setPrice(Integer.parseInt(doc.getData().get("price").toString()));
                                                    i.setId(key);
                                                    i.setQuantity(Integer.parseInt(String.valueOf(o.get(key))));
                                                    orderItems.add(i);
                                                } else {
                                                    Log.d("DashboardFragment", "No such document");
                                                }
                                            } else {
                                                Log.d("DashboardFragment", "Failed");
                                            }
                                        }
                                    });
                                }
                            }
                            Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    order.setItems(orderItems);
                                    orderList.add(order);
                                    Log.d("DashboardFragment", new Gson().toJson(order));
                                    Log.d("DashboardFragment", localSessionStore.getData(StaticData.USER_ADDRESS));
                                    orderAdapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                }
                            }, 2000);

                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "You have no orders!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}