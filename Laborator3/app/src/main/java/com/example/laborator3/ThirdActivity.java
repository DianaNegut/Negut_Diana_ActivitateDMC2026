package com.example.laborator3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {

    private static final String TAG = "a3";
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() apelat third activity");

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() apelat – third activity");

    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() apelat – third activity");

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() apelat");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() apelat");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() apelat – VERBOSE third activity");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        Log.d(TAG, "ThirdActivity - onCreate() apelat");


        Bundle bundle = getIntent().getExtras();

        int valoare1 = 0;
        int valoare2 = 0;

        if (bundle != null) {
            String mesaj = bundle.getString("mesaj");
            valoare1     = bundle.getInt("valoare1");
            valoare2     = bundle.getInt("valoare2");


            Toast.makeText(this,
                    mesaj + "\nValoare1: " + valoare1 + "\nValoare2: " + valoare2,
                    Toast.LENGTH_LONG).show();

        }



        final int suma = valoare1 + valoare2;

        Button btnTrimite = findViewById(R.id.btnTrimite);

        btnTrimite.setOnClickListener(v -> {

            Intent rezultat = new Intent();
            rezultat.putExtra("mesajRaspuns", "Salut din activitate 3!");
            rezultat.putExtra("suma", suma);


            setResult(RESULT_OK, rezultat);
            finish();
        });
    }
}