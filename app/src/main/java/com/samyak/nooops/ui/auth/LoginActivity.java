package com.samyak.nooops.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.samyak.nooops.MainActivity;
import com.samyak.nooops.R;
import com.samyak.nooops.databinding.ActivityLoginBinding;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private ActivityLoginBinding binding;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Setup click listeners
        setupClickListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMain();
        }
    }

    private void setupClickListeners() {
        // Login button click
        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.editEmail.getText().toString().trim();
            String password = binding.editPassword.getText().toString().trim();

            if (validateForm(email, password)) {
                showLoading(true);
                signInWithEmail(email, password);
            }
        });

        // Google sign-in button click
        binding.buttonGoogleLogin.setOnClickListener(v -> {
            showLoading(true);
            signInWithGoogle();
        });

        // Register text click
        binding.textRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // Forgot password text click
        binding.textForgotPassword.setOnClickListener(v -> {
            // Handle forgot password flow
            // TODO: Implement forgot password functionality
            Toast.makeText(LoginActivity.this, "Forgot password functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            binding.layoutEmail.setError("Required");
            valid = false;
        } else if (!isValidEmail(email)) {
            binding.layoutEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            binding.layoutEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.layoutPassword.setError("Required");
            valid = false;
        } else if (password.length() < 6) {
            binding.layoutPassword.setError("Password must be at least 6 characters");
            valid = false;
        } else {
            binding.layoutPassword.setError(null);
        }

        return valid;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        // Sign in success
                        navigateToMain();
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + 
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed
                showLoading(false);
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Check if this is a new user
                        checkIfNewUser(user);
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + 
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfNewUser(FirebaseUser user) {
        if (user != null) {
            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        // Create a new user entry in Firestore
                        createNewUserDocument(user);
                    } else {
                        // Existing user, navigate to main
                        navigateToMain();
                    }
                } else {
                    // Error accessing Firestore
                    Toast.makeText(LoginActivity.this, "Error checking user status: " + 
                            task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    navigateToMain();
                }
            });
        }
    }

    private void createNewUserDocument(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", user.getUid());
        userData.put("email", user.getEmail());
        userData.put("displayName", user.getDisplayName());
        userData.put("photoUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        userData.put("createdAt", System.currentTimeMillis());

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    // User document created successfully
                    navigateToMain();
                })
                .addOnFailureListener(e -> {
                    // Failed to create user document
                    Toast.makeText(LoginActivity.this, "Error creating user profile: " + 
                            e.getMessage(), Toast.LENGTH_SHORT).show();
                    navigateToMain();
                });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.buttonLogin.setEnabled(!isLoading);
        binding.buttonGoogleLogin.setEnabled(!isLoading);
        binding.editEmail.setEnabled(!isLoading);
        binding.editPassword.setEnabled(!isLoading);
    }
}