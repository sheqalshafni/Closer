package com.sheqal.closer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id._btnLogout)
    TextView btnLogout;
    @BindView(R.id._settingsBtnBack)
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        btnLogout.setOnClickListener(v -> logout());
        btnBack.setOnClickListener(v -> finish());

    }

    public void logout(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure you want to logout?");
        alertDialog.setCancelable(true);

        alertDialog.setPositiveButton(Html.fromHtml("<font color='#00000'>Yes</font>"), (dialog, which) -> {
            Intent _logout = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(_logout);
        });

        alertDialog.setNegativeButton(Html.fromHtml("<font color='#00000'>No</font>"), (dialog, which) -> dialog.cancel());

        AlertDialog dialog = alertDialog.create();
        dialog.show();

    }
}
