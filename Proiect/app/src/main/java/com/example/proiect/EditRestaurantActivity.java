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
import java.util.Arrays;
import java.util.List;
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

public class EditRestaurantActivity extends AppCompatActivity {

    private static final List<String> REGIONS =
            Arrays.asList("EU-West", "EU-East", "NA", "Asia-Pacific", "South-America");

    private TextInputEditText etName, etAddress, etPhone, etStorageCapacity,
                               etWebsite, etUptime, etLatitude, etLongitude;
    private Spinner spinnerCategory;
    private ProgressBar progressBar;
    private Button btnSave, btnDelete;
    private DatabaseHelper db;
    private Restaurant provider;

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
        setContentView(R.layout.activity_edit_restaurant);

        db = DatabaseHelper.getInstance(this);

        int providerId = getIntent().getIntExtra("provider_id", -1);
        provider = db.getProviderById(providerId);
        if (provider == null) { finish(); return; }

        String loggedUser = db.getLoggedUser();
        if (!provider.addedBy.equals(loggedUser)) {
            Toast.makeText(this, "Nu ai permisiunea de a edita acest provider", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Editeaza: " + provider.name);
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
        btnDelete         = findViewById(R.id.btnDelete);

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

        prefillFields();

        btnSave.setOnClickListener(v -> attemptSave());
        btnDelete.setOnClickListener(v -> confirmDelete());
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

    private void prefillFields() {
        etName.setText(provider.name);
        etAddress.setText(provider.nodeUrl);
        etPhone.setText(provider.peerId != null && provider.peerId.equals("-") ? "" : provider.peerId);
        etStorageCapacity.setText(provider.storageCapacity != null && provider.storageCapacity.equals("-") ? "" : provider.storageCapacity);
        etWebsite.setText(provider.pricePerGB != null ? provider.pricePerGB : "");
        etUptime.setText(provider.uptime != null ? provider.uptime : "");
        if (provider.latitude != 0) etLatitude.setText(String.valueOf(provider.latitude));
        if (provider.longitude != 0) etLongitude.setText(String.valueOf(provider.longitude));

        int regionIdx = REGIONS.indexOf(provider.region);
        if (regionIdx >= 0) spinnerCategory.setSelection(regionIdx);

        if (provider.imageUrl != null && !provider.imageUrl.isEmpty()) {
            File f = new File(provider.imageUrl);
            if (f.exists()) {
                savedImagePath = provider.imageUrl;
                displayPhoto(savedImagePath);
            }
        }
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

        provider.name            = name;
        provider.region          = REGIONS.get(spinnerCategory.getSelectedItemPosition());
        provider.nodeUrl         = nodeUrl;
        provider.storageCapacity = storage.isEmpty() ? "-" : storage;
        provider.peerId          = peerId.isEmpty() ? "-" : peerId;
        provider.pricePerGB      = price;
        provider.uptime          = uptime;
        provider.latitude        = latitude;
        provider.longitude       = longitude;
        if (savedImagePath != null) provider.imageUrl = savedImagePath;

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        btnDelete.setEnabled(false);

        new Handler().postDelayed(() -> {
            if (db.updateProvider(provider)) {
                Toast.makeText(this, "Provider actualizat!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                btnDelete.setEnabled(true);
                Toast.makeText(this, "Eroare la salvare", Toast.LENGTH_SHORT).show();
            }
        }, 600);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Sterge provider")
                .setMessage("Esti sigur ca vrei sa stergi \"" + provider.name
                        + "\"? Aceasta actiune va sterge si toate ratingurile asociate.")
                .setPositiveButton("Sterge", (dialog, which) -> deleteProvider())
                .setNegativeButton("Anuleaza", null)
                .show();
    }

    private void deleteProvider() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        btnDelete.setEnabled(false);

        new Handler().postDelayed(() -> {
            if (db.deleteProvider(provider.id)) {
                Toast.makeText(this, "Provider sters!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                btnDelete.setEnabled(true);
                Toast.makeText(this, "Eroare la stergere", Toast.LENGTH_SHORT).show();
            }
        }, 400);
    }
}
