# Cheatsheet Lab 6 – Custom Adapter + Edit cu Parcelable

> **Proiect de baza:** laboratorul precedent (Lab 5). Toate modificarile se fac in Java + XML.
> Obiectul din exemplu se numeste `Disciplina` — la examen inlocuieste cu obiectul tau.

---

## Cerinta 1 – Pornesti de la proiectul precedent
Nimic de facut. Deschizi proiectul din Lab 5 in Android Studio.

---

## Cerinta 4 – Parcelable pe clasa model

**Fisier:** `Disciplina.java` (clasa ta de model)

Clasa trebuie sa implementeze `Parcelable`. Pasi:

### 1. Adauga `implements Parcelable` la declaratia clasei
```java
public class Disciplina implements Parcelable {
```

### 2. Constructor care citeste din Parcel (dupa constructorii normali)
```java
protected Disciplina(Parcel in) {
    numeDisciplina = in.readString();
    estePromovata  = in.readByte() != 0;
    valoareNota    = in.readInt();
    punctajExamen  = in.readDouble();
    semestru       = Semestru.values()[in.readInt()];   // enum: salveaza ordinal
    long ms        = in.readLong();
    dataAdaugare   = (ms == -1) ? null : new Date(ms); // Date: salveaza milisecunde
}
```
> **Regula:** citeste campurile in EXACT aceeasi ordine in care le scrii in `writeToParcel`.

### 3. `writeToParcel` – serializeaza campurile
```java
@Override
public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(numeDisciplina);
    dest.writeByte((byte) (estePromovata ? 1 : 0));
    dest.writeInt(valoareNota);
    dest.writeDouble(punctajExamen);
    dest.writeInt(semestru.ordinal());                 // enum -> int
    dest.writeLong(dataAdaugare != null ? dataAdaugare.getTime() : -1); // Date -> long
}
```

### 4. `describeContents` si `CREATOR` (obligatorii, boilerplate)
```java
@Override
public int describeContents() { return 0; }

public static final Creator<Disciplina> CREATOR = new Creator<Disciplina>() {
    @Override
    public Disciplina createFromParcel(Parcel in) { return new Disciplina(in); }

    @Override
    public Disciplina[] newArray(int size) { return new Disciplina[size]; }
};
```

### Tipuri de date – cum le scrii/citesti
| Tip Java | Scriere | Citire |
|---|---|---|
| `String` | `writeString(s)` | `readString()` |
| `int` | `writeInt(n)` | `readInt()` |
| `double` | `writeDouble(d)` | `readDouble()` |
| `boolean` | `writeByte((byte)(b?1:0))` | `readByte() != 0` |
| `Enum` | `writeInt(e.ordinal())` | `MyEnum.values()[readInt()]` |
| `Date` | `writeLong(d.getTime())` | `new Date(readLong())` |

---

## Cerinta 2 – Adapter personalizat pentru ListView

### Pasul A – Creeaza layout-ul unui item: `res/layout/item_disciplina.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="12dp">

    <!-- Poti pune orice view-uri vrei -->
    <TextView
        android:id="@+id/tvNume"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:textSize="16sp"
        android:textStyle="bold"/>

    <TextView
        android:id="@+id/tvNota"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="14sp"/>

</LinearLayout>
```

### Pasul B – Creeaza `DisciplinaAdapter.java`

**Fisier nou:** `DisciplinaAdapter.java` (langa `MainActivity.java`)

```java
package com.example.laborator4;   // <-- pachetul tau

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class DisciplinaAdapter extends ArrayAdapter<Disciplina> {

    private final LayoutInflater inflater;

    public DisciplinaAdapter(Context context, ArrayList<Disciplina> lista) {
        super(context, 0, lista);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Recicleaza view-ul daca exista
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_disciplina, parent, false);
        }

        Disciplina d = getItem(position);

        // Gaseste view-urile din layout-ul itemului
        TextView tvNume = convertView.findViewById(R.id.tvNume);
        TextView tvNota = convertView.findViewById(R.id.tvNota);

        // Seteaza datele
        tvNume.setText(d.getNumeDisciplina());
        tvNota.setText(String.valueOf(d.getValoareNota()));

        return convertView;
    }
}
```

### Pasul C – Foloseste adapterul in `MainActivity.java`

**Inlocuieste** orice adapter vechi cu cel nou. In `onCreate`:
```java
private ArrayList<Disciplina> listaDiscipline;
private DisciplinaAdapter adapter;

// in onCreate():
listaDiscipline = new ArrayList<>();
adapter = new DisciplinaAdapter(this, listaDiscipline);

ListView listView = findViewById(R.id.listViewDiscipline);
listView.setAdapter(adapter);
```

---

## Cerinta 3 – ItemClick deschide activitatea de editare

### Cum functioneaza fluxul
```
MainActivity  --[Intent + obiect + pozitie]-->  AdaugaDisciplinaActivity
MainActivity  <--[Intent + obiect modificat + pozitie]--  AdaugaDisciplinaActivity
```

### Pasul A – Declara `ActivityResultLauncher` in `MainActivity.java`

**Sus in clasa** (camp, nu in `onCreate`):
```java
private final ActivityResultLauncher<Intent> launcher =
    registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        this::proceseazaRezultat
    );
```

### Pasul B – `setOnItemClickListener` in `MainActivity.java`

In `onCreate`, dupa `listView.setAdapter(adapter)`:
```java
listView.setOnItemClickListener((parent, view, position, id) -> {
    Disciplina disciplina = listaDiscipline.get(position);
    Intent intent = new Intent(MainActivity.this, AdaugaDisciplinaActivity.class);
    intent.putExtra("disciplina", disciplina);   // trimite obiectul (Parcelable)
    intent.putExtra("position", position);        // trimite pozitia din lista
    launcher.launch(intent);
});
```

### Pasul C – Metoda de procesare rezultat in `MainActivity.java`

```java
private void proceseazaRezultat(ActivityResult result) {
    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
        Intent data = result.getData();
        Disciplina disciplina = data.getParcelableExtra("disciplina");
        int position = data.getIntExtra("position", -1);

        if (disciplina != null) {
            if (position >= 0) {
                listaDiscipline.set(position, disciplina);  // MODIFICA, nu adauga
            } else {
                listaDiscipline.add(disciplina);            // adauga nou
            }
            adapter.notifyDataSetChanged();
        }
    }
}
```

> **Atentie:** `position >= 0` inseamna editare; `position == -1` inseamna adaugare noua.

### Pasul D – Butonul de adaugare tot foloseste `launcher`

```java
Button btnAdauga = findViewById(R.id.btnAdaugaDisciplina);
btnAdauga.setOnClickListener(v -> {
    Intent intent = new Intent(MainActivity.this, AdaugaDisciplinaActivity.class);
    // Nu trimite "disciplina" si nici "position" => e adaugare noua
    launcher.launch(intent);
});
```

### Pasul E – `AdaugaDisciplinaActivity.java` – pre-completare si salvare

**Camp `pozitie`** sus in clasa:
```java
private int pozitie = -1;
```

**In `onCreate`**, dupa `setContentView` si dupa ce ai legat toate view-urile:
```java
Disciplina disciplinaExistenta = getIntent().getParcelableExtra("disciplina");
if (disciplinaExistenta != null) {
    pozitie = getIntent().getIntExtra("position", -1);
    preCompleteazaCampuri(disciplinaExistenta);
}
```

**Metoda `preCompleteazaCampuri`** – pune datele in fiecare view:
```java
private void preCompleteazaCampuri(Disciplina d) {
    etNumeDisciplina.setText(d.getNumeDisciplina());
    etPunctajExamen.setText(String.valueOf(d.getPunctajExamen()));
    checkBoxPromovata.setChecked(d.isEstePromovata());

    // Spinner – selecteaza valoarea corecta
    Disciplina.Semestru[] valori = Disciplina.Semestru.values();
    for (int i = 0; i < valori.length; i++) {
        if (valori[i] == d.getSemestru()) {
            spinnerSemestru.setSelection(i);
            break;
        }
    }

    // RadioGroup – selecteaza butonul cu nota (id-urile rb4, rb5, ..., rb10)
    int nota = d.getValoareNota();
    int radioId = getResources().getIdentifier("rb" + nota, "id", getPackageName());
    if (radioId != 0) radioGroupNota.check(radioId);

    // DatePicker
    if (d.getDataAdaugare() != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d.getDataAdaugare());
        datePicker.updateDate(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        );
    }
}
```

**Metoda `salveazaDisciplina`** – trimite inapoi obiectul SI pozitia:
```java
private void salveazaDisciplina() {
    String nume = etNumeDisciplina.getText().toString();
    double punctaj = Double.parseDouble(etPunctajExamen.getText().toString());
    Disciplina.Semestru semestru = Disciplina.Semestru.valueOf(
        spinnerSemestru.getSelectedItem().toString());

    int notaSelectata = 5;
    int radioId = radioGroupNota.getCheckedRadioButtonId();
    if (radioId != -1) {
        RadioButton rb = findViewById(radioId);
        notaSelectata = Integer.parseInt(rb.getText().toString());
    }

    boolean promovata = checkBoxPromovata.isChecked();

    Calendar cal = Calendar.getInstance();
    cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
    Date data = cal.getTime();

    Disciplina disciplina = new Disciplina(
        nume, promovata, notaSelectata, punctaj, semestru, data);

    Intent intent = new Intent();
    intent.putExtra("disciplina", disciplina);
    intent.putExtra("position", pozitie);   // -1 daca e nou, altfel indexul
    setResult(RESULT_OK, intent);
    finish();
}
```

---

## AndroidManifest.xml – declara a doua activitate
```xml
<activity android:name=".AdaugaDisciplinaActivity"
          android:exported="false"/>
```

---

## Import-uri necesare in MainActivity.java
```java
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
```

## Import-uri necesare in AdaugaDisciplinaActivity.java
```java
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import java.util.Calendar;
import java.util.Date;
```

---

## Checklist rapid la examen

- [ ] Clasa model implementeaza `Parcelable` (constructor din Parcel, writeToParcel, CREATOR)
- [ ] Fisier layout item (`item_X.xml`) cu ID-urile necesare
- [ ] Clasa adapter extinde `ArrayAdapter<X>`, suprascrie `getView`
- [ ] In `MainActivity`: `ArrayList` + `CustomAdapter` + `listView.setAdapter`
- [ ] `ActivityResultLauncher` declarat ca camp (nu in `onCreate`)
- [ ] `setOnItemClickListener`: trimite `putExtra("obiect", obj)` si `putExtra("position", pos)`
- [ ] Metoda `proceseazaRezultat`: `set(pos, obj)` daca `pos >= 0`, altfel `add(obj)`
- [ ] `AdaugaDisciplinaActivity`: citeste `getParcelableExtra` + `getIntExtra("position", -1)`
- [ ] `preCompleteazaCampuri` seteaza fiecare view cu datele obiectului primit
- [ ] `salveazaDisciplina` trimite inapoi `putExtra("position", pozitie)`
- [ ] A doua activitate declarata in `AndroidManifest.xml`
