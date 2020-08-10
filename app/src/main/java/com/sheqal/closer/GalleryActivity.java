package com.sheqal.closer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sheqal.closer.adapter.MemoryAdapter;
import com.sheqal.closer.models.Memory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = "GalleryActivity";

    @BindView(R.id._galleryRV)
    RecyclerView galleryRV;

    @BindView(R.id._backBtn)
    ImageView backBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private MemoryAdapter memoryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        db = FirebaseFirestore.getInstance();

        backBtn.setOnClickListener(v -> finish());
        init();
        getMemoryList();

    }

    private void init(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        galleryRV.setLayoutManager(layoutManager);
    }

    private void getMemoryList(){

        ProgressDialog progressDialog = new ProgressDialog(GalleryActivity.this);
        progressDialog.setMessage("Retrieving your memories");
        progressDialog.show();
        progressDialog.setCancelable(false);

        CollectionReference memoryRef = FirebaseFirestore.getInstance().collection("Memories");
        memoryRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                progressDialog.dismiss();
                QuerySnapshot queryDocumentSnapshots = task.getResult();
                if (queryDocumentSnapshots != null){
                    List<Memory> memories = task.getResult().toObjects(Memory.class);
                    memoryListAdapter = new MemoryAdapter(memories, GalleryActivity.this);
                    memoryListAdapter.notifyDataSetChanged();
                    galleryRV.setAdapter(memoryListAdapter);
                }
            }
        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
    }

}
