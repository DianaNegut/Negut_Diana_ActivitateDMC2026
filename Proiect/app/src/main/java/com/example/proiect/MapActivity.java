package com.example.proiect;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proiect.database.DatabaseHelper;
import com.example.proiect.model.Restaurant;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this,
                PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue(getPackageName());

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
            getSupportActionBar().setTitle("Harta Providerilor");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        DatabaseHelper db       = DatabaseHelper.getInstance(this);
        int            focusId  = getIntent().getIntExtra("provider_id", -1);
        List<Restaurant>  providers = db.getProviders();

        GeoPoint centerPoint = new GeoPoint(30.0, 10.0);
        double zoom          = 2.5;

        for (Restaurant r : providers) {
            if (r.latitude == 0 && r.longitude == 0) continue;

            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(r.latitude, r.longitude));
            marker.setTitle(r.name);
            marker.setSnippet(r.region + " · " + r.storageCapacity + " · $" + r.pricePerGB + "/GB");
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(marker);

            if (r.id == focusId) {
                centerPoint = new GeoPoint(r.latitude, r.longitude);
                zoom        = 8.0;
                marker.showInfoWindow();
            }
        }

        mapView.getController().setZoom(zoom);
        mapView.getController().setCenter(centerPoint);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
