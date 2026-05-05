package com.example.laborator4;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SetariActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "setari_app";
    public static final String KEY_TEXT_SIZE = "text_size";
    public static final String KEY_TEXT_COLOR = "text_color";

    private Spinner spinnerCuloare;
    private SeekBar seekBarDimensiune;
    private TextView tvDimensiuneValoare;
    private TextView tvPreview;

    private final String[] numeCulori = {"Negru", "Albastru", "Rosu", "Verde"};
    private final int[] valoriCulori = {
            Color.parseColor("#000000"),
            Color.parseColor("#1A237E"),
            Color.parseColor("#D32F2F"),
            Color.parseColor("#2E7D32")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setari);

        spinnerCuloare = findViewById(R.id.spinnerCuloare);
        seekBarDimensiune = findViewById(R.id.seekBarDimensiune);
        tvDimensiuneValoare = findViewById(R.id.tvDimensiuneValoare);
        tvPreview = findViewById(R.id.tvPreview);
        Button btnSalveazaSetari = findViewById(R.id.btnSalveazaSetari);

        ArrayAdapter<String> adapterCulori = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, numeCulori);
        adapterCulori.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuloare.setAdapter(adapterCulori);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedColor = prefs.getInt(KEY_TEXT_COLOR, Color.parseColor("#546E7A"));
        float savedSize = prefs.getFloat(KEY_TEXT_SIZE, 14f);

        for (int i = 0; i < valoriCulori.length; i++) {
            if (valoriCulori[i] == savedColor) {
                spinnerCuloare.setSelection(i);
                break;
            }
        }

        int progress = Math.max(0, Math.min(14, (int) savedSize - 10));
        seekBarDimensiune.setProgress(progress);
        tvDimensiuneValoare.setText((int) savedSize + " sp");
        tvPreview.setTextColor(savedColor);
        tvPreview.setTextSize(savedSize);

        seekBarDimensiune.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser) {
                int size = p + 10;
                tvDimensiuneValoare.setText(size + " sp");
                tvPreview.setTextSize(size);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        spinnerCuloare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                tvPreview.setTextColor(valoriCulori[position]);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSalveazaSetari.setOnClickListener(v -> {
            int culoareSelectata = valoriCulori[spinnerCuloare.getSelectedItemPosition()];
            float dimensiuneSelectata = seekBarDimensiune.getProgress() + 10f;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_TEXT_COLOR, culoareSelectata);
            editor.putFloat(KEY_TEXT_SIZE, dimensiuneSelectata);
            editor.apply();

            Toast.makeText(this, "Setarile au fost salvate", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
