package com.sheqal.closer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import aglibs.loading.skeleton.layout.SkeletonRelativeLayout;
import aglibs.loading.skeleton.view.SkeletonTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private DocumentReference mRef;

    @BindView(R.id._userImage)
    SkeletonRelativeLayout _userImage;
    @BindView(R.id._profileImage)
    CircleImageView _profileImage;
    @BindView(R.id._profileBtnBack)
    ImageView btnBack;
    @BindView(R.id._profilebtnSettings)
    ImageView btnSettings;
    @BindView(R.id._profileName)
    SkeletonTextView _Name;
    @BindView(R.id._profileEmail)
    SkeletonTextView _Email;
    @BindView(R.id._profileKey)
    SkeletonTextView _partnerKey;
    @BindView(R.id._profileConnectedPartner)
    SkeletonTextView _connectedPartnerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mRef = db.collection("users").document(mUser.getUid());

        _loadUserData();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent _Settings = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(_Settings);
            }
        });
    }

    private void _loadUserData() {
        if (mUser != null) {
            try {

                ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setMessage("Fetching data");
                progressDialog.show();

                mRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    progressDialog.dismiss();
                                    String name = documentSnapshot.getString("Name");
                                    String email = documentSnapshot.getString("Email");
                                    String partnerKey = documentSnapshot.getString("PartnerKey");
                                    String profilePhoto = documentSnapshot.getString("ProfilePhotoURL");
                                    String connectedPartnerName = documentSnapshot.getString("ConnectedPartner");

                                    _Name.stopLoading();
                                    _Name.setText(name);
                                    _Email.stopLoading();
                                    _Email.setText(email);
                                    _partnerKey.stopLoading();
                                    _partnerKey.setText(partnerKey);
                                    _connectedPartnerName.stopLoading();
                                    _connectedPartnerName.setText(connectedPartnerName);
                                    _userImage.stopLoading();
                                    Glide.with(ProfileActivity.this)
                                            .load(profilePhoto)
                                            .into(_profileImage);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });
            } catch (Exception ex) {
                Log.d(TAG, "_loadUserData: " + ex.toString());
            }
        } else {
            Toast.makeText(ProfileActivity.this, "Unable to fetch user data", Toast.LENGTH_SHORT).show();
        }
    }
}
