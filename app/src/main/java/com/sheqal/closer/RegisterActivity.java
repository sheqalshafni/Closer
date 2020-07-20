package com.sheqal.closer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.manojbhadane.QButton;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int GALLERY_CODE = 101;
    private static final int REQUEST_CODE = 102;
    private static final int PICKFILE_REQUEST_CODE = 103;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private StorageReference mRef;

    boolean isVerified;
    String pk;
    String _userID;
    String imageURL;
    Uri _imgURI;

    @BindView(R.id._profileImage)
    CircleImageView _profileImg;
    @BindView(R.id._registerBtnBack)
    ImageView _btnBack;
    @BindView(R.id._userEmail)
    TextView _Email;
    @BindView(R.id._name)
    EditText _Name;
    @BindView(R.id._randomPartnerKey)
    QButton _rngPK;
    @BindView(R.id._partnerKey)
    EditText _partnerKey;
    @BindView(R.id._btnConfirm)
    Button _Confirm;
    @BindView(R.id._password)
    EditText Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mRef = FirebaseStorage.getInstance().getReference("ProfilePhoto");

        //Generate random key
        _rngPK.setText(RegisterActivity.RandomString.getAlphaNumericString(6));

        //Get generated key
        pk = _rngPK.getText().toString();

        _btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        _Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _accountCreation();
            }
        });

        _profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _chooseImage();
                _verifyPermission();
            }
        });
    }

    public static boolean isValidEmail(CharSequence validEmail) {
        return (!TextUtils.isEmpty(validEmail) && Patterns.EMAIL_ADDRESS.matcher(validEmail).matches());
    }

    public static class RandomString {

        // function to generate a random string of length n
        static String getAlphaNumericString(int n) {

            // chose a Character random from this String
            String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "0123456789";

            // create StringBuffer size of AlphaNumericString
            StringBuilder sb = new StringBuilder(n);

            for (int i = 0; i < n; i++) {

                // generate a random number between
                // 0 to AlphaNumericString variable length
                int index
                        = (int) (AlphaNumericString.length()
                        * Math.random());

                // add Character one by one in end of sb
                sb.append(AlphaNumericString
                        .charAt(index));
            }

            return sb.toString();
        }

        public void main(String[] args) {

            // Get the size n
            int n = 6;

            // Get and display the alphanumeric string
            System.out.println(RegisterActivity.RandomString
                    .getAlphaNumericString(n));
        }
    }

    private void _homeIntent() {
        Intent homeIntent = new Intent(RegisterActivity.this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }

    private void _chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    private void _verifyPermission() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,};

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        permissions[2]) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(RegisterActivity.this, permissions, REQUEST_CODE);
        }
    }

    private void _uploadImage() {

        ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Registering, please wait");
        progressDialog.show();

        StorageReference filepath = mRef.child(System.currentTimeMillis() + getExtension(_imgURI));

        UploadTask uploadTask = filepath.putFile(_imgURI);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    filepath.getDownloadUrl();
                }
                return filepath.getDownloadUrl();
            }

        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Uri downloadUri = task.getResult();
                    imageURL = downloadUri.toString();
                    _createUser();
                    Log.d(TAG, "image url " + imageURL);
                } else {
                    progressDialog.dismiss();
                    Log.d(TAG, "onComplete: " + task.getException().toString());
                }
            }
        });

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
            Glide.with(RegisterActivity.this)
                    .load(_imgURI)
                    .centerCrop()
                    .into(_profileImg);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void _createUser() {


        if (_Email.getText().toString().equals("")) {
            Toast.makeText(RegisterActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (!isValidEmail(_Email.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
        } else if (Password.getText().toString().equals("")) {
            Toast.makeText(RegisterActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (Password.getText().length() < 8) {
            Toast.makeText(RegisterActivity.this, "Password is too short", Toast.LENGTH_SHORT).show();
        } else if (_Name.getText().toString().equals("")) {
            Toast.makeText(RegisterActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (!_partnerKey.getText().toString().equals(pk)) {
            Toast.makeText(RegisterActivity.this, "Partner Key does not match", Toast.LENGTH_SHORT).show();
        } else {

            mAuth.createUserWithEmailAndPassword(_Email.getText().toString(), Password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                try {

                                    mAuth.getCurrentUser().sendEmailVerification();
                                    _userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    String _partner = "";

                                    Map<String, Object> user = new HashMap<>();
                                    user.put("Name", _Name.getText().toString());
                                    user.put("Email", _Email.getText().toString());
                                    user.put("PartnerKey", _partnerKey.getText().toString());
                                    user.put("ProfilePhotoURL", imageURL);
                                    user.put("ConnectedPartner", _partner);
                                    user.put("userID", _userID);

                                    db.collection("users").document(_userID).set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "onSuccess: file uploaded @ at " + _userID);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: fail");
                                                }
                                            });

                                    _homeIntent();

                                } catch (Exception ex) {
                                    Log.d(TAG, "failed " + ex.toString());
                                }

                            } else {
                                Log.w(TAG, "fail", task.getException());
                                //Toast.makeText(RegisterActivity.this, "Email is already registered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void _accountCreation() {
        _uploadImage();
        _createUser();
    }

}
