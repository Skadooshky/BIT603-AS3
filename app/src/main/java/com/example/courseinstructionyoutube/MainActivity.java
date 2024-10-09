package com.example.courseinstructionyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // One Tap sign-in client
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;  // Request for Google sign-in
    private FirebaseAuth firebaseAuth;         // Firebase authentication instance
    private FirebaseFirestore db;              // Firestore database instance

    // ActivityResultLauncher to handle the result of the sign-in request
    private ActivityResultLauncher<IntentSenderRequest> signInRequestLauncher;

    private static void onFailure(Exception e) {
        Log.d("FAILURE", "Sign-in failed");
        e.printStackTrace();  // Log failure
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Set layout for the main activity (login screen)

        // Initialize Firebase authentication and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check if a user is already signed in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // If user is already signed in, redirect to AppMainActivity
            startActivity(new Intent(MainActivity.this, AppMainActivity.class));
            finish();  // Close the current activity to prevent going back
            return;
        }

        // Initialize One Tap sign-in client
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.web_client_id))  // Web client ID
                        .setFilterByAuthorizedAccounts(false)  // Allow accounts not yet signed in
                        .build()
                )
                .setAutoSelectEnabled(true)  // Automatically select account if possible
                .build();

        // Set up the Google Sign-In button
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(view -> signInOnClick());  // Attach listener for sign-in

        // ActivityResultLauncher to handle the sign-in result
        signInRequestLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                this::onActivityResult);
    }

    // Method to handle sign-in click
    public void signInOnClick() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, beginSignInResult -> {
                    // Launch the sign-in activity
                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest
                            .Builder(beginSignInResult.getPendingIntent().getIntentSender())
                            .build();
                    signInRequestLauncher.launch(intentSenderRequest);  // Launch the sign-in process
                })
                .addOnFailureListener(this, MainActivity::onFailure);
    }

    // Method to authenticate with Firebase using the Google ID token
    private void firebaseAuthWithGoogle(String idToken) {
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in successful
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Save the user's details to Firestore
                            saveUserToFirestore(user);
                            // Redirect to the main application activity
                            startActivity(new Intent(MainActivity.this, AppMainActivity.class));
                            finish();  // Close current activity to prevent back navigation
                        }
                    } else {
                        Log.d("FIREBASE AUTH", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    // Method to save user data to Firestore
    private void saveUserToFirestore(FirebaseUser user) {
        // Create a map of user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getDisplayName());  // Add user's display name
        userData.put("email", user.getEmail());       // Add user's email
        userData.put("profilePicture", Objects.requireNonNull(user.getPhotoUrl()).toString());  // Add user's profile picture URL

        // Save the data to the Firestore "users" collection
        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "User data successfully written!"))
                .addOnFailureListener(e -> Log.d("FIRESTORE", "Error writing user data", e));  // Log any errors
    }

    private void onActivityResult(ActivityResult result) {
        if (result.getData() != null) {
            try {
                // Get the sign-in credential from the result data
                SignInCredential credential = oneTapClient
                        .getSignInCredentialFromIntent(result.getData());
                String idToken = credential.getGoogleIdToken();  // Retrieve ID token
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken);  // Authenticate with Firebase using the Google ID token
                }
            } catch (ApiException e) {
                Log.d("API Exception", "Issue with sign-in");
                e.printStackTrace();  // Log the exception
            }
        } else {
            Log.d("RESULT", "No data found in the result.");
        }
    }
}
