package com.example.laborator4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DisciplinaAdapter extends ArrayAdapter<Disciplina> {

    private final LayoutInflater inflater;

    public DisciplinaAdapter(Context context, ArrayList<Disciplina> lista) {
        super(context, 0, lista);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_disciplina, parent, false);
        }

        Disciplina d = getItem(position);

        TextView tvNota = convertView.findViewById(R.id.tvNota);
        TextView tvNume = convertView.findViewById(R.id.tvNumeDisciplina);
        TextView tvSemestru = convertView.findViewById(R.id.tvSemestru);
        TextView tvPunctaj = convertView.findViewById(R.id.tvPunctaj);
        TextView tvData = convertView.findViewById(R.id.tvData);
        TextView tvPromovata = convertView.findViewById(R.id.tvPromovata);

        tvNota.setText(String.valueOf(d.getValoareNota()));
        tvNume.setText(d.getNumeDisciplina());
        tvSemestru.setText(semestryLabel(d.getSemestru()));
        tvPunctaj.setText("Punctaj: " + d.getPunctajExamen());

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        tvData.setText(d.getDataAdaugare() != null ? sdf.format(d.getDataAdaugare()) : "-");

        if (d.isEstePromovata()) {
            tvPromovata.setText("PROMOVAT");
            tvPromovata.setTextColor(0xFF2E7D32);
            tvPromovata.setBackgroundColor(0xFFE8F5E9);
        } else {
            tvPromovata.setText("NEPROMOVAT");
            tvPromovata.setTextColor(0xFFC62828);
            tvPromovata.setBackgroundColor(0xFFFFEBEE);
        }

        return convertView;
    }

    private String semestryLabel(Disciplina.Semestru semestru) {
        switch (semestru) {
            case SEMESTRUL_1: return "Sem. 1";
            case SEMESTRUL_2: return "Sem. 2";
            case RESTANTA:    return "Restanta";
            default:          return semestru.name();
        }
    }
}
