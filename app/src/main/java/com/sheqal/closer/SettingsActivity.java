package com.sheqal.closer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sheqal.closer.ConnectingProcess.AddPartnerActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private String _currentUserName, _currentUserEmail, _currentUserPartnerKey, _currentUserPhotoURL, _currentUserID, _currentUserConnectedPartner;

    //Firebase init
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private DocumentReference mRef;

    String Key, connectedPartnerStatus;

    @BindView(R.id._btnLogout)
    TextView btnLogout;
    @BindView(R.id._settingsBtnBack)
    ImageView btnBack;
    @BindView(R.id._btnAddPartner)
    TextView btnAddPartner;
    @BindView(R.id._btnAddPartnerUnavailable)
    TextView btnAddPartnerUnavailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mRef = db.collection("users").document(mUser.getUid());




        btnLogout.setOnClickListener(v -> logout());

        btnBack.setOnClickListener(v -> finish());

        btnAddPartner.setOnClickListener(v -> {
            Intent _addPartner = new Intent(SettingsActivity.this, AddPartnerActivity.class);
            startActivity(_addPartner);
        });

        /*Disable "Add Partner" function if user is already connected*/


        if(mUser!=null){

            _getUserLoggedOnData();
            Log.d(TAG, "partner" +_currentUserConnectedPartner);
            try{
                if(!_currentUserConnectedPartner.isEmpty()){
                    btnAddPartnerUnavailable.setVisibility(View.VISIBLE);
                    btnAddPartner.setVisibility(View.INVISIBLE);
                }
            }catch (Exception ex){
                Log.d(TAG, "error "+ ex.toString());
            }
        }


    }

    private void logout() {

        if (mUser != null) {

            try {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("Are you sure you want to logout?");
                alertDialog.setCancelable(true);

                alertDialog.setPositiveButton(Html.fromHtml("<font color='#00000'>Yes</font>"), (dialog, which) -> {
                    mAuth.signOut();
                    Intent _logout = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(_logout);
                    finish();
                });

                alertDialog.setNegativeButton(Html.fromHtml("<font color='#00000'>No</font>"), (dialog, which) -> dialog.cancel());

                AlertDialog dialog = alertDialog.create();
                dialog.show();

            } catch (Exception ex) {
                Log.d(TAG, "logout: " + ex.toString());
            }
        }
    }

    private void _getUserLoggedOnData() {

//        mRef.get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        String name = documentSnapshot.getString("Name");
//                        String email = documentSnapshot.getString("Email");
//                        String partnerKey = documentSnapshot.getString("PartnerKey");
//                        String profilePhoto = documentSnapshot.getString("ProfilePhotoURL");
//                        String connectedPartnerName = documentSnapshot.getString("ConnectedPartner");
//                        String userID = documentSnapshot.getString("userID");
//
//                        _currentUserName = name;
//                        _currentUserEmail = email;
//                        _currentUserPartnerKey = partnerKey;
//                        _currentUserPhotoURL = profilePhoto;
//                        _currentUserConnectedPartner = connectedPartnerName;
//                        _currentUserID = userID;
//
//                        Log.d(TAG, "onCreate: status "+_currentUserConnectedPartner );
//
//                    }
//                })
//                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));

        mRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("Name");
                            String email = documentSnapshot.getString("Email");
                            String partnerKey = documentSnapshot.getString("PartnerKey");
                            String profilePhoto = documentSnapshot.getString("ProfilePhotoURL");
                            String connectedPartnerName = documentSnapshot.getString("ConnectedPartner");

                        _currentUserName = name;
                        _currentUserEmail = email;
                        _currentUserPartnerKey = partnerKey;
                        _currentUserPhotoURL = profilePhoto;
                        _currentUserConnectedPartner = connectedPartnerName;
                            Log.d(TAG, "onSuccess: status" +_currentUserConnectedPartner);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });

    }
}
