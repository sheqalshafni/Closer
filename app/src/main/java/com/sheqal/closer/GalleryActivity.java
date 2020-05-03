package com.sheqal.closer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryActivity extends AppCompatActivity {

    @BindView(R.id._galleryRV)
    RecyclerView galleryRV;

    @BindView(R.id._backBtn)
    ImageView backBtn;

    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);

        ArrayList<GalleryItem> galleryItems = new ArrayList<>();
        galleryItems.add(new GalleryItem(R.drawable.cat, "25.12.2020"));
        galleryItems.add(new GalleryItem(R.drawable.cat, "25.12.2020"));
        galleryItems.add(new GalleryItem(R.drawable.cat, "25.12.2020"));
        galleryItems.add(new GalleryItem(R.drawable.cat, "25.12.2020"));
        galleryItems.add(new GalleryItem(R.drawable.cat, "25.12.2020"));
        galleryRV.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new GalleryAdapter(galleryItems);

        galleryRV.setLayoutManager(mLayoutManager);
        galleryRV.setAdapter(mAdapter);

        backBtn.setOnClickListener(v -> finish());

    }
}
