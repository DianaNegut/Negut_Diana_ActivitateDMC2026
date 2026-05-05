package com.example.proiect;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proiect.database.DatabaseHelper;
import com.example.proiect.model.Restaurant;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Harta");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        DatabaseHelper db  = DatabaseHelper.getInstance(this);
        int providerId     = getIntent().getIntExtra("provider_id", -1);
        TextView tvInfo    = findViewById(R.id.tvMapInfo);
        TextView tvAddress = findViewById(R.id.tvRestaurantAddress);

        if (providerId != -1) {
            Restaurant r = db.getProviderById(providerId);
            if (r != null) {
                tvInfo.setText("Focus pe: " + r.name);
                tvAddress.setVisibility(android.view.View.VISIBLE);
                tvAddress.setText(r.nodeUrl);
            }
        } else {
            tvInfo.setText("Harta va fi disponibila in versiunea urmatoare.\n\n"
                    + db.getProviders().size()
                    + " provideri vor fi afisati cu markere.");
        }
    }
}
