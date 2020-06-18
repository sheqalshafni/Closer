package com.sheqal.closer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.UnicodeSetSpanner;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manojbhadane.QButton;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "";
    @BindView(R.id._btnSignIn)
    QButton btnSignIn;
    @BindView(R.id._btnSignUp)
    TextView btnSignUp;
    @BindView(R.id._email)
    EditText Email;
    @BindView(R.id._password)
    EditText Password;

    private Dialog loginDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    boolean isVerified;
    int count = 0;

    String register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        customString();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent _registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(_registerActivity);

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent _homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(_homeIntent);
                finish();
            }
        });

    }

    private void customString(){
        register = "Don't have an account? Sign up here";

        SpannableString ss = new SpannableString(register);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLUE);
        ss.setSpan(foregroundColorSpan, 23, 35, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        btnSignUp.setText(ss);
    }

}
