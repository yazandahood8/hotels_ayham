package com.example.hotels;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DELAY = 3000; // 3 seconds delay
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        firebaseAuth = FirebaseAuth.getInstance();


        // Delay and transition to MainActivity
        new Handler().postDelayed(() -> {

            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                // User is already signed in, go to MainActivity
                goToMainActivity();
            } else {
                // User is not signed in, go to SignInActivity
                goToSignInActivity();
            }
        }, SPLASH_SCREEN_DELAY);


        VideoView videoView = findViewById(R.id.videoView);

        // Set the path to the video file
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
        videoView.setVideoURI(videoUri);

        // Start playing the video
        videoView.start();


    }


    private void goToMainActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close SplashScreenActivity so user can't go back to it
    }

    private void goToSignInActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close SplashScreenActivity so user can't go back to it
    }
}
