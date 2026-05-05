package com.example.proiect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proiect.adapter.ReviewAdapter;
import com.example.proiect.database.DatabaseHelper;
import com.example.proiect.model.Restaurant;
import com.example.proiect.model.Review;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private int providerId;
    private Restaurant provider;

    private FrameLayout imageContainer;
    private ImageView ivProviderPhoto;
    private TextView tvRegionLetter, tvName, tvRegion, tvNodeUrl, tvStorage, tvPeerId, tvPrice, tvUptime;
    private RatingBar ratingBar;
    private CheckBox checkBoxFavorite;
    private ListView listViewRatings;
    private TextView tvNoRatings;
    private Button btnMap, btnAddRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        db         = DatabaseHelper.getInstance(this);
        providerId = getIntent().getIntExtra("provider_id", -1);
        provider   = db.getProviderById(providerId);
        if (provider == null) { finish(); return; }

        initViews();
        populateData();
        setupButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRatings();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(provider.name);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        imageContainer  = findViewById(R.id.imageContainer);
        ivProviderPhoto = findViewById(R.id.ivRestaurantPhoto);
        tvRegionLetter  = findViewById(R.id.tvCategoryLetter);
        tvName          = findViewById(R.id.tvName);
        tvRegion        = findViewById(R.id.tvCategory);
        tvNodeUrl       = findViewById(R.id.tvAddress);
        tvStorage       = findViewById(R.id.tvSchedule);
        tvPeerId        = findViewById(R.id.tvPhone);
        tvPrice         = findViewById(R.id.tvWebsite);
        tvUptime        = findViewById(R.id.tvUptime);
        ratingBar       = findViewById(R.id.ratingBar);
        checkBoxFavorite= findViewById(R.id.checkBoxFavorite);
        listViewRatings = findViewById(R.id.listViewReviews);
        tvNoRatings     = findViewById(R.id.tvNoReviews);
        btnMap          = findViewById(R.id.btnMap);
        btnAddRating    = findViewById(R.id.btnAddReview);
    }

    private void populateData() {
        imageContainer.setBackgroundColor(getRegionColor(provider.region));

        boolean hasPhoto = provider.imageUrl != null && !provider.imageUrl.isEmpty()
                && new File(provider.imageUrl).exists();
        if (hasPhoto) {
            Bitmap bmp = decodeSampledBitmap(provider.imageUrl, 900, 400);
            ivProviderPhoto.setImageBitmap(bmp);
            ivProviderPhoto.setVisibility(View.VISIBLE);
            tvRegionLetter.setVisibility(View.GONE);
        } else {
            tvRegionLetter.setText(String.valueOf(provider.region.charAt(0)));
        }

        tvName.setText(provider.name);
        tvRegion.setText(provider.region);
        tvNodeUrl.setText("Nod: " + (provider.nodeUrl != null ? provider.nodeUrl : "-"));
        tvStorage.setText("Stocare: " + (provider.storageCapacity != null ? provider.storageCapacity : "-"));
        tvPeerId.setText("Peer ID: " + (provider.peerId != null ? provider.peerId : "-"));

        if (provider.pricePerGB != null && !provider.pricePerGB.isEmpty()) {
            tvPrice.setVisibility(View.VISIBLE);
            tvPrice.setText("Pret/GB: $" + provider.pricePerGB);
            tvPrice.setOnClickListener(null);
        }

        if (tvUptime != null) {
            if (provider.uptime != null && !provider.uptime.isEmpty()) {
                tvUptime.setVisibility(View.VISIBLE);
                tvUptime.setText("Uptime: " + provider.uptime + "%");
            } else {
                tvUptime.setVisibility(View.GONE);
            }
        }

        ratingBar.setIsIndicator(true);
        refreshRatings();

        checkBoxFavorite.setChecked(db.isFavorite(providerId));
        checkBoxFavorite.setOnCheckedChangeListener((btn, isChecked) ->
                db.setFavorite(providerId, isChecked));
    }

    private void refreshRatings() {
        List<Review> ratings = db.getRatingsForProvider(providerId);
        ratingBar.setRating(db.getAverageRating(providerId));

        if (ratings.isEmpty()) {
            listViewRatings.setVisibility(View.GONE);
            tvNoRatings.setVisibility(View.VISIBLE);
        } else {
            listViewRatings.setVisibility(View.VISIBLE);
            tvNoRatings.setVisibility(View.GONE);

            ReviewAdapter adapter = new ReviewAdapter(this, ratings, false);
            adapter.setOnReviewActionListener(new ReviewAdapter.OnReviewActionListener() {
                @Override public void onEdit(Review review)   { showEditDialog(review); }
                @Override public void onDelete(Review review) { showDeleteConfirm(review); }
            });
            listViewRatings.setAdapter(adapter);
        }
    }

    private void showEditDialog(Review review) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_review, null);
        TextInputEditText etComment   = dialogView.findViewById(R.id.etEditComment);
        RatingBar         ratingEdit  = dialogView.findViewById(R.id.ratingBarEdit);
        Switch            swRecommend = dialogView.findViewById(R.id.switchEditRecommend);

        etComment.setText(review.comment);
        ratingEdit.setRating(review.rating);
        swRecommend.setChecked(review.recommend);

        new AlertDialog.Builder(this)
                .setTitle("Editeaza rating")
                .setView(dialogView)
                .setPositiveButton("Salveaza", (dialog, which) -> {
                    String newComment = etComment.getText() != null
                            ? etComment.getText().toString().trim() : "";
                    float newRating   = ratingEdit.getRating();

                    if (newComment.isEmpty()) {
                        Toast.makeText(this, "Comentariul nu poate fi gol", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newRating == 0) {
                        Toast.makeText(this, "Adauga o nota", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    review.comment   = newComment;
                    review.rating    = newRating;
                    review.recommend = swRecommend.isChecked();

                    if (db.updateRating(review)) {
                        Toast.makeText(this, "Rating actualizat!", Toast.LENGTH_SHORT).show();
                        refreshRatings();
                    }
                })
                .setNegativeButton("Anuleaza", null)
                .show();
    }

    private void showDeleteConfirm(Review review) {
        new AlertDialog.Builder(this)
                .setTitle("Sterge rating")
                .setMessage("Esti sigur ca vrei sa stergi acest rating?")
                .setPositiveButton("Sterge", (dialog, which) -> {
                    if (db.deleteRating(review.id)) {
                        Toast.makeText(this, "Rating sters!", Toast.LENGTH_SHORT).show();
                        refreshRatings();
                    }
                })
                .setNegativeButton("Anuleaza", null)
                .show();
    }

    private void setupButtons() {
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("provider_id", providerId);
            startActivity(intent);
        });

        btnAddRating.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddReviewActivity.class);
            intent.putExtra("provider_id", providerId);
            startActivity(intent);
        });
    }

    private Bitmap decodeSampledBitmap(String path, int reqW, int reqH) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int inSampleSize = 1;
        if (opts.outHeight > reqH || opts.outWidth > reqW) {
            int halfH = opts.outHeight / 2, halfW = opts.outWidth / 2;
            while ((halfH / inSampleSize) >= reqH && (halfW / inSampleSize) >= reqW)
                inSampleSize *= 2;
        }
        opts.inSampleSize = inSampleSize;
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, opts);
    }

    private int getRegionColor(String region) {
        if (region == null) return Color.parseColor("#607D8B");
        switch (region) {
            case "EU-West":       return Color.parseColor("#1565C0");
            case "EU-East":       return Color.parseColor("#00695C");
            case "NA":            return Color.parseColor("#4527A0");
            case "Asia-Pacific":  return Color.parseColor("#E65100");
            case "South-America": return Color.parseColor("#2E7D32");
            default:              return Color.parseColor("#607D8B");
        }
    }
}
