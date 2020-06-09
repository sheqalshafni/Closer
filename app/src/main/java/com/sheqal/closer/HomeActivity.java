package com.sheqal.closer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.BinderThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joaquimley.faboptions.FabOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id._fabMenu)
    FabOptions fabMenu;
    @BindView(R.id._eventRV)
    RecyclerView eventRV;

    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        fabMenu.setButtonsMenu(R.menu.fab_menu);
        _fabMenu();
        _eventItem();

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

    public void _eventItem(){
        ArrayList<EventItem> eventItem = new ArrayList<>();
        eventItem.add(new EventItem("Anniversary", "501" + " days left", "3rd Anniversary", "12.3.45"));
        eventItem.add(new EventItem("Anniversary", "501" + " days left", "3rd Anniversary", "12.3.45"));
        eventItem.add(new EventItem("Anniversary", "501" + " days left", "3rd Anniversary", "12.3.45"));
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new EventAdapter(eventItem);

        eventRV.setLayoutManager(mLayoutManager);
        eventRV.setAdapter(mAdapter);
    }

}
