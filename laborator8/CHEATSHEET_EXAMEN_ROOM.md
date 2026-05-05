# CHEATSHEET EXAMEN - ROOM Database Android

> **Clasa folosita ca exemplu**: `Produs` (nume, categorie, pret, cantitate)
> Inlocuieste `Produs`/`produse` cu clasa ta oriunde apare.

---

## PASUL 0 — Dependinte in `app/build.gradle.kts`

```kotlin
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.runtime)       // <-- ROOM
    annotationProcessor(libs.room.compiler) // <-- ROOM annotation processor
}
```

Daca `libs.room.*` nu exista, adauga in `gradle/libs.versions.toml`:
```toml
[versions]
room = "2.6.1"

[libraries]
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
```

---

## FISIER 1 — Entitatea (`Produs.java`)

> **Ce face**: Reprezinta tabelul din baza de date. Fiecare camp = coloana.

```java
package com.example.laborator8;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "produse")          // numele tabelului in SQLite
public class Produs {

    @PrimaryKey(autoGenerate = true)    // id auto-incrementat
    public int id;

    @ColumnInfo(name = "nume")          // coloana string
    public String nume;

    @ColumnInfo(name = "categorie")     // al doilea string
    public String categorie;

    @ColumnInfo(name = "pret")          // valoare numerica double
    public double pret;

    @ColumnInfo(name = "cantitate")     // valoare intreaga
    public int cantitate;

    // Constructor FARA id (id se genereaza automat)
    public Produs(String nume, String categorie, double pret, int cantitate) {
        this.nume = nume;
        this.categorie = categorie;
        this.pret = pret;
        this.cantitate = cantitate;
    }

    // toString() pentru afisare in ListView
    @Override
    public String toString() {
        return "[" + id + "] " + nume
                + " | Cat: " + categorie
                + " | Pret: " + String.format("%.2f", pret) + " lei"
                + " | Cant: " + cantitate;
    }
}
```

**Reguli de adaptat:**
- Minim 3 atribute (pe langa id)
- Un atribut `String` (folosit la cerinta 3 si 6)
- Un atribut `int` sau `double` (folosit la cerinta 4 si 5)
- `@Entity(tableName = "...")` — numele tabelului in SQL

---

## FISIER 2 — DAO (`ProdusDao.java`)

> **Ce face**: Interfata cu toate metodele SQL. Fiecare cerinta = o metoda.

```java
package com.example.laborator8;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProdusDao {

    // CERINTA 1 — Inserare
    @Insert
    void inserare(Produs produs);

    // CERINTA 2 — Selectie toate inregistrarile
    @Query("SELECT * FROM produse")
    List<Produs> selectareTot();

    // CERINTA 3 — Selectie dupa valoarea STRING egala cu parametru
    // Inlocuieste `nume` cu campul tau String
    @Query("SELECT * FROM produse WHERE nume = :numeParam")
    List<Produs> selectareDupaNume(String numeParam);

    // CERINTA 4 — Selectie dupa valoarea INT intr-un interval [min, max]
    // Inlocuieste `cantitate` cu campul tau int
    @Query("SELECT * FROM produse WHERE cantitate BETWEEN :min AND :max")
    List<Produs> selectareDupaCantitate(int min, int max);

    // CERINTA 5a — Stergere inregistrari cu valoare numerica MAI MARE decat parametru
    // Inlocuieste `pret` cu campul tau numeric
    @Query("DELETE FROM produse WHERE pret > :pretParam")
    void stergerePretMaiMare(double pretParam);

    // CERINTA 5b — Stergere inregistrari cu valoare numerica MAI MICA decat parametru
    @Query("DELETE FROM produse WHERE pret < :pretParam")
    void stergerePretMaiMic(double pretParam);

    // CERINTA 6 — Creste cu 1 valoarea int pentru inregistrarile al caror String incepe cu o litera
    // UPPER(nume) LIKE 'A%'  — pattern se trimite ca "A%" din Java
    // Inlocuieste `cantitate` si `nume` cu campurile tale
    @Query("UPDATE produse SET cantitate = cantitate + 1 WHERE UPPER(nume) LIKE :pattern")
    void crescCantitate(String pattern);
}
```

**Atentie la sintaxa:**
- Parametrii in SQL se scriu ca `:numePrametru` (cu `:`)
- `BETWEEN :min AND :max` — inclusiv ambele capete
- `UPPER(camp) LIKE :pattern` — pattern trimis din Java ca `"A%"` (litera mare + %)

---

## FISIER 3 — Database (`AppDatabase.java`)

> **Ce face**: Singleton care gestioneaza conexiunea la fisierul SQLite.

```java
package com.example.laborator8;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Listeaza TOATE entitatile, seteaza version = 1 la inceput
@Database(entities = {Produs.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Getter pentru DAO
    public abstract ProdusDao produsDao();

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "produse_db"        // numele fisierului .db
                    )
                    .allowMainThreadQueries()   // permite query-uri pe main thread (necesar la exam)
                    .build();
        }
        return instance;
    }
}
```

**Daca adaugi o entitate noua sau modifici schema** → creste `version = 2` si adauga:
```java
.fallbackToDestructiveMigration()  // sterge si recreeaza baza (ok la exam)
```

---

## FISIER 4 — MainActivity (`MainActivity.java`)

> **Ce face**: UI-ul + apelarea metodelor DAO.

```java
package com.example.laborator8;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private ProdusDao produsDao;

    // ---------- campuri UI ----------
    private TextInputEditText etNume, etCategorie, etPret, etCantitate;
    private TextInputEditText etCautareNume;
    private TextInputEditText etCantMin, etCantMax;
    private TextInputEditText etPretStergere;
    private TextInputEditText etLitera;
    private ListView listViewProduse;
    private TextView tvListLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- initializare DB ---
        db = AppDatabase.getInstance(this);
        produsDao = db.produsDao();

        // --- legare view-uri (ID-urile corespund layout-ului) ---
        etNume          = findViewById(R.id.etNume);
        etCategorie     = findViewById(R.id.etCategorie);
        etPret          = findViewById(R.id.etPret);
        etCantitate     = findViewById(R.id.etCantitate);
        etCautareNume   = findViewById(R.id.etCautareNume);
        etCantMin       = findViewById(R.id.etCantMin);
        etCantMax       = findViewById(R.id.etCantMax);
        etPretStergere  = findViewById(R.id.etPretStergere);
        etLitera        = findViewById(R.id.etLitera);
        listViewProduse = findViewById(R.id.listViewProduse);
        tvListLabel     = findViewById(R.id.tvListLabel);

        // ========= CERINTA 1 — Inserare =========
        MaterialButton btnAdauga = findViewById(R.id.btnAdauga);
        btnAdauga.setOnClickListener(v -> {
            String nume      = getText(etNume);
            String categorie = getText(etCategorie);
            String pretStr   = getText(etPret);
            String cantStr   = getText(etCantitate);

            if (nume.isEmpty() || categorie.isEmpty() || pretStr.isEmpty() || cantStr.isEmpty()) {
                toast("Completati toate campurile!");
                return;
            }

            double pret      = Double.parseDouble(pretStr);
            int    cantitate = Integer.parseInt(cantStr);

            produsDao.inserare(new Produs(nume, categorie, pret, cantitate));
            toast("Produs adaugat: " + nume);

            // curata campurile dupa adaugare
            etNume.setText(""); etCategorie.setText("");
            etPret.setText(""); etCantitate.setText("");

            afiseaza(produsDao.selectareTot(), "Toate produsele");
        });

        // ========= CERINTA 2 — Afisare toate =========
        MaterialButton btnAfisareTot = findViewById(R.id.btnAfisareTot);
        btnAfisareTot.setOnClickListener(v -> {
            List<Produs> lista = produsDao.selectareTot();
            afiseaza(lista, "Toate produsele (" + lista.size() + ")");
        });

        // ========= CERINTA 3 — Selectie dupa String =========
        MaterialButton btnCautareNume = findViewById(R.id.btnCautareNume);
        btnCautareNume.setOnClickListener(v -> {
            String numeParam = getText(etCautareNume);
            if (numeParam.isEmpty()) { toast("Introduceti un nume!"); return; }
            List<Produs> lista = produsDao.selectareDupaNume(numeParam);
            afiseaza(lista, "Produse cu numele \"" + numeParam + "\" (" + lista.size() + ")");
        });

        // ========= CERINTA 4 — Selectie dupa interval int =========
        MaterialButton btnFiltruCantitate = findViewById(R.id.btnFiltruCantitate);
        btnFiltruCantitate.setOnClickListener(v -> {
            String minStr = getText(etCantMin);
            String maxStr = getText(etCantMax);
            if (minStr.isEmpty() || maxStr.isEmpty()) { toast("Introduceti min si max!"); return; }
            int min = Integer.parseInt(minStr);
            int max = Integer.parseInt(maxStr);
            List<Produs> lista = produsDao.selectareDupaCantitate(min, max);
            afiseaza(lista, "Cantitate intre " + min + " si " + max + " (" + lista.size() + ")");
        });

        // ========= CERINTA 5 — Stergere dupa valoare numerica =========
        MaterialButton btnStergeMaiMare = findViewById(R.id.btnStergeMaiMare);
        btnStergeMaiMare.setOnClickListener(v -> {
            String pretStr = getText(etPretStergere);
            if (pretStr.isEmpty()) { toast("Introduceti pretul!"); return; }
            double pret = Double.parseDouble(pretStr);
            produsDao.stergerePretMaiMare(pret);
            List<Produs> lista = produsDao.selectareTot();
            afiseaza(lista, "Dupa stergere pret > " + pret + " (" + lista.size() + ")");
            toast("Sterse produse cu pret > " + pret);
        });

        MaterialButton btnStergeMaiMic = findViewById(R.id.btnStergeMaiMic);
        btnStergeMaiMic.setOnClickListener(v -> {
            String pretStr = getText(etPretStergere);
            if (pretStr.isEmpty()) { toast("Introduceti pretul!"); return; }
            double pret = Double.parseDouble(pretStr);
            produsDao.stergerePretMaiMic(pret);
            List<Produs> lista = produsDao.selectareTot();
            afiseaza(lista, "Dupa stergere pret < " + pret + " (" + lista.size() + ")");
            toast("Sterse produse cu pret < " + pret);
        });

        // ========= CERINTA 6 — Creste cu 1 valorea int dupa litera =========
        MaterialButton btnCreste = findViewById(R.id.btnCresteCantitateLitera);
        btnCreste.setOnClickListener(v -> {
            String litera = getText(etLitera);
            if (litera.isEmpty()) { toast("Introduceti o litera!"); return; }
            String pattern = litera.toUpperCase() + "%";  // ex: "A%"
            produsDao.crescCantitate(pattern);
            List<Produs> lista = produsDao.selectareTot();
            afiseaza(lista, "Dupa +1 cantitate litera \"" + litera.toUpperCase() + "\"");
            toast("Cantitate +1 pentru produse ce incep cu \"" + litera.toUpperCase() + "\"");
        });

        // afisare initiala
        afiseaza(produsDao.selectareTot(), "Toate produsele");
    }

    // --- helper: citire text dintr-un TextInputEditText ---
    private String getText(TextInputEditText field) {
        CharSequence cs = field.getText();
        return cs != null ? cs.toString().trim() : "";
    }

    // --- helper: afisare lista in ListView ---
    private void afiseaza(List<Produs> lista, String eticheta) {
        ArrayAdapter<Produs> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, lista);
        listViewProduse.setAdapter(adapter);
        tvListLabel.setText(eticheta + (lista.isEmpty() ? " — lista goala" : ""));
    }

    // --- helper: toast scurt ---
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
```

---

## FISIER 5 — Layout (`res/layout/activity_main.xml`)

> Structura: `ScrollView` > `LinearLayout` > carduri pe sectiuni

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#F5F5F5">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <!-- TITLU -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Gestiune Produse"
            android:textSize="24sp"
            android:textStyle="bold"
            android:gravity="center"
            android:layout_marginBottom="16dp" />

        <!-- CARD 1 — CERINTA 1: Adaugare -->
        <androidx.cardview.widget.CardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="12dp"
            app:cardCornerRadius="12dp"
            app:cardElevation="4dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Adauga Produs"
                    android:textSize="16sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="8dp" />

                <com.google.android.material.textfield.TextInputLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="Nume">
                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/etNume"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="Categorie"
                    android:layout_marginTop="4dp">
                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/etCategorie"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="Pret"
                    android:layout_marginTop="4dp">
                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/etPret"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:inputType="numberDecimal" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="Cantitate"
                    android:layout_marginTop="4dp">
                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/etCantitate"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:inputType="number" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.button.MaterialButton
                    android:id="@+id/btnAdauga"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="Adauga in baza de date"
                    android:layout_marginTop="8dp" />
            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <!-- CARD 2 — CERINTA 2 si 3: Afisare + Cautare -->
        <androidx.cardview.widget.CardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="12dp"
            app:cardCornerRadius="12dp"
            app:cardElevation="4dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Afisare si Cautare"
                    android:textSize="16sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="8dp" />

                <!-- Cerinta 2 -->
                <com.google.android.material.button.MaterialButton
                    android:id="@+id/btnAfisareTot"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="Afiseaza toate produsele" />

                <!-- Cerinta 3 -->
                <com.google.android.material.textfield.TextInputLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="Cauta dupa nume exact"
                    android:layout_marginTop="8dp">
                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/etCautareNume"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.button.MaterialButton
                    android:id="@+id/btnCautareNume"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="Cauta dupa nume"
                    android:layout_marginTop="4dp" />
            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <!-- CARD 3 — CERINTA 4: Filtru interval int -->
        <androidx.cardview.widget.CardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="12dp"
            app:cardCornerRadius="12dp"
            app:cardElevation="4dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Filtru Cantitate"
                    android:textSize="16sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="8dp" />

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal">

                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:hint="Min"
                        android:layout_marginEnd="4dp">
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/etCantMin"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="number" />
                    </com.google.android.material.textfield.TextInputLayout>

                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:hint="Max"
                        android:layout_marginStart="4dp">
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/etCantMax"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="number" />
                    </com.google.android.material.textfield.TextInputLayout>
                </LinearLayout>

                <com.google.android.material.button.MaterialButton
                    android:id="@+id/btnFiltruCantitate"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="Filtreaza dupa cantitate"
                    android:layout_marginTop="8dp" />
            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <!-- CARD 4 — CERINTA 5: Stergere dupa pret -->
        <androidx.cardview.widget.CardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="12dp"
            app:cardCornerRadius="12dp"
            app:cardElevation="4dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Stergere dupa Pret"
                    android:textSize="16sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="8dp" />

                <com.google.android.material.textfield.TextInputLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="Pret referinta">
                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/etPretStergere"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:inputType="numberDecimal" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.button.MaterialButton
                    android:id="@+id/btnStergeMaiMare"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="Sterge pret &gt; valoare"
                    android:layout_marginTop="8dp" />

                <com.google.android.material.button.MaterialButton
                    android:id="@+id/btnStergeMaiMic"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="Sterge pret &lt; valoare"
                    android:layout_marginTop="4dp" />
            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <!-- CARD 5 — CERINTA 6: Creste cantitate dupa litera -->
        <androidx.cardview.widget.CardView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="12dp"
            app:cardCornerRadius="12dp"
            app:cardElevation="4dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:padding="16dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Creste Cantitate dupa Litera"
                    android:textSize="16sp"
                    android:textStyle="bold"
                    android:layout_marginBottom="8dp" />

                <com.google.android.material.textfield.TextInputLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="Litera initiala (ex: A)">
                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/etLitera"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:inputType="textCapCharacters"
                        android:maxLength="1" />
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.button.MaterialButton
                    android:id="@+id/btnCresteCantitateLitera"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="Creste cantitate (+1) dupa litera"
                    android:layout_marginTop="8dp" />
            </LinearLayout>
        </androidx.cardview.widget.CardView>

        <!-- LISTA REZULTATE — CERINTA 2 si 4 -->
        <TextView
            android:id="@+id/tvListLabel"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Toate produsele"
            android:textStyle="bold"
            android:textSize="14sp"
            android:layout_marginBottom="4dp" />

        <ListView
            android:id="@+id/listViewProduse"
            android:layout_width="match_parent"
            android:layout_height="250dp"
            android:background="#FFFFFF"
            android:divider="#E0E0E0"
            android:dividerHeight="1dp" />

    </LinearLayout>
</ScrollView>
```

---

## MAPARE CERINTE → COD

| # | Cerinta | Fisier | Ce adaugi |
|---|---------|--------|-----------|
| 1 | Inserare | `ProdusDao.java` | `@Insert void inserare(Produs p)` |
| 1 | UI inserare | `activity_main.xml` | 4 `TextInputEditText` + `btnAdauga` |
| 1 | Logica inserare | `MainActivity.java` | `produsDao.inserare(new Produs(...))` |
| 2 | Selectare tot | `ProdusDao.java` | `@Query("SELECT * FROM produse")` |
| 2 | UI afisare | `activity_main.xml` | `ListView` + `btnAfisareTot` |
| 2 | Logica afisare | `MainActivity.java` | `ArrayAdapter` + `setAdapter` |
| 3 | Selectare dupa String | `ProdusDao.java` | `WHERE nume = :numeParam` |
| 3 | UI cautare | `activity_main.xml` | `etCautareNume` + `btnCautareNume` |
| 4 | Selectare interval int | `ProdusDao.java` | `WHERE cantitate BETWEEN :min AND :max` |
| 4 | UI filtru | `activity_main.xml` | `etCantMin`, `etCantMax` + buton |
| 5 | Stergere dupa numeric | `ProdusDao.java` | `DELETE WHERE pret > :p` si `< :p` |
| 5 | UI stergere | `activity_main.xml` | `etPretStergere` + 2 butoane |
| 6 | Update dupa litera | `ProdusDao.java` | `UPDATE SET cantitate+1 WHERE UPPER(nume) LIKE :pattern` |
| 6 | UI update | `activity_main.xml` | `etLitera` + buton |
| 6 | Pattern Java | `MainActivity.java` | `litera.toUpperCase() + "%"` |

---

## ADAPTARE RAPIDA LA ALTA CLASA

Daca clasa ta se numeste `Carte` cu campurile `titlu` (String), `autor` (String), `an` (int), `rating` (double):

1. **`Carte.java`** — schimba `@Entity(tableName = "carti")`, campurile, constructorul, `toString()`
2. **`CarteDao.java`** — schimba `Carte` peste tot, `carti` in SQL, `titlu`/`an`/`rating` in query-uri
3. **`AppDatabase.java`** — `entities = {Carte.class}`, `public abstract CarteDao carteDao()`
4. **`MainActivity.java`** — schimba tipurile si numele variabilelor
5. **`activity_main.xml`** — schimba textele hint-urilor

---

## ERORI FRECVENTE

| Eroare | Cauza | Fix |
|--------|-------|-----|
| `Cannot access database on main thread` | Lipsa `allowMainThreadQueries()` | Adauga in `AppDatabase.getInstance()` |
| `no such table` | Entity neadaugata in `@Database` | Verifica `entities = {Produs.class}` |
| `Cannot figure out how to save field` | Camp fara `@ColumnInfo` sau tip necunoscut | Adauga `@ColumnInfo` sau `@Ignore` |
| `Room schema mismatch` | Modifici entitatea fara a creste versiunea | Creste `version` si adauga `.fallbackToDestructiveMigration()` |
| `NullPointerException` pe `getText()` | `EditText.getText()` poate fi null | Foloseste helper-ul `getText(field)` din MainActivity |

---

## REZUMAT FISIERE DE CREAT

```
app/src/main/java/com/example/PACHET/
├── Produs.java          ← @Entity — modelul
├── ProdusDao.java       ← @Dao — toate metodele SQL
├── AppDatabase.java     ← @Database — singleton conexiune
└── MainActivity.java    ← UI + logica

app/src/main/res/layout/
└── activity_main.xml    ← ScrollView cu carduri

app/build.gradle.kts     ← adauga room.runtime + room.compiler
```
