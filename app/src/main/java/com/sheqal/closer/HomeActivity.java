package com.sheqal.closer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.joaquimley.faboptions.FabOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id._fabMenu)
    FabOptions fabMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        fabMenu.setButtonsMenu(R.menu.fab_menu);
        _fabMenu();

    }

    public void _fabMenu(){

        fabMenu.setOnClickListener(v -> {
            switch (v.getId()){
                case R.id._fabMenuMemories:
                    Intent _memoryIntent = new Intent(HomeActivity.this, MemoryActivity.class);
                    startActivity(_memoryIntent);
                    break;
                case R.id._fabMenuProfile:
                    Intent _profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(_profileIntent);
                    break;
                case R.id._fabMenuGallery:
                    Intent _galleryIntent = new Intent(HomeActivity.this, GalleryActivity.class);
                    startActivity(_galleryIntent);
                    break;
                case R.id._fabMenuSettings:
                    Intent _settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(_settingsIntent);
                    break;
                default:
            }
        });

    }

}
