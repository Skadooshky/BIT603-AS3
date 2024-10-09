package com.example.courseinstructionyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    private ActivityResultLauncher<IntentSenderRequest> signInRequestLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Layout for login

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check if user is already logged in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, redirect to AppMainActivity
            startActivity(new Intent(MainActivity.this, AppMainActivity.class));
            finish();
            return;
        }

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.web_client_id))  // Web client ID
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .setAutoSelectEnabled(true)
                .build();

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(view -> signInOnClick());

        signInRequestLauncher =
                registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                        result -> {
                            if (result.getData() != null) {
                                try {
                                    SignInCredential credential = oneTapClient
                                            .getSignInCredentialFromIntent(result.getData());
                                    String idToken = credential.getGoogleIdToken();
                                    if (idToken != null) {
                                        firebaseAuthWithGoogle(idToken);
                                    }

                                } catch (ApiException e) {
                                    Log.d("API Exception", "issue with sign in");
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d("RESULT", "No data found in the result.");
                            }
                        });
    }

    public void signInOnClick() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, beginSignInResult -> {
                    IntentSenderRequest intentSenderRequest =
                            new IntentSenderRequest.Builder(beginSignInResult.getPendingIntent().getIntentSender())
                                    .build();
                    signInRequestLauncher.launch(intentSenderRequest);
                })
                .addOnFailureListener(this, e -> {
                    Log.d("FAILURE", "sign in failed");
                    e.printStackTrace();
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user);
                            startActivity(new Intent(MainActivity.this, AppMainActivity.class));  // Redirect after login
                            finish();
                        }
                    } else {
                        Log.d("FIREBASE AUTH", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void saveUserToFirestore(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getDisplayName());
        userData.put("email", user.getEmail());
        userData.put("profilePicture", user.getPhotoUrl().toString());

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "User data successfully written!"))
                .addOnFailureListener(e -> Log.d("FIRESTORE", "Error writing user data", e));
    }
}
