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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class FirebaseRepository {
    private static final String TAG = "FirebaseRepository";
    
    // Firebase instances
    protected FirebaseAuth firebaseAuth;
    protected FirebaseFirestore firestore;
    protected FirebaseStorage storage;
    
    // Constructor
    public FirebaseRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }
    
    // Authentication methods
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
    
    public String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }
    
    // Firestore helper methods
    protected DocumentReference getUserDocument() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return null;
        }
        return firestore.collection("users").document(userId);
    }
    
    // Upload image to Firebase Storage
    public LiveData<String> uploadImage(Uri imageUri, String folderPath) {
        MutableLiveData<String> result = new MutableLiveData<>();
        
        if (imageUri == null) {
            result.setValue(null);
            return result;
        }
        
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference storageRef = storage.getReference().child(folderPath).child(fileName);
        
        storageRef.putFile(imageUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            result.setValue(downloadUri.toString());
                        }
                    });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Image upload failed", e);
                    result.setValue(null);
                }
            });
        
        return result;
    }
    
    // Helper method for task handling
    protected <T> LiveData<T> handleTask(Task<T> task) {
        MutableLiveData<T> result = new MutableLiveData<>();
        
        task.addOnCompleteListener(new OnCompleteListener<T>() {
            @Override
            public void onComplete(@NonNull Task<T> task) {
                if (task.isSuccessful()) {
                    result.setValue(task.getResult());
                } else {
                    Log.e(TAG, "Task failed", task.getException());
                    result.setValue(null);
                }
            }
        });
        
        return result;
    }
} 