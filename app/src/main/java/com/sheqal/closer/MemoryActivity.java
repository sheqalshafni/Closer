package com.sheqal.closer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MemoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id._backBtn)
    ImageView backBtn;
    @BindView(R.id._spinnerMemoryType)
    Spinner _memoryType;

    String[] itemType = new String[]{"Select Type","Anniversary", "Birthday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        ButterKnife.bind(this);

        ArrayAdapter<String>adapter = new ArrayAdapter<String>(MemoryActivity.this, android.R.layout.simple_spinner_dropdown_item, itemType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _memoryType.setAdapter(adapter);
        _memoryType.setOnItemSelectedListener(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                //Do nothing
                break;
            case 1:
                Toast.makeText(MemoryActivity.this, "Anniversary", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(MemoryActivity.this, "Birthday", Toast.LENGTH_SHORT).show();
                break;
            default:
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
