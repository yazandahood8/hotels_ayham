package com.example.hotels;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotels.adapters.HotelWithCartAdapter;
import com.example.hotels.data.CartItem;
import com.example.hotels.data.Hotel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HotelsFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView hotelsRecyclerView;
    private HotelWithCartAdapter hotelAdapter;
    private List<Hotel> hotelList = new ArrayList<>();
    private List<Hotel> filteredHotelList = new ArrayList<>(); // Separate list for filtering

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotels, container, false);

        searchView = view.findViewById(R.id.searchView);
        hotelsRecyclerView = view.findViewById(R.id.hotelsRecyclerView);

        // Set up RecyclerView
        hotelsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        hotelAdapter = new HotelWithCartAdapter(getContext(), filteredHotelList); // Use filteredHotelList
        hotelsRecyclerView.setAdapter(hotelAdapter);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("hotels");

        // Load hotels from Firebase
        loadHotels();

        // Set up search functionality
        setupSearchView();

        return view;
    }

    private void loadHotels() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hotelList.clear();
                for (DataSnapshot hotelSnapshot : snapshot.getChildren()) {
                    Hotel hotel = hotelSnapshot.getValue(Hotel.class);
                    if (hotel != null) {
                        hotelList.add(hotel);
                    }
                }
                // Initially display all hotels
                filteredHotelList.clear();
                filteredHotelList.addAll(hotelList);
                hotelAdapter.notifyDataSetChanged(); // Notify adapter of the full list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterHotels(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterHotels(newText);
                return true;
            }
        });
    }

    private void filterHotels(String query) {
        filteredHotelList.clear();
        if (query.isEmpty()) {
            // Show all hotels if the query is empty
            filteredHotelList.addAll(hotelList);
        } else {
            for (Hotel hotel : hotelList) {
                if (hotel.getName().toLowerCase().contains(query.toLowerCase()) ||
                        hotel.getCity().toLowerCase().contains(query.toLowerCase())) {
                    filteredHotelList.add(hotel);
                }
            }
        }
        hotelAdapter.notifyDataSetChanged(); // Notify adapter of the filtered list
    }


}
