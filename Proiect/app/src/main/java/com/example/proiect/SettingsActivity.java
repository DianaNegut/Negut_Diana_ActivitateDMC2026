package com.example.proiect;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proiect.database.DatabaseHelper;

import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    private static final String[] REGIONS =
            {"Toate", "EU-West", "EU-East", "NA", "Asia-Pacific", "South-America"};

    private TextView tvCurrentCategory;
    private Spinner  spinnerCategory;
    private Button   btnSaveCategory;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = DatabaseHelper.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvCurrentCategory = findViewById(R.id.tvCurrentCategory);
        spinnerCategory   = findViewById(R.id.spinnerCategory);
        btnSaveCategory   = findViewById(R.id.btnSaveCategory);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, REGIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        String current = db.getUserPref("preferred_region", "Toate");
        tvCurrentCategory.setText(current);

        int idx = Arrays.asList(REGIONS).indexOf(current);
        if (idx >= 0) spinnerCategory.setSelection(idx);

        btnSaveCategory.setOnClickListener(v -> {
            String selected = REGIONS[spinnerCategory.getSelectedItemPosition()];
            db.setUserPref("preferred_region", selected);
            tvCurrentCategory.setText(selected);
            Toast.makeText(this, "Preferinta salvata!", Toast.LENGTH_SHORT).show();
        });
    }
}
