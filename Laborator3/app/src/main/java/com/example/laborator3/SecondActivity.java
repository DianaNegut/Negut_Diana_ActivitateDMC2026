package com.example.laborator3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "a2";


    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    (ActivityResult result) -> {

                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            String mesajRaspuns = result.getData().getStringExtra("mesajRaspuns");
                            int suma = result.getData().getIntExtra("suma", 0);

                            Toast.makeText(this,
                                    mesajRaspuns + "\nSuma: " + suma,
                                    Toast.LENGTH_LONG).show();

                        }
                    }
            );
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart() apelat second activity");

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() apelat – second activity");

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
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() apelat – second activity");

    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() apelat – VERBOSE second activity");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.d(TAG, "SecondActivity - onCreate() apelat");

        Button btnDeschide = findViewById(R.id.btnDeschide);

        btnDeschide.setOnClickListener(v -> {
            Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("mesaj", "Salut din activitate 2!");
            bundle.putInt("valoare1", 42);
            bundle.putInt("valoare2", 100);
            intent.putExtras(bundle);

            launcher.launch(intent);
        });
    }
}