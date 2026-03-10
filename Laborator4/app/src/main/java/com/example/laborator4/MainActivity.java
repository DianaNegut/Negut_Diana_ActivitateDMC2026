package com.example.laborator4;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvNumeDisciplina, tvValoareNota, tvPunctajExamen,
            tvSemestru, tvEstePromovata;

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::proceseazaRezultat
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvNumeDisciplina  = findViewById(R.id.tvNumeDisciplina);
        tvValoareNota     = findViewById(R.id.tvValoareNota);
        tvPunctajExamen   = findViewById(R.id.tvPunctajExamen);
        tvSemestru        = findViewById(R.id.tvSemestru);
        tvEstePromovata   = findViewById(R.id.tvEstePromovata);

        Button btnAdauga = findViewById(R.id.btnAdaugaDisciplina);
        btnAdauga.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdaugaDisciplinaActivity.class);
            launcher.launch(intent);
        });
    }

    private void proceseazaRezultat(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Bundle bundle = result.getData().getExtras();
            Disciplina disciplina = (Disciplina) bundle.getSerializable("disciplina");

            if (disciplina != null) {
                tvNumeDisciplina.setText(disciplina.getNumeDisciplina());
                tvValoareNota.setText(String.valueOf(disciplina.getValoareNota()));
                tvPunctajExamen.setText(String.valueOf(disciplina.getPunctajExamen()));
                tvSemestru.setText(disciplina.getSemestru().toString());
                tvEstePromovata.setText(disciplina.isEstePromovata() ? "Da" : "Nu");
            }
        }
    }
}