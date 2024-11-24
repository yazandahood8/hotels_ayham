package com.example.hotels.adapters;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hotels.R;
import com.example.hotels.data.CartItem;
import com.example.hotels.data.Hotel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class HotelWithCartAdapter extends RecyclerView.Adapter<HotelWithCartAdapter.HotelViewHolder> {

    private final Context context;
    private final List<Hotel> hotelList;
    private static final int NOTIFICATION_PERMISSION_CODE = 100;

    public HotelWithCartAdapter(Context context, List<Hotel> hotelList) {
        this.context = context;
        this.hotelList = hotelList;
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView hotelImage;
        TextView hotelName;
        TextView hotelLocation;
        Button addToCartButton;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelImage = itemView.findViewById(R.id.hotelImage);
            hotelName = itemView.findViewById(R.id.hotelName);
            hotelLocation = itemView.findViewById(R.id.hotelLocation);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hotel_with_cart, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);

        holder.hotelName.setText(hotel.getName());
        holder.hotelLocation.setText(hotel.getCity() + ", " + hotel.getCountry());

        Glide.with(context)
                .load(hotel.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .transform(new CircleCrop())
                .into(holder.hotelImage);

        holder.addToCartButton.setOnClickListener(v -> showAddToCartDialog(hotel));
    }

    private void showAddToCartDialog(Hotel hotel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart, null);
        builder.setView(dialogView);

        EditText inputDays = dialogView.findViewById(R.id.inputDays);
        Spinner roomTypeSpinner = dialogView.findViewById(R.id.roomTypeSpinner);
        TextView totalPriceTextView = dialogView.findViewById(R.id.totalPrice);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        roomTypeSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, hotel.getRoomTypes()));

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
                    double totalPrice = hotel.getBaseRoomPrice() * days;
                    totalPriceTextView.setText("Total Price: $" + totalPrice);
                }
            }
        });

        confirmButton.setOnClickListener(v -> {
            if(inputDays.getText().length() > 0) {
                String roomType = roomTypeSpinner.getSelectedItem().toString();
                int days = Integer.parseInt(inputDays.getText().toString());
                double totalPrice = hotel.getBaseRoomPrice() * days;

                CartItem cartItem = new CartItem(hotel, days, roomType, totalPrice);
                saveToCart(cartItem);
                dialog.dismiss();
                Toast.makeText(context, hotel.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToCart(CartItem cartItem) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (userEmail != null) {
            String sanitizedEmail = userEmail.replace(".", "_");
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(sanitizedEmail)
                    .child("my_cart");

            // Generate a unique ID for the cart item if it doesn't already have one
            if (cartItem.getId() == null || cartItem.getId().isEmpty()) {
                String cartItemId = cartRef.push().getKey(); // Generate unique key in Firebase
                cartItem.setId(cartItemId); // Set the id in CartItem object
            }

            // Save cartItem with the id as the key in Firebase
            cartRef.child(cartItem.getId()).setValue(cartItem)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show();
                        if (hasNotificationPermission()) {
                            sendCartNotification(cartItem);
                        } else {
                            requestNotificationPermission();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show());
        }
    }

    private boolean hasNotificationPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    (androidx.fragment.app.FragmentActivity) context,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_CODE
            );
        }
    }

    private void sendCartNotification(CartItem cartItem) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "cart_notification_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Cart Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Item Added to Cart")
                .setContentText("Added: " + cartItem.getHotel().getName() + " | Price: $" + cartItem.getTotalPrice())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }
}
