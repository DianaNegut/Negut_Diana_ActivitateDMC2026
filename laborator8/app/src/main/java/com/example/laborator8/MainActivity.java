package com.example.laborator8;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private ProdusDao produsDao;

    // Campuri adaugare
    private TextInputEditText etNume, etCategorie, etPret, etCantitate;
    // Cautare dupa nume
    private TextInputEditText etCautareNume;
    // Filtru cantitate
    private TextInputEditText etCantMin, etCantMax;
    // Stergere dupa pret
    private TextInputEditText etPretStergere;
    // Creste cantitate
    private TextInputEditText etLitera;

    private ListView listViewProduse;
    private TextView tvListLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        produsDao = db.produsDao();

        etNume           = findViewById(R.id.etNume);
        etCategorie      = findViewById(R.id.etCategorie);
        etPret           = findViewById(R.id.etPret);
        etCantitate      = findViewById(R.id.etCantitate);
        etCautareNume    = findViewById(R.id.etCautareNume);
        etCantMin        = findViewById(R.id.etCantMin);
        etCantMax        = findViewById(R.id.etCantMax);
        etPretStergere   = findViewById(R.id.etPretStergere);
        etLitera         = findViewById(R.id.etLitera);
        listViewProduse  = findViewById(R.id.listViewProduse);
        tvListLabel      = findViewById(R.id.tvListLabel);

        MaterialButton btnAdauga = findViewById(R.id.btnAdauga);
        btnAdauga.setOnClickListener(v -> {
            String nume      = getText(etNume);
            String categorie = getText(etCategorie);
            String pretStr   = getText(etPret);
            String cantStr   = getText(etCantitate);

            if (nume.isEmpty() || categorie.isEmpty() || pretStr.isEmpty() || cantStr.isEmpty()) {
                toast("Completati toate campurile!");
                return;
            }

            double pret     = Double.parseDouble(pretStr);
            int    cantitate = Integer.parseInt(cantStr);

            produsDao.inserare(new Produs(nume, categorie, pret, cantitate));
            toast("Produs \"" + nume + "\" adaugat!");

            etNume.setText("");
            etCategorie.setText("");
            etPret.setText("");
            etCantitate.setText("");

            afiseaza(produsDao.selectareTot(), "Toate produsele");
        });

        MaterialButton btnAfisareTot = findViewById(R.id.btnAfisareTot);
        btnAfisareTot.setOnClickListener(v -> {
            List<Produs> lista = produsDao.selectareTot();
            afiseaza(lista, "Toate produsele (" + lista.size() + ")");
        });

        MaterialButton btnCautareNume = findViewById(R.id.btnCautareNume);
        btnCautareNume.setOnClickListener(v -> {
            String numeParam = getText(etCautareNume);
            if (numeParam.isEmpty()) { toast("Introduceti un nume!"); return; }
            List<Produs> lista = produsDao.selectareDupaNume(numeParam);
            afiseaza(lista, "Produse cu numele \"" + numeParam + "\" (" + lista.size() + ")");
        });

        MaterialButton btnFiltruCantitate = findViewById(R.id.btnFiltruCantitate);
        btnFiltruCantitate.setOnClickListener(v -> {
            String minStr = getText(etCantMin);
            String maxStr = getText(etCantMax);
            if (minStr.isEmpty() || maxStr.isEmpty()) { toast("Introduceti min si max!"); return; }
            int min = Integer.parseInt(minStr);
            int max = Integer.parseInt(maxStr);
            List<Produs> lista = produsDao.selectareDupaCantitate(min, max);
            afiseaza(lista, "Cantitate intre " + min + " si " + max + " (" + lista.size() + ")");
        });

        MaterialButton btnStergeMaiMare = findViewById(R.id.btnStergeMaiMare);
        btnStergeMaiMare.setOnClickListener(v -> {
            String pretStr = getText(etPretStergere);
            if (pretStr.isEmpty()) { toast("Introduceti pretul referinta!"); return; }
            double pret = Double.parseDouble(pretStr);
            produsDao.stergerePretMaiMare(pret);
            List<Produs> lista = produsDao.selectareTot();
            afiseaza(lista, "Dupa stergere pret > " + pret + " lei (" + lista.size() + ")");
            toast("Produse cu pret > " + pret + " lei au fost sterse!");
        });

        MaterialButton btnStergeMaiMic = findViewById(R.id.btnStergeMaiMic);
        btnStergeMaiMic.setOnClickListener(v -> {
            String pretStr = getText(etPretStergere);
            if (pretStr.isEmpty()) { toast("Introduceti pretul referinta!"); return; }
            double pret = Double.parseDouble(pretStr);
            produsDao.stergerePretMaiMic(pret);
            List<Produs> lista = produsDao.selectareTot();
            afiseaza(lista, "Dupa stergere pret < " + pret + " lei (" + lista.size() + ")");
            toast("Produse cu pret < " + pret + " lei au fost sterse!");
        });

        MaterialButton btnCreste = findViewById(R.id.btnCresteCantitateLitera);
        btnCreste.setOnClickListener(v -> {
            String litera = getText(etLitera);
            if (litera.isEmpty()) { toast("Introduceti o litera!"); return; }
            String pattern = litera.toUpperCase() + "%";
            produsDao.crescCantitate(pattern);
            List<Produs> lista = produsDao.selectareTot();
            afiseaza(lista, "Dupa +1 cantitate pentru litera \"" + litera.toUpperCase() + "\"");
            toast("Cantitate +1 pentru produse ce incep cu \"" + litera.toUpperCase() + "\"");
        });

        afiseaza(produsDao.selectareTot(), "Toate produsele");
    }

    private String getText(TextInputEditText field) {
        CharSequence cs = field.getText();
        return cs != null ? cs.toString().trim() : "";
    }

    private void afiseaza(List<Produs> lista, String eticheta) {
        ArrayAdapter<Produs> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, lista);
        listViewProduse.setAdapter(adapter);
        tvListLabel.setText(eticheta + (lista.isEmpty() ? " — lista goala" : ""));
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
