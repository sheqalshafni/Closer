package com.sheqal.closer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.manojbhadane.QButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = "";

    private Dialog loginDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    String register;

    @BindView(R.id._btnSignIn)
    QButton btnSignIn;
    @BindView(R.id._btnSignUp)
    TextView btnSignUp;
    @BindView(R.id._email)
    EditText Email;
    @BindView(R.id._password)
    EditText Password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        _checkUserSession();
        _customString();

        btnSignUp.setOnClickListener(v -> {

            Intent _registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(_registerActivity);

        });

        btnSignIn.setOnClickListener(v -> {

            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Signing in");
            progressDialog.setCancelable(false);
            progressDialog.show();

            if (Email.getText().toString().equals("") && Password.getText().toString().equals("")) {
                Toast.makeText(LoginActivity.this, "Please fill in your credentials", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(Email.getText().toString(), Password.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                FirebaseUser user = mAuth.getCurrentUser();
                                _homeIntent();
                            } else {
                                progressDialog.dismiss();
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private void _homeIntent() {
        Intent _homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(_homeIntent);
        finish();
    }

    private void _checkUserSession() {
        if (mUser != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void _customString() {
        register = "Don't have an account? Sign up here";

        SpannableString ss = new SpannableString(register);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLUE);
        ss.setSpan(foregroundColorSpan, 23, 35, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        btnSignUp.setText(ss);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

        if (mUser != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
