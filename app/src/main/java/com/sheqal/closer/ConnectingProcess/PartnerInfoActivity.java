package com.sheqal.closer.ConnectingProcess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sheqal.closer.HomeActivity;
import com.sheqal.closer.R;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PartnerInfoActivity extends AppCompatActivity {

    private static final String TAG = "PartnerInfoActivity";

    //Current user info
    private String _currentUserName, _currentUserEmail, _currentUserPartnerKey, _currentUserPhotoURL, _currentUserID, _currentUserConnectedPartner;

    //Partner B info
    private String partnerB_Name, partnerB_Email, partnerB_partnerKey, partnerB_photoURL, partnerB_ID, partnerB_connectedPartner;

    //Biometric init
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    //Firebase init
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private CollectionReference mRef;

    @BindView(R.id._partnerInfoImage)
    CircleImageView _profileImage;
    @BindView(R.id._partnerInfoName)
    TextView _Name;
    @BindView(R.id._partnerInfoEmail)
    TextView _Email;
    @BindView(R.id._partnerInfoKey)
    TextView _partnerKey;
    @BindView(R.id._partnerInfoBtnBack)
    ImageView _btnBack;
    @BindView(R.id._btnConfirm)
    Button _btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_info);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mRef = db.collection("users");

        _retrieveData();
        _checkBiometricAvailability();
        _checkBiometric();
        _getUserLoggedOnData();

        _btnBack.setOnClickListener(v -> finish());

        _btnConfirm.setOnClickListener(v -> _promptBiometric());

    }

    private void _retrieveData() {
        Bundle getData = getIntent().getExtras();
        if (getData != null) {
            
            partnerB_Name = getData.getString("name");
            partnerB_Email = getData.getString("email");
            partnerB_partnerKey = getData.getString("key");
            partnerB_photoURL = getData.getString("url");
            partnerB_ID = getData.getString("userID");
            partnerB_connectedPartner = getData.getString("partner");

            _Name.setText(partnerB_Name);
            _Email.setText(partnerB_Email);
            _partnerKey.setText(partnerB_partnerKey);
            Glide.with(PartnerInfoActivity.this)
                    .load(partnerB_photoURL)
                    .into(_profileImage);

        }
    }

    private void _checkBiometric() {

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(PartnerInfoActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Map<String, Object> user = new HashMap<>();
                user.put("Name", partnerB_Name);
                user.put("Email", partnerB_Email);
                user.put("PartnerKey", partnerB_partnerKey);
                user.put("ProfilePhotoURL", partnerB_photoURL);
                user.put("ConnectedPartner", _currentUserName);
                user.put("userID", partnerB_ID);

                db.collection("users").document(partnerB_ID).set(user)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, " Connection success " + _currentUserName + " + " + partnerB_Name);

                            if(mUser!=null){

                                FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                                DocumentReference mRef1 = db1.collection("users").document(mUser.getUid());

                                Map<String, Object> user1 = new HashMap<>();

                                user1.put("Name", _currentUserName);
                                user1.put("Email", _currentUserEmail);
                                user1.put("PartnerKey", _currentUserPartnerKey);
                                user1.put("ProfilePhotoURL", _currentUserPhotoURL);
                                user1.put("ConnectedPartner", partnerB_Name);
                                user1.put("userID", _currentUserID);

                                db1.collection("users").document(mUser.getUid()).set(user1)
                                        .addOnSuccessListener(aVoid1 -> Log.d(TAG, "Connection success " + partnerB_Name + " + " + _currentUserName))
                                        .addOnFailureListener(e -> Log.d(TAG, "connection failed " + e.toString()));

                                Intent _homeIntent = new Intent(PartnerInfoActivity.this, HomeActivity.class);
                                startActivity(_homeIntent);
                                
                            }else {
                                Log.d(TAG, "onAuthenticationSucceeded: failed to get current user data");
                            }
                        })
                        .addOnFailureListener(e -> Log.d(TAG, "onFailure: fail"));

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void _promptBiometric() {
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Confirmation")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void _checkBiometricAvailability() {

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.e("MY_APP_TAG", "The user hasn't associated " +
                        "any biometric credentials with their account.");
                break;
        }

    }

    private void _getUserLoggedOnData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference mRef = db.collection("users").document(mUser.getUid());

        mRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("Name");
                        String email = documentSnapshot.getString("Email");
                        String partnerKey = documentSnapshot.getString("PartnerKey");
                        String profilePhoto = documentSnapshot.getString("ProfilePhotoURL");
                        String connectedPartnerName = documentSnapshot.getString("ConnectedPartner");
                        String userID = documentSnapshot.getString("userID");
                        _currentUserName = name;
                        _currentUserEmail = email;
                        _currentUserPartnerKey = partnerKey;
                        _currentUserPhotoURL = profilePhoto;
                        _currentUserConnectedPartner = connectedPartnerName;
                        _currentUserID = userID;
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));

    }

}
