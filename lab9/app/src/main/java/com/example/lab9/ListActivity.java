package com.example.lab9;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        List<Disciplina> items = new ArrayList<>();
        items.add(new Disciplina(
                "https://picsum.photos/id/24/320/240",
                "Matematica - Algebra si Geometrie",
                "https://en.wikipedia.org/wiki/Mathematics"
        ));
        items.add(new Disciplina(
                "https://picsum.photos/id/0/320/240",
                "Informatica - Programare si Algoritmi",
                "https://en.wikipedia.org/wiki/Computer_science"
        ));
        items.add(new Disciplina(
                "https://picsum.photos/id/110/320/240",
                "Fizica - Optica si Lumina",
                "https://en.wikipedia.org/wiki/Physics"
        ));
        items.add(new Disciplina(
                "https://picsum.photos/id/326/320/240",
                "Chimie - Reactii Chimice",
                "https://en.wikipedia.org/wiki/Chemistry"
        ));
        items.add(new Disciplina(
                "https://picsum.photos/id/160/320/240",
                "Electronica - Circuite Integrate",
                "https://en.wikipedia.org/wiki/Electronics"
        ));

        ListView listView = findViewById(R.id.listView);
        CustomAdapter adapter = new CustomAdapter(this, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Disciplina item = items.get(position);
            Intent intent = new Intent(ListActivity.this, WebViewActivity.class);
            intent.putExtra("url", item.getWebLink());
            startActivity(intent);
        });
    }
}
