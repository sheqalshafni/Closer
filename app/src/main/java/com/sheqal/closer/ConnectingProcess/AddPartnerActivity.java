package com.sheqal.closer.ConnectingProcess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sheqal.closer.R;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddPartnerActivity extends AppCompatActivity {

    private static final String TAG = "AddPartnerActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private CollectionReference mRef;

    //Current user info
    private String _currentUserName, _currentUserEmail, _currentUserPartnerKey, _currentUserPhotoURL, _currentUserID, _currentUserConnectedPartner;

    @BindView(R.id._addPartnerBtnBack)
    ImageView _btnBack;
    @BindView(R.id._partnerKey)
    EditText partnerKey;
    @BindView(R.id._btnConfirm)
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_partner);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mRef = db.collection("users");

        _btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectPartner();
            }
        });

    }

    private void connectPartner() {

        if (mUser != null) {

            ProgressDialog progressDialog = new ProgressDialog(AddPartnerActivity.this);
            progressDialog.setMessage("Connecting you with your partner");
            progressDialog.setCancelable(false);

            String getKey = partnerKey.getText().toString();
            int keyLength = getKey.length();

            if (partnerKey.getText().toString().equals("")) {
                Toast.makeText(AddPartnerActivity.this, "Invalid partner key", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.show();

                Query userQuery = mRef.whereEqualTo("PartnerKey", partnerKey.getText().toString());

                userQuery.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();

                        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    String key;
                                    key = documentSnapshot.getString("PartnerKey");
                                    if (partnerKey.getText().toString().equals(key)) {
                                        String a, b, c, d, f;
                                        a = documentSnapshot.getString("Name");
                                        b = documentSnapshot.getString("ProfilePhotoURL");
                                        c = documentSnapshot.getString("userID");
                                        d = documentSnapshot.getString("Email");
                                        f = documentSnapshot.getString("PartnerKey");

                                        Log.d(TAG, "retrieved name " + a + " img url " + b + " userid " + c + " email " + d);

                                        _getUserLoggedOnData();

                                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                                        try {

                                            if (!c.equals("") || c != null) {

                                                DocumentReference mRef = db.collection("users").document(c);

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
                                                                    String userID = documentSnapshot.getString("userID");

                                                                    Map<String, Object> user = new HashMap<>();
                                                                    user.put("Name", a);
                                                                    user.put("Email", d);
                                                                    user.put("PartnerKey", f);
                                                                    user.put("ProfilePhotoURL", b);
                                                                    user.put("ConnectedPartner", _currentUserName);
                                                                    user.put("userID", userID);

                                                                    Intent passData = new Intent(AddPartnerActivity.this, PartnerInfoActivity.class);
                                                                    passData.putExtra("name", name);
                                                                    passData.putExtra("email", email);
                                                                    passData.putExtra("key", partnerKey);
                                                                    passData.putExtra("url", profilePhoto);
                                                                    startActivity(passData);

                                                                    Log.d(TAG, "partner name " + _currentUserName);

                                                                    db.collection("users").document(c).set(user)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Log.d(TAG, " Connection success " + _currentUserName + " + " + a);

                                                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                                    DocumentReference mRef = db.collection("users").document(mUser.getUid());

                                                                                    _getUserLoggedOnData();

                                                                                    Map<String, Object> user = new HashMap<>();

                                                                                    user.put("Name", _currentUserName);
                                                                                    user.put("Email", _currentUserEmail);
                                                                                    user.put("PartnerKey", _currentUserPartnerKey);
                                                                                    user.put("ProfilePhotoURL", _currentUserPhotoURL);
                                                                                    user.put("ConnectedPartner", a);
                                                                                    user.put("userID", _currentUserID);

                                                                                    db.collection("users").document(mUser.getUid()).set(user)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    Log.d(TAG, "Connection success " + a + " + " + _currentUserName);
                                                                                                }
                                                                                            })
                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    Log.d(TAG, "connection failed " + e.toString());
                                                                                                }
                                                                                            });

                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.d(TAG, "onFailure: fail");
                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));

                                            }

                                        } catch (Exception ex) {
                                            Log.d(TAG, "User ID empty " + ex.toString());
                                        }

                                    } else {
                                        progressDialog.dismiss();
                                        Log.d(TAG, "key mismatched");
                                    }
                                }
                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        Log.d(TAG, " pairing fail");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
            }

        } else {
            Toast.makeText(AddPartnerActivity.this, "User authentication error", Toast.LENGTH_SHORT).show();
        }
    }

    private void _getUserLoggedOnData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference mRef = db.collection("users").document(mUser.getUid());

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
                            String userID = documentSnapshot.getString("userID");
                            _currentUserName = name;
                            _currentUserEmail = email;
                            _currentUserPartnerKey = partnerKey;
                            _currentUserPhotoURL = profilePhoto;
                            _currentUserConnectedPartner = connectedPartnerName;
                            _currentUserID = userID;
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