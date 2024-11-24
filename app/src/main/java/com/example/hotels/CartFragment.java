package com.example.hotels;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotels.adapters.CartAdapter;
import com.example.hotels.data.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private TextView totalPriceTextView;
    private Button checkoutButton;
    private DatabaseReference cartRef;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private double totalPrice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize views
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView);
        checkoutButton = view.findViewById(R.id.checkoutButton);

        // Initialize RecyclerView and Adapter
        cartItemList = new ArrayList<>();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");

        // Initialize the CartAdapter with the list, email, and context
        cartAdapter = new CartAdapter(cartItemList, userEmail, getContext());
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartRecyclerView.setAdapter(cartAdapter);

        // Get current user email and initialize Firebase reference
        cartRef = FirebaseDatabase.getInstance().getReference("Users").child(userEmail).child("my_cart");

        // Fetch cart data from Firebase
        fetchCartData();

        return view;
    }

    private void fetchCartData() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItemList.clear();
                totalPrice = 0.0;

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    CartItem cartItem = itemSnapshot.getValue(CartItem.class);

                    if (cartItem != null) {
                        cartItemList.add(cartItem);
                        totalPrice += cartItem.getTotalPrice();
                    }
                }

                // Update RecyclerView and total price
                cartAdapter.notifyDataSetChanged();
                totalPriceTextView.setText("Total: $" + totalPrice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }
}
