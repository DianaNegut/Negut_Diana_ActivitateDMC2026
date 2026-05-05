package com.example.laborator4;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;

import java.util.Calendar;
import java.util.Date;

public class AdaugaDisciplinaActivity extends AppCompatActivity {

    private EditText etNumeDisciplina, etPunctajExamen;
    private Spinner spinnerSemestru;
    private RadioGroup radioGroupNota;
    private CheckBox checkBoxPromovata;
    private Button btnSalveaza;
    private DatePicker datePicker;

    private TextView tvLabelInfoGenerale, tvLabelNume, tvLabelPunctaj, tvLabelData;
    private TextView tvLabelSemestru, tvLabelValoareNota, tvLabelStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adauga_disciplina);

        etNumeDisciplina  = findViewById(R.id.etNumeDisciplina);
        etPunctajExamen   = findViewById(R.id.etPunctajExamen);
        spinnerSemestru   = findViewById(R.id.spinnerSemestru);
        radioGroupNota    = findViewById(R.id.radioGroupNota);
        checkBoxPromovata = findViewById(R.id.checkBoxPromovata);
        btnSalveaza       = findViewById(R.id.btnSalveaza);
        datePicker        = findViewById(R.id.datePicker);

        tvLabelInfoGenerale = findViewById(R.id.tvLabelInfoGenerale);
        tvLabelNume         = findViewById(R.id.tvLabelNume);
        tvLabelPunctaj      = findViewById(R.id.tvLabelPunctaj);
        tvLabelData         = findViewById(R.id.tvLabelData);
        tvLabelSemestru     = findViewById(R.id.tvLabelSemestru);
        tvLabelValoareNota  = findViewById(R.id.tvLabelValoareNota);
        tvLabelStatus       = findViewById(R.id.tvLabelStatus);

        aplicaSetari();

        Disciplina.Semestru[] semestreValues = Disciplina.Semestru.values();
        ArrayAdapter<Disciplina.Semestru> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                semestreValues
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemestru.setAdapter(adapter);

        btnSalveaza.setOnClickListener(v -> salveazaDisciplina());
    }

    private void aplicaSetari() {
        SharedPreferences prefs = getSharedPreferences(SetariActivity.PREFS_NAME, MODE_PRIVATE);
        int culoare = prefs.getInt(SetariActivity.KEY_TEXT_COLOR, Color.parseColor("#546E7A"));
        float dimensiune = prefs.getFloat(SetariActivity.KEY_TEXT_SIZE, 14f);

        TextView[] etichete = {
                tvLabelInfoGenerale, tvLabelNume, tvLabelPunctaj, tvLabelData,
                tvLabelSemestru, tvLabelValoareNota, tvLabelStatus
        };
        for (TextView tv : etichete) {
            tv.setTextColor(culoare);
            tv.setTextSize(dimensiune);
        }
    }

    private void salveazaDisciplina() {
        String nume = etNumeDisciplina.getText().toString();

        String punctajStr = etPunctajExamen.getText().toString();
        double punctaj = punctajStr.isEmpty() ? 0.0 : Double.parseDouble(punctajStr);

        String semestruSelectat = spinnerSemestru.getSelectedItem().toString();
        Disciplina.Semestru semestru = Disciplina.Semestru.valueOf(semestruSelectat);

        int notaSelectata = 5;
        int radioId = radioGroupNota.getCheckedRadioButtonId();
        if (radioId != -1) {
            RadioButton rb = findViewById(radioId);
            notaSelectata = Integer.parseInt(rb.getText().toString());
        }

        boolean promovata = checkBoxPromovata.isChecked();

        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        Date dataAdaugare = calendar.getTime();

        Disciplina disciplina = new Disciplina(nume, promovata, notaSelectata, punctaj, semestru, dataAdaugare);

        FileHelper.salveazaDisciplina(this, MainActivity.FISIER_DISCIPLINE, disciplina);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("disciplina", disciplina);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
