package com.example.laborator4;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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

    private int pozitie = -1;

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

        Disciplina.Semestru[] semestreValues = Disciplina.Semestru.values();
        ArrayAdapter<Disciplina.Semestru> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                semestreValues
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemestru.setAdapter(adapter);

        Disciplina disciplinaExistenta = getIntent().getParcelableExtra("disciplina");
        if (disciplinaExistenta != null) {
            pozitie = getIntent().getIntExtra("position", -1);
            preCompleteazaCampuri(disciplinaExistenta);
        }

        btnSalveaza.setOnClickListener(v -> salveazaDisciplina());
    }

    private void preCompleteazaCampuri(Disciplina d) {
        etNumeDisciplina.setText(d.getNumeDisciplina());
        etPunctajExamen.setText(String.valueOf(d.getPunctajExamen()));
        checkBoxPromovata.setChecked(d.isEstePromovata());

        Disciplina.Semestru[] semestreValues = Disciplina.Semestru.values();
        for (int i = 0; i < semestreValues.length; i++) {
            if (semestreValues[i] == d.getSemestru()) {
                spinnerSemestru.setSelection(i);
                break;
            }
        }

        int nota = d.getValoareNota();
        int radioId = getResources().getIdentifier("rb" + nota, "id", getPackageName());
        if (radioId != 0) {
            radioGroupNota.check(radioId);
        }

        if (d.getDataAdaugare() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(d.getDataAdaugare());
            datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
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

        Intent intent = new Intent();
        intent.putExtra("disciplina", disciplina);
        intent.putExtra("position", pozitie);
        setResult(RESULT_OK, intent);
        finish();
    }
}
