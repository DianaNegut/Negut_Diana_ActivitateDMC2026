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

import java.util.Locale;

public class CompareActivity extends AppCompatActivity {

    public static final String EXTRA_ID1 = "provider_id_1";
    public static final String EXTRA_ID2 = "provider_id_2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

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

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Restaurant p1 = db.getProviderById(getIntent().getIntExtra(EXTRA_ID1, -1));
        Restaurant p2 = db.getProviderById(getIntent().getIntExtra(EXTRA_ID2, -1));

        if (p1 == null || p2 == null) { finish(); return; }

        float rating1 = db.getAverageRating(p1.id);
        float rating2 = db.getAverageRating(p2.id);

        ((TextView) findViewById(R.id.tvName1)).setText(p1.name);
        ((TextView) findViewById(R.id.tvName2)).setText(p2.name);

        ((TextView) findViewById(R.id.tvRegion1)).setText(p1.region);
        ((TextView) findViewById(R.id.tvRegion2)).setText(p2.region);

        ((TextView) findViewById(R.id.tvStorage1)).setText(p1.storageCapacity != null ? p1.storageCapacity : "-");
        ((TextView) findViewById(R.id.tvStorage2)).setText(p2.storageCapacity != null ? p2.storageCapacity : "-");

        String price1 = (p1.pricePerGB != null && !p1.pricePerGB.isEmpty()) ? "$" + p1.pricePerGB + "/GB" : "-";
        String price2 = (p2.pricePerGB != null && !p2.pricePerGB.isEmpty()) ? "$" + p2.pricePerGB + "/GB" : "-";
        TextView tvPrice1 = findViewById(R.id.tvPrice1);
        TextView tvPrice2 = findViewById(R.id.tvPrice2);
        tvPrice1.setText(price1);
        tvPrice2.setText(price2);

        // evidentiaza pretul mai mic
        highlightBetter(tvPrice1, tvPrice2,
                p1.pricePerGB != null && !p1.pricePerGB.isEmpty() ? parseFloat(p1.pricePerGB) : Float.MAX_VALUE,
                p2.pricePerGB != null && !p2.pricePerGB.isEmpty() ? parseFloat(p2.pricePerGB) : Float.MAX_VALUE,
                false);

        String uptime1 = (p1.uptime != null && !p1.uptime.isEmpty()) ? p1.uptime + "%" : "-";
        String uptime2 = (p2.uptime != null && !p2.uptime.isEmpty()) ? p2.uptime + "%" : "-";
        TextView tvUptime1 = findViewById(R.id.tvUptime1);
        TextView tvUptime2 = findViewById(R.id.tvUptime2);
        tvUptime1.setText(uptime1);
        tvUptime2.setText(uptime2);

        // evidentiaza uptime mai mare
        highlightBetter(tvUptime1, tvUptime2,
                p1.uptime != null && !p1.uptime.isEmpty() ? parseFloat(p1.uptime) : 0f,
                p2.uptime != null && !p2.uptime.isEmpty() ? parseFloat(p2.uptime) : 0f,
                true);

        TextView tvRating1 = findViewById(R.id.tvRating1);
        TextView tvRating2 = findViewById(R.id.tvRating2);
        tvRating1.setText(rating1 > 0 ? String.format(Locale.getDefault(), "%.1f ★", rating1) : "—");
        tvRating2.setText(rating2 > 0 ? String.format(Locale.getDefault(), "%.1f ★", rating2) : "—");

        // evidentiaza rating mai mare
        highlightBetter(tvRating1, tvRating2, rating1, rating2, true);

        // Verdict final
        TextView tvVerdict1 = findViewById(R.id.tvVerdict1);
        TextView tvVerdict2 = findViewById(R.id.tvVerdict2);
        int score1 = getScore(p1, rating1);
        int score2 = getScore(p2, rating2);
        if (score1 > score2) {
            tvVerdict1.setText("✅");
            tvVerdict2.setText("❌");
        } else if (score2 > score1) {
            tvVerdict1.setText("❌");
            tvVerdict2.setText("✅");
        } else {
            tvVerdict1.setText("🤝");
            tvVerdict2.setText("🤝");
        }
    }

    private void highlightBetter(TextView tv1, TextView tv2, float val1, float val2, boolean higherIsBetter) {
        int green  = 0xFF2E7D32;
        int normal = 0xFF0D1117;
        if (val1 == val2 || val1 == Float.MAX_VALUE || val2 == Float.MAX_VALUE) return;
        boolean first = higherIsBetter ? (val1 > val2) : (val1 < val2);
        tv1.setTextColor(first ? green : normal);
        tv2.setTextColor(first ? normal : green);
    }

    private int getScore(Restaurant r, float rating) {
        int score = 0;
        if (rating > 0) score += (int)(rating * 10);
        if (r.uptime != null && !r.uptime.isEmpty()) score += (int)(parseFloat(r.uptime) * 2);
        if (r.pricePerGB != null && !r.pricePerGB.isEmpty()) score -= (int)(parseFloat(r.pricePerGB) * 100);
        return score;
    }

    private float parseFloat(String s) {
        try { return Float.parseFloat(s); } catch (Exception e) { return 0f; }
    }
}
