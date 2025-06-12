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
import com.google.firebase.firestore.FirebaseFirestore;
import com.samyak.nooops.MainActivity;
import com.samyak.nooops.R;
import com.samyak.nooops.databinding.ActivityRegisterBinding;
import com.samyak.nooops.util.SharedPreferencesHelper;
import com.samyak.nooops.util.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    
    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;
    private SharedPreferencesHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Initialize SharedPreferencesHelper
        prefsHelper = new SharedPreferencesHelper(this);
        
        // Check if user is already logged in
        if (prefsHelper.isLoggedIn()) {
            startMainActivity();
            finish();
            return;
        }
        
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        
        // Set up click listeners
        setupListeners();
    }

    private void setupListeners() {
        // Register button click
        binding.buttonRegister.setOnClickListener(v -> registerWithEmailPassword());
        
        // Google sign up button click
        binding.buttonGoogleRegister.setOnClickListener(v -> signInWithGoogle());
        
        // Login text click
        binding.textLogin.setOnClickListener(v -> {
            finish(); // Go back to login screen
        });
    }

    private void registerWithEmailPassword() {
        String name = binding.editName.getText().toString().trim();
        String email = binding.editEmail.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();
        String confirmPassword = binding.editConfirmPassword.getText().toString().trim();
        boolean termsAccepted = binding.checkboxTerms.isChecked();
        
        // Validate input
        if (!validateForm(name, email, password, confirmPassword, termsAccepted)) {
            return;
        }
        
        showProgressBar();
        
        // Create user with email and password
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressBar();
                        
                        if (task.isSuccessful()) {
                            // Sign up success
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Create user document in Firestore
                                createUserInFirestore(user, name);
                            }
                        } else {
                            // Sign up failed
                            Toast.makeText(RegisterActivity.this, 
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        showProgressBar();
        
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressBar();
                        
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Check if this is a new user
                                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                                if (isNewUser) {
                                    // Create user document in Firestore
                                    createUserInFirestore(user, user.getDisplayName());
                                } else {
                                    // Fetch existing user data
                                    fetchUserDataAndSave(user);
                                }
                            }
                        } else {
                            // Sign in failed
                            Toast.makeText(RegisterActivity.this, 
                                    "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUserInFirestore(FirebaseUser firebaseUser, String name) {
        // Create a new user document in Firestore
        Map<String, Object> user = new HashMap<>();
        user.put("uid", firebaseUser.getUid());
        user.put("name", name);
        user.put("email", firebaseUser.getEmail());
        user.put("photoUrl", firebaseUser.getPhotoUrl() != null ? 
                firebaseUser.getPhotoUrl().toString() : "");
        user.put("createdAt", System.currentTimeMillis());
        
        db.collection("users").document(firebaseUser.getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // Save user data in SharedPreferences
                    saveUserDataAndProceed(firebaseUser.getUid(), name, firebaseUser.getEmail());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, 
                            "Error creating user: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchUserDataAndSave(FirebaseUser firebaseUser) {
        db.collection("users").document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User exists in Firestore
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        
                        // Save user data in SharedPreferences
                        saveUserDataAndProceed(firebaseUser.getUid(), name, email);
                    } else {
                        // User doesn't exist in Firestore, create new document
                        createUserInFirestore(firebaseUser, firebaseUser.getDisplayName());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, 
                            "Error fetching user data: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserDataAndProceed(String userId, String name, String email) {
        // Save user data in SharedPreferences
        prefsHelper.saveUserData(userId, name, email);
        
        // Save authentication token if available
        auth.getCurrentUser().getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        prefsHelper.saveAuthToken(token);
                    }
                    
                    // Navigate to main activity
                    startMainActivity();
                    finish();
                });
    }

    private boolean validateForm(String name, String email, String password, 
                                String confirmPassword, boolean termsAccepted) {
        boolean valid = true;
        
        // Validate name
        if (TextUtils.isEmpty(name)) {
            binding.layoutName.setError("Required");
            valid = false;
        } else if (!ValidationUtils.isValidName(name)) {
            binding.layoutName.setError("Name must be at least 3 characters");
            valid = false;
        } else {
            binding.layoutName.setError(null);
        }
        
        // Validate email
        if (TextUtils.isEmpty(email)) {
            binding.layoutEmail.setError("Required");
            valid = false;
        } else if (!ValidationUtils.isValidEmail(email)) {
            binding.layoutEmail.setError("Invalid email format");
            valid = false;
        } else {
            binding.layoutEmail.setError(null);
        }
        
        // Validate password
        if (TextUtils.isEmpty(password)) {
            binding.layoutPassword.setError("Required");
            valid = false;
        } else if (!ValidationUtils.isValidPassword(password)) {
            binding.layoutPassword.setError("Password must be at least 8 characters with letters and numbers");
            valid = false;
        } else {
            binding.layoutPassword.setError(null);
        }
        
        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.layoutConfirmPassword.setError("Required");
            valid = false;
        } else if (!ValidationUtils.doPasswordsMatch(password, confirmPassword)) {
            binding.layoutConfirmPassword.setError("Passwords do not match");
            valid = false;
        } else {
            binding.layoutConfirmPassword.setError(null);
        }
        
        // Validate terms acceptance
        if (!termsAccepted) {
            Toast.makeText(this, "You must accept the Terms & Conditions", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        
        return valid;
    }

    private void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonRegister.setEnabled(false);
        binding.buttonGoogleRegister.setEnabled(false);
    }

    private void hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
        binding.buttonRegister.setEnabled(true);
        binding.buttonGoogleRegister.setEnabled(true);
    }

    private void startMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
} 