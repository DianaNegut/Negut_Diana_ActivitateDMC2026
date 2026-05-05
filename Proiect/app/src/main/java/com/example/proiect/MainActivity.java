package com.example.proiect;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proiect.adapter.RestaurantGridAdapter;
import com.example.proiect.database.DatabaseHelper;
import com.example.proiect.model.Restaurant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SORT_DEFAULT     = 0;
    private static final int SORT_NAME_ASC    = 1;
    private static final int SORT_RATING_DESC = 2;

    private GridView gridView;
    private SearchView searchView;
    private ImageButton btnFilter;
    private FloatingActionButton fabAddRating;
    private Button btnMap, btnStatistics, btnLogout;

    private DatabaseHelper db;
    private RestaurantGridAdapter adapter;
    private List<Restaurant> allProviders;
    private final List<Restaurant> filteredProviders = new ArrayList<>();

    private String  currentRegion      = "Toate";
    private String  currentSearch      = "";
    private int     currentSort        = SORT_DEFAULT;
    private boolean showOnlyFavorites  = false;

    private static final String[] REGIONS =
            {"Toate", "EU-West", "EU-East", "NA", "Asia-Pacific", "South-America"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });
        setSupportActionBar(toolbar);

        gridView      = findViewById(R.id.gridViewRestaurants);
        searchView    = findViewById(R.id.searchView);
        btnFilter     = findViewById(R.id.btnFilter);
        fabAddRating  = findViewById(R.id.fabAddReview);
        btnMap        = findViewById(R.id.btnMap);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnLogout     = findViewById(R.id.btnLogout);

        db = DatabaseHelper.getInstance(this);

        if (db.getLoggedUser().isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        allProviders = db.getProviders();

        setupSearchView();
        setupGridView();
        setupButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_restaurant) {
            startActivity(new Intent(this, AddRestaurantActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        allProviders = db.getProviders();
        filterProviders();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                currentSearch = query;
                filterProviders();
                return true;
            }
            @Override public boolean onQueryTextChange(String newText) {
                currentSearch = newText;
                filterProviders();
                return true;
            }
        });
    }

    private void setupGridView() {
        adapter = new RestaurantGridAdapter(this, filteredProviders, db);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Restaurant r = filteredProviders.get(position);
            Intent intent = new Intent(this, RestaurantDetailActivity.class);
            intent.putExtra("provider_id", r.id);
            startActivity(intent);
        });
    }

    private void setupButtons() {
        btnFilter.setOnClickListener(v -> showFilterDialog());

        fabAddRating.setOnClickListener(v ->
                startActivity(new Intent(this, AddReviewActivity.class)));

        btnMap.setOnClickListener(v ->
                startActivity(new Intent(this, MapActivity.class)));

        btnStatistics.setOnClickListener(v ->
                startActivity(new Intent(this, StatisticsActivity.class)));

        btnLogout.setOnClickListener(v -> {
            db.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    private void showFilterDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter, null);

        Spinner    spinnerRegion = dialogView.findViewById(R.id.spinnerCategoryDialog);
        RadioGroup radioGroup   = dialogView.findViewById(R.id.radioGroupSort);
        CheckBox   checkboxFav  = dialogView.findViewById(R.id.checkboxFavoritesOnly);

        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, REGIONS);
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegion.setAdapter(regionAdapter);
        spinnerRegion.setSelection(Arrays.asList(REGIONS).indexOf(currentRegion));

        switch (currentSort) {
            case SORT_NAME_ASC:    radioGroup.check(R.id.radioNameAsc);    break;
            case SORT_RATING_DESC: radioGroup.check(R.id.radioRatingDesc); break;
            default:               radioGroup.check(R.id.radioDefault);    break;
        }
        checkboxFav.setChecked(showOnlyFavorites);

        new AlertDialog.Builder(this)
                .setTitle("Filtrare si Sortare")
                .setView(dialogView)
                .setPositiveButton("Aplica", (dialog, which) -> {
                    currentRegion = REGIONS[spinnerRegion.getSelectedItemPosition()];

                    int checked = radioGroup.getCheckedRadioButtonId();
                    if      (checked == R.id.radioNameAsc)    currentSort = SORT_NAME_ASC;
                    else if (checked == R.id.radioRatingDesc) currentSort = SORT_RATING_DESC;
                    else                                      currentSort = SORT_DEFAULT;

                    showOnlyFavorites = checkboxFav.isChecked();
                    filterProviders();
                    updateFilterIcon();
                })
                .setNegativeButton("Anuleaza", null)
                .setNeutralButton("Reseteaza", (dialog, which) -> {
                    currentRegion     = "Toate";
                    currentSort       = SORT_DEFAULT;
                    showOnlyFavorites = false;
                    filterProviders();
                    updateFilterIcon();
                })
                .show();
    }

    private void updateFilterIcon() {
        boolean active = !currentRegion.equals("Toate")
                || currentSort != SORT_DEFAULT
                || showOnlyFavorites;
        if (active) {
            btnFilter.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN);
        } else {
            btnFilter.clearColorFilter();
        }
    }

    private void filterProviders() {
        filteredProviders.clear();

        for (Restaurant r : allProviders) {
            boolean matchRegion = currentRegion.equals("Toate") || r.region.equals(currentRegion);
            boolean matchSearch = currentSearch.isEmpty() ||
                    r.name.toLowerCase().contains(currentSearch.toLowerCase());
            boolean matchFav    = !showOnlyFavorites || db.isFavorite(r.id);

            if (matchRegion && matchSearch && matchFav) filteredProviders.add(r);
        }

        switch (currentSort) {
            case SORT_NAME_ASC:
                filteredProviders.sort((a, b) -> a.name.compareTo(b.name));
                break;
            case SORT_RATING_DESC:
                filteredProviders.sort((a, b) -> Float.compare(
                        db.getAverageRating(b.id),
                        db.getAverageRating(a.id)));
                break;
        }

        if (adapter != null) adapter.updateData(filteredProviders);
    }
}
