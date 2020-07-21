package com.sheqal.closer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.joaquimley.faboptions.FabOptions;
import com.sheqal.closer.adapter.EventAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import aglibs.loading.skeleton.layout.SkeletonRelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private DocumentReference mRef;
    private CollectionReference collectionReference;

    String combinedKeys;

    @BindView(R.id._fabMenu)
    FabOptions fabMenu;
    @BindView(R.id._eventRV)
    RecyclerView eventRV;
    @BindView(R.id._firstPartner)
    CircleImageView firstPartnerImage;
    @BindView(R.id._secondPartner)
    CircleImageView secondPartnerImage;
    @BindView(R.id._countedDays)
    TextView countedDays;

    //Layouts
    @BindView(R.id._topView)
    SkeletonRelativeLayout topView;
    @BindView(R.id.noPartnerLayout)
    RelativeLayout _noPartnerLayout;
    @BindView(R.id._coverLayout)
    RelativeLayout coverLayout;

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
        _checkPartnerStatus();

    }

    public void _fabMenu() {

        fabMenu.setOnClickListener(v -> {
            switch (v.getId()) {
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

    public void _eventItem() {
        ArrayList<EventItem> eventItem = new ArrayList<>();
        eventItem.add(new EventItem("Anniversary", "501" + " days left", "3rd Anniversary", "12.3.45"));
        eventItem.add(new EventItem("Anniversary", "501" + " days left", "3rd Anniversary", "12.3.45"));
        eventItem.add(new EventItem("Anniversary", "501" + " days left", "3rd Anniversary", "12.3.45"));
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new EventAdapter(eventItem);

        eventRV.setLayoutManager(mLayoutManager);
        eventRV.setAdapter(mAdapter);
    }

    private void _checkPartnerStatus() {
        if (mUser != null) {
            try {

                ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Synchronizing with server");
                progressDialog.show();
                progressDialog.setCancelable(false);

                mRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    progressDialog.dismiss();
                                    String connectedPartnerName = documentSnapshot.getString("ConnectedPartner");
                                    String partnerAkey = documentSnapshot.getString("PartnerKey");
                                    String partnerBkey = documentSnapshot.getString("PartnerBKey");

                                    combinedKeys = partnerBkey + partnerAkey;

                                    if (connectedPartnerName.isEmpty()) {
                                        coverLayout.setVisibility(View.GONE);
                                    } else {
                                        coverLayout.setVisibility(View.GONE);
                                        _noPartnerLayout.setVisibility(View.GONE);
                                        _loadUserData();
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
        } else {
            Toast.makeText(HomeActivity.this, "Unable to fetch user data", Toast.LENGTH_SHORT).show();
        }
    }

    private void _loadUserData() {

        if (mUser != null) {

            collectionReference = db.collection("ConnectedCouples");

            Query userQuery = collectionReference.whereEqualTo("PartnerA_UID", mUser.getUid());

            userQuery.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String key = documentSnapshot.getString("CombinedKey");
                            String partnerA_PhotoURL = documentSnapshot.getString("PartnerA_PhotoURL");
                            String partnerB_PhotoURL = documentSnapshot.getString("PartnerB_PhotoURL");
                            String officialDate = documentSnapshot.getString("OfficialDate");

                            //Get Current Date
                            Date c = Calendar.getInstance().getTime();
                            System.out.println("Current time => " + c);

                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            String formattedDate = df.format(c);

                            boolean flag = sameChars(key, combinedKeys);

                            if (flag) {
                                topView.stopLoading();
                                Glide.with(HomeActivity.this)
                                        .load(partnerA_PhotoURL)
                                        .into(firstPartnerImage);

                                Glide.with(HomeActivity.this)
                                        .load(partnerB_PhotoURL)
                                        .into(secondPartnerImage);

                                countedDays.setText(getCountOfDays(formattedDate, officialDate).replaceAll("-", ""));
                            }
                        }

                    });
                }
            });

        }

    }

    private boolean sameChars(String firstStr, String secondStr) {
        char[] first = firstStr.toCharArray();
        char[] second = secondStr.toCharArray();
        Arrays.sort(first);
        Arrays.sort(second);
        return Arrays.equals(first, second);
    }

    public String getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }
        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return ("" + (int) dayCount);
    }

}
