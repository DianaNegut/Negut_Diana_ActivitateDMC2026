package com.example.laborator4;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final String FISIER_DISCIPLINE = "discipline.dat";
    static final String FISIER_FAVORITE = "discipline_favorite.dat";

    private ArrayList<Disciplina> listaDiscipline;
    private ArrayAdapter<Disciplina> adapter;

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::proceseazaRezultat
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaDiscipline = FileHelper.incarcaDiscipline(this, FISIER_DISCIPLINE);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDiscipline);

        ListView listView = findViewById(R.id.listViewDiscipline);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Disciplina disciplina = listaDiscipline.get(position);
            Toast.makeText(this, disciplina.toString(), Toast.LENGTH_LONG).show();
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Disciplina disciplina = listaDiscipline.get(position);
            FileHelper.salveazaDisciplina(this, FISIER_FAVORITE, disciplina);
            Toast.makeText(this, "Adaugat la favorite: " + disciplina.getNumeDisciplina(), Toast.LENGTH_SHORT).show();
            return true;
        });

        Button btnAdauga = findViewById(R.id.btnAdaugaDisciplina);
        btnAdauga.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdaugaDisciplinaActivity.class);
            launcher.launch(intent);
        });

        Button btnSetari = findViewById(R.id.btnSetari);
        btnSetari.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SetariActivity.class);
            startActivity(intent);
        });
    }

    private void proceseazaRezultat(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Bundle bundle = result.getData().getExtras();
            Disciplina disciplina = (Disciplina) bundle.getSerializable("disciplina");
            if (disciplina != null) {
                listaDiscipline.add(disciplina);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
