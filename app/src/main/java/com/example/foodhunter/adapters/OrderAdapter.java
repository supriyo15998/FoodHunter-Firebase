package com.example.foodhunter.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhunter.R;
import com.example.foodhunter.keys.StaticData;
import com.example.foodhunter.models.Order;
import com.example.foodhunter.sessions.LocalSessionStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderItemHolder> {
    private Context context;
    private List<Order> orderList;
    private LocalSessionStore localSessionStore;
    private String userAddress;
    @NonNull
    @Override
    public OrderAdapter.OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View ordersView = layoutInflater.inflate(R.layout.recycler_orders,parent,false);
        OrderItemHolder holder = new OrderItemHolder(ordersView);
        return holder;
    }

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        localSessionStore = new LocalSessionStore(context);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemHolder holder, int position) {
        int totalAmt = 0;

        final Order order = orderList.get(position);
        order.setUserId(localSessionStore.getData(StaticData.USER_ID));
        holder.orderAddress.setText("Delivered To: " + "\n" + localSessionStore.getData(StaticData.USER_ADDRESS));
        for(int i=0;i<order.getItems().size();i++) {
            totalAmt += order.getItems().get(i).getQuantity() * order.getItems().get(i).getPrice();
            holder.itemsName.append(order.getItems().get(i).getName() + " x " + order.getItems().get(i).getQuantity() + "\n");
        }
        holder.itemsPrice.setText("Total Amount: Rs. ₹ " + totalAmt);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderItemHolder extends RecyclerView.ViewHolder {
        TextView itemsName, itemsPrice, orderAddress;
        public OrderItemHolder(@NonNull View itemView) {
            super(itemView);
            itemsName = itemView.findViewById(R.id.order_items_name);
            itemsPrice = itemView.findViewById(R.id.order_amount);
            orderAddress = itemView.findViewById(R.id.order_address);
        }
    }
}
