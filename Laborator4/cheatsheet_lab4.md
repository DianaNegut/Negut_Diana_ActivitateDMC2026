# Cheatsheet Lab 4 Android — Examen Practic

> Proiect: `Laborator4` | Package: `com.example.laborator4`
> Clasa model: `Disciplina` (Diana **D**iscipli**N**a — conține D și N din Diana Negut)

---

## ORDINEA DE LUCRU (urmeaz-o exact)

```
1. Creezi proiectul / verifici minSdk = 23
2. Modifici AndroidManifest.xml  →  activitate implicită + a 2-a activitate
3. Creezi Disciplina.java         →  clasa cu enum + Serializable
4. Creezi activity_adauga_disciplina.xml  →  toate view-urile cerute
5. Creezi AdaugaDisciplinaActivity.java   →  citești din view-uri, returnezi bundle
6. Modifici activity_main.xml     →  buton deschidere + TextViewuri afișare
7. Modifici MainActivity.java     →  launcher + primire rezultat
8. (Optional) Îmbunătățești layout cu AI  →  cardview etc.
```

---

## Cerința 1 — Proiect nou + Activitate implicită

### Fișier: `app/build.gradle.kts`
```kotlin
android {
    defaultConfig {
        minSdk = 23   // ← 23 sau 24
    }
}
```

### Fișier: `app/src/main/AndroidManifest.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Laborator4">

        <!-- ACTIVITATE IMPLICITA = are intent-filter MAIN + LAUNCHER + exported=true -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- A 2-a activitate = fara intent-filter, exported=false -->
        <activity
            android:name=".AdaugaDisciplinaActivity"
            android:exported="false"/>

    </application>
</manifest>
```

**Reguli cheie:**
- Activitate implicită = `intent-filter` cu `MAIN` + `LAUNCHER` + `exported="true"`
- A 2-a activitate = fără intent-filter, `exported="false"`
- Orice activitate nouă TREBUIE declarată în Manifest

---

## Cerința 2 — Clasa model cu inițiale + enum

### Fișier nou: `app/src/main/java/com/example/laborator4/Disciplina.java`

Clasa trebuie să conțină **inițialele numelui** (D și N din Diana Negut → **D**iscipli**N**a).

```java
package com.example.laborator4;

import java.io.Serializable;

public class Disciplina implements Serializable {

    // ENUM = multime finita de valori (implements Serializable separat!)
    public enum Semestru implements Serializable {
        SEMESTRUL_1,
        SEMESTRUL_2,
        RESTANTA
    }

    // 5 atribute de tipuri DIFERITE
    private String numeDisciplina;   // String  ✓
    private boolean estePromovata;   // boolean ✓
    private int valoareNota;         // int     ✓
    private double punctajExamen;    // double  ✓
    private Semestru semestru;       // enum    ✓

    // Constructor gol
    public Disciplina() {
        this.numeDisciplina = "Necunoscuta";
        this.estePromovata = false;
        this.valoareNota = 1;
        this.punctajExamen = 0.0;
        this.semestru = Semestru.SEMESTRUL_1;
    }

    // Constructor cu toti parametrii
    public Disciplina(String numeDisciplina, boolean estePromovata,
                      int valoareNota, double punctajExamen, Semestru semestru) {
        this.numeDisciplina = numeDisciplina;
        this.estePromovata = estePromovata;
        this.valoareNota = valoareNota;
        this.punctajExamen = punctajExamen;
        this.semestru = semestru;
    }

    // Getteri si setteri
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

    @Override
    public String toString() {
        return "Disciplina{" +
                "numeDisciplina='" + numeDisciplina + '\'' +
                ", estePromovata=" + estePromovata +
                ", valoareNota=" + valoareNota +
                ", punctajExamen=" + punctajExamen +
                ", semestru=" + semestru +
                '}';
    }
}
```

**Checklist cerința 2:**
- [ ] Clasa conține inițialele numelui (D și N → Disciplina)
- [ ] `implements Serializable` pe clasa principală
- [ ] `implements Serializable` pe enum (separat!)
- [ ] Minim 5 atribute: String, boolean, int, + încă 2 (double, enum)

---

## Cerința 3 — Activity cu toate view-urile + deschidere din MainActivity

### Fișier nou: `app/src/main/res/layout/activity_adauga_disciplina.xml`

Trebuie să conțină OBLIGATORIU: `TextView`, `EditText`, `Button`, `CheckBox`, `RadioButton`, `Spinner`, `RatingBar`, `Switch`, `ToggleButton`

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#F0F4F8">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="20dp">

        <!-- TextView (label) -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Nume disciplina"
            android:textStyle="bold"/>

        <!-- EditText - text -->
        <EditText
            android:id="@+id/etNumeDisciplina"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Ex: Programare Android"
            android:inputType="text"/>

        <!-- TextView (label) -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Punctaj examen"
            android:textStyle="bold"/>

        <!-- EditText - numar decimal -->
        <EditText
            android:id="@+id/etPunctajExamen"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Ex: 8.50"
            android:inputType="numberDecimal"/>

        <!-- TextView (label) -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Semestru"
            android:textStyle="bold"/>

        <!-- Spinner (pentru enum) -->
        <Spinner
            android:id="@+id/spinnerSemestru"
            android:layout_width="match_parent"
            android:layout_height="48dp"/>

        <!-- TextView (label) -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Nota"
            android:textStyle="bold"/>

        <!-- RadioGroup cu RadioButton-uri -->
        <RadioGroup
            android:id="@+id/radioGroupNota"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal">
            <RadioButton android:id="@+id/rb4"  android:text="4"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb5"  android:text="5"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb6"  android:text="6"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb7"  android:text="7"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb8"  android:text="8"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb9"  android:text="9"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
            <RadioButton android:id="@+id/rb10" android:text="10" android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content"/>
        </RadioGroup>

        <!-- CheckBox -->
        <CheckBox
            android:id="@+id/checkBoxPromovata"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Disciplina promovata"/>

        <!-- RatingBar -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Rating"
            android:textStyle="bold"/>
        <RatingBar
            android:id="@+id/ratingBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:numStars="5"
            android:stepSize="1"/>

        <!-- Switch -->
        <Switch
            android:id="@+id/switchActiv"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Disciplina activa"/>

        <!-- ToggleButton -->
        <ToggleButton
            android:id="@+id/toggleButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textOn="OBLIGATORIE"
            android:textOff="OPTIONALA"/>

        <!-- Button Salvare -->
        <Button
            android:id="@+id/btnSalveaza"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:layout_marginTop="16dp"
            android:text="SALVEAZA"/>

    </LinearLayout>
</ScrollView>
```

### Fișier: `app/src/main/res/layout/activity_main.xml` — butonul de deschidere
```xml
<!-- Adaugă asta în activity_main.xml -->
<Button
    android:id="@+id/btnAdaugaDisciplina"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Adauga disciplina"/>
```

---

## Cerința 4 — Returnare obiect prin Bundle + afișare în MainActivity

### Fișier nou: `app/src/main/java/com/example/laborator4/AdaugaDisciplinaActivity.java`

```java
package com.example.laborator4;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class AdaugaDisciplinaActivity extends AppCompatActivity {

    private EditText etNumeDisciplina, etPunctajExamen;
    private Spinner spinnerSemestru;
    private RadioGroup radioGroupNota;
    private CheckBox checkBoxPromovata;
    private Button btnSalveaza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adauga_disciplina);

        // Leaga view-urile (ID-urile trebuie sa existe in XML)
        etNumeDisciplina  = findViewById(R.id.etNumeDisciplina);
        etPunctajExamen   = findViewById(R.id.etPunctajExamen);
        spinnerSemestru   = findViewById(R.id.spinnerSemestru);
        radioGroupNota    = findViewById(R.id.radioGroupNota);
        checkBoxPromovata = findViewById(R.id.checkBoxPromovata);
        btnSalveaza       = findViewById(R.id.btnSalveaza);

        // Populeaza Spinner cu valorile enum-ului
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
        // EditText → String
        String nume = etNumeDisciplina.getText().toString();

        // EditText → double
        String punctajStr = etPunctajExamen.getText().toString();
        double punctaj = punctajStr.isEmpty() ? 0.0 : Double.parseDouble(punctajStr);

        // Spinner → Enum  (valueOf converteste String in enum)
        String semestruSelectat = spinnerSemestru.getSelectedItem().toString();
        Disciplina.Semestru semestru = Disciplina.Semestru.valueOf(semestruSelectat);

        // RadioGroup → int
        int notaSelectata = 5; // default
        int radioId = radioGroupNota.getCheckedRadioButtonId();
        if (radioId != -1) {
            RadioButton rb = findViewById(radioId);
            notaSelectata = Integer.parseInt(rb.getText().toString());
        }

        // CheckBox → boolean
        boolean promovata = checkBoxPromovata.isChecked();

        // Creare obiect
        Disciplina disciplina = new Disciplina(nume, promovata, notaSelectata, punctaj, semestru);

        // RETURNARE prin Bundle catre MainActivity
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("disciplina", disciplina);  // cheia = "disciplina"
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();  // inchide activity si revine la MainActivity
    }
}
```

### Fișier: `app/src/main/res/layout/activity_main.xml` — TextViewuri de afișare

```xml
<!-- Adaugă câte un TextView pentru fiecare atribut -->
<TextView android:id="@+id/tvNumeDisciplina"  android:layout_width="match_parent" android:layout_height="wrap_content" android:text="-"/>
<TextView android:id="@+id/tvValoareNota"     android:layout_width="match_parent" android:layout_height="wrap_content" android:text="-"/>
<TextView android:id="@+id/tvPunctajExamen"   android:layout_width="match_parent" android:layout_height="wrap_content" android:text="-"/>
<TextView android:id="@+id/tvSemestru"        android:layout_width="match_parent" android:layout_height="wrap_content" android:text="-"/>
<TextView android:id="@+id/tvEstePromovata"   android:layout_width="match_parent" android:layout_height="wrap_content" android:text="-"/>
```

### Fișier: `app/src/main/java/com/example/laborator4/MainActivity.java`

```java
package com.example.laborator4;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvNumeDisciplina, tvValoareNota, tvPunctajExamen,
            tvSemestru, tvEstePromovata;

    // LAUNCHER = declarat ca FIELD al clasei (NU in onCreate!)
    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::proceseazaRezultat
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Leaga TextViewurile
        tvNumeDisciplina  = findViewById(R.id.tvNumeDisciplina);
        tvValoareNota     = findViewById(R.id.tvValoareNota);
        tvPunctajExamen   = findViewById(R.id.tvPunctajExamen);
        tvSemestru        = findViewById(R.id.tvSemestru);
        tvEstePromovata   = findViewById(R.id.tvEstePromovata);

        // Buton care deschide a 2-a activitate (intent explicit)
        Button btnAdauga = findViewById(R.id.btnAdaugaDisciplina);
        btnAdauga.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdaugaDisciplinaActivity.class);
            launcher.launch(intent);  // launcher, NU startActivity!
        });
    }

    // Callback apelat automat cand AdaugaDisciplinaActivity face finish()
    private void proceseazaRezultat(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Bundle bundle = result.getData().getExtras();
            Disciplina disciplina = (Disciplina) bundle.getSerializable("disciplina");

            if (disciplina != null) {
                tvNumeDisciplina.setText(disciplina.getNumeDisciplina());
                tvValoareNota.setText(String.valueOf(disciplina.getValoareNota()));
                tvPunctajExamen.setText(String.valueOf(disciplina.getPunctajExamen()));
                tvSemestru.setText(disciplina.getSemestru().toString());
                tvEstePromovata.setText(disciplina.isEstePromovata() ? "Da" : "Nu");
            }
        }
    }
}
```

### Flux date (vizual)

```
[AdaugaDisciplinaActivity]
  ← citire din EditText, Spinner, RadioGroup, CheckBox
  ← new Disciplina(...)
  ← bundle.putSerializable("disciplina", disciplina)
  ← setResult(RESULT_OK, intent)
  ← finish()
        ↓
[MainActivity - proceseazaRezultat()]
  ← bundle.getSerializable("disciplina")
  ← cast: (Disciplina)
        ↓
[TextView-uri] ← .setText(disciplina.getXxx())
```

---

## Cerința 5 — Îmbunătățire layout cu AI

### Ce faci:
1. Copiezi conținutul lui `activity_adauga_disciplina.xml`
2. Il trimiți la ChatGPT / Claude / Gemini cu mesajul:
   > "Îmbunătățește aspectul acestui layout Android. Păstrează toate android:id-urile exact cum sunt. Poți folosi CardView, culori, margini. Nu schimba niciun id."
3. Înlocuiești conținutul fișierului cu răspunsul AI

### Dacă AI-ul adaugă CardView, adaugă dependency în `build.gradle.kts`:
```kotlin
dependencies {
    implementation("androidx.cardview:cardview:1.0.0")
}
```

---

## Greșeli frecvente la examen

| Greșeală | Corect |
|----------|--------|
| `startActivityForResult()` | `ActivityResultLauncher` + `registerForActivityResult()` |
| `launcher` declarat în `onCreate` | Declarat ca **field al clasei** ÎNAINTE de `onCreate` |
| Clasa fără `implements Serializable` | `public class Disciplina implements Serializable` |
| Enum fără `implements Serializable` | `public enum Semestru implements Serializable` |
| `setResult()` uitat | `setResult(RESULT_OK, intent); finish();` — ambele! |
| A 2-a activitate lipsește din Manifest | Orice activitate trebuie declarată în `AndroidManifest.xml` |
| `bundle.getSerializable()` fără cast | `(Disciplina) bundle.getSerializable("disciplina")` |
| Cheia la put ≠ cheia la get | `putSerializable("disciplina", ...)` → `getSerializable("disciplina")` |

---

## Rezumat fișiere

| Fișier | Ce faci |
|--------|---------|
| `AndroidManifest.xml` | Adaugi a 2-a activitate, verifici intent-filter pe MainActivity |
| `app/build.gradle.kts` | `minSdk = 23` |
| `Disciplina.java` | Clasa model: enum + Serializable + 5 atribute |
| `activity_adauga_disciplina.xml` | Toate view-urile cerute (9 tipuri) |
| `AdaugaDisciplinaActivity.java` | Citire view-uri → creare obiect → bundle → setResult → finish |
| `activity_main.xml` | Buton deschidere + TextViewuri afișare |
| `MainActivity.java` | Launcher ca field + onClick buton + proceseazaRezultat |

---

## Alternativa transmitere fara Serializable (atribut cu atribut)

```java
// In AdaugaDisciplinaActivity — in loc de bundle cu obiect:
Intent intent = new Intent();
intent.putExtra("nume", disciplina.getNumeDisciplina());
intent.putExtra("nota", disciplina.getValoareNota());
intent.putExtra("promovata", disciplina.isEstePromovata());
intent.putExtra("punctaj", disciplina.getPunctajExamen());
intent.putExtra("semestru", disciplina.getSemestru().name()); // enum → String
setResult(RESULT_OK, intent);
finish();

// In MainActivity — in proceseazaRezultat:
String nume     = result.getData().getStringExtra("nume");
int nota        = result.getData().getIntExtra("nota", 0);
boolean prom    = result.getData().getBooleanExtra("promovata", false);
double punctaj  = result.getData().getDoubleExtra("punctaj", 0.0);
String sem      = result.getData().getStringExtra("semestru");
Disciplina.Semestru semestru = Disciplina.Semestru.valueOf(sem);
```
