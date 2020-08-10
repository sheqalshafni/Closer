package com.sheqal.closer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class MemoryActivity extends AppCompatActivity {

    private static final String TAG = "MemoryActivity";
    private static final int GALLERY_CODE = 101;
    private static final int REQUEST_CODE = 102;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private StorageReference mRef;

    String imageURL;
    Uri _imgURI;

    @BindView(R.id._txtMemoryTitle)
    EditText _memoryAddTitle;
    @BindView(R.id._memoryImage)
    ImageView _memoryAddImage;
    @BindView(R.id._memoryDate)
    EditText _memoryAddDate;
    @BindView(R.id._memoryNotes)
    EditText _memoryAddNotes;
    @BindView(R.id._btnConfirm)
    Button _btnConfirm;
    @BindView(R.id._backBtn)
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mRef = FirebaseStorage.getInstance().getReference("MemoryPhotos");

        _customDateEditText();

        backBtn.setOnClickListener(v -> finish());

        _memoryAddImage.setOnClickListener(v -> {
            _chooseImage();
            _verifyPermission();
        });

        _btnConfirm.setOnClickListener(v -> {
            _memoryCreation();
        });
    }

    private void _chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    private void _verifyPermission() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        permissions[2]) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(MemoryActivity.this, permissions, REQUEST_CODE);
        }
    }

    private void _uploadImage() {

        if (_memoryAddTitle.getText().toString().equals("")) {
            Toast.makeText(MemoryActivity.this, "Memory title cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (_memoryAddDate.getText().toString().equals("")) {
            Toast.makeText(MemoryActivity.this, "Memory date cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog progressDialog = new ProgressDialog(MemoryActivity.this);
            progressDialog.setMessage("Creating memory");
            progressDialog.show();
            progressDialog.setCancelable(false);

            StorageReference filepath = mRef.child(System.currentTimeMillis() + getExtension(_imgURI));

            UploadTask uploadTask = filepath.putFile(_imgURI);
            Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    filepath.getDownloadUrl();
                }
                return filepath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Uri downloadUri = task.getResult();
                    imageURL = downloadUri.toString();
                    _createMemory();
                } else {
                    progressDialog.dismiss();
                }
            });
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            _imgURI = data.getData();
            Glide.with(MemoryActivity.this)
                    .load(_imgURI)
                    .centerCrop()
                    .into(_memoryAddImage);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void _createMemory() {

        Map<Object, String> memory = new HashMap<>();
        memory.put("MemoryTitle", _memoryAddTitle.getText().toString());
        memory.put("MemoryPhoto", imageURL);
        memory.put("MemoryDate", _memoryAddDate.getText().toString());
        memory.put("MemoryNotes", _memoryAddNotes.getText().toString());

        db.collection("Memories").document().set(memory)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Memory Activity: Create Memory: Success"))
                .addOnFailureListener(e -> Log.d(TAG, "Memory Activity: Create Memory: Error " + e.getMessage()));

    }

    private void _memoryCreation() {
        _uploadImage();
    }

    private void _customDateEditText() {
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
                    _memoryAddDate.setText(current);
                    _memoryAddDate.setSelection(sel < current.length() ? sel : current.length());
                }

            }
        };

        _memoryAddDate.addTextChangedListener(tw);

    }
}
