package com.example.proiect;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proiect.adapter.ReviewAdapter;
import com.example.proiect.database.DatabaseHelper;
import com.example.proiect.model.Restaurant;
import com.example.proiect.model.Review;
import com.example.proiect.view.BarChartView;
import com.example.proiect.view.PieChartView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private PieChartView pieChartView;
    private BarChartView barChartView;
    private ListView listViewRecentRatings;
    private TextView tvUserName, tvPreferredCategory, tvAvatarLetter;
    private LinearLayout layoutTopProviders;
    private ImageView ivTopArrow;
    private boolean topExpanded = false;
    private ImageView ivPieArrow;
    private boolean pieExpanded = false;
    private ImageView ivBarArrow;
    private boolean barExpanded = false;
    private TextView tvSeeMore;
    private List<Review> allRecentRatings;
    private int visibleRatingCount = 5;

    private static final String[] REGIONS =
            {"EU-West", "EU-East", "NA", "Asia-Pacific", "South-America"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Statistici");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db                    = DatabaseHelper.getInstance(this);
        pieChartView          = findViewById(R.id.pieChartView);
        barChartView          = findViewById(R.id.barChartView);
        listViewRecentRatings = findViewById(R.id.listViewRecentReviews);
        tvUserName            = findViewById(R.id.tvUserName);
        tvPreferredCategory   = findViewById(R.id.tvPreferredCategory);
        tvAvatarLetter        = findViewById(R.id.tvAvatarLetter);
        layoutTopProviders    = findViewById(R.id.layoutTopRestaurants);
        ivTopArrow            = findViewById(R.id.ivTopArrow);
        ivPieArrow            = findViewById(R.id.ivPieArrow);
        ivBarArrow            = findViewById(R.id.ivBarArrow);
        tvSeeMore             = findViewById(R.id.tvSeeMore);

        findViewById(R.id.headerTopRestaurants).setOnClickListener(v -> toggleTopProviders());
        findViewById(R.id.headerPieChart).setOnClickListener(v -> togglePieChart());
        findViewById(R.id.headerBarChart).setOnClickListener(v -> toggleBarChart());
        tvSeeMore.setOnClickListener(v -> loadMoreRatings());

        loadTopProviders();
        loadUserPrefs();
        loadChartData();
        loadRecentRatings();
    }

    private void toggleTopProviders() {
        topExpanded = !topExpanded;
        layoutTopProviders.setVisibility(topExpanded ? View.VISIBLE : View.GONE);
        ivTopArrow.setRotation(topExpanded ? 180f : 0f);
    }

    private void togglePieChart() {
        pieExpanded = !pieExpanded;
        pieChartView.setVisibility(pieExpanded ? View.VISIBLE : View.GONE);
        ivPieArrow.setRotation(pieExpanded ? 180f : 0f);
    }

    private void toggleBarChart() {
        barExpanded = !barExpanded;
        barChartView.setVisibility(barExpanded ? View.VISIBLE : View.GONE);
        ivBarArrow.setRotation(barExpanded ? 180f : 0f);
    }

    private void loadTopProviders() {
        List<Restaurant> top = db.getTopRatedProviders(5);
        layoutTopProviders.removeAllViews();

        for (int i = 0; i < top.size(); i++) {
            Restaurant r = top.get(i);
            float avg = db.getAverageRating(r.id);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.bottomMargin = (int) (8 * getResources().getDisplayMetrics().density);
            row.setLayoutParams(rowParams);

            TextView tvRank = new TextView(this);
            tvRank.setText(String.valueOf(i + 1) + ".");
            tvRank.setTextSize(15);
            tvRank.setTextColor(0xFF1E88E5);
            tvRank.setTypeface(null, android.graphics.Typeface.BOLD);
            tvRank.setMinWidth((int) (28 * getResources().getDisplayMetrics().density));
            row.addView(tvRank);

            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvName = new TextView(this);
            tvName.setText(r.name);
            tvName.setTextSize(14);
            tvName.setTextColor(0xFF212121);
            tvName.setTypeface(null, android.graphics.Typeface.BOLD);
            info.addView(tvName);

            TextView tvRegion = new TextView(this);
            tvRegion.setText(r.region);
            tvRegion.setTextSize(12);
            tvRegion.setTextColor(0xFF374151);
            info.addView(tvRegion);

            row.addView(info);

            LinearLayout ratingLayout = new LinearLayout(this);
            ratingLayout.setOrientation(LinearLayout.VERTICAL);
            ratingLayout.setGravity(Gravity.CENTER_HORIZONTAL);

            RatingBar ratingBar = new RatingBar(this, null, android.R.attr.ratingBarStyleSmall);
            ratingBar.setNumStars(5);
            ratingBar.setMax(5);
            ratingBar.setRating(avg);
            ratingBar.setIsIndicator(true);
            ratingLayout.addView(ratingBar);

            TextView tvAvg = new TextView(this);
            tvAvg.setText(String.format(Locale.getDefault(), "%.1f / 5", avg));
            tvAvg.setTextSize(12);
            tvAvg.setTextColor(0xFF374151);
            tvAvg.setGravity(Gravity.CENTER);
            ratingLayout.addView(tvAvg);

            row.addView(ratingLayout);
            layoutTopProviders.addView(row);

            if (i < top.size() - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                dp.bottomMargin = (int) (8 * getResources().getDisplayMetrics().density);
                divider.setLayoutParams(dp);
                divider.setBackgroundColor(0xFFEEEEEE);
                layoutTopProviders.addView(divider);
            }
        }
    }

    private void loadUserPrefs() {
        String name   = db.getUserPref("user_name", "Utilizator");
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        String region = prefs.getString(SettingsActivity.KEY_REGION, "Toate");
        tvUserName.setText(name);
        tvPreferredCategory.setText(region);
        tvAvatarLetter.setText(name.isEmpty() ? "?" : String.valueOf(name.charAt(0)).toUpperCase());
    }

    private void loadChartData() {
        List<Restaurant> providers = db.getProviders();

        Map<String, Integer> countMap  = new LinkedHashMap<>();
        Map<String, List<Float>> ratings = new LinkedHashMap<>();

        for (String region : REGIONS) {
            countMap.put(region, 0);
            ratings.put(region, new ArrayList<>());
        }

        for (Restaurant r : providers) {
            if (countMap.containsKey(r.region)) {
                countMap.put(r.region, countMap.get(r.region) + 1);
                float avg = db.getAverageRating(r.id);
                if (avg > 0) ratings.get(r.region).add(avg);
            }
        }

        Map<String, Integer> pieData = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : countMap.entrySet()) {
            if (e.getValue() > 0) pieData.put(e.getKey(), e.getValue());
        }
        pieChartView.setData(pieData);

        Map<String, Float> barData = new LinkedHashMap<>();
        for (String region : REGIONS) {
            List<Float> list = ratings.get(region);
            if (list != null && !list.isEmpty()) {
                float sum = 0f;
                for (float f : list) sum += f;
                barData.put(region, sum / list.size());
            }
        }
        barChartView.setData(barData);
    }

    private void loadRecentRatings() {
        allRecentRatings  = db.getRecentRatings(50);
        visibleRatingCount = 5;
        updateRatingsList();
    }

    private void loadMoreRatings() {
        visibleRatingCount += 3;
        updateRatingsList();
    }

    private void updateRatingsList() {
        int count = Math.min(visibleRatingCount, allRecentRatings.size());
        listViewRecentRatings.setAdapter(
                new ReviewAdapter(this, allRecentRatings.subList(0, count), true));
        tvSeeMore.setVisibility(count < allRecentRatings.size() ? View.VISIBLE : View.GONE);
    }
}
