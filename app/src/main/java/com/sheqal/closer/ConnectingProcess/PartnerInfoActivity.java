package com.sheqal.closer.ConnectingProcess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sheqal.closer.HomeActivity;
import com.sheqal.closer.R;

import java.util.Arrays;
import java.util.Calendar;
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
    @BindView(R.id._officialDate)
    EditText officialDate;

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
        _customDateEditText();

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
                user.put("PartnerBKey", _currentUserPartnerKey);
                user.put("userID", partnerB_ID);

                db.collection("users").document(partnerB_ID).set(user)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, " Connection success " + _currentUserName + " + " + partnerB_Name);

                            if (mUser != null && officialDate.getText().toString() != null) {

                                FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                                DocumentReference mRef1 = db1.collection("users").document(mUser.getUid());

                                Map<String, Object> user1 = new HashMap<>();

                                user1.put("Name", _currentUserName);
                                user1.put("Email", _currentUserEmail);
                                user1.put("PartnerKey", _currentUserPartnerKey);
                                user1.put("ProfilePhotoURL", _currentUserPhotoURL);
                                user1.put("ConnectedPartner", partnerB_Name);
                                user1.put("PartnerBKey", partnerB_partnerKey);
                                user1.put("userID", _currentUserID);

                                db1.collection("users").document(mUser.getUid()).set(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Map<String, Object> ConnectedUser = new HashMap<>();
                                        ConnectedUser.put("PartnerA_UID", _currentUserID);
                                        ConnectedUser.put("PartnerB_UID", partnerB_ID);
                                        ConnectedUser.put("PartnerA_Name", _currentUserName);
                                        ConnectedUser.put("PartnerB_Name", partnerB_Name);
                                        ConnectedUser.put("CombinedKey", _currentUserPartnerKey + partnerB_partnerKey);
                                        ConnectedUser.put("PartnerA_PhotoURL", _currentUserPhotoURL);
                                        ConnectedUser.put("OfficialDate", officialDate.getText().toString());
                                        ConnectedUser.put("PartnerB_PhotoURL", partnerB_photoURL);
                                        //ConnectedUser.put("OfficialDate", );

                                        db.collection("ConnectedCouples")
                                                .add(ConnectedUser)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Intent _homeIntent = new Intent(PartnerInfoActivity.this, HomeActivity.class);
                                                        startActivity(_homeIntent);
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: Failed to add couple into database " + e.getMessage());
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                            } else {
                                Log.d(TAG, "onAuthenticationSucceeded: failed to get current user data");
                                Toast.makeText(PartnerInfoActivity.this, "Error connecting with partner", Toast.LENGTH_SHORT).show();
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

    private void _customDateEditText(){
        TextWatcher tw = new TextWatcher() {

            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    officialDate.setText(current);
                    officialDate.setSelection(sel < current.length() ? sel : current.length());
                }

            }
        };

        officialDate.addTextChangedListener(tw);

    }
}
