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

    //Firebase init
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private CollectionReference mRef;

    //Current user info
    private String _currentUserName, _currentUserEmail, _currentUserPartnerKey, _currentUserPhotoURL, _currentUserID, _currentUserConnectedPartner;

    //Partner B info
    private String partnerB_Name, partnerB_Email, partnerB_PartnerKey, partnerB_PhotoURL, partnerB_UID, partnerB_connectedPartner;

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

        _getUserLoggedOnData();

        _btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> connectPartner());

    }

    private void connectPartner()   {

        if (mUser != null) {

            ProgressDialog progressDialog = new ProgressDialog(AddPartnerActivity.this);
            progressDialog.setMessage("Connecting you with your partner");
            progressDialog.setCancelable(false);

            String getKey = partnerKey.getText().toString();

            if (partnerKey.getText().toString().equals("")) {
                Toast.makeText(AddPartnerActivity.this, "Invalid partner key", Toast.LENGTH_SHORT).show();
            }else {
                progressDialog.show();

                //Find user equal to partner key
                Query userQuery = mRef.whereEqualTo("PartnerKey", partnerKey.getText().toString());

                userQuery.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();

                        mRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String key;
                                key = documentSnapshot.getString("PartnerKey");
                                if (partnerKey.getText().toString().equals(key)) {

                                    /*Get Partner B info if "Partner Key" input
                                    is equal to Partner B "Partner Key"*/

                                    partnerB_Name = documentSnapshot.getString("Name");
                                    partnerB_PhotoURL = documentSnapshot.getString("ProfilePhotoURL");
                                    partnerB_UID = documentSnapshot.getString("userID");
                                    partnerB_Email = documentSnapshot.getString("Email");
                                    partnerB_PartnerKey = documentSnapshot.getString("PartnerKey");
                                    partnerB_connectedPartner = documentSnapshot.getString("ConnectedPartner");

                                    Log.d(TAG, "retrieved name " + partnerB_Name + " img url " + partnerB_PhotoURL + " userid " + partnerB_UID + " email " + partnerB_Email);

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    try {

                                        DocumentReference mRef = db.collection("users").document(partnerB_UID);

                                        mRef.get()
                                                .addOnSuccessListener(documentSnapshot1 -> {
                                                    if (documentSnapshot1.exists()) {

                                                        /* Get Partner B info */

                                                        String name = documentSnapshot1.getString("Name");
                                                        String email = documentSnapshot1.getString("Email");
                                                        String partnerKey = documentSnapshot1.getString("PartnerKey");
                                                        String profilePhoto = documentSnapshot1.getString("ProfilePhotoURL");
                                                        String connectedPartnerName = documentSnapshot1.getString("ConnectedPartner");
                                                        String userID = documentSnapshot1.getString("userID");

                                                        Map<String, Object> user = new HashMap<>();
                                                        user.put("Name", partnerB_Name);
                                                        user.put("Email", partnerB_Email);
                                                        user.put("PartnerKey", partnerB_PartnerKey);
                                                        user.put("ProfilePhotoURL", partnerB_PhotoURL);
                                                        user.put("ConnectedPartner", _currentUserName);
                                                        user.put("userID", userID);

                                                        /* Send Partner B info to the next Activity */

                                                        Intent passData = new Intent(AddPartnerActivity.this, PartnerInfoActivity.class);

                                                        passData.putExtra("name", name);
                                                        passData.putExtra("email", email);
                                                        passData.putExtra("key", partnerKey);
                                                        passData.putExtra("url", profilePhoto);
                                                        passData.putExtra("userID", partnerB_UID);
                                                        passData.putExtra("partner", partnerB_connectedPartner);
                                                        startActivity(passData);

                                                        Log.d(TAG, "partner name " + _currentUserName);

                                                        _getUserLoggedOnData();

                                                        //Change partner to connected partner
                                                        /*db.collection("users").document(c).set(user)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    Log.d(TAG, " Connection success " + _currentUserName + " + " + a);

                                                                    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                                                                    DocumentReference mRef1 = db1.collection("users").document(mUser.getUid());

                                                                    _getUserLoggedOnData();

                                                                    Map<String, Object> user1 = new HashMap<>();

                                                                    user1.put("Name", _currentUserName);
                                                                    user1.put("Email", _currentUserEmail);
                                                                    user1.put("PartnerKey", _currentUserPartnerKey);
                                                                    user1.put("ProfilePhotoURL", _currentUserPhotoURL);
                                                                    user1.put("ConnectedPartner", a);
                                                                    user1.put("userID", _currentUserID);

                                                                    db1.collection("users").document(mUser.getUid()).set(user1)
                                                                            .addOnSuccessListener(aVoid1 -> Log.d(TAG, "Connection success " + a + " + " + _currentUserName))
                                                                            .addOnFailureListener(e -> Log.d(TAG, "connection failed " + e.toString()));

                                                                })
                                                                .addOnFailureListener(e -> Log.d(TAG, "onFailure: fail"));*/

                                                    }
                                                })
                                                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));

                                    } catch (Exception ex) {
                                        Log.d(TAG, "User ID empty " + ex.toString());
                                    }

                                } else {
                                    progressDialog.dismiss();
                                    Log.d(TAG, "key mismatched");
                                }
                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        Log.d(TAG, " pairing fail");
                        Toast.makeText(AddPartnerActivity.this, "Unable to pair. Check log", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onFailure: " + e.toString());
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
                        _currentUserPhotoURL = profilePhoto;_currentUserConnectedPartner = connectedPartnerName;

                        _currentUserID = userID;
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));

    }
}
