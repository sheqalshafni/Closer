package com.sheqal.closer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

        ArrayList<EventItem> eventItems = new ArrayList<>();
        eventItems.add(new EventItem("Anniversary", "50 days left", "Our Anniversary", "8.12.2010"));
        eventItems.add(new EventItem("Birthday", "144 days left", "Sheqal's Birthday", "8.12.1997"));

        eventRV.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new EventAdapter(eventItems);

        eventRV.setLayoutManager(mLayoutManager);
        eventRV.setAdapter(mAdapter);

    }

    public void _fabMenu(){

        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        Toast.makeText(HomeActivity.this, "Gallery", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id._fabMenuSettings:
                        Intent _settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                        startActivity(_settingsIntent);
                        break;
                    default:
                }
            }
        });

    }

}
