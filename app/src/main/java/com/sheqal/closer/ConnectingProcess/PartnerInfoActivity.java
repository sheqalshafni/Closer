package com.sheqal.closer.ConnectingProcess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sheqal.closer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PartnerInfoActivity extends AppCompatActivity {

    private static final String TAG = "PartnerInfoActivity";

    private String name, email, partnerKey, url;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_info);
        ButterKnife.bind(this);

        _retrieveData();

        _btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

}
