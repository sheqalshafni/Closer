package com.sheqal.closer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.BinderThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.admin.v1beta1.Progress;
import com.joaquimley.faboptions.FabOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private DocumentReference mRef;

    @BindView(R.id._fabMenu)
    FabOptions fabMenu;
    @BindView(R.id._eventRV)
    RecyclerView eventRV;
    @BindView(R.id._noConnectedPartner)
    RelativeLayout relativeLayout;

    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mRef = db.collection("users").document(mUser.getUid());

        fabMenu.setButtonsMenu(R.menu.fab_menu);
        _fabMenu();
        _eventItem();

        _loadUserData();
    }

    public void _fabMenu(){

        fabMenu.setOnClickListener(v -> {
            switch (v.getId()){
                case R.id._fabMenuMemories:
                    Intent _memoryIntent = new Intent(HomeActivity.this, MemoryActivity.class);
                    startActivity(_memoryIntent);
                    break;
                case R.id._fabMenuProfile:
                    Intent _profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(_profileIntent);
                    break;
                case R.id._fabMenuGallery:
                    Intent _galleryIntent = new Intent(HomeActivity.this, GalleryActivity.class);
                    startActivity(_galleryIntent);
                    break;
                case R.id._fabMenuSettings:
                    Intent _settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(_settingsIntent);
                    break;
                default:
            }
        });

    }

    public void _eventItem(){
        ArrayList<EventItem> eventItem = new ArrayList<>();
        eventItem.add(new EventItem("Anniversary", "501" + " days left", "3rd Anniversary", "12.3.45"));
        eventItem.add(new EventItem("Anniversary", "501" + " days left", "3rd Anniversary", "12.3.45"));
        eventItem.add(new EventItem("Anniversary", "501" + " days left", "3rd Anniversary", "12.3.45"));
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new EventAdapter(eventItem);

        eventRV.setLayoutManager(mLayoutManager);
        eventRV.setAdapter(mAdapter);
    }

    private void _loadUserData(){
        if (mUser != null){

            try {

                ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Fetching data");
                progressDialog.show();

                mRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    progressDialog.dismiss();
                                    String connectedPartner = documentSnapshot.getString("ConnectedPartner");

                                    if (!connectedPartner.equals("Unavailable")){
                                        relativeLayout.setVisibility(View.INVISIBLE);

                                        //show upcoming events

                                    }else {
                                        //update ui
                                    }

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

        }
    }

}
