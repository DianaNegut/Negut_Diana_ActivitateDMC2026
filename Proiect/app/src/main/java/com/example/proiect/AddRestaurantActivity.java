package com.example.proiect;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proiect.database.DatabaseHelper;
import com.example.proiect.model.Restaurant;
import com.google.android.material.textfield.TextInputEditText;

public class AddRestaurantActivity extends AppCompatActivity {

    private static final String[] REGIONS =
            {"EU-West", "EU-East", "NA", "Asia-Pacific", "South-America"};

    private TextInputEditText etName, etAddress, etPhone, etStorageCapacity,
                               etWebsite, etUptime, etLatitude, etLongitude;
    private Spinner spinnerCategory;
    private ProgressBar progressBar;
    private Button btnSave;
    private DatabaseHelper db;

    private ImageView ivProviderPhoto;
    private View layoutPhotoPlaceholder;
    private TextView tvChangePhoto;
    private String savedImagePath = null;
    private Uri cameraImageUri;

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

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

        etName            = findViewById(R.id.etName);
        etAddress         = findViewById(R.id.etAddress);
        etPhone           = findViewById(R.id.etPhone);
        etStorageCapacity = findViewById(R.id.etStorageCapacity);
        etWebsite         = findViewById(R.id.etWebsite);
        etUptime          = findViewById(R.id.etUptime);
        etLatitude        = findViewById(R.id.etLatitude);
        etLongitude       = findViewById(R.id.etLongitude);
        spinnerCategory   = findViewById(R.id.spinnerCategory);
        progressBar       = findViewById(R.id.progressBar);
        btnSave           = findViewById(R.id.btnSave);

        ivProviderPhoto       = findViewById(R.id.ivRestaurantPhoto);
        layoutPhotoPlaceholder = findViewById(R.id.layoutPhotoPlaceholder);
        tvChangePhoto         = findViewById(R.id.tvChangePhoto);

        setupImageLaunchers();

        FrameLayout photoFrame = (FrameLayout) ivProviderPhoto.getParent();
        photoFrame.setOnClickListener(v -> showImageSourceDialog());

        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, REGIONS);
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(regionAdapter);

        btnSave.setOnClickListener(v -> attemptSave());
    }

    private void setupImageLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> { if (uri != null) processSelectedImage(uri); });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> { if (success && cameraImageUri != null) processSelectedImage(cameraImageUri); });

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> { if (granted) launchCamera(); else
                    Toast.makeText(this, "Permisiunea pentru camera a fost refuzata", Toast.LENGTH_SHORT).show(); });
    }

    private void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Adauga fotografie")
                .setItems(new String[]{"Galerie foto", "Camera"}, (dialog, which) -> {
                    if (which == 0) galleryLauncher.launch("image/*");
                    else checkCameraAndLaunch();
                })
                .setNegativeButton("Anuleaza", null)
                .show();
    }

    private void checkCameraAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            File dir = new File(getFilesDir(), "provider_images");
            dir.mkdirs();
            File photoFile = new File(dir, "camera_" + UUID.randomUUID() + ".jpg");
            cameraImageUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", photoFile);
            cameraLauncher.launch(cameraImageUri);
        } catch (Exception e) {
            Toast.makeText(this, "Eroare la deschiderea camerei", Toast.LENGTH_SHORT).show();
        }
    }

    private void processSelectedImage(Uri uri) {
        try {
            File dir = new File(getFilesDir(), "provider_images");
            dir.mkdirs();
            File dest = new File(dir, UUID.randomUUID() + ".jpg");

            try (InputStream in = getContentResolver().openInputStream(uri);
                 FileOutputStream out = new FileOutputStream(dest)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }

            savedImagePath = dest.getAbsolutePath();
            displayPhoto(savedImagePath);
        } catch (IOException e) {
            Toast.makeText(this, "Eroare la salvarea imaginii", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayPhoto(String path) {
        Bitmap bmp = decodeSampledBitmap(path, 800, 400);
        if (bmp != null) {
            ivProviderPhoto.setImageBitmap(bmp);
            layoutPhotoPlaceholder.setVisibility(View.GONE);
            tvChangePhoto.setVisibility(View.VISIBLE);
        }
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

    private void attemptSave() {
        String name    = etName.getText().toString().trim();
        String nodeUrl = etAddress.getText().toString().trim();
        String peerId  = etPhone.getText().toString().trim();
        String storage = etStorageCapacity.getText().toString().trim();
        String price   = etWebsite.getText().toString().trim();
        String uptime  = etUptime.getText().toString().trim();
        String latStr  = etLatitude.getText().toString().trim();
        String lngStr  = etLongitude.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Numele este obligatoriu");
            return;
        }
        if (nodeUrl.isEmpty()) {
            etAddress.setError("URL-ul nodului este obligatoriu");
            return;
        }

        double latitude = 0, longitude = 0;
        if (!latStr.isEmpty()) {
            try { latitude = Double.parseDouble(latStr); }
            catch (NumberFormatException e) { etLatitude.setError("Valoare invalida"); return; }
        }
        if (!lngStr.isEmpty()) {
            try { longitude = Double.parseDouble(lngStr); }
            catch (NumberFormatException e) { etLongitude.setError("Valoare invalida"); return; }
        }

        String region = REGIONS[spinnerCategory.getSelectedItemPosition()];

        Restaurant provider = new Restaurant(
                0, name, region, nodeUrl,
                storage.isEmpty() ? "-" : storage,
                peerId.isEmpty()  ? "-" : peerId,
                savedImagePath != null ? savedImagePath : "",
                latitude, longitude);
        provider.pricePerGB = price;
        provider.uptime     = uptime;
        provider.addedBy    = db.getLoggedUser();

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        new Handler().postDelayed(() -> {
            long newId = db.addProvider(provider);
            progressBar.setVisibility(View.GONE);

            if (newId != -1) {
                Toast.makeText(this, "Provider adaugat!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                btnSave.setEnabled(true);
                Toast.makeText(this, "Eroare la salvare", Toast.LENGTH_SHORT).show();
            }
        }, 800);
    }
}
