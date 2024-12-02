package com.example.hotels;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hotels.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private EditText editName, editEmail, editAddress;
    private Button buttonSave;
    private Uri imageUri;

    private DatabaseReference userRef;
    private StorageReference storageRef;
    private FirebaseAuth auth;
    private User user;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    profileImage.setImageURI(imageUri); // Display the selected image
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profileImage);
        editName = view.findViewById(R.id.editName);
        editEmail = view.findViewById(R.id.editEmail);
        editAddress = view.findViewById(R.id.editAddress);
        buttonSave = view.findViewById(R.id.buttonSave);

        auth = FirebaseAuth.getInstance();
        String userEmail = auth.getCurrentUser().getEmail().replace(".", "_");

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userEmail).child("info");
        storageRef = FirebaseStorage.getInstance().getReference("profile_images").child(userEmail);

        // Load the user profile data
        loadProfileData();

        // Set click listener to open image picker when profile image is clicked
        profileImage.setOnClickListener(v -> openImagePicker());

        // Save changes to profile
        buttonSave.setOnClickListener(v -> saveProfileChanges());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void loadProfileData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null) {
                    editName.setText(user.getName());
                    editEmail.setText(user.getEmail());
                    editAddress.setText(user.getAddress());
                    if (user.getImage() != null && !user.getImage().isEmpty()) {
                        Glide.with(getActivity()).load(user.getImage()).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileChanges() {
        String name = editName.getText().toString().trim();
        String address = editAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name)  || TextUtils.isEmpty(address)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the profile image if a new one was selected
        if (imageUri != null) {
            uploadImageToFirebase(name, address);
        } else {
            // No new image selected, just save the text fields
            saveUserProfile(name, address, user.getImage());
        }
    }

    private void uploadImageToFirebase(String name, String address) {
        storageRef.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the download URL for the image
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveUserProfile(name, address, imageUrl); // Save profile with new image URL
                });
            } else {
                Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserProfile(String name, String address, String imageUrl) {
        User updatedUser = new User(name, editEmail.getText().toString(), user.getPassword(), address, imageUrl);

        userRef.setValue(updatedUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
