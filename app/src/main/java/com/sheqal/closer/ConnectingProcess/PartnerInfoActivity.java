package com.sheqal.closer.ConnectingProcess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sheqal.closer.R;

import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PartnerInfoActivity extends AppCompatActivity {

    private static final String TAG = "PartnerInfoActivity";

    private String name, email, partnerKey, url;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @BindView(R.id._partnerInfoImage)
    CircleImageView _profileImage;
    @BindView(R.id._partnerInfoName)
    TextView _Name;
    @BindView(R.id._partnerInfoEmail)
    TextView _Email;
    @BindView(R.id._partnerInfoKey)
    TextView _partnerKey;
    @BindView(R.id._partnerInfoBtnBack)
    ImageView _btnBack;
    @BindView(R.id._btnConfirm)
    Button _btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_info);
        ButterKnife.bind(this);

        _retrieveData();
        _checkBiometricAvailability();
        _checkBiometric();

        _btnBack.setOnClickListener(v -> finish());

        _btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _promptBiometric();
            }
        });

    }

    private void _retrieveData() {
        Bundle getData = getIntent().getExtras();
        if (getData != null) {
            name = getData.getString("name");
            email = getData.getString("email");
            partnerKey = getData.getString("key");
            url = getData.getString("url");

            _Name.setText(name);
            _Email.setText(email);
            _partnerKey.setText(partnerKey);
            Glide.with(PartnerInfoActivity.this)
                    .load(url)
                    .into(_profileImage);

        }
    }

    private void _checkBiometric() {

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(PartnerInfoActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });



    }

    private void _promptBiometric(){
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Confirmation")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void _checkBiometricAvailability() {

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.e("MY_APP_TAG", "The user hasn't associated " +
                        "any biometric credentials with their account.");
                break;
        }

    }

}
