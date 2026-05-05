package com.example.proiect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proiect.database.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword, etConfirmPassword;
    private TextInputLayout tilPassword, tilConfirmPassword;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etUsername        = findViewById(R.id.etUsername);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tilPassword       = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        Button btnRegister    = findViewById(R.id.btnRegister);
        TextView tvGoToLogin  = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void showError(TextInputLayout layout, String message) {
        layout.setError(message);
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    private void attemptRegister() {
        String username  = etUsername.getText().toString().trim();
        String password  = etPassword.getText().toString();
        String password2 = etConfirmPassword.getText().toString();

        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        etUsername.setError(null);

        if (username.isEmpty()) {
            etUsername.setError("Introduceti numele de utilizator");
            Snackbar.make(findViewById(android.R.id.content), "Introduceti numele de utilizator", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (username.length() < 3) {
            etUsername.setError("Minim 3 caractere pentru username");
            Snackbar.make(findViewById(android.R.id.content), "Username-ul trebuie sa aiba minim 3 caractere", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (password.isEmpty()) {
            showError(tilPassword, "Introduceti parola");
            return;
        }
        if (password.length() < 6) {
            showError(tilPassword, "Parola trebuie sa aiba minim 6 caractere");
            return;
        }
        if (!password.equals(password2)) {
            showError(tilConfirmPassword, "Parolele nu coincid");
            return;
        }
        if (db.usernameExists(username)) {
            etUsername.setError("Numele de utilizator este deja folosit");
            Snackbar.make(findViewById(android.R.id.content), "Username-ul este deja folosit", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (db.registerUser(username, password)) {
            Toast.makeText(this, "Cont creat cu succes!", Toast.LENGTH_SHORT).show();
            db.setLoggedUser(username);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Eroare la crearea contului", Toast.LENGTH_SHORT).show();
        }
    }
}
