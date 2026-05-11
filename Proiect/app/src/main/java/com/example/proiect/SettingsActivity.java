package com.example.proiect;

import android.content.SharedPreferences;
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

import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    static final String PREFS_NAME = "cloudpod_prefs";
    static final String KEY_REGION = "preferred_region";

    private static final String[] REGIONS =
            {"Toate", "EU-West", "EU-East", "NA", "Asia-Pacific", "South-America"};

    private TextView tvCurrentCategory;
    private Spinner  spinnerCategory;
    private Button   btnSaveCategory;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

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

        String current = prefs.getString(KEY_REGION, "Toate");
        tvCurrentCategory.setText(current);

        int idx = Arrays.asList(REGIONS).indexOf(current);
        if (idx >= 0) spinnerCategory.setSelection(idx);

        btnSaveCategory.setOnClickListener(v -> {
            String selected = REGIONS[spinnerCategory.getSelectedItemPosition()];
            prefs.edit().putString(KEY_REGION, selected).apply();
            tvCurrentCategory.setText(selected);
            Toast.makeText(this, "Preferinta salvata!", Toast.LENGTH_SHORT).show();
        });
    }
}
