# Laborator 1 - Ghid Examen Android

---

## Cerinta 1 — Creare proiect nou

**In Android Studio:**
1. `File > New > New Project`
2. Selecteaza **Empty Views Activity**
3. Language: **Java**
4. Minimum SDK: **API 23** sau **API 24**
5. Click Finish

**Fisiere generate automat (importante):**
```
app/src/main/java/com/example/<nume>/MainActivity.java
app/src/main/res/layout/activity_main.xml
app/src/main/res/values/strings.xml
app/src/main/AndroidManifest.xml
```

---

## Cerinta 2 — Rulare pe emulator

**In Android Studio:**
- Apasa butonul verde ▶ (Run) din toolbar
- Sau `Shift+F10`
- Selecteaza emulatorul din Device Manager

---

## Cerinta 3 — Identificarea fisierelor din proiect

Structura principala (in panoul **Project > Android**):

| Fisier | Rol |
|---|---|
| `MainActivity.java` | Logica activitatii principale |
| `activity_main.xml` | Layout-ul ecranului principal |
| `strings.xml` | Resurse text (internationalizare) |
| `colors.xml` | Resurse culori |
| `themes.xml` | Tema aplicatiei |
| `AndroidManifest.xml` | Configuratia aplicatiei (activitati, permisiuni) |
| `build.gradle.kts` | Configuratia build-ului si dependintele |

---

## Cerinta 4 — Debug cu breakpoint in onCreate()

**Pasi:**
1. Deschide `MainActivity.java`
2. Click in bara din stanga liniei `super.onCreate(savedInstanceState);` — apare un punct rosu (breakpoint)
3. Apasa **Debug** (iconita gândac 🐛) sau `Shift+F9`
4. Aplicatia se opreste la breakpoint
5. **F8** = Step Over (pas cu pas), **F7** = Step Into, **F9** = Resume

---

## Cerinta 5 — Adaugare Button DEASUPRA TextView

**Fisier: `app/src/main/res/layout/activity_main.xml`**

Adauga blocul `<Button>` in `ConstraintLayout`. Constrangerile: `Bottom` legat de `Top`-ul TextView-ului.

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/hello_text"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/button_text"
        app:layout_constraintBottom_toTopOf="@+id/textView"
        app:layout_constraintEnd_toEndOf="@+id/textView"
        app:layout_constraintStart_toStartOf="@+id/textView"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Regula constrangeri Button deasupra TextView:**
- `app:layout_constraintBottom_toTopOf="@+id/textView"` ← fundul butonului legat de varful textview
- `app:layout_constraintTop_toTopOf="parent"` ← varful legat de parinte

---


la certinta asta am avut eroarea:
Sunt două probleme în XML-ul tău:

1. @string/button_text nu există în strings.xml al noului proiect.

Adaugă în res/values/strings.xml:


<string name="button_text">Apasa</string>
2. TextView-ul nu are android:id, dar Button-ul se ancorează de el.

Adaugă android:id la TextView:


<TextView
    android:id="@+id/textView"
    android:layout_width="wrap_content"
    ...

## Cerinta 6 — Adaugare EditText SUB TextView

**Fisier: `app/src/main/res/layout/activity_main.xml`**

Adauga blocul `<EditText>` dupa Button. Constrangerile: `Top` legat de `Bottom`-ul TextView-ului.

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/hello_text"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/button_text"
        app:layout_constraintBottom_toTopOf="@+id/textView"
        app:layout_constraintEnd_toEndOf="@+id/textView"
        app:layout_constraintStart_toStartOf="@+id/textView"
        app:layout_constraintTop_toTopOf="parent" />

    <EditText
        android:id="@+id/editTextText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:ems="10"
        android:inputType="text"
        android:text="@string/edit_text"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textView" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Regula constrangeri EditText sub TextView:**
- `app:layout_constraintTop_toBottomOf="@+id/textView"` ← varful edittext legat de fundul textview
- `app:layout_constraintBottom_toBottomOf="parent"` ← fundul legat de parinte

---

În strings.xml adaugi:


<string name="edit_hint">Edit me!</string>
Apoi în layout înlocuiești:


android:text="Edit me!"
cu:


android:text="@string/edit_hint"

## Cerinta 7 — Texte din fisierul de resurse (strings.xml)

**Fisier: `app/src/main/res/values/strings.xml`**

In loc sa scrii textul direct (`android:text="Buna ziua"`), definesti stringuri in `strings.xml` si le referentiezi cu `@string/nume`.

```xml
<resources>
    <string name="app_name">laborator 1</string>
    <string name="hello_text">Buna ziua, Diana!</string>
    <string name="button_text">buton de apasat</string>
    <string name="edit_text">poti edita aici!</string>
    <string name="text_nou">Ai apasat butonul!</string>
</resources>
```

**In layout XML folosesti:** `android:text="@string/hello_text"` (nu text hardcodat)

---

## Cerinta 8 (Optional) — Click pe buton schimba textul din TextView

**Fisier: `app/src/main/java/com/example/laborator1/MainActivity.java`**

```java
package com.example.laborator1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textView = findViewById(R.id.textView);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            textView.setText(R.string.text_nou);
        });
    }
}
```

**Ce face:** La click pe buton, `textView` isi schimba textul in cel definit la `text_nou` din `strings.xml`.

---

## Cerinta 9 — Internationalizare (minim 2 limbi straine)

### Pasul 1 — Creeaza folderele pentru fiecare limba

In Android Studio, click dreapta pe `res > New > Android Resource Directory`:
- Resource type: `values`
- Locale: alege limba (ex: `fr` pentru franceza, `en` pentru engleza)

Sau creeaza manual folderele:
```
app/src/main/res/values/        ← romana (default)
app/src/main/res/values-en/     ← engleza
app/src/main/res/values-fr/     ← franceza
```

### Pasul 2 — Creeaza `strings.xml` in fiecare folder

**`app/src/main/res/values/strings.xml`** (Romana — default):
```xml
<resources>
    <string name="app_name">laborator 1</string>
    <string name="hello_text">Buna ziua, Diana!</string>
    <string name="button_text">buton de apasat</string>
    <string name="edit_text">poti edita aici!</string>
    <string name="text_nou">Ai apasat butonul!</string>
</resources>
```

**`app/src/main/res/values-en/strings.xml`** (Engleza):
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">laborator 1</string>
    <string name="hello_text">Hello, Diana!</string>
    <string name="button_text">button</string>
    <string name="edit_text">you can edit here</string>
    <string name="text_nou">button pressed</string>
</resources>
```

**`app/src/main/res/values-fr/strings.xml`** (Franceza):
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">laborator 1</string>
    <string name="hello_text">Bonjour, Diana!</string>
    <string name="button_text">Appuyez sur ce bouton</button>
    <string name="edit_text">Vous pouvez modifier ici!</string>
    <string name="text_nou">Vous avez appuyé sur le bouton!</string>
</resources>
```

### Pasul 3 — Schimba limba pe emulator

1. Pe emulator: `Settings > General Management > Language and Input > Language`
2. `Add Language` → alege Franceza / Engleza
3. Trage noua limba in sus (sa fie prima)
4. Reporneste aplicatia — textele se schimba automat

---

## Rezumat fisiere modificate per cerinta

| Cerinta | Fisier(e) modificate |
|---|---|
| 1 | — (proiect nou generat automat) |
| 2 | — (rulare) |
| 3 | — (identificare, nimic de modificat) |
| 4 | — (debug, nimic de modificat) |
| 5 | `activity_main.xml` |
| 6 | `activity_main.xml` |
| 7 | `activity_main.xml` + `values/strings.xml` |
| 8 | `MainActivity.java` + `values/strings.xml` |
| 9 | `values/strings.xml` + `values-en/strings.xml` (nou) + `values-fr/strings.xml` (nou) |

---

## Reguli de aur pentru constrangeri ConstraintLayout

```
DEASUPRA unui element X:
    app:layout_constraintBottom_toTopOf="@+id/X"
    app:layout_constraintTop_toTopOf="parent"

SUB un element X:
    app:layout_constraintTop_toBottomOf="@+id/X"
    app:layout_constraintBottom_toBottomOf="parent"

CENTRAT pe ecran:
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
```
