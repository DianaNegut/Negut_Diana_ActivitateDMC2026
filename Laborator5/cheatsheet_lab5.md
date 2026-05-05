# 📱 Cheatsheet Lab 5 Android — Toate Cerințele

> **Proiect:** `Laborator5` | **Package:** `com.example.laborator4`
> **Clasă model:** `Disciplina` (D.N. → **D**iana **N**egut)

---

## ✅ Cerința 1 — Proiect + API 23/24

### `AndroidManifest.xml` — structură obligatorie
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Laborator4">

        <!-- ✅ Activitate PRINCIPALĂ — exported=true + intent-filter MAIN+LAUNCHER -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- ✅ A 2-a activitate — exported=false, fără intent-filter -->
        <activity
            android:name=".AdaugaDisciplinaActivity"
            android:exported="false"/>

    </application>
</manifest>
```

### `build.gradle.kts` — API minim
```kotlin
android {
    compileSdk = 34
    defaultConfig {
        minSdk = 23       // ← API 23 sau 24
        targetSdk = 34
    }
}
```

---

## ✅ Cerința 2 — Clasa `Disciplina` (cu `Date` + `enum`)

> **NOUTATE față de Lab 4:** s-a adăugat atributul `Date dataAdaugare` și `DatePicker` în formular.

### `Disciplina.java` — codul complet
```java
package com.example.laborator4;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Disciplina implements Serializable {

    // ✅ ENUM — mulțime finită de valori
    public enum Semestru implements Serializable {
        SEMESTRUL_1,
        SEMESTRUL_2,
        RESTANTA
    }

    // ✅ 6 atribute de tipuri diferite (minim 5 + cel puțin un Date)
    private String numeDisciplina;   // String
    private boolean estePromovata;   // boolean
    private int valoareNota;         // int
    private double punctajExamen;    // double
    private Semestru semestru;       // enum
    private Date dataAdaugare;       // Date ← cerință Lab 5

    // Constructor gol
    public Disciplina() {
        this.numeDisciplina = "Necunoscuta";
        this.estePromovata = false;
        this.valoareNota = 1;
        this.punctajExamen = 0.0;
        this.semestru = Semestru.SEMESTRUL_1;
        this.dataAdaugare = new Date();
    }

    // Constructor cu parametri
    public Disciplina(String numeDisciplina, boolean estePromovata,
                      int valoareNota, double punctajExamen,
                      Semestru semestru, Date dataAdaugare) {
        this.numeDisciplina = numeDisciplina;
        this.estePromovata = estePromovata;
        this.valoareNota = valoareNota;
        this.punctajExamen = punctajExamen;
        this.semestru = semestru;
        this.dataAdaugare = dataAdaugare;
    }

    // Getteri și setteri
    public String getNumeDisciplina() { return numeDisciplina; }
    public void setNumeDisciplina(String numeDisciplina) { this.numeDisciplina = numeDisciplina; }

    public boolean isEstePromovata() { return estePromovata; }
    public void setEstePromovata(boolean estePromovata) { this.estePromovata = estePromovata; }

    public int getValoareNota() { return valoareNota; }
    public void setValoareNota(int valoareNota) { this.valoareNota = valoareNota; }

    public double getPunctajExamen() { return punctajExamen; }
    public void setPunctajExamen(double punctajExamen) { this.punctajExamen = punctajExamen; }

    public Semestru getSemestru() { return semestru; }
    public void setSemestru(Semestru semestru) { this.semestru = semestru; }

    public Date getDataAdaugare() { return dataAdaugare; }
    public void setDataAdaugare(Date dataAdaugare) { this.dataAdaugare = dataAdaugare; }

    // ✅ OBLIGATORIU — toString folosit de ListView prin ArrayAdapter
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dataStr = dataAdaugare != null ? sdf.format(dataAdaugare) : "-";
        return numeDisciplina +
                " | Nota: " + valoareNota +
                " | Sem: " + semestru +
                " | Promovata: " + (estePromovata ? "Da" : "Nu") +
                " | Punctaj: " + punctajExamen +
                " | Data: " + dataStr;
    }
}
```

### Tipare de ținut minte
| Element | Regulă |
|---------|--------|
| `Serializable` | **Clasa + enum** implementează ambele `Serializable` |
| `Date` | `import java.util.Date;` ← nu `java.sql.Date`! |
| `toString()` | Returnat de `ArrayAdapter` → afișat în `ListView` |
| Enum | Declarat **înăuntrul clasei** |

---

## ✅ Cerința 3 — Activitatea de adăugare + Intent dependent

### `AdaugaDisciplinaActivity.java` — codul complet
```java
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

        // ✅ Spinner alimentat cu valorile enum-ului
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

        // ✅ DatePicker → Date
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        Date dataAdaugare = calendar.getTime();

        Disciplina disciplina = new Disciplina(
                nume, promovata, notaSelectata, punctaj, semestru, dataAdaugare);

        // ✅ Returnare prin Bundle (Intent dependent)
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("disciplina", disciplina);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
```

---

## ✅ Cerința 5 — ListView cu ArrayAdapter + toString

### `MainActivity.java` — codul complet
```java
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

    private ArrayList<Disciplina> listaDiscipline;
    private ArrayAdapter<Disciplina> adapter;

    // ✅ Launcher declarat CA FIELD (înainte de onCreate!)
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
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,  // ← layout standard 1 text
                listaDiscipline
        );

        ListView listView = findViewById(R.id.listViewDiscipline);
        listView.setAdapter(adapter);

        // ✅ Cerința 6 — Click → Toast cu toString()
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Disciplina disciplina = listaDiscipline.get(position);
            Toast.makeText(this, disciplina.toString(), Toast.LENGTH_LONG).show();
        });

        // ✅ Cerința 7 — LongClick → Ștergere
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            listaDiscipline.remove(position);
            adapter.notifyDataSetChanged();
            return true;   // ← OBLIGATORIU!
        });

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
                listaDiscipline.add(disciplina);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
```

### Cum funcționează `ArrayAdapter<Disciplina>`?
```
ArrayList<Disciplina>  →  ArrayAdapter<Disciplina>  →  ListView
                                    │
                          apelează .toString()
                          pe fiecare obiect
                                    ↓
                          afișează textul în item
```

---

## ✅ Cerința 6 — Toast la click

```java
listView.setOnItemClickListener((parent, view, position, id) -> {
    Disciplina disciplina = listaDiscipline.get(position);
    Toast.makeText(this, disciplina.toString(), Toast.LENGTH_LONG).show();
});
```

---

## ✅ Cerința 7 — LongClick → Ștergere

```java
listView.setOnItemLongClickListener((parent, view, position, id) -> {
    listaDiscipline.remove(position);   // ← șterge din ArrayList
    adapter.notifyDataSetChanged();     // ← actualizează ListView
    return true;   // ← OBLIGATORIU: consumă evenimentul!
});
```

---

## ✅ Layout-uri XML

### `activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="20dp">

    <Button
        android:id="@+id/btnAdaugaDisciplina"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:text="+ ADAUGA DISCIPLINA"/>

    <ListView
        android:id="@+id/listViewDiscipline"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"/>

</LinearLayout>
```

### `activity_adauga_disciplina.xml` — view-uri esențiale
```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="20dp">

        <EditText android:id="@+id/etNumeDisciplina"
            android:layout_width="match_parent" android:layout_height="wrap_content"
            android:hint="Nume disciplina" android:inputType="text"/>

        <EditText android:id="@+id/etPunctajExamen"
            android:layout_width="match_parent" android:layout_height="wrap_content"
            android:hint="Punctaj examen (ex: 8.5)" android:inputType="numberDecimal"/>

        <Spinner android:id="@+id/spinnerSemestru"
            android:layout_width="match_parent" android:layout_height="48dp"/>

        <RadioGroup android:id="@+id/radioGroupNota"
            android:layout_width="match_parent" android:layout_height="wrap_content"
            android:orientation="horizontal">
            <RadioButton android:id="@+id/rb4"  android:text="4"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb5"  android:text="5"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb6"  android:text="6"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb7"  android:text="7"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb8"  android:text="8"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb9"  android:text="9"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb10" android:text="10" android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
        </RadioGroup>

        <CheckBox android:id="@+id/checkBoxPromovata"
            android:layout_width="wrap_content" android:layout_height="wrap_content"
            android:text="Disciplina promovata"/>

        <!-- ✅ DatePicker — cerință Lab 5 -->
        <DatePicker android:id="@+id/datePicker"
            android:layout_width="match_parent" android:layout_height="wrap_content"
            android:datePickerMode="spinner"
            android:calendarViewShown="false"/>

        <Button android:id="@+id/btnSalveaza"
            android:layout_width="match_parent" android:layout_height="56dp"
            android:text="SALVEAZA"/>

    </LinearLayout>
</ScrollView>
```

---

## 📌 Rezumat rapid — fișiere și scopul lor

| Fișier | Scop |
|--------|------|
| `AndroidManifest.xml` | Înregistrare activități; MAIN+LAUNCHER pe MainActivity |
| `Disciplina.java` | Model: String, boolean, int, double, enum, Date + toString() |
| `MainActivity.java` | ListView + ArrayAdapter + OnItemClick + OnLongClick + launcher |
| `AdaugaDisciplinaActivity.java` | Formular: citire date, creare obiect, returnare prin Bundle |
| `activity_main.xml` | Button + ListView |
| `activity_adauga_disciplina.xml` | EditText, Spinner, RadioGroup, CheckBox, DatePicker, Button |

---

## ⚠️ Greșeli frecvente

| Greșeală | Corect |
|----------|--------|
| `startActivityForResult()` (deprecated) | `ActivityResultLauncher` + `registerForActivityResult()` |
| `launcher` declarat în `onCreate` | Declarat ca **field al clasei** (înainte de `onCreate`) |
| Clasa nu implements Serializable | `public class Disciplina implements Serializable` |
| Enum fără Serializable | `public enum Semestru implements Serializable` |
| `setResult()` uitat | `setResult(RESULT_OK, intent); finish();` |
| `return false` în LongClick | `return true;` — obligatoriu! |
| `adapter.notifyDataSetChanged()` uitat | Apelat după orice modificare a listei |
| `java.sql.Date` | `import java.util.Date;` |
| A doua activitate lipsește din Manifest | Orice activitate trebuie declarată în `AndroidManifest.xml` |

---

## 🔑 Mini-recapitulare cerințe → cod

| # | Cerință | Implementată în |
|---|---------|-----------------|
| 1 | Proiect cu API 23/24 | `build.gradle.kts` → `minSdk = 23` |
| 2 | Clasă cu inițiale, 5+ atribute, enum, Date | `Disciplina.java` |
| 3 | Activitate adăugare + Intent dependent + Bundle | `AdaugaDisciplinaActivity.java` + `launcher` în `MainActivity` |
| 5 | ListView cu ArrayAdapter + toString | `MainActivity.java` → `ArrayAdapter<Disciplina>` |
| 6 | Click → Toast | `listView.setOnItemClickListener(...)` |
| 7 | LongClick → Ștergere din listă + ListView | `listView.setOnItemLongClickListener(...)` |
