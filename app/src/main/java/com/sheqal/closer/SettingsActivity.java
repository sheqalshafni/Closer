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
    @BindView(R.id._btnAccount)
    TextView btnAccount;
    @BindView(R.id._btnAbout)
    TextView btnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mRef = db.collection("users").document(mUser.getUid());

        btnAccount.setOnClickListener(v -> {
            Intent _accountIntent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(_accountIntent);
            finish();
        });

        btnAbout.setOnClickListener(v -> {
            Intent _aboutIntent = new Intent(SettingsActivity.this, AboutActivity.class);
            startActivity(_aboutIntent);
        });

        btnLogout.setOnClickListener(v -> logout());

        btnBack.setOnClickListener(v -> finish());

        btnAddPartner.setOnClickListener(v -> {
            Intent _addPartner = new Intent(SettingsActivity.this, AddPartnerActivity.class);
            startActivity(_addPartner);
        });
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

}
