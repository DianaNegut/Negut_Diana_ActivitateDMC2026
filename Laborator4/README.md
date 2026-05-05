# Android Studio Lab 4 – Clase, View-uri & Bundle

> **Legendă:** liniile marcate cu `// >>> NOU` (sau `<!-- >>> NOU -->`) sunt adăugate față de pasul anterior.
> 

---

## PASUL 1

**Enunț:** Continuați proiectul din laboratorul precedent sau creați unul nou în Android Studio cu API 23 sau API 24. Realizați o activitate nouă pe care să o setați ca activitate implicită.

---

### `MainActivity.java`

```java
// >>> NOU
package com.example.laborator4;

// >>> NOU
import android.os.Bundle;
// >>> NOU
import androidx.appcompat.app.AppCompatActivity;

// >>> NOU
public class MainActivity extends AppCompatActivity {

    // >>> NOU
    @Override
    // >>> NOU
    protected void onCreate(Bundle savedInstanceState) {
        // >>> NOU
        super.onCreate(savedInstanceState);
        // >>> NOU
        setContentView(R.layout.activity_main);
    }
}
```

### `activity_main.xml`

```xml
<!-- >>> NOU -->
<?xml version="1.0" encoding="utf-8"?>
<!-- >>> NOU -->
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- >>> NOU -->
    <TextView
        android:id="@+id/tvTitlu"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Gestiune Discipline"
        android:textSize="24sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        android:layout_marginTop="32dp" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### `AndroidManifest.xml`

```xml
<!-- >>> NOU -->
<?xml version="1.0" encoding="utf-8"?>
<!-- >>> NOU -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- >>> NOU -->
    <application
        android:allowBackup="true"
        android:label="@string/app_name"
        android:theme="@style/Theme.Laborator4">

        <!-- >>> NOU: MainActivity setata ca activitate implicita -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <!-- >>> NOU -->
            <intent-filter>
                <!-- >>> NOU -->
                <action android:name="android.intent.action.MAIN" />
                <!-- >>> NOU -->
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>
</manifest>
```

---

---

## PASUL 2

**Enunț:** Creați o clasă pentru un obiect din realitate care conține inițialele numelui. Această clasă trebuie să conțină minim cinci atribute de tipuri diferite, dintre care minim unul să fie String, unul de tipul boolean, unul de tip întreg și un atribut să aibă valori posibile într-o mulțime finită (enum).

> Clasa aleasă: **Disciplina** – conține atributele necesare pentru o disciplină universitară.
> 

---

### `Disciplina.java` *(fisier nou)*

```java
// >>> NOU
package com.example.laborator4;

// >>> NOU
import java.io.Serializable;

// >>> NOU: implementam Serializable ca sa putem trimite obiectul prin Bundle intre activitati
// >>> NOU
public class Disciplina implements Serializable {

    // >>> NOU: enum pentru semestru, declarat ca inner class
    public enum Semestru implements Serializable {
        // >>> NOU
        SEMESTRUL_1,
        // >>> NOU
        SEMESTRUL_2,
        // >>> NOU
        RESTANTA
    }

    // >>> NOU: atribut String – numele disciplinei
    private String numeDisciplina;
    // >>> NOU: atribut boolean – daca disciplina este promovata
    private boolean estePromovata;
    // >>> NOU: atribut int – valoarea notei (4-10)
    private int valoareNota;
    // >>> NOU: atribut double – punctajul de la examen
    private double punctajExamen;
    // >>> NOU: atribut enum – semestrul disciplinei
    private Semestru semestru;

    // >>> NOU: constructor implicit
    public Disciplina() {
        // >>> NOU
        this.numeDisciplina = "Necunoscuta";
        // >>> NOU
        this.estePromovata = false;
        // >>> NOU
        this.valoareNota = 1;
        // >>> NOU
        this.punctajExamen = 0.0;
        // >>> NOU
        this.semestru = Semestru.SEMESTRUL_1;
    }

    // >>> NOU: constructor cu toti parametrii
    public Disciplina(String numeDisciplina, boolean estePromovata,
                      int valoareNota, double punctajExamen, Semestru semestru) {
        // >>> NOU
        this.numeDisciplina = numeDisciplina;
        // >>> NOU
        this.estePromovata = estePromovata;
        // >>> NOU
        this.valoareNota = valoareNota;
        // >>> NOU
        this.punctajExamen = punctajExamen;
        // >>> NOU
        this.semestru = semestru;
    }

    // >>> NOU: getteri si setteri
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

    // >>> NOU: toString pentru afisare rapida
    @Override
    public String toString() {
        // >>> NOU
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

### `MainActivity.java` *(neschimbat)*

```java
package com.example.laborator4;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
```

### `activity_main.xml` *(neschimbat)*

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/tvTitlu"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Gestiune Discipline"
        android:textSize="24sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        android:layout_marginTop="32dp" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### `AndroidManifest.xml` *(neschimbat)*

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:label="@string/app_name"
        android:theme="@style/Theme.Laborator4">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>
</manifest>
```

---

---

## PASUL 3

**Enunț:** Adăugați o nouă activitate pentru preluarea de date. Activitatea trebuie să conțină view-uri de tipul: `TextView`, `EditText`, `Button`, `CheckBox`, `RadioButton`, `Spinner`. Această activitate este deschisă din prima activitate printr-un buton de adăugare printr-un intent explicit.

---

### `Disciplina.java` *(neschimbat)*

```java
package com.example.laborator4;

import java.io.Serializable;

public class Disciplina implements Serializable {

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

    public Disciplina() {
        this.numeDisciplina = "Necunoscuta";
        this.estePromovata = false;
        this.valoareNota = 1;
        this.punctajExamen = 0.0;
        this.semestru = Semestru.SEMESTRUL_1;
    }

    public Disciplina(String numeDisciplina, boolean estePromovata,
                      int valoareNota, double punctajExamen, Semestru semestru) {
        this.numeDisciplina = numeDisciplina;
        this.estePromovata = estePromovata;
        this.valoareNota = valoareNota;
        this.punctajExamen = punctajExamen;
        this.semestru = semestru;
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

### `AdaugaDisciplinaActivity.java` *(fisier nou)*

```java
// >>> NOU
package com.example.laborator4;

// >>> NOU
import androidx.appcompat.app.AppCompatActivity;
// >>> NOU
import android.content.Intent;
// >>> NOU
import android.os.Bundle;
// >>> NOU
import android.widget.*;

// >>> NOU
public class AdaugaDisciplinaActivity extends AppCompatActivity {

    // >>> NOU: referinte catre toate view-urile din layout
    private EditText etNumeDisciplina, etPunctajExamen;
    // >>> NOU
    private Spinner spinnerSemestru;
    // >>> NOU
    private RadioGroup radioGroupNota;
    // >>> NOU
    private CheckBox checkBoxPromovata;
    // >>> NOU
    private Button btnSalveaza;

    // >>> NOU
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // >>> NOU
        super.onCreate(savedInstanceState);
        // >>> NOU
        setContentView(R.layout.activity_adauga_disciplina);

        // >>> NOU: legam variabilele de view-urile din XML prin id
        etNumeDisciplina  = findViewById(R.id.etNumeDisciplina);
        // >>> NOU
        etPunctajExamen   = findViewById(R.id.etPunctajExamen);
        // >>> NOU
        spinnerSemestru   = findViewById(R.id.spinnerSemestru);
        // >>> NOU
        radioGroupNota    = findViewById(R.id.radioGroupNota);
        // >>> NOU
        checkBoxPromovata = findViewById(R.id.checkBoxPromovata);
        // >>> NOU
        btnSalveaza       = findViewById(R.id.btnSalveaza);

        // >>> NOU: populam Spinner-ul cu valorile enum-ului Semestru
        Disciplina.Semestru[] semestreValues = Disciplina.Semestru.values();
        // >>> NOU
        ArrayAdapter<Disciplina.Semestru> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                semestreValues
        );
        // >>> NOU
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // >>> NOU
        spinnerSemestru.setAdapter(adapter);

        // >>> NOU: click pe buton – doar schelet, datele vor fi trimise in pasul urmator
        btnSalveaza.setOnClickListener(v -> {
            // >>> NOU: va fi completat in pasul 4
        });
    }
}
```

### `activity_adauga_disciplina.xml` *(fisier nou)*

```xml
<!-- >>> NOU -->
<?xml version="1.0" encoding="utf-8"?>
<!-- >>> NOU -->
<ScrollView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- >>> NOU -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <!-- >>> NOU: TextView + EditText pentru Nume disciplina -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Nume disciplina:" />

        <!-- >>> NOU -->
        <EditText
            android:id="@+id/etNumeDisciplina"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Ex: Programare Android"
            android:inputType="text" />

        <!-- >>> NOU: TextView + EditText pentru Punctaj examen -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Punctaj examen:"
            android:layout_marginTop="8dp" />

        <!-- >>> NOU -->
        <EditText
            android:id="@+id/etPunctajExamen"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Ex: 8.50"
            android:inputType="numberDecimal" />

        <!-- >>> NOU: Spinner pentru semestru (enum) -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Semestru:"
            android:layout_marginTop="8dp" />

        <!-- >>> NOU -->
        <Spinner
            android:id="@+id/spinnerSemestru"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />

        <!-- >>> NOU: RadioGroup + RadioButton pentru nota (4-10) -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Valoare nota:"
            android:layout_marginTop="8dp" />

        <!-- >>> NOU -->
        <RadioGroup
            android:id="@+id/radioGroupNota"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <!-- >>> NOU -->
            <RadioButton android:id="@+id/rb4"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="4" />
            <RadioButton android:id="@+id/rb5"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="5" />
            <RadioButton android:id="@+id/rb6"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="6" />
            <RadioButton android:id="@+id/rb7"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="7" />
            <RadioButton android:id="@+id/rb8"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="8" />
            <RadioButton android:id="@+id/rb9"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="9" />
            <RadioButton android:id="@+id/rb10" android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="10" />

        </RadioGroup>

        <!-- >>> NOU: CheckBox pentru promovata -->
        <CheckBox
            android:id="@+id/checkBoxPromovata"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Disciplina promovata"
            android:layout_marginTop="8dp" />

        <!-- >>> NOU: Button pentru salvare -->
        <Button
            android:id="@+id/btnSalveaza"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Salveaza disciplina"
            android:layout_marginTop="16dp" />

    </LinearLayout>
</ScrollView>
```

### `MainActivity.java`

```java
package com.example.laborator4;

// >>> NOU
import android.content.Intent;
import android.os.Bundle;
// >>> NOU
import android.widget.Button;
// >>> NOU
import androidx.activity.result.ActivityResultLauncher;
// >>> NOU
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // >>> NOU: launcher pentru a primi disciplina creata in AdaugaDisciplinaActivity
    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    // >>> NOU: callback – va fi completat in pasul 4
                    result -> { }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // >>> NOU: buton de adaugare care deschide AdaugaDisciplinaActivity
        Button btnAdauga = findViewById(R.id.btnAdaugaDisciplina);
        // >>> NOU
        btnAdauga.setOnClickListener(v -> {
            // >>> NOU
            Intent intent = new Intent(MainActivity.this, AdaugaDisciplinaActivity.class);
            // >>> NOU
            launcher.launch(intent);
        });
    }
}
```

### `activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/tvTitlu"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Gestiune Discipline"
        android:textSize="24sp"
        android:layout_marginTop="32dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <!-- >>> NOU: buton pentru a deschide AdaugaDisciplinaActivity -->
    <Button
        android:id="@+id/btnAdaugaDisciplina"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="+ ADAUGA DISCIPLINA"
        android:layout_marginTop="16dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvTitlu" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### `AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:label="@string/app_name"
        android:theme="@style/Theme.Laborator4">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- >>> NOU: AdaugaDisciplinaActivity inregistrata in manifest -->
        <activity
            android:name=".AdaugaDisciplinaActivity"
            android:exported="false"/>

    </application>
</manifest>
```

---

---

## PASUL 4

**Enunț:** Datele introduse de utilizator sunt folosite pentru crearea unei instanțe a clasei. Această instanță este returnată către prima activitate prin Bundle (Serializable). În activitatea principală este afișat obiectul prin intermediul unor view-uri de tip `TextView`.

---

### `Disciplina.java` *(neschimbat)*

```java
package com.example.laborator4;

import java.io.Serializable;

public class Disciplina implements Serializable {

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

    public Disciplina() {
        this.numeDisciplina = "Necunoscuta";
        this.estePromovata = false;
        this.valoareNota = 1;
        this.punctajExamen = 0.0;
        this.semestru = Semestru.SEMESTRUL_1;
    }

    public Disciplina(String numeDisciplina, boolean estePromovata,
                      int valoareNota, double punctajExamen, Semestru semestru) {
        this.numeDisciplina = numeDisciplina;
        this.estePromovata = estePromovata;
        this.valoareNota = valoareNota;
        this.punctajExamen = punctajExamen;
        this.semestru = semestru;
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

### `AdaugaDisciplinaActivity.java`

```java
package com.example.laborator4;

import androidx.appcompat.app.AppCompatActivity;
// >>> NOU
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

        etNumeDisciplina  = findViewById(R.id.etNumeDisciplina);
        etPunctajExamen   = findViewById(R.id.etPunctajExamen);
        spinnerSemestru   = findViewById(R.id.spinnerSemestru);
        radioGroupNota    = findViewById(R.id.radioGroupNota);
        checkBoxPromovata = findViewById(R.id.checkBoxPromovata);
        btnSalveaza       = findViewById(R.id.btnSalveaza);

        Disciplina.Semestru[] semestreValues = Disciplina.Semestru.values();
        ArrayAdapter<Disciplina.Semestru> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                semestreValues
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemestru.setAdapter(adapter);

        // >>> NOU: apelam metoda de salvare la click pe buton
        btnSalveaza.setOnClickListener(v -> salveazaDisciplina());
    }

    // >>> NOU: metoda care citeste datele, creeaza obiectul si il trimite inapoi
    private void salveazaDisciplina() {
        // >>> NOU: citim numele din EditText
        String nume = etNumeDisciplina.getText().toString();

        // >>> NOU: citim punctajul din EditText (0.0 daca e gol)
        String punctajStr = etPunctajExamen.getText().toString();
        // >>> NOU
        double punctaj = punctajStr.isEmpty() ? 0.0 : Double.parseDouble(punctajStr);

        // >>> NOU: citim semestrul ales din Spinner
        String semestruSelectat = spinnerSemestru.getSelectedItem().toString();
        // >>> NOU
        Disciplina.Semestru semestru = Disciplina.Semestru.valueOf(semestruSelectat);

        // >>> NOU: citim nota aleasa din RadioGroup (implicit 5)
        int notaSelectata = 5;
        // >>> NOU
        int radioId = radioGroupNota.getCheckedRadioButtonId();
        // >>> NOU
        if (radioId != -1) {
            // >>> NOU
            RadioButton rb = findViewById(radioId);
            // >>> NOU
            notaSelectata = Integer.parseInt(rb.getText().toString());
        }

        // >>> NOU: citim starea CheckBox-ului
        boolean promovata = checkBoxPromovata.isChecked();

        // >>> NOU: cream obiectul Disciplina cu datele citite
        Disciplina disciplina = new Disciplina(nume, promovata, notaSelectata, punctaj, semestru);

        // >>> NOU: punem obiectul intr-un Bundle prin Serializable si il trimitem inapoi
        Intent intent = new Intent();
        // >>> NOU
        Bundle bundle = new Bundle();
        // >>> NOU
        bundle.putSerializable("disciplina", disciplina);
        // >>> NOU
        intent.putExtras(bundle);
        // >>> NOU
        setResult(RESULT_OK, intent);
        // >>> NOU
        finish();
    }
}
```

### `activity_adauga_disciplina.xml` *(neschimbat)*

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Nume disciplina:" />

        <EditText
            android:id="@+id/etNumeDisciplina"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Ex: Programare Android"
            android:inputType="text" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Punctaj examen:"
            android:layout_marginTop="8dp" />

        <EditText
            android:id="@+id/etPunctajExamen"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Ex: 8.50"
            android:inputType="numberDecimal" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Semestru:"
            android:layout_marginTop="8dp" />

        <Spinner
            android:id="@+id/spinnerSemestru"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Valoare nota:"
            android:layout_marginTop="8dp" />

        <RadioGroup
            android:id="@+id/radioGroupNota"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <RadioButton android:id="@+id/rb4"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="4" />
            <RadioButton android:id="@+id/rb5"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="5" />
            <RadioButton android:id="@+id/rb6"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="6" />
            <RadioButton android:id="@+id/rb7"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="7" />
            <RadioButton android:id="@+id/rb8"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="8" />
            <RadioButton android:id="@+id/rb9"  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="9" />
            <RadioButton android:id="@+id/rb10" android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="10" />

        </RadioGroup>

        <CheckBox
            android:id="@+id/checkBoxPromovata"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Disciplina promovata"
            android:layout_marginTop="8dp" />

        <Button
            android:id="@+id/btnSalveaza"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Salveaza disciplina"
            android:layout_marginTop="16dp" />

    </LinearLayout>
</ScrollView>
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
import android.widget.Button;
// >>> NOU
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // >>> NOU: referinte catre TextView-urile de afisare
    private TextView tvNumeDisciplina, tvValoareNota, tvPunctajExamen,
            tvSemestru, tvEstePromovata;

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    // >>> NOU: acum callback-ul proceseaza disciplina primita
                    this::proceseazaRezultat
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // >>> NOU: legam TextView-urile de view-urile din XML
        tvNumeDisciplina  = findViewById(R.id.tvNumeDisciplina);
        // >>> NOU
        tvValoareNota     = findViewById(R.id.tvValoareNota);
        // >>> NOU
        tvPunctajExamen   = findViewById(R.id.tvPunctajExamen);
        // >>> NOU
        tvSemestru        = findViewById(R.id.tvSemestru);
        // >>> NOU
        tvEstePromovata   = findViewById(R.id.tvEstePromovata);

        Button btnAdauga = findViewById(R.id.btnAdaugaDisciplina);
        btnAdauga.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdaugaDisciplinaActivity.class);
            launcher.launch(intent);
        });
    }

    // >>> NOU: callback apelat de launcher cand AdaugaDisciplinaActivity se inchide
    private void proceseazaRezultat(ActivityResult result) {
        // >>> NOU
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            // >>> NOU: extragem obiectul Disciplina din Bundle (Serializable)
            Bundle bundle = result.getData().getExtras();
            // >>> NOU
            Disciplina disciplina = (Disciplina) bundle.getSerializable("disciplina");
            // >>> NOU
            if (disciplina != null) {
                // >>> NOU: afisam fiecare atribut intr-un TextView
                tvNumeDisciplina.setText(disciplina.getNumeDisciplina());
                // >>> NOU
                tvValoareNota.setText(String.valueOf(disciplina.getValoareNota()));
                // >>> NOU
                tvPunctajExamen.setText(String.valueOf(disciplina.getPunctajExamen()));
                // >>> NOU
                tvSemestru.setText(disciplina.getSemestru().toString());
                // >>> NOU
                tvEstePromovata.setText(disciplina.isEstePromovata() ? "Da" : "Nu");
            }
        }
    }
}
```

### `activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Gestiune Discipline"
            android:textSize="24sp"
            android:layout_marginBottom="8dp" />

        <Button
            android:id="@+id/btnAdaugaDisciplina"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="+ ADAUGA DISCIPLINA"
            android:layout_marginBottom="16dp" />

        <!-- >>> NOU: TextView-uri pentru afisarea disciplinei primite -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Disciplina Adaugata"
            android:textSize="16sp"
            android:textStyle="bold"
            android:layout_marginBottom="8dp" />

        <!-- >>> NOU -->
        <TextView
            android:id="@+id/tvNumeDisciplina"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="-"
            android:textSize="14sp" />

        <!-- >>> NOU -->
        <TextView
            android:id="@+id/tvValoareNota"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="-"
            android:textSize="14sp" />

        <!-- >>> NOU -->
        <TextView
            android:id="@+id/tvPunctajExamen"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="-"
            android:textSize="14sp" />

        <!-- >>> NOU -->
        <TextView
            android:id="@+id/tvSemestru"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="-"
            android:textSize="14sp" />

        <!-- >>> NOU -->
        <TextView
            android:id="@+id/tvEstePromovata"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="-"
            android:textSize="14sp" />

    </LinearLayout>
</ScrollView>
```

### `AndroidManifest.xml` *(neschimbat)*

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:label="@string/app_name"
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

---

## PASUL 5

**Enunț:** Utilizați Inteligența Artificială pentru a îmbunătăți modul de aranjare al view-urilor în cadrul activității de preluare de date. Păstrați id-urile view-urilor astfel încât funcționalitatea implementată să se păstreze.

> Layout-urile au fost reproiectate cu un design card-based, cu secțiuni grupate vizual
> și o paletă de culori consistentă (#1A237E). **Toate id-urile rămân identice**, deci
> `AdaugaDisciplinaActivity.java` și `MainActivity.java` nu necesită nicio modificare.
> 

---

### `AdaugaDisciplinaActivity.java` *(neschimbat)*

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

        etNumeDisciplina  = findViewById(R.id.etNumeDisciplina);
        etPunctajExamen   = findViewById(R.id.etPunctajExamen);
        spinnerSemestru   = findViewById(R.id.spinnerSemestru);
        radioGroupNota    = findViewById(R.id.radioGroupNota);
        checkBoxPromovata = findViewById(R.id.checkBoxPromovata);
        btnSalveaza       = findViewById(R.id.btnSalveaza);

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

        Disciplina disciplina = new Disciplina(nume, promovata, notaSelectata, punctaj, semestru);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("disciplina", disciplina);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
```

### `activity_adauga_disciplina.xml` *(imbunatatit de AI – id-uri pastrate)*

```xml
<!-- >>> NOU: layout imbunatatit – card-based, grupat pe sectiuni, design mai curat -->
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

        <!-- >>> NOU: header card albastru -->
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

        <!-- >>> NOU: Card 1 – Informatii generale (id-urile etNumeDisciplina, etPunctajExamen pastrate) -->
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

                <!-- >>> NOU: id-ul etNumeDisciplina pastrat identic -->
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

                <!-- >>> NOU: id-ul etPunctajExamen pastrat identic -->
                <EditText
                    android:id="@+id/etPunctajExamen"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="Ex: 8.50"
                    android:inputType="numberDecimal"
                    android:padding="12dp"
                    android:background="@drawable/edittext_bg"
                    android:textSize="15sp"/>

            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <!-- >>> NOU: Card 2 – Semestru (id-ul spinnerSemestru pastrat) -->
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

                <!-- >>> NOU: id-ul spinnerSemestru pastrat identic -->
                <Spinner
                    android:id="@+id/spinnerSemestru"
                    android:layout_width="match_parent"
                    android:layout_height="48dp"
                    android:background="@drawable/edittext_bg"
                    android:padding="8dp"/>

            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <!-- >>> NOU: Card 3 – Valoare nota (id-ul radioGroupNota pastrat) -->
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

                <!-- >>> NOU: id-ul radioGroupNota pastrat identic -->
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
                    <RadioButton android:id="@+id/rb10" android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:text="10" android:textColor="#37474F"/>

                </RadioGroup>

            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <!-- >>> NOU: Card 4 – Status (id-ul checkBoxPromovata pastrat) -->
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

                <!-- >>> NOU: id-ul checkBoxPromovata pastrat identic -->
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

        <!-- >>> NOU: id-ul btnSalveaza pastrat identic -->
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

### `MainActivity.java` *(neschimbat)*

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

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::proceseazaRezultat
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvNumeDisciplina  = findViewById(R.id.tvNumeDisciplina);
        tvValoareNota     = findViewById(R.id.tvValoareNota);
        tvPunctajExamen   = findViewById(R.id.tvPunctajExamen);
        tvSemestru        = findViewById(R.id.tvSemestru);
        tvEstePromovata   = findViewById(R.id.tvEstePromovata);

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

### `activity_main.xml` *(imbunatatit de AI – id-uri pastrate)*

```xml
<!-- >>> NOU: layout imbunatatit – card-based cu header si display card -->
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

        <!-- >>> NOU: header card albastru -->
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

        <!-- >>> NOU: id-ul btnAdaugaDisciplina pastrat identic -->
        <Button
            android:id="@+id/btnAdaugaDisciplina"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:text="+ ADAUGA DISCIPLINA"
            android:textColor="#FFFFFF"
            android:textStyle="bold"
            android:textSize="15sp"
            android:backgroundTint="#1A237E"
            android:layout_marginBottom="24dp"/>

        <!-- >>> NOU: display card cu id-urile TextView-urilor pastrate -->
        <androidx.cardview.widget.CardView
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
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
                    android:text="Disciplina Adaugata"
                    android:textColor="#1A237E"
                    android:textSize="16sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="16dp"/>

                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:layout_marginBottom="12dp" android:background="#F5F7FA" android:padding="12dp">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Nume: " android:textColor="#546E7A" android:textSize="14sp" android:textStyle="bold"/>
                    <!-- >>> NOU: id-ul tvNumeDisciplina pastrat identic -->
                    <TextView android:id="@+id/tvNumeDisciplina" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="-" android:textColor="#37474F" android:textSize="14sp"/>
                </LinearLayout>

                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:layout_marginBottom="12dp" android:background="#F5F7FA" android:padding="12dp">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Nota: " android:textColor="#546E7A" android:textSize="14sp" android:textStyle="bold"/>
                    <!-- >>> NOU: id-ul tvValoareNota pastrat identic -->
                    <TextView android:id="@+id/tvValoareNota" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="-" android:textColor="#37474F" android:textSize="14sp"/>
                </LinearLayout>

                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:layout_marginBottom="12dp" android:background="#F5F7FA" android:padding="12dp">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Punctaj: " android:textColor="#546E7A" android:textSize="14sp" android:textStyle="bold"/>
                    <!-- >>> NOU: id-ul tvPunctajExamen pastrat identic -->
                    <TextView android:id="@+id/tvPunctajExamen" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="-" android:textColor="#37474F" android:textSize="14sp"/>
                </LinearLayout>

                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:layout_marginBottom="12dp" android:background="#F5F7FA" android:padding="12dp">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Semestru: " android:textColor="#546E7A" android:textSize="14sp" android:textStyle="bold"/>
                    <!-- >>> NOU: id-ul tvSemestru pastrat identic -->
                    <TextView android:id="@+id/tvSemestru" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="-" android:textColor="#37474F" android:textSize="14sp"/>
                </LinearLayout>

                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:orientation="horizontal" android:background="#F5F7FA" android:padding="12dp">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Promovata: " android:textColor="#546E7A" android:textSize="14sp" android:textStyle="bold"/>
                    <!-- >>> NOU: id-ul tvEstePromovata pastrat identic -->
                    <TextView android:id="@+id/tvEstePromovata" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="-" android:textColor="#37474F" android:textSize="14sp"/>
                </LinearLayout>

            </LinearLayout>
        </androidx.cardview.widget.CardView>

    </LinearLayout>
</ScrollView>
```

### `AndroidManifest.xml` *(neschimbat)*

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:label="@string/app_name"
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

## Rezumat fisiere

| Fisier | Apare la pasul | Modificat la pasul |
| --- | --- | --- |
| `Disciplina.java` | 2 | – |
| `MainActivity.java` | 1 | 3 (launcher+buton), 4 (TextViews+callback) |
| `activity_main.xml` | 1 | 3 (btnAdaugaDisciplina), 4 (TextViews), 5 (redesign AI) |
| `AdaugaDisciplinaActivity.java` | 3 | 4 (salveazaDisciplina completata) |
| `activity_adauga_disciplina.xml` | 3 | 5 (redesign AI) |
| `AndroidManifest.xml` | 1 | 3 (AdaugaDisciplinaActivity) |

---

## Sfaturi examen

- `Serializable` este cea mai simpla metoda de a trimite un obiect prin `Bundle` – clasa si enum-ul inner trebuie sa implementeze `Serializable`
- `bundle.putSerializable("cheie", obiect)` si `(Tip) bundle.getSerializable("cheie")` – cheia trebuie sa fie **identica**
- Enum-ul declarat ca **inner class** in clasa de date nu necesita fisier separat; trebuie sa implementeze si el `Serializable`
- `ActivityResultLauncher` se declara ca **field** in clasa, nu in `onCreate`
- `setResult(RESULT_OK, intent)` + `finish()` in activitatea de adaugare declanseaza callback-ul
- `RadioGroup.getCheckedRadioButtonId()` returneaza `-1` daca nu e selectat niciun buton – verifica mereu inainte de `findViewById`
- `CardView` necesita dependenta `implementation 'androidx.cardview:cardview:1.0.0'` in `build.gradle`
- `android:exported="true"` e obligatoriu pe `MainActivity` (activitatea implicita) incepand cu API 31
