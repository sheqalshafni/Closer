package com.sheqal.closer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.manojbhadane.QButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id._btnSignIn)
    QButton btnSignIn;
    @BindView(R.id._btnSignUp)
    QButton btnSignUp;
    @BindView(R.id._email)
    TextView Email;
    @BindView(R.id._password)
    TextView Password;

    TextView _Email;
    EditText _Name;
    QButton _rngPK;
    EditText _partnerKey;
    Button _Confirm;

    Dialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _showDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Sign in success", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void _showDialog (){

        loginDialog = new Dialog(this);
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loginDialog.setContentView(R.layout.info_dialog);
        loginDialog.setCancelable(true);

        loginDialog.show();

        _Email = loginDialog.findViewById(R.id._userEmail);
        _Name = loginDialog.findViewById(R.id._name);
        _rngPK =  loginDialog.findViewById(R.id._randomPartnerKey);
        _partnerKey = loginDialog.findViewById(R.id._partnerKey);
        _Confirm = loginDialog.findViewById(R.id._btnConfirm);

        _Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CompleteActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
