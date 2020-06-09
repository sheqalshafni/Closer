package com.sheqal.closer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manojbhadane.QButton;

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
    QButton btnSignUp;
    @BindView(R.id._email)
    EditText Email;
    @BindView(R.id._password)
    EditText Password;

    /*variables for showDialog method*/
    TextView _Email;
    EditText _Name;
    QButton _rngPK;
    EditText _partnerKey;
    Button _Confirm;

    private Dialog loginDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Email.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                }else if (!isValidEmail(Email.getText().toString())){
                    Toast.makeText(LoginActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                }else if (Password.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                }else if (Password.getText().length() < 8){
                    Toast.makeText(LoginActivity.this, "Password is too short", Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.createUserWithEmailAndPassword(Email.getText().toString(), Password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        _showDialog();
                                    } else {
                                        Log.w(TAG, "fail", task.getException());

                                    }
                                }
                            });
                }

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

    public static boolean isValidEmail(CharSequence validEmail){
        return (!TextUtils.isEmpty(validEmail) && Patterns.EMAIL_ADDRESS.matcher(validEmail).matches());
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

        _rngPK.setText(RandomString.getAlphaNumericString(6));

        String email = Email.getText().toString();
        _Email.setText(email);

        //get generated key
        String pk = _rngPK.getText().toString();

        _Confirm.setOnClickListener(v -> {

            if(_Name.getText().toString().equals("")){
                Toast.makeText(LoginActivity.this, "Name field cannot be empty", Toast.LENGTH_SHORT).show();
            }else if (_partnerKey.getText().toString().equals("")){
                Toast.makeText(LoginActivity.this, "PK cannot be empty", Toast.LENGTH_SHORT).show();
            } else if (!_partnerKey.getText().toString().equals(pk)){
                Toast.makeText(LoginActivity.this, "Partner Key does not match", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(LoginActivity.this, CompleteActivity.class);
                startActivity(intent);
                finish();
            }

        });

    }

    public static class RandomString {

        // function to generate a random string of length n
        static String getAlphaNumericString(int n)
        {

            // chose a Character random from this String
            String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "0123456789";

            // create StringBuffer size of AlphaNumericString
            StringBuilder sb = new StringBuilder(n);

            for (int i = 0; i < n; i++) {

                // generate a random number between
                // 0 to AlphaNumericString variable length
                int index
                        = (int)(AlphaNumericString.length()
                        * Math.random());

                // add Character one by one in end of sb
                sb.append(AlphaNumericString
                        .charAt(index));
            }

            return sb.toString();
        }

        public void main(String[] args)
        {

            // Get the size n
            int n = 6;

            // Get and display the alphanumeric string
            System.out.println(RandomString
                    .getAlphaNumericString(n));
        }
    }

}
