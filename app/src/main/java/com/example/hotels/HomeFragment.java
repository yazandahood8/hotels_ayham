package com.example.hotels;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.hotels.adapters.HotelAdapter;
import com.example.hotels.data.Hotel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private TextView randomHotelName, randomHotelRating, randomHotelLocation;
    private ImageView randomHotelImage;
    private RecyclerView horizontalRecyclerView;
    private SearchView searchView;
    private DatabaseReference databaseReference;
    private List<Hotel> hotelList = new ArrayList<>();
    private List<Hotel> filteredHotelList = new ArrayList<>();
    private HotelAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        randomHotelName = view.findViewById(R.id.randomHotelName);
        randomHotelRating = view.findViewById(R.id.randomHotelRating);
        randomHotelLocation = view.findViewById(R.id.randomHotelLocation);
        randomHotelImage = view.findViewById(R.id.randomHotelImage);
        horizontalRecyclerView = view.findViewById(R.id.horizontalRecyclerView);
        searchView = view.findViewById(R.id.searchView);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("hotels");

        // Load hotels and display a random hotel
        loadHotels();

        // Set up search functionality
        setupSearch();

        return view;
    }

    private void loadHotels() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hotelList.clear();
                for (DataSnapshot hotelSnapshot : snapshot.getChildren()) {
                    Hotel hotel = hotelSnapshot.getValue(Hotel.class);
                    hotelList.add(hotel);
                }
                if (!hotelList.isEmpty()) {
                    displayRandomHotel();
                    setupRecyclerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any database errors here
            }
        });
    }

    private void displayRandomHotel() {
        Random random = new Random();
        int randomIndex = random.nextInt(hotelList.size());
        Hotel randomHotel = hotelList.get(randomIndex);

        randomHotelName.setText(randomHotel.getName());
        randomHotelRating.setText("Rating: " + randomHotel.getReviewRating());
        randomHotelLocation.setText(randomHotel.getCity() + ", " + randomHotel.getCountry());
        Glide.with(this).load(randomHotel.getImageUrl()).into(randomHotelImage);
    }

    private void setupRecyclerView() {
        filteredHotelList.addAll(hotelList); // Initially display all hotels
        adapter = new HotelAdapter(getContext(), filteredHotelList);
        horizontalRecyclerView.setAdapter(adapter);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard();
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
            filteredHotelList.addAll(hotelList); // Show all hotels if query is empty
        } else {
            for (Hotel hotel : hotelList) {
                if (hotel.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredHotelList.add(hotel);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
