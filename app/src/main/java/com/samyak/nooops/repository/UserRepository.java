package com.samyak.nooops.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.samyak.nooops.model.Address;
import com.samyak.nooops.model.PaymentMethod;
import com.samyak.nooops.model.User;

public class UserRepository extends FirebaseRepository {
    private static final String TAG = "UserRepository";
    private static final String USERS_COLLECTION = "users";
    private static final String PROFILE_IMAGES_PATH = "profile_images";

    // Constructor
    public UserRepository() {
        super();
    }

    // Authentication methods
    public LiveData<FirebaseUser> login(String email, String password) {
        MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userLiveData.setValue(firebaseAuth.getCurrentUser());
                        } else {
                            Log.e(TAG, "Login failed", task.getException());
                            userLiveData.setValue(null);
                        }
                    }
                });

        return userLiveData;
    }

    public LiveData<FirebaseUser> register(String name, String email, String password, String mobile) {
        MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            
                            // Update display name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            
                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Create user in Firestore
                                                User user = new User(firebaseUser.getUid(), name, email, mobile);
                                                createUserInFirestore(user);
                                                userLiveData.setValue(firebaseUser);
                                            } else {
                                                Log.e(TAG, "Profile update failed", task.getException());
                                                userLiveData.setValue(null);
                                            }
                                        }
                                    });
                        } else {
                            Log.e(TAG, "Registration failed", task.getException());
                            userLiveData.setValue(null);
                        }
                    }
                });

        return userLiveData;
    }

    public LiveData<FirebaseUser> loginWithGoogle(String idToken) {
        MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            
                            // Check if user exists in Firestore
                            firestore.collection(USERS_COLLECTION)
                                    .document(firebaseUser.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (!document.exists()) {
                                                    // Create new user in Firestore
                                                    String name = firebaseUser.getDisplayName();
                                                    String email = firebaseUser.getEmail();
                                                    String mobile = firebaseUser.getPhoneNumber();
                                                    
                                                    User user = new User(firebaseUser.getUid(), name, email, mobile);
                                                    if (firebaseUser.getPhotoUrl() != null) {
                                                        user.setProfileImageUrl(firebaseUser.getPhotoUrl().toString());
                                                    }
                                                    
                                                    createUserInFirestore(user);
                                                }
                                                
                                                userLiveData.setValue(firebaseUser);
                                            } else {
                                                Log.e(TAG, "Firestore check failed", task.getException());
                                                userLiveData.setValue(firebaseUser); // Still return user since auth was successful
                                            }
                                        }
                                    });
                        } else {
                            Log.e(TAG, "Google sign-in failed", task.getException());
                            userLiveData.setValue(null);
                        }
                    }
                });

        return userLiveData;
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    public LiveData<Boolean> resetPassword(String email) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        result.setValue(task.isSuccessful());
                    }
                });

        return result;
    }

    // User data methods
    private void createUserInFirestore(User user) {
        firestore.collection(USERS_COLLECTION)
                .document(user.getUid())
                .set(user)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error creating user in Firestore", e);
                    }
                });
    }

    public LiveData<User> getUserData() {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            userLiveData.setValue(null);
            return userLiveData;
        }
        
        firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        userLiveData.setValue(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting user data", e);
                        userLiveData.setValue(null);
                    }
                });
        
        return userLiveData;
    }

    public LiveData<Boolean> updateUserProfile(String name, String mobile) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        firestore.collection(USERS_COLLECTION)
                .document(userId)
                .update("name", name, "mobile", mobile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Also update Auth profile
                        FirebaseUser user = getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        result.setValue(task.isSuccessful());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error updating profile", e);
                        result.setValue(false);
                    }
                });
        
        return result;
    }

    public LiveData<String> updateProfileImage(Uri imageUri) {
        return uploadImage(imageUri, PROFILE_IMAGES_PATH);
    }

    public LiveData<Boolean> saveProfileImageUrl(String imageUrl) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        firestore.collection(USERS_COLLECTION)
                .document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Also update Auth profile
                        FirebaseUser user = getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(imageUrl))
                                .build();
                        
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        result.setValue(task.isSuccessful());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error saving profile image URL", e);
                        result.setValue(false);
                    }
                });
        
        return result;
    }

    // Address methods
    public LiveData<Boolean> addAddress(Address address) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        getUserData().observeForever(user -> {
            if (user != null) {
                user.addAddress(address);
                
                firestore.collection(USERS_COLLECTION)
                        .document(userId)
                        .update("addresses", user.getAddresses())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                result.setValue(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error adding address", e);
                                result.setValue(false);
                            }
                        });
            } else {
                result.setValue(false);
            }
        });
        
        return result;
    }

    // Wishlist methods
    public LiveData<Boolean> addToWishlist(String productId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        getUserData().observeForever(user -> {
            if (user != null) {
                user.addToWishlist(productId);
                
                firestore.collection(USERS_COLLECTION)
                        .document(userId)
                        .update("wishlist", user.getWishlist())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                result.setValue(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error adding to wishlist", e);
                                result.setValue(false);
                            }
                        });
            } else {
                result.setValue(false);
            }
        });
        
        return result;
    }

    public LiveData<Boolean> removeFromWishlist(String productId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        getUserData().observeForever(user -> {
            if (user != null) {
                user.removeFromWishlist(productId);
                
                firestore.collection(USERS_COLLECTION)
                        .document(userId)
                        .update("wishlist", user.getWishlist())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                result.setValue(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error removing from wishlist", e);
                                result.setValue(false);
                            }
                        });
            } else {
                result.setValue(false);
            }
        });
        
        return result;
    }

    // Payment methods
    public LiveData<Boolean> addPaymentMethod(PaymentMethod paymentMethod) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        
        String userId = getCurrentUserId();
        if (userId == null) {
            result.setValue(false);
            return result;
        }
        
        getUserData().observeForever(user -> {
            if (user != null) {
                user.addPaymentMethod(paymentMethod);
                
                firestore.collection(USERS_COLLECTION)
                        .document(userId)
                        .update("paymentMethods", user.getPaymentMethods())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                result.setValue(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error adding payment method", e);
                                result.setValue(false);
                            }
                        });
            } else {
                result.setValue(false);
            }
        });
        
        return result;
    }
}