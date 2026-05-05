# Android Studio Lab 5 – ListView, ArrayAdapter & Date

> **Legendă:** `// >>> NOU` = adăugat în acest pas față de pasul anterior.
Fișierele neschimbate sunt doar menționate, **nu** rescrise.
> 

---

## PASUL 1

**Enunț:** Folosiți aplicația pe care o aveți deja sau creați o nouă aplicație în Android Studio cu o activitate Empty Views, în Java, cu API 23/24.

> Se continuă proiectul din Lab 4. Package-ul proiectului este `com.example.laborator4`.
Nu există fișiere noi sau modificate în acest pas.
`AndroidManifest.xml` rămâne **neschimbat** față de Lab 4 pe tot parcursul acestui laborator.
> 

---

---

## PASUL 2

**Enunț:** Modificați clasa pentru obiectul din realitate. Mai adăugați un atribut de tip `Date` sau `Time`.

> Se adaugă atributul `dataAdaugare` de tip `java.util.Date` în clasa `Disciplina`.
Clasa implementează `Serializable` (transmitere prin `Bundle` cu `putSerializable`).
Enum-ul `Semestru` este definit ca **clasă internă** în `Disciplina` (nu există fișier separat `TipDisciplina.java`).
> 

---

### `Disciplina.java`

```java
package com.example.laborator4;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Disciplina implements Serializable {

    // >>> NOU: enum Semestru definit ca clasa interna
    public enum Semestru implements Serializable {
        SEMESTRUL_1,
        SEMESTRUL_2,
        RESTANTA
    }

    private String numeDisciplina;
    private boolean estePromovata;
    private int valoareNota;
    private double punctajExamen;
    private Semestru semestru;
    // >>> NOU: atribut de tip Date – data la care a fost adaugata disciplina
    private Date dataAdaugare;

    public Disciplina() {
        this.numeDisciplina = "Necunoscuta";
        this.estePromovata = false;
        this.valoareNota = 1;
        this.punctajExamen = 0.0;
        this.semestru = Semestru.SEMESTRUL_1;
        // >>> NOU
        this.dataAdaugare = new Date();
    }

    public Disciplina(String numeDisciplina, boolean estePromovata,
                      int valoareNota, double punctajExamen, Semestru semestru,
                      // >>> NOU
                      Date dataAdaugare) {
        this.numeDisciplina = numeDisciplina;
        this.estePromovata = estePromovata;
        this.valoareNota = valoareNota;
        this.punctajExamen = punctajExamen;
        this.semestru = semestru;
        // >>> NOU
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

    // >>> NOU
    public Date getDataAdaugare() { return dataAdaugare; }
    public void setDataAdaugare(Date dataAdaugare) { this.dataAdaugare = dataAdaugare; }

    // >>> NOU: toString actualizat cu data – folosit de ArrayAdapter in ListView
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

### Fișiere neschimbate față de Lab 4

- `AndroidManifest.xml` — neschimbat
- `activity_main.xml` — neschimbat (se modifica la pasul 5)
- `activity_adauga_disciplina.xml` — neschimbat (se modifica la pasul 3)

---

---

## PASUL 3

**Enunț:** Modificați activitatea de adăugare obiect. Datele introduse sunt folosite pentru crearea unei instanțe a clasei, care este returnată prin Bundle (Serializable) către prima activitate. Trebuie să folosiți un Intent explicit.

> Se adaugă un `DatePicker` în layout și se citește data selectată la apăsarea butonului Salvează.
Rezultatul este returnat ca `Serializable` printr-un `Bundle` cu cheia `"disciplina"`.
`ActivityResultLauncher` din `MainActivity` rămâne același ca în Lab 4.
> 

---

### `activity_adauga_disciplina.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#F0F4F8">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="20dp">

        <!-- Card header -->
        <androidx.cardview.widget.CardView
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="20dp"
            app:cardCornerRadius="16dp"
            app:cardElevation="6dp"
            app:cardBackgroundColor="#1A237E">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="24dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Adauga Disciplina"
                    android:textColor="#FFFFFF"
                    android:textSize="24sp"
                    android:textStyle="bold"/>

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Completeaza datele disciplinei"
                    android:textColor="#90CAF9"
                    android:textSize="14sp"
                    android:layout_marginTop="4dp"/>
            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <!-- Card: Informatii Generale – nume, punctaj, >>> NOU: datePicker -->
        <androidx.cardview.widget.CardView
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            app:cardCornerRadius="12dp"
            app:cardElevation="4dp"
            app:cardBackgroundColor="#FFFFFF">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="20dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Informatii Generale"
                    android:textColor="#1A237E"
                    android:textSize="16sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="16dp"/>

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Nume disciplina"
                    android:textColor="#546E7A"
                    android:textSize="12sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="6dp"/>
                <EditText
                    android:id="@+id/etNumeDisciplina"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="Ex: Programare Android"
                    android:inputType="text"
                    android:padding="12dp"
                    android:background="@drawable/edittext_bg"
                    android:textSize="15sp"
                    android:layout_marginBottom="16dp"/>

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Punctaj examen"
                    android:textColor="#546E7A"
                    android:textSize="12sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="6dp"/>
                <EditText
                    android:id="@+id/etPunctajExamen"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="Ex: 8.50"
                    android:inputType="numberDecimal"
                    android:padding="12dp"
                    android:background="@drawable/edittext_bg"
                    android:textSize="15sp"
                    android:layout_marginBottom="16dp"/>

                <!-- >>> NOU: sectiune pentru selectarea datei -->
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Data adaugare"
                    android:textColor="#546E7A"
                    android:textSize="12sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="6dp"/>

                <!-- >>> NOU: DatePicker pentru selectarea datei de adaugare -->
                <DatePicker
                    android:id="@+id/datePicker"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:datePickerMode="spinner"
                    android:calendarViewShown="false"/>

            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <!-- Card: Semestru – Spinner alimentat cu Disciplina.Semestru.values() -->
        <androidx.cardview.widget.CardView
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            app:cardCornerRadius="12dp"
            app:cardElevation="4dp"
            app:cardBackgroundColor="#FFFFFF">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="20dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Semestru"
                    android:textColor="#1A237E"
                    android:textSize="16sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="12dp"/>

                <Spinner
                    android:id="@+id/spinnerSemestru"
                    android:layout_width="match_parent"
                    android:layout_height="48dp"
                    android:background="@drawable/edittext_bg"
                    android:padding="8dp"/>

            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <!-- Card: Valoare nota – RadioGroup cu butoane 4..10 -->
        <androidx.cardview.widget.CardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            app:cardCornerRadius="12dp"
            app:cardElevation="4dp"
            app:cardBackgroundColor="#FFFFFF">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="20dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Valoare nota"
                    android:textColor="#1A237E"
                    android:textSize="16sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="12dp"/>

                <RadioGroup
                    android:id="@+id/radioGroupNota"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal">
                    <RadioButton android:id="@+id/rb4"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="4"  android:textColor="#37474F"/>
                    <RadioButton android:id="@+id/rb5"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="5"  android:textColor="#37474F"/>
                    <RadioButton android:id="@+id/rb6"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="6"  android:textColor="#37474F"/>
                    <RadioButton android:id="@+id/rb7"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="7"  android:textColor="#37474F"/>
                    <RadioButton android:id="@+id/rb8"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="8"  android:textColor="#37474F"/>
                    <RadioButton android:id="@+id/rb9"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="9"  android:textColor="#37474F"/>
                    <RadioButton
                        android:id="@+id/rb10"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:text="10"
                        android:textColor="#37474F" />
                </RadioGroup>

            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <!-- Card: Status – CheckBox promovata -->
        <androidx.cardview.widget.CardView
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="24dp"
            app:cardCornerRadius="12dp"
            app:cardElevation="4dp"
            app:cardBackgroundColor="#FFFFFF">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="20dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Status"
                    android:textColor="#1A237E"
                    android:textSize="16sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="12dp"/>

                <CheckBox
                    android:id="@+id/checkBoxPromovata"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Disciplina promovata"
                    android:textColor="#37474F"
                    android:textSize="15sp"
                    android:buttonTint="#1A237E"/>

            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <Button
            android:id="@+id/btnSalveaza"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="SALVEAZA DISCIPLINA"
            android:textColor="#FFFFFF"
            android:textStyle="bold"
            android:textSize="15sp"
            android:backgroundTint="#1A237E"
            android:layout_marginBottom="16dp"/>

    </LinearLayout>
</ScrollView>
```

### `AdaugaDisciplinaActivity.java`

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
    // >>> NOU
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
        // >>> NOU
        datePicker        = findViewById(R.id.datePicker);

        // Spinner alimentat cu valorile enum-ului intern Disciplina.Semestru
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

        // Semestru vine din Spinner ca String -> convertit cu valueOf
        String semestruSelectat = spinnerSemestru.getSelectedItem().toString();
        Disciplina.Semestru semestru = Disciplina.Semestru.valueOf(semestruSelectat);

        // Nota vine din RadioGroup – textul butonului selectat, parsat ca int
        int notaSelectata = 5;
        int radioId = radioGroupNota.getCheckedRadioButtonId();
        if (radioId != -1) {
            RadioButton rb = findViewById(radioId);
            notaSelectata = Integer.parseInt(rb.getText().toString());
        }

        boolean promovata = checkBoxPromovata.isChecked();

        // >>> NOU: citim data selectata din DatePicker si o convertim in Date
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        Date dataAdaugare = calendar.getTime();

        // >>> NOU: constructorul Disciplina primeste acum si dataAdaugare
        Disciplina disciplina = new Disciplina(nume, promovata, notaSelectata, punctaj, semestru, dataAdaugare);

        // Rezultatul se trimite ca Serializable prin Bundle cu cheia "disciplina"
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

---

## PASUL 5 + 6 + 7

**Enunț (5):** Folosiți un `ListView` pentru afișarea tuturor obiectelor din listă. Utilizați `ArrayAdapter` și metoda `toString` pentru afișare.

**Enunț (6):** Când utilizatorul selectează un obiect din `ListView`, acesta este afișat printr-un `Toast`.

**Enunț (7):** Pentru evenimentul `LongItemClick` pe un obiect din listă, acesta este șters din `ListView` și din lista de obiecte.

---

### `activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="#F0F4F8"
    android:padding="20dp">

    <!-- Card header – titlu aplicatie -->
    <androidx.cardview.widget.CardView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="20dp"
        app:cardCornerRadius="16dp"
        app:cardElevation="6dp"
        app:cardBackgroundColor="#1A237E">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="24dp">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Gestiune Discipline"
                android:textColor="#FFFFFF"
                android:textSize="26sp"
                android:textStyle="bold"/>

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Evidenta disciplinelor universitare"
                android:textColor="#90CAF9"
                android:textSize="14sp"
                android:layout_marginTop="4dp"/>

        </LinearLayout>
    </androidx.cardview.widget.CardView>

    <Button
        android:id="@+id/btnAdaugaDisciplina"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:text="+ ADAUGA DISCIPLINA"
        android:textColor="#FFFFFF"
        android:textStyle="bold"
        android:textSize="15sp"
        android:backgroundTint="#1A237E"
        android:layout_marginBottom="16dp"/>

    <!-- >>> NOU: ListView pentru afisarea tuturor disciplinelor (cerinta 5) -->
    <ListView
        android:id="@+id/listViewDiscipline"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:divider="#E0E0E0"
        android:dividerHeight="1dp"/>

</LinearLayout>
```

### `MainActivity.java`

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
// >>> NOU
import android.widget.ListView;
import android.widget.Toast;

// >>> NOU
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // >>> NOU: lista de obiecte Disciplina
    private ArrayList<Disciplina> listaDiscipline;
    // >>> NOU: ArrayAdapter leaga lista de ListView; foloseste toString() din Disciplina
    private ArrayAdapter<Disciplina> adapter;

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::proceseazaRezultat
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // >>> NOU: initializam lista si ArrayAdapter
        listaDiscipline = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDiscipline);

        ListView listView = findViewById(R.id.listViewDiscipline);
        // >>> NOU
        listView.setAdapter(adapter);

        // >>> NOU: click simplu pe un element – afisam obiectul intr-un Toast (cerinta 6)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Disciplina disciplina = listaDiscipline.get(position);
            // >>> NOU
            Toast.makeText(this, disciplina.toString(), Toast.LENGTH_LONG).show();
        });

        // >>> NOU: long click pe un element – stergem din lista si din ListView (cerinta 7)
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            listaDiscipline.remove(position);
            // >>> NOU: notificam adapter-ul ca datele s-au schimbat, ListView se redeseneaza
            adapter.notifyDataSetChanged();
            // >>> NOU: returnam true ca sa consumam evenimentul (nu se declanseaza si click simplu)
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
            Bundle bundle = result.getData().getExtras();
            // Disciplina vine ca Serializable cu cheia "disciplina" (litere mici)
            Disciplina disciplina = (Disciplina) bundle.getSerializable("disciplina");
            if (disciplina != null) {
                // >>> NOU: adaugam obiectul in lista si notificam adapter-ul
                listaDiscipline.add(disciplina);
                // >>> NOU
                adapter.notifyDataSetChanged();
            }
        }
    }
}
```

### `AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Laborator4">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".AdaugaDisciplinaActivity"
            android:exported="false"/>

    </application>

</manifest>
```

---

## Rezumat modificări față de Lab 4

| Fișier | Status |
| --- | --- |
| `AndroidManifest.xml` | **neschimbat** |
| `Disciplina.java` | **modificat** – câmp `Date dataAdaugare` adăugat; enum intern `Semestru` (SEMESTRUL_1, SEMESTRUL_2, RESTANTA); implementează `Serializable`; `toString` actualizat |
| `AdaugaDisciplinaActivity.java` | **modificat** – `DatePicker` citit + pasat la constructorul `Disciplina`; rezultat trimis ca `Serializable` cu cheia `"disciplina"` |
| `activity_adauga_disciplina.xml` | **modificat** – `DatePicker` adăugat în cardul „Informatii Generale" |
| `MainActivity.java` | **modificat** – `ListView`, `ArrayList`, `ArrayAdapter`, click (Toast), long click (ștergere); recepție rezultat via `getSerializable("disciplina")` |
| `activity_main.xml` | **modificat** – CardView header + buton `btnAdaugaDisciplina` + `ListView` |

---

## Sfaturi examen

- `ArrayAdapter` folosește automat `toString()` al obiectelor din listă pentru afișare în `ListView`
- `adapter.notifyDataSetChanged()` trebuie apelat după orice modificare a listei (add / remove)
- `setOnItemLongClickListener` trebuie să returneze `true` ca să consume evenimentul
- `Disciplina` implementează `Serializable` → transmitere cu `bundle.putSerializable("disciplina", obj)` / `(Disciplina) bundle.getSerializable("disciplina")`
- Cheia din Bundle este `"disciplina"` (litere mici) – respectați exact același șir la scriere și citire
- `Calendar.getInstance()` + `calendar.set(year, month, day)` + `calendar.getTime()` = conversie `DatePicker` → `Date`
- `listaDiscipline.remove(position)` șterge după index; există și `remove(Object)` care șterge după referință
- `Semestru` este enum **intern** în `Disciplina` – se referă ca `Disciplina.Semestru` și se populează cu `Disciplina.Semestru.values()`
