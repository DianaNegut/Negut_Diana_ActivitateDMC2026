# CHEATSHEET EXAMEN ANDROID — Laborator 5

> **La examen: înlocuiește `Disciplina` / `disciplina` / `AdaugaDisciplinaActivity` cu numele clasei tale.**
> Inițiale D.N. (Diana Negut) → clasă reală cu D și N, ex: `Disciplina`, `Dosar`, `Destinatie` etc.

---

## ORDINEA DE LUCRU (nu sări pași!)

```
1. Creezi proiectul (Empty Views Activity, Java, minSdk 23)
2. Creezi Disciplina.java (clasa model)
3. Creezi activity_adauga_disciplina.xml (layout formular)
4. Creezi AdaugaDisciplinaActivity.java
5. Modifici activity_main.xml (Button + ListView)
6. Modifici MainActivity.java (adapter + launcher + click events)
7. Verifici AndroidManifest.xml (a 2-a activitate declarată)
```

---

## FIȘIER 1 — `Disciplina.java`

**Locație:** `app/src/main/java/com/example/[numeproiect]/Disciplina.java`

```java
package com.example.laborator4;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Disciplina implements Serializable {

    // ENUM — obligatoriu implements Serializable
    public enum Semestru implements Serializable {
        SEMESTRUL_1,
        SEMESTRUL_2,
        RESTANTA
    }

    // 5+ atribute de TIPURI DIFERITE — obligatoriu String, boolean, int, enum, Date
    private String numeDisciplina;   // String
    private boolean estePromovata;   // boolean
    private int valoareNota;         // int
    private double punctajExamen;    // double (tip în plus față de minim)
    private Semestru semestru;       // enum
    private Date dataAdaugare;       // Date (cerință lab 5)

    // Constructor gol
    public Disciplina() {
        this.numeDisciplina = "Necunoscuta";
        this.estePromovata = false;
        this.valoareNota = 1;
        this.punctajExamen = 0.0;
        this.semestru = Semestru.SEMESTRUL_1;
        this.dataAdaugare = new Date();
    }

    // Constructor cu toți parametrii
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

    // toString — OBLIGATORIU, folosit de ArrayAdapter pentru ListView
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dataStr = dataAdaugare != null ? sdf.format(dataAdaugare) : "-";
        return numeDisciplina +
                " | Nota: " + valoareNota +
                " | Sem: " + semestru +
                " | Promovata: " + (estePromovata ? "Da" : "Nu") +
                " | Data: " + dataStr;
    }
}
```

**CE TREBUIE SĂ AI ÎN CLASĂ:**
- `implements Serializable` pe clasă
- `enum` cu `implements Serializable` declarat ÎNĂUNTRUL clasei
- `import java.util.Date;` — NU `java.sql.Date`!
- constructor gol + constructor cu parametri
- getteri + setteri pentru fiecare atribut
- `toString()` override

---

## FIȘIER 2 — `activity_adauga_disciplina.xml`

**Locație:** `app/src/main/res/layout/activity_adauga_disciplina.xml`
**Cum creezi:** New → Activity → Empty Views Activity → numești `AdaugaDisciplinaActivity`

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

        <!-- String: EditText -->
        <EditText android:id="@+id/etNumeDisciplina"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Nume disciplina"
            android:inputType="text"/>

        <!-- double: EditText numeric -->
        <EditText android:id="@+id/etPunctajExamen"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Punctaj examen (ex: 8.5)"
            android:inputType="numberDecimal"/>

        <!-- enum: Spinner -->
        <Spinner android:id="@+id/spinnerSemestru"
            android:layout_width="match_parent"
            android:layout_height="48dp"/>

        <!-- int: RadioGroup -->
        <RadioGroup android:id="@+id/radioGroupNota"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal">
            <RadioButton android:id="@+id/rb4"  android:text="4"
                android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb5"  android:text="5"
                android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb6"  android:text="6"
                android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb7"  android:text="7"
                android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb8"  android:text="8"
                android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb9"  android:text="9"
                android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb10" android:text="10"
                android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
        </RadioGroup>

        <!-- boolean: CheckBox -->
        <CheckBox android:id="@+id/checkBoxPromovata"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Disciplina promovata"/>

        <!-- Date: DatePicker -->
        <DatePicker android:id="@+id/datePicker"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:datePickerMode="spinner"
            android:calendarViewShown="false"/>

        <Button android:id="@+id/btnSalveaza"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="SALVEAZA"/>

    </LinearLayout>
</ScrollView>
```

**TABEL VIEW → TIP ATRIBUT:**
| View XML | Tip Java | Cum citești valoarea |
|----------|----------|----------------------|
| `EditText` | `String` | `.getText().toString()` |
| `EditText numberDecimal` | `double` | `Double.parseDouble(str)` |
| `RadioGroup` + `RadioButton` | `int` | `Integer.parseInt(rb.getText().toString())` |
| `CheckBox` | `boolean` | `.isChecked()` |
| `Spinner` + ArrayAdapter | `enum` | `Enum.valueOf(spinner.getSelectedItem().toString())` |
| `DatePicker` | `Date` | `Calendar.getInstance()` + `.set(year, month, day)` + `.getTime()` |

---

## FIȘIER 3 — `AdaugaDisciplinaActivity.java`

**Locație:** `app/src/main/java/com/example/[numeproiect]/AdaugaDisciplinaActivity.java`

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

        // 1. Legăm view-urile
        etNumeDisciplina  = findViewById(R.id.etNumeDisciplina);
        etPunctajExamen   = findViewById(R.id.etPunctajExamen);
        spinnerSemestru   = findViewById(R.id.spinnerSemestru);
        radioGroupNota    = findViewById(R.id.radioGroupNota);
        checkBoxPromovata = findViewById(R.id.checkBoxPromovata);
        btnSalveaza       = findViewById(R.id.btnSalveaza);
        datePicker        = findViewById(R.id.datePicker);

        // 2. Alimentăm Spinner-ul cu valorile enum-ului
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
        // Citim fiecare câmp
        String nume = etNumeDisciplina.getText().toString();

        String punctajStr = etPunctajExamen.getText().toString();
        double punctaj = punctajStr.isEmpty() ? 0.0 : Double.parseDouble(punctajStr);

        Disciplina.Semestru semestru = Disciplina.Semestru.valueOf(
                spinnerSemestru.getSelectedItem().toString()
        );

        int notaSelectata = 5; // valoare default
        int radioId = radioGroupNota.getCheckedRadioButtonId();
        if (radioId != -1) {
            RadioButton rb = findViewById(radioId);
            notaSelectata = Integer.parseInt(rb.getText().toString());
        }

        boolean promovata = checkBoxPromovata.isChecked();

        // DatePicker → Date
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        Date dataAdaugare = calendar.getTime();

        // Creăm obiectul
        Disciplina disciplina = new Disciplina(
                nume, promovata, notaSelectata, punctaj, semestru, dataAdaugare
        );

        // Returnăm prin Bundle (Intent dependent)
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("disciplina", disciplina);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
```

**CE NU UITA NICIODATĂ:**
- `setResult(RESULT_OK, intent);` — ÎNAINTE de `finish()`
- `finish();` — ca să închidem activitatea și să revenim
- `bundle.putSerializable("disciplina", disciplina);` — cheia "disciplina" trebuie să fie aceeași în MainActivity

---

## FIȘIER 4 — `activity_main.xml`

**Locație:** `app/src/main/res/layout/activity_main.xml`

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
        android:text="+ ADAUGA"/>

    <ListView
        android:id="@+id/listViewDiscipline"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"/>

</LinearLayout>
```

**IMPORTANT:** ListView cu `layout_height="0dp"` + `layout_weight="1"` = ocupă tot spațiul rămas.

---

## FIȘIER 5 — `MainActivity.java`

**Locație:** `app/src/main/java/com/example/[numeproiect]/MainActivity.java`

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

    // LAUNCHER declarat CA FIELD (nu în onCreate!) — OBLIGATORIU
    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::proceseazaRezultat
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Lista și adapter-ul
        listaDiscipline = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,   // layout standard Android
                listaDiscipline
        );

        // 2. Legăm adapter-ul de ListView
        ListView listView = findViewById(R.id.listViewDiscipline);
        listView.setAdapter(adapter);

        // 3. Click simplu → Toast (cerința 6)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Disciplina disciplina = listaDiscipline.get(position);
            Toast.makeText(this, disciplina.toString(), Toast.LENGTH_LONG).show();
        });

        // 4. Long click → Ștergere (cerința 7)
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            listaDiscipline.remove(position);
            adapter.notifyDataSetChanged();
            return true;   // OBLIGATORIU — consumă evenimentul
        });

        // 5. Buton adăugare → lansăm a 2-a activitate
        Button btnAdauga = findViewById(R.id.btnAdaugaDisciplina);
        btnAdauga.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdaugaDisciplinaActivity.class);
            launcher.launch(intent);
        });
    }

    // 6. Procesăm rezultatul întors din a 2-a activitate
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

---

## FIȘIER 6 — `AndroidManifest.xml`

**Locație:** `app/src/main/AndroidManifest.xml`
**Ce modifici:** adaugi a doua activitate (se generează automat dacă ai creat activitatea din Android Studio)

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

        <!-- Activitatea principală — exported=true + intent-filter -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- A 2-a activitate — exported=false, FĂRĂ intent-filter -->
        <activity
            android:name=".AdaugaDisciplinaActivity"
            android:exported="false"/>

    </application>
</manifest>
```

---

## CHECKLIST FINAL (bifează înainte să predai)

- [ ] `Disciplina implements Serializable`
- [ ] `enum Semestru implements Serializable` (declarat în interiorul clasei)
- [ ] Clasa are minim 5 atribute de tipuri diferite: String, boolean, int, + altele
- [ ] Clasa are atribut `Date` (nu `java.sql.Date`!)
- [ ] Clasa are `toString()` override
- [ ] Constructor gol + constructor cu parametri
- [ ] `AdaugaDisciplinaActivity` are `setResult(RESULT_OK, intent)` + `finish()`
- [ ] Obiectul e pus în Bundle cu `putSerializable`
- [ ] `ActivityResultLauncher` declarat ca **field al clasei** (nu în `onCreate`)
- [ ] `adapter.notifyDataSetChanged()` apelat după add și după remove
- [ ] `return true` în `setOnItemLongClickListener`
- [ ] `AdaugaDisciplinaActivity` declarată în `AndroidManifest.xml`
- [ ] `minSdk = 23` în `build.gradle.kts`

---

## GREȘELI CARE CAUZEAZĂ CRASH / BUILD ERROR

| Greșeală | Efect | Fix |
|----------|-------|-----|
| `launcher` declarat în `onCreate` | Build error / crash | Mută-l ca field al clasei |
| `Disciplina` fără `Serializable` | `ClassCastException` la `getSerializable` | `implements Serializable` |
| `enum` fără `Serializable` | Crash la serializare | `enum X implements Serializable` |
| `import java.sql.Date` | Funcții lipsă | `import java.util.Date` |
| `setResult()` lipsă în a 2-a activitate | Rezultatul nu ajunge la MainActivity | Adaugă înainte de `finish()` |
| `return false` în LongClick | Evenimentul nu e consumat (poate apărea Toast + delete simultan) | `return true` |
| A 2-a activitate lipsește din Manifest | `ActivityNotFoundException` crash | Adaugă în `AndroidManifest.xml` |
| Cheia din `putSerializable` ≠ cheia din `getSerializable` | `null` la preluarea obiectului | Aceeași cheie "disciplina" în ambele locuri |

---

## ADAPTARE RAPIDĂ PENTRU ALTĂ CLASĂ

Dacă la examen trebuie o altă clasă (nu Disciplina), fă **Find & Replace** în tot proiectul:

| Înlocuiește | Cu |
|-------------|-----|
| `Disciplina` | `NumeleClaseiTale` |
| `disciplina` (minuscul) | `numeClasaTa` |
| `AdaugaDisciplinaActivity` | `AdaugaNumeClasaActivity` |
| `listaDiscipline` | `listaNumeClasa` |
| `numeDisciplina` | `atribut1` (și restul atributelor) |

Atributele din clasa model se schimbă în: `Disciplina.java`, `AdaugaDisciplinaActivity.java` (citire date + constructor), `toString()`.
