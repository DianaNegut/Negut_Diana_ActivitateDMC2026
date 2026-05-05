package com.example.proiect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.proiect.view.NonScrollListView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_EDIT_PROVIDER = 1;

    private TextView tvAvatarLetter, tvCurrentUsername;
    private TextInputEditText etNewUsername;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private Button btnSaveUsername, btnSavePassword;
    private NonScrollListView listMyRatings;
    private TextView tvNoMyRatings;
    private LinearLayout layoutMyProviders;
    private TextView tvNoMyProviders, tvSeeMoreProviders;

    private DatabaseHelper db;
    private String currentUsername;
    private List<Restaurant> allMyProviders;
    private int visibleProviderCount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = DatabaseHelper.getInstance(this);
        currentUsername = db.getLoggedUser();

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

        tvAvatarLetter       = findViewById(R.id.tvAvatarLetter);
        tvCurrentUsername    = findViewById(R.id.tvCurrentUsername);
        etNewUsername        = findViewById(R.id.etNewUsername);
        etCurrentPassword    = findViewById(R.id.etCurrentPassword);
        etNewPassword        = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnSaveUsername      = findViewById(R.id.btnSaveUsername);
        btnSavePassword      = findViewById(R.id.btnSavePassword);
        listMyRatings        = findViewById(R.id.listMyReviews);
        tvNoMyRatings        = findViewById(R.id.tvNoMyReviews);
        layoutMyProviders    = findViewById(R.id.layoutMyRestaurants);
        tvNoMyProviders      = findViewById(R.id.tvNoMyRestaurants);
        tvSeeMoreProviders   = findViewById(R.id.tvSeeMoreRestaurants);

        tvSeeMoreProviders.setOnClickListener(v -> {
            visibleProviderCount += 3;
            updateMyProvidersList();
        });

        refreshHeader();
        refreshMyProviders();
        refreshMyRatings();

        btnSaveUsername.setOnClickListener(v -> attemptUpdateUsername());
        btnSavePassword.setOnClickListener(v -> attemptUpdatePassword());
    }

    private void refreshHeader() {
        tvCurrentUsername.setText(currentUsername);
        tvAvatarLetter.setText(currentUsername.isEmpty() ? "?"
                : String.valueOf(currentUsername.charAt(0)).toUpperCase());
    }

    private void attemptUpdateUsername() {
        String newUsername = etNewUsername.getText().toString().trim();

        if (newUsername.isEmpty()) {
            etNewUsername.setError("Introduceti noul nume");
            return;
        }
        if (newUsername.length() < 3) {
            etNewUsername.setError("Minim 3 caractere");
            return;
        }
        if (newUsername.equals(currentUsername)) {
            etNewUsername.setError("Este deja numele tau curent");
            return;
        }

        if (db.updateUsername(currentUsername, newUsername)) {
            currentUsername = newUsername;
            refreshHeader();
            etNewUsername.setText("");
            Toast.makeText(this, "Numele a fost actualizat!", Toast.LENGTH_SHORT).show();
        } else {
            etNewUsername.setError("Numele este deja folosit de alt cont");
        }
    }

    private void attemptUpdatePassword() {
        String currentPass = etCurrentPassword.getText().toString();
        String newPass     = etNewPassword.getText().toString();
        String confirmPass = etConfirmNewPassword.getText().toString();

        if (currentPass.isEmpty()) { etCurrentPassword.setError("Introduceti parola actuala"); return; }
        if (newPass.isEmpty())     { etNewPassword.setError("Introduceti parola noua"); return; }
        if (newPass.length() < 6)  { etNewPassword.setError("Minim 6 caractere"); return; }
        if (!newPass.equals(confirmPass)) { etConfirmNewPassword.setError("Parolele nu coincid"); return; }

        if (db.updatePassword(currentUsername, currentPass, newPass)) {
            etCurrentPassword.setText("");
            etNewPassword.setText("");
            etConfirmNewPassword.setText("");
            Toast.makeText(this, "Parola a fost schimbata!", Toast.LENGTH_SHORT).show();
        } else {
            etCurrentPassword.setError("Parola actuala este incorecta");
        }
    }

    private void refreshMyProviders() {
        allMyProviders = db.getProvidersByUser(currentUsername);
        visibleProviderCount = 3;
        updateMyProvidersList();
    }

    private void updateMyProvidersList() {
        layoutMyProviders.removeAllViews();

        if (allMyProviders.isEmpty()) {
            tvNoMyProviders.setVisibility(View.VISIBLE);
            tvSeeMoreProviders.setVisibility(View.GONE);
            return;
        }

        tvNoMyProviders.setVisibility(View.GONE);
        int count = Math.min(visibleProviderCount, allMyProviders.size());

        for (int i = 0; i < count; i++) {
            Restaurant r = allMyProviders.get(i);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setClickable(true);
            row.setFocusable(true);
            row.setBackground(getDrawable(android.R.drawable.list_selector_background));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = (int) (4 * getResources().getDisplayMetrics().density);
            row.setLayoutParams(params);
            int pad = (int) (12 * getResources().getDisplayMetrics().density);
            row.setPadding(0, pad, 0, pad);

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

            TextView tvDetails = new TextView(this);
            tvDetails.setText(r.region + " · " + r.nodeUrl);
            tvDetails.setTextSize(12);
            tvDetails.setTextColor(0xFF757575);
            tvDetails.setMaxLines(1);
            tvDetails.setEllipsize(android.text.TextUtils.TruncateAt.END);
            info.addView(tvDetails);

            row.addView(info);

            TextView tvEdit = new TextView(this);
            tvEdit.setText("Editeaza");
            tvEdit.setTextSize(13);
            tvEdit.setTextColor(0xFF1565C0);
            tvEdit.setTypeface(null, android.graphics.Typeface.BOLD);
            tvEdit.setPadding(pad, 0, 0, 0);
            row.addView(tvEdit);

            final int pid = r.id;
            row.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditRestaurantActivity.class);
                intent.putExtra("provider_id", pid);
                startActivityForResult(intent, REQUEST_EDIT_PROVIDER);
            });

            layoutMyProviders.addView(row);

            if (i < count - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                divider.setLayoutParams(dp);
                divider.setBackgroundColor(0xFFEEEEEE);
                layoutMyProviders.addView(divider);
            }
        }

        tvSeeMoreProviders.setVisibility(count < allMyProviders.size() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_PROVIDER && resultCode == RESULT_OK) {
            refreshMyProviders();
        }
    }

    private void refreshMyRatings() {
        List<Review> myRatings = db.getRatingsByUser(currentUsername);

        if (myRatings.isEmpty()) {
            listMyRatings.setVisibility(View.GONE);
            tvNoMyRatings.setVisibility(View.VISIBLE);
        } else {
            listMyRatings.setVisibility(View.VISIBLE);
            tvNoMyRatings.setVisibility(View.GONE);

            ReviewAdapter adapter = new ReviewAdapter(this, myRatings, true);
            adapter.setOnReviewActionListener(new ReviewAdapter.OnReviewActionListener() {
                @Override public void onEdit(Review review)   { showEditDialog(review); }
                @Override public void onDelete(Review review) { showDeleteConfirm(review); }
            });
            listMyRatings.setAdapter(adapter);
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
                        refreshMyRatings();
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
                        refreshMyRatings();
                    }
                })
                .setNegativeButton("Anuleaza", null)
                .show();
    }
}
