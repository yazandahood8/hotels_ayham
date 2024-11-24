// CartAdapter.java
package com.example.hotels.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotels.R;
import com.example.hotels.data.CartItem;
import com.example.hotels.data.Hotel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private String currentUserEmail;
    private Context context;

    public CartAdapter(List<CartItem> cartItemList, String currentUserEmail, Context context) {
        this.cartItemList = cartItemList;
        this.currentUserEmail = currentUserEmail; // Replace "." in email for Firebase
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        // Load hotel image
        Glide.with(context)
                .load(cartItem.getHotel().getImageUrl()) // Replace with actual image URL field
                .placeholder(R.drawable.ic_launcher_background) // Placeholder image in case of loading delays
                .into(holder.itemImageView);

        holder.itemNameTextView.setText(cartItem.getHotel().getName());
        holder.itemDetailsTextView.setText(cartItem.getRoomType() + " | " + cartItem.getNumberOfDays() + " Days");
        holder.itemPriceTextView.setText("$" + cartItem.getTotalPrice());

        // Edit room number listener
        holder.editRoomButton.setOnClickListener(v -> {
            // Handle editing the room number (e.g., show a dialog or edit screen)
            editCart(cartItem);
        });

        // Delete item listener
        holder.deleteItemButton.setOnClickListener(v -> {
           // currentUserEmail = currentUserEmail.replace(".", "_"); // Replace "." in email for Firebase

            // Show confirmation dialog
            Toast.makeText(context, cartItem.getId()+" "+currentUserEmail, Toast.LENGTH_SHORT).show();

            new AlertDialog.Builder(context)

                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item from your cart?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Handle deletion if the user confirms
                        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("Users")
                                .child(currentUserEmail).child("my_cart").child(cartItem.getId());
                        itemRef.removeValue().addOnSuccessListener(aVoid -> {
                            // Successfully deleted from Firebase
                            try{
                                cartItemList.remove(position);
                                notifyItemRemoved(position); // Refresh the RecyclerView
                            }catch (Exception ex){}

                            Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Close the dialog if the user cancels
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    // Function to edit the room number (to be implemented based on your UI needs)
    private void editCart(CartItem cartItem) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart, null);
            builder.setView(dialogView);

            EditText inputDays = dialogView.findViewById(R.id.inputDays);
            Spinner roomTypeSpinner = dialogView.findViewById(R.id.roomTypeSpinner);
            TextView totalPriceTextView = dialogView.findViewById(R.id.totalPrice);
            Button confirmButton = dialogView.findViewById(R.id.confirmButton);
            try{
                inputDays.setText(cartItem.getNumberOfDays()+"");

            }catch (Exception ex){}
        totalPriceTextView.setText("Total Price: $" + cartItem.getTotalPrice());
        ArrayAdapter<String> roomTypeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, cartItem.getHotel().getRoomTypes());
        roomTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomTypeSpinner.setAdapter(roomTypeAdapter);

// Set the default selected value based on cartItem's current room type
        String currentRoomType = cartItem.getRoomType();
        if (currentRoomType != null) {
            int spinnerPosition = roomTypeAdapter.getPosition(currentRoomType);
            if (spinnerPosition >= 0) {
                roomTypeSpinner.setSelection(spinnerPosition); // Set the default selection
            }
        }
            AlertDialog dialog = builder.create();
            dialog.show();

            inputDays.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    String daysText = s.toString();
                    if (!daysText.isEmpty()) {
                        int days = Integer.parseInt(daysText);

                        double totalPrice = cartItem.getHotel().getBaseRoomPrice() * days;
                        totalPriceTextView.setText("Total Price: $" + totalPrice);
                    }
                }
            });

        confirmButton.setOnClickListener(v -> {
            if(inputDays.getText().length() > 0) {
                String roomType = roomTypeSpinner.getSelectedItem().toString();
                int days = Integer.parseInt(inputDays.getText().toString());
                double totalPrice = cartItem.getHotel().getBaseRoomPrice() * days;
                if(days>0){
                CartItem c = new CartItem(cartItem.getHotel(), days, roomType, totalPrice);
                c.setId(cartItem.getId());
                saveToCart(c);
                dialog.dismiss();
                Toast.makeText(context, cartItem.getHotel().getName() + " updated  cart", Toast.LENGTH_SHORT).show();
            }
            }

        });
    }
    private void saveToCart(CartItem cartItem) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (userEmail != null) {
            String sanitizedEmail = userEmail.replace(".", "_");
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(sanitizedEmail).child("my_cart").child(cartItem.getId());
            cartRef.setValue(cartItem)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show();

                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show());
        }
    }


    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameTextView;
        TextView itemDetailsTextView;
        TextView itemPriceTextView;
        Button editRoomButton;
        Button deleteItemButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemDetailsTextView = itemView.findViewById(R.id.itemDetailsTextView);
            itemPriceTextView = itemView.findViewById(R.id.itemPriceTextView);
            editRoomButton = itemView.findViewById(R.id.editRoomButton);
            deleteItemButton = itemView.findViewById(R.id.deleteItemButton);
        }
    }
}
