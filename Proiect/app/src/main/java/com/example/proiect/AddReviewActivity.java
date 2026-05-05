package com.example.proiect;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proiect.database.DatabaseHelper;
import com.example.proiect.model.Restaurant;
import com.example.proiect.model.Review;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddReviewActivity extends AppCompatActivity {

    private View cardProviderPicker;
    private Spinner spinnerProvider;
    private EditText etComment;
    private RatingBar ratingBar;
    private TextView tvSelectedDate;
    private Button btnSelectDate;
    private Switch switchRecommend;
    private ProgressBar progressBar;
    private Button btnSave;

    private DatabaseHelper db;
    private int providerId = -1;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        db         = DatabaseHelper.getInstance(this);
        providerId = getIntent().getIntExtra("provider_id", -1);

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

        cardProviderPicker = findViewById(R.id.cardRestaurantPicker);
        spinnerProvider    = findViewById(R.id.spinnerRestaurant);
        etComment          = findViewById(R.id.etComment);
        ratingBar          = findViewById(R.id.ratingBar);
        tvSelectedDate     = findViewById(R.id.tvSelectedDate);
        btnSelectDate      = findViewById(R.id.btnSelectDate);
        switchRecommend    = findViewById(R.id.switchRecommend);
        progressBar        = findViewById(R.id.progressBar);
        btnSave            = findViewById(R.id.btnSave);

        setupProviderPicker();
        setupDefaultDate();
        setupDatePicker();
        setupSaveButton();
    }

    private void setupProviderPicker() {
        if (providerId != -1) {
            cardProviderPicker.setVisibility(View.GONE);
        } else {
            cardProviderPicker.setVisibility(View.VISIBLE);
            List<Restaurant> list = db.getProviders();
            String[] names = new String[list.size()];
            for (int i = 0; i < list.size(); i++) names[i] = list.get(i).name;
            ArrayAdapter<String> a = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, names);
            a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProvider.setAdapter(a);
        }
    }

    private void setupDefaultDate() {
        Calendar cal = Calendar.getInstance();
        selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
        tvSelectedDate.setText(selectedDate);
    }

    private void setupDatePicker() {
        btnSelectDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedDate = String.format(Locale.getDefault(),
                        "%04d-%02d-%02d", year, month + 1, day);
                tvSelectedDate.setText(selectedDate);
            }, cal.get(Calendar.YEAR),
               cal.get(Calendar.MONTH),
               cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            String comment = etComment.getText().toString().trim();
            float rating   = ratingBar.getRating();

            if (comment.isEmpty()) {
                etComment.setError("Introduceti un comentariu");
                return;
            }
            if (rating == 0) {
                Toast.makeText(this, "Selectati o nota", Toast.LENGTH_SHORT).show();
                return;
            }

            int targetId = providerId;
            if (targetId == -1) {
                List<Restaurant> list = db.getProviders();
                int pos = spinnerProvider.getSelectedItemPosition();
                if (pos >= 0 && pos < list.size()) {
                    targetId = list.get(pos).id;
                }
            }

            final int finalTargetId = targetId;
            progressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);

            new Handler().postDelayed(() -> {
                Review review = new Review(
                        0,
                        finalTargetId,
                        comment,
                        rating,
                        selectedDate,
                        switchRecommend.isChecked(),
                        db.getLoggedUser());
                db.addRating(review);

                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Rating salvat!", Toast.LENGTH_SHORT).show();

                if (providerId != -1) {
                    Intent intent = new Intent(this, RestaurantDetailActivity.class);
                    intent.putExtra("provider_id", providerId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    finish();
                }
            }, 1000);
        });
    }
}
