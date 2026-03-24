package com.example.laborator4;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Disciplina> listaDiscipline;
    private DisciplinaAdapter adapter;

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::proceseazaRezultat
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaDiscipline = new ArrayList<>();
        adapter = new DisciplinaAdapter(this, listaDiscipline);

        ListView listView = findViewById(R.id.listViewDiscipline);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Disciplina disciplina = listaDiscipline.get(position);
            Intent intent = new Intent(MainActivity.this, AdaugaDisciplinaActivity.class);
            intent.putExtra("disciplina", disciplina);
            intent.putExtra("position", position);
            launcher.launch(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            listaDiscipline.remove(position);
            adapter.notifyDataSetChanged();
            return true;
        });

        Button btnAdauga = findViewById(R.id.btnAdaugaDisciplina);
        btnAdauga.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdaugaDisciplinaActivity.class);
            launcher.launch(intent);
        });
    }

    private void proceseazaRezultat(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Intent data = result.getData();
            Disciplina disciplina = data.getParcelableExtra("disciplina");
            int position = data.getIntExtra("position", -1);
            if (disciplina != null) {
                if (position >= 0) {
                    listaDiscipline.set(position, disciplina);
                } else {
                    listaDiscipline.add(disciplina);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}
