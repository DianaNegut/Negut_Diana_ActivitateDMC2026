# Cheatsheet Laborator 7 - File I/O + SharedPreferences

> **La examen clasa ta va fi alta** (ex: `Carte`, `Produs`, `Angajat`).  
> Înlocuiește `Disciplina` cu numele clasei tale și câmpurile cu ale tale.

---

## CERINTA 2 — Salvare obiect nou în fișier (în AdaugaActivity)

### Fișiere implicate:
- `FileHelper.java` — **creezi tu** (sau adaugi metode dacă există)
- `AdaugaDisciplinaActivity.java` — adaugi apel la FileHelper
- `MainActivity.java` — declari constanta cu numele fișierului

---

### Pas 1 — Declară constanta fișierului în `MainActivity.java`

```java
// În clasa MainActivity, înainte de onCreate:
static final String FISIER_DISCIPLINE = "discipline.dat";
static final String FISIER_FAVORITE   = "discipline_favorite.dat";
```

---

### Pas 2 — Creează `FileHelper.java` (fișier nou)

**Locație:** `app/src/main/java/com/example/laboratorX/FileHelper.java`

```java
package com.example.laboratorX;  // schimba pachetul

import android.content.Context;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileHelper {

    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    // Salvează un obiect — se ADAUGĂ la sfârșitul fișierului (append = true)
    public static void salveazaDisciplina(Context context, String numeFisier, Disciplina d) {
        File file = new File(context.getFilesDir(), numeFisier);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(inText(d));
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Încarcă toate obiectele din fișier
    public static ArrayList<Disciplina> incarcaDiscipline(Context context, String numeFisier) {
        ArrayList<Disciplina> lista = new ArrayList<>();
        File file = new File(context.getFilesDir(), numeFisier);
        if (!file.exists()) return lista;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linie;
            while ((linie = br.readLine()) != null) {
                if (!linie.trim().isEmpty()) {
                    Disciplina d = dinText(linie);
                    if (d != null) lista.add(d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Obiect -> string (câmpurile separate cu |)
    private static String inText(Disciplina d) {
        String data = d.getDataAdaugare() != null ? SDF.format(d.getDataAdaugare()) : "";
        return d.getNumeDisciplina() + "|" +
               d.isEstePromovata()   + "|" +
               d.getValoareNota()    + "|" +
               d.getPunctajExamen()  + "|" +
               d.getSemestru().name() + "|" +
               data;
    }

    // String -> obiect
    private static Disciplina dinText(String linie) {
        String[] parts = linie.split("\\|");
        if (parts.length < 6) return null;
        try {
            String nume    = parts[0];
            boolean prom   = Boolean.parseBoolean(parts[1]);
            int nota       = Integer.parseInt(parts[2]);
            double punctaj = Double.parseDouble(parts[3]);
            Disciplina.Semestru sem = Disciplina.Semestru.valueOf(parts[4]);
            Date data      = SDF.parse(parts[5]);
            return new Disciplina(nume, prom, nota, punctaj, sem, data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
```

> **Adapteaza** `inText` si `dinText` cu câmpurile clasei tale de la examen.  
> Câte câmpuri are obiectul tău, atâtea valori separi cu `|`.

---

### Pas 3 — Apelează `FileHelper.salveazaDisciplina` în `AdaugaDisciplinaActivity.java`

Găsești metoda unde construiești obiectul și îl trimiți înapoi. **Adaugă un rând înainte de `finish()`:**

```java
private void salveazaDisciplina() {
    // ... cod existent care construieste obiectul ...

    Disciplina disciplina = new Disciplina(/* parametrii */);

    // ★ ADAUGAT: salvare in fisier
    FileHelper.salveazaDisciplina(this, MainActivity.FISIER_DISCIPLINE, disciplina);

    // cod existent: trimite rezultat inapoi si inchide
    Intent intent = new Intent();
    Bundle bundle = new Bundle();
    bundle.putSerializable("disciplina", disciplina);
    intent.putExtras(bundle);
    setResult(RESULT_OK, intent);
    finish();
}
```

---

### Pas 4 — Încarcă din fișier la pornire în `MainActivity.java`

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // ★ Incarca lista din fisier la pornirea aplicatiei
    listaDiscipline = FileHelper.incarcaDiscipline(this, FISIER_DISCIPLINE);

    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDiscipline);
    ListView listView = findViewById(R.id.listViewDiscipline);
    listView.setAdapter(adapter);
    // ... restul codului ...
}
```

---

## CERINTA 3 — LongItemClick salvează la favorite (fișier separat)

### Fișiere implicate:
- `MainActivity.java` — modifici `setOnItemLongClickListener`

**Găsești în `onCreate` din `MainActivity.java` listener-ul de long click și îl înlocuiești / adaugi:**

```java
listView.setOnItemLongClickListener((parent, view, position, id) -> {
    Disciplina disciplina = listaDiscipline.get(position);

    // ★ Salveaza in fisierul de favorite (FISIER_FAVORITE, nu FISIER_DISCIPLINE)
    FileHelper.salveazaDisciplina(this, FISIER_FAVORITE, disciplina);

    Toast.makeText(this, "Adaugat la favorite: " + disciplina.getNumeDisciplina(),
                   Toast.LENGTH_SHORT).show();
    return true;  // ← obligatoriu true ca sa consumi evenimentul
});
```

> Fișierul `FISIER_FAVORITE = "discipline_favorite.dat"` este **separat** de cel principal.  
> Declarația lui e tot în `MainActivity` (vezi Pas 1 de sus).

---

## CERINTA 4 — Activitate de Setări cu SharedPreferences

### Fișiere implicate:
- `SetariActivity.java` — **creezi tu** (activitate nouă)
- `activity_setari.xml` — **creezi tu** (layout-ul activității)
- `AndroidManifest.xml` — **adaugi** declarația activității
- `AdaugaDisciplinaActivity.java` — **adaugi** citirea setărilor și aplicarea lor
- `MainActivity.java` — **adaugi** buton de Setări și lansarea activității

---

### Pas 1 — Declară activitatea în `AndroidManifest.xml`

```xml
<activity
    android:name=".SetariActivity"
    android:exported="false"/>
```

---

### Pas 2 — Creează `activity_setari.xml`

**Locație:** `app/src/main/res/layout/activity_setari.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="20dp">

    <!-- Culoare text -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Culoare text:"/>

    <Spinner
        android:id="@+id/spinnerCuloare"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"/>

    <!-- Dimensiune text -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Dimensiune text:"/>

    <SeekBar
        android:id="@+id/seekBarDimensiune"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:max="14"/>

    <TextView
        android:id="@+id/tvDimensiuneValoare"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="14 sp"
        android:layout_marginBottom="16dp"/>

    <!-- Preview -->
    <TextView
        android:id="@+id/tvPreview"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Text de preview"
        android:layout_marginBottom="24dp"/>

    <Button
        android:id="@+id/btnSalveazaSetari"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Salveaza"/>

</LinearLayout>
```

---

### Pas 3 — Creează `SetariActivity.java`

**Locație:** `app/src/main/java/com/example/laboratorX/SetariActivity.java`

```java
package com.example.laboratorX;  // schimba pachetul

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;

public class SetariActivity extends AppCompatActivity {

    // ★ Constante publice — folosite si in AdaugaActivity
    public static final String PREFS_NAME    = "setari_app";
    public static final String KEY_TEXT_SIZE  = "text_size";
    public static final String KEY_TEXT_COLOR = "text_color";

    private Spinner  spinnerCuloare;
    private SeekBar  seekBarDimensiune;
    private TextView tvDimensiuneValoare, tvPreview;

    // Optiunile de culoare disponibile
    private final String[] numeCulori  = {"Negru", "Albastru", "Rosu", "Verde"};
    private final int[]    valoriCulori = {
        Color.parseColor("#000000"),
        Color.parseColor("#1A237E"),
        Color.parseColor("#D32F2F"),
        Color.parseColor("#2E7D32")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setari);

        spinnerCuloare      = findViewById(R.id.spinnerCuloare);
        seekBarDimensiune   = findViewById(R.id.seekBarDimensiune);
        tvDimensiuneValoare = findViewById(R.id.tvDimensiuneValoare);
        tvPreview           = findViewById(R.id.tvPreview);
        Button btnSalveaza  = findViewById(R.id.btnSalveazaSetari);

        // Umple spinner-ul cu culori
        ArrayAdapter<String> adapterCulori = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, numeCulori);
        adapterCulori.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuloare.setAdapter(adapterCulori);

        // ★ Citeste valorile salvate anterior din SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int   savedColor = prefs.getInt(KEY_TEXT_COLOR, Color.parseColor("#000000"));
        float savedSize  = prefs.getFloat(KEY_TEXT_SIZE, 14f);

        // Seteaza UI cu valorile salvate
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

        // Live preview la schimbarea SeekBar
        seekBarDimensiune.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser) {
                int size = p + 10;   // range: 10-24 sp
                tvDimensiuneValoare.setText(size + " sp");
                tvPreview.setTextSize(size);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Live preview la schimbarea culorii
        spinnerCuloare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int pos, long id) {
                tvPreview.setTextColor(valoriCulori[pos]);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ★ Salveaza in SharedPreferences la click pe buton
        btnSalveaza.setOnClickListener(v -> {
            int   culoareSelectata    = valoriCulori[spinnerCuloare.getSelectedItemPosition()];
            float dimensiuneSelectata = seekBarDimensiune.getProgress() + 10f;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_TEXT_COLOR, culoareSelectata);
            editor.putFloat(KEY_TEXT_SIZE, dimensiuneSelectata);
            editor.apply();  // ← async, mai rapid decat commit()

            Toast.makeText(this, "Setarile au fost salvate", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
```

---

### Pas 4 — Citește setările în `AdaugaDisciplinaActivity.java` și aplică-le

**Adaugă la finalul `onCreate`** (după ce ai legat toate view-urile cu `findViewById`):

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_adauga_disciplina);

    // ... findViewById pentru toate campurile ...

    // ★ Aplica setarile utilizatorului (culoare + dimensiune text)
    aplicaSetari();

    // ... restul codului (adapter spinner, button listener etc.) ...
}

private void aplicaSetari() {
    SharedPreferences prefs = getSharedPreferences(SetariActivity.PREFS_NAME, MODE_PRIVATE);
    int   culoare    = prefs.getInt(SetariActivity.KEY_TEXT_COLOR, Color.parseColor("#000000"));
    float dimensiune = prefs.getFloat(SetariActivity.KEY_TEXT_SIZE, 14f);

    // ★ Aplica pe toate TextView-urile / label-urile din layout
    TextView[] etichete = {
        tvLabelNume,
        tvLabelPunctaj,
        // ... adauga toate TextView-urile pe care vrei sa le afecteze
    };
    for (TextView tv : etichete) {
        tv.setTextColor(culoare);
        tv.setTextSize(dimensiune);
    }
}
```

> **Import necesar** în `AdaugaDisciplinaActivity.java`:
> ```java
> import android.content.SharedPreferences;
> import android.graphics.Color;
> ```

---

### Pas 5 — Lansează SetariActivity din `MainActivity.java`

**Adaugă buton în `activity_main.xml`:**
```xml
<Button
    android:id="@+id/btnSetari"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="SETARI"/>
```

**Adaugă în `onCreate` din `MainActivity.java`:**
```java
Button btnSetari = findViewById(R.id.btnSetari);
btnSetari.setOnClickListener(v -> {
    Intent intent = new Intent(MainActivity.this, SetariActivity.class);
    startActivity(intent);  // ← simplu startActivity, nu launcher (nu asteaptam rezultat)
});
```

---

## CLASA MODEL — ce trebuie sa implementeze

Clasa ta de model (ex: `Disciplina`, `Carte`, `Produs`) **trebuie** să implementeze `Serializable`
ca să poată fi trimisă între activități prin Bundle:

```java
public class Disciplina implements Serializable {
    // ... campurile tale ...
}
```

---

## REZUMAT RAPID — Ce fișiere modifici / creezi

| Cerință | Fișier | Ce faci |
|---------|--------|---------|
| 2 | `FileHelper.java` | **Creezi** clasa cu `salveaza` și `incarca` |
| 2 | `AdaugaDisciplinaActivity.java` | Adaugi `FileHelper.salveaza(...)` înainte de `finish()` |
| 2 | `MainActivity.java` | Încarci lista cu `FileHelper.incarca(...)` în `onCreate` |
| 3 | `MainActivity.java` | Modifici `setOnItemLongClickListener` să apeleze `FileHelper.salveaza(FISIER_FAVORITE, ...)` |
| 4 | `AndroidManifest.xml` | Adaugi `<activity android:name=".SetariActivity"/>` |
| 4 | `activity_setari.xml` | **Creezi** layout cu Spinner, SeekBar, TextView preview, Button |
| 4 | `SetariActivity.java` | **Creezi** activitatea cu SharedPreferences |
| 4 | `AdaugaDisciplinaActivity.java` | Adaugi metoda `aplicaSetari()` care citește din SharedPreferences |
| 4 | `MainActivity.java` | Adaugi buton Setări și `startActivity(SetariActivity)` |

---

## STRUCTURA SHAREDPREFERENCES — Cheat rapid

```java
// Scriere (în SetariActivity):
SharedPreferences prefs  = getSharedPreferences("setari_app", MODE_PRIVATE);
SharedPreferences.Editor editor = prefs.edit();
editor.putInt("text_color", culoare);    // int
editor.putFloat("text_size", 14f);       // float
editor.putString("cheie", "valoare");    // string
editor.apply();

// Citire (în orice Activity):
SharedPreferences prefs = getSharedPreferences("setari_app", MODE_PRIVATE);
int   culoare    = prefs.getInt("text_color", Color.BLACK);   // al doilea param = default
float dimensiune = prefs.getFloat("text_size", 14f);
String val       = prefs.getString("cheie", "default");
```

---

## STRUCTURA FILE I/O — Cheat rapid

```java
// Scrie (append):
File file = new File(context.getFilesDir(), "fisier.dat");
try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
    bw.write("linie de scris");
    bw.newLine();
} catch (Exception e) { e.printStackTrace(); }

// Citeste:
File file = new File(context.getFilesDir(), "fisier.dat");
try (BufferedReader br = new BufferedReader(new FileReader(file))) {
    String linie;
    while ((linie = br.readLine()) != null) {
        // proceseaza linia
    }
} catch (Exception e) { e.printStackTrace(); }
```

> `context.getFilesDir()` = storage intern al aplicației, nu necesită permisiuni speciale.
