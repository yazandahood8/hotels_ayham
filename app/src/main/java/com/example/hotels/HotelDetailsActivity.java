package com.example.hotels;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hotels.data.Hotel;

import java.util.Map;

public class HotelDetailsActivity extends AppCompatActivity {

    private ImageView hotelImage;
    private TextView hotelName, hotelLocation, hotelDescription, hotelAmenities, hotelAdditionalInfo;
    private RatingBar hotelRatingBar;
    private Button roomInfoButton;
    private Hotel hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details);

        hotelImage = findViewById(R.id.hotelImage);
        hotelName = findViewById(R.id.hotelName);
        hotelLocation = findViewById(R.id.hotelLocation);
        hotelRatingBar = findViewById(R.id.hotelRating); // Updated to use RatingBar
        hotelDescription = findViewById(R.id.hotelDescription);
        hotelAmenities = findViewById(R.id.hotelAmenities);
        hotelAdditionalInfo = findViewById(R.id.hotelAdditionalInfo);
        roomInfoButton = findViewById(R.id.roomInfoButton);

        // Retrieve hotel data from Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("hotel")) {
            hotel = (Hotel) intent.getSerializableExtra("hotel");
            displayHotelDetails();
        }

        // Show room information in a dialog
        roomInfoButton.setOnClickListener(v -> showRoomInfoDialog());
    }

    private void displayHotelDetails() {
        // Display hotel details
        hotelName.setText(hotel.getName());
        hotelLocation.setText(hotel.getCity() + ", " + hotel.getCountry());
        hotelRatingBar.setRating((float) hotel.getReviewRating());
        hotelDescription.setText(hotel.getAddress());

        // Load hotel image
        Glide.with(this)
                .load(hotel.getImageUrl())
                .into(hotelImage);

        // Display amenities
        String amenities = "Parking: " + (hotel.isHasParking() ? "Yes" : "No") +
                "\nWifi: " + (hotel.isHasWifi() ? "Yes" : "No") +
                "\nPool: " + (hotel.isHasSwimmingPool() ? "Yes" : "No") +
                "\nGym: " + (hotel.isHasGym() ? "Yes" : "No") +
                "\nSpa: " + (hotel.isHasSpa() ? "Yes" : "No") +
                "\nRestaurant: " + (hotel.isHasRestaurant() ? "Yes" : "No");
        hotelAmenities.setText(amenities);

        // Display additional info
        String additionalInfo = "Check-in: " + hotel.getCheckInTime() +
                "\nCheck-out: " + hotel.getCheckOutTime() +
                "\nBase Room Price: $" + hotel.getBaseRoomPrice();
        hotelAdditionalInfo.setText(additionalInfo);
    }

    private void showRoomInfoDialog() {
        // Create dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.room_info_dialog);

        // Room types and additional charges
        TextView roomTypesView = dialog.findViewById(R.id.roomTypes);
        TextView additionalChargesView = dialog.findViewById(R.id.additionalCharges);

        // Display room types
        StringBuilder roomTypesBuilder = new StringBuilder();
        for (String type : hotel.getRoomTypes()) {
            roomTypesBuilder.append("- ").append(type).append("\n");
        }
        roomTypesView.setText(roomTypesBuilder.toString());

        // Display additional charges
        StringBuilder additionalChargesBuilder = new StringBuilder();
        for (Map.Entry<String, Double> entry : hotel.getAdditionalCharges().entrySet()) {
            additionalChargesBuilder.append(entry.getKey()).append(": $").append(entry.getValue()).append("\n");
        }
        additionalChargesView.setText(additionalChargesBuilder.toString());

        dialog.show();
    }
}
