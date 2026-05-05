package com.example.proiect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proiect.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = DatabaseHelper.getInstance(this);

        if (!db.getLoggedUser().isEmpty()) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin       = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (username.isEmpty()) {
            etUsername.setError("Introduceti numele de utilizator");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Introduceti parola");
            return;
        }

        if (db.loginUser(username, password)) {
            db.setLoggedUser(username);
            goToMain();
        } else {
            Toast.makeText(this, "Nume de utilizator sau parola incorecte",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
