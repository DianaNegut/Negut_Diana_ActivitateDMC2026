# Cheatsheet Laborator 3 - Android Activity Lifecycle & Intents

> **Pachet proiect:** `com.example.laborator3`
> **Min SDK:** 24 | **Compile SDK:** 36
> **Activități:** MainActivity, SecondActivity (LAUNCHER), ThirdActivity

---

## Cerinta 1 — Activitate noua setata ca activitate implicita

**Ce faci:** Creezi o activitate noua si o setezi ca launcher in `AndroidManifest.xml`.

### Fisier: `AndroidManifest.xml`
```xml
<activity android:name=".NumeActivitate" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

> Doar O singura activitate poate fi launcher. Daca vrei sa schimbi care e implicita, muti blocul `<intent-filter>` la activitatea dorita si stergi de la celelalte.

---

## Cerinta 2 — Ciclu de viata: onStart, onResume, onPause, onStop

**Ce faci:** In clasa activitatii, suprascrii metodele ciclului de viata.

### Fisier: `MainActivity.java` (sau orice activitate)
```java
@Override
protected void onStart() {
    super.onStart();
    // cod aici
}

@Override
protected void onResume() {
    super.onResume();
    // cod aici
}

@Override
protected void onPause() {
    super.onPause();
    // cod aici
}

@Override
protected void onStop() {
    super.onStop();
    // cod aici
}
```

---

## Cerinta 3 — Log-uri in fiecare metoda

**Ce faci:** In fiecare metoda de ciclu de viata adaugi un Log cu nivel diferit.

### Fisier: `MainActivity.java`
```java
private static final String TAG = "a1"; // tag pentru Logcat

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.e(TAG, "onCreate - ERROR");
}

@Override
protected void onStart() {
    super.onStart();
    Log.w(TAG, "onStart - WARNING");
}

@Override
protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume - DEBUG");
}

@Override
protected void onPause() {
    super.onPause();
    Log.i(TAG, "onPause - INFO");
}

@Override
protected void onStop() {
    super.onStop();
    Log.v(TAG, "onStop - VERBOSE");
}
```

**Import necesar:**
```java
import android.util.Log;
```

**Tipuri de log (toate 5):**
| Metoda | Nivel | Culoare in Logcat |
|--------|-------|-------------------|
| `Log.e(TAG, "msg")` | Error | Rosu |
| `Log.w(TAG, "msg")` | Warning | Portocaliu |
| `Log.d(TAG, "msg")` | Debug | Albastru |
| `Log.i(TAG, "msg")` | Info | Verde |
| `Log.v(TAG, "msg")` | Verbose | Gri |

---

## Cerinta 4 — Filtrare loguri in Logcat

**Nu e cod — e actiune in Android Studio:**

1. Deschide tabul **Logcat** (jos in Android Studio)
2. In bara de cautare din Logcat scrii: `tag:a1` (sau tagul tau)
3. Sau filtrezi dupa nivel: selectezi din dropdown **Error / Warning / Debug / Info / Verbose**
4. Poti scrie si textul mesajului direct in bara de search

---

## Cerinta 5 — Activitate noua setata ca launcher (a doua activitate)

**Ce faci:**
1. Creezi `SecondActivity.java` + `activity_second.xml`
2. O declari in `AndroidManifest.xml` ca LAUNCHER
3. Muti `intent-filter` de la MainActivity la SecondActivity

### Fisier: `AndroidManifest.xml`
```xml
<!-- MainActivity - nu mai e launcher -->
<activity android:name=".MainActivity" android:exported="false" />

<!-- SecondActivity - devine launcher -->
<activity android:name=".SecondActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

### Cum creezi activitatea in Android Studio:
- Click dreapta pe pachet → **New → Activity → Empty Views Activity**
- Numesti `SecondActivity`
- Android Studio adauga automat in Manifest

---

## Cerinta 6 — A treia activitate

**Ce faci:** La fel ca la cerinta 5, dar creezi `ThirdActivity`.

### Fisier: `AndroidManifest.xml`
```xml
<activity android:name=".ThirdActivity" android:exported="false" />
```

> `android:exported="false"` = activitatea nu poate fi lansata din afara aplicatiei.

---

## Cerinta 7 — Button in SecondActivity care deschide ThirdActivity prin Intent

### Fisier: `activity_second.xml` — adaugi butonul
```xml
<Button
    android:id="@+id/btnDeschide"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Deschide activitatea 3"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

### Fisier: `SecondActivity.java` — simplu, fara rezultat returnat
```java
Button btnDeschide = findViewById(R.id.btnDeschide);
btnDeschide.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
        startActivity(intent);
    }
});
```

**Import necesar:**
```java
import android.content.Intent;
import android.widget.Button;
import android.view.View;
```

---

## Cerinta 8 — Trimitere mesaj + 2 valori int prin Bundle catre ThirdActivity

**Ce faci:** La click pe buton, adaugi date in Intent prin Bundle.

### Fisier: `SecondActivity.java` — onClick cu Bundle
```java
btnDeschide.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("mesaj", "Salut din activitate 2!");
        bundle.putInt("valoare1", 42);
        bundle.putInt("valoare2", 100);
        intent.putExtras(bundle);

        startActivity(intent);
    }
});
```

---

## Cerinta 9 — In ThirdActivity: preluare date din Intent si afisare Toast

### Fisier: `ThirdActivity.java` — in onCreate()
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_third);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
        String mesaj = bundle.getString("mesaj");
        int valoare1 = bundle.getInt("valoare1");
        int valoare2 = bundle.getInt("valoare2");

        Toast.makeText(this,
            "Mesaj: " + mesaj + "\nVal1: " + valoare1 + "\nVal2: " + valoare2,
            Toast.LENGTH_LONG).show();
    }
}
```

**Import necesar:**
```java
import android.widget.Toast;
import android.os.Bundle;
```

---

## Cerinta 10 — Buton in ThirdActivity care trimite mesaj + suma inapoi la SecondActivity

**Atentie:** Acum SecondActivity trebuie sa foloseasca `startActivityForResult` sau `ActivityResultLauncher` (varianta moderna).

### Fisier: `activity_third.xml` — adaugi butonul
```xml
<Button
    android:id="@+id/btnTrimite"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Trimite rezultat"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

### Fisier: `ThirdActivity.java` — onCreate() complet cu buton de return

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_third);

    // Preia date primite
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
        String mesaj = bundle.getString("mesaj");
        int valoare1 = bundle.getInt("valoare1");
        int valoare2 = bundle.getInt("valoare2");
        final int suma = valoare1 + valoare2;

        // Afiseaza ce a primit
        Toast.makeText(this,
            "Mesaj: " + mesaj + "\nVal1: " + valoare1 + "\nVal2: " + valoare2,
            Toast.LENGTH_LONG).show();

        // Buton care trimite rezultatul inapoi
        Button btnTrimite = findViewById(R.id.btnTrimite);
        btnTrimite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rezultat = new Intent();
                rezultat.putExtra("mesajRaspuns", "Salut din activitate 3!");
                rezultat.putExtra("suma", suma);
                setResult(RESULT_OK, rezultat);
                finish();
            }
        });
    }
}
```

---

## Cerinta 11 — In SecondActivity: afisare Toast cu mesajul si suma primite

**Aceasta cerinta schimba cum lansezi ThirdActivity.** Trebuie sa folosesti `ActivityResultLauncher` pentru a putea primi rezultatul.

### Fisier: `SecondActivity.java` — VERSIUNEA COMPLETA

```java
public class SecondActivity extends AppCompatActivity {

    // 1. Declari launcher-ul ca variabila membru
    private ActivityResultLauncher<Intent> activityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // 2. Inregistrezi launcher-ul INAINTE de setOnClickListener
        activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // 4. Aici primesti rezultatul de la ThirdActivity
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String mesajRaspuns = result.getData().getStringExtra("mesajRaspuns");
                        int suma = result.getData().getIntExtra("suma", 0);

                        Toast.makeText(SecondActivity.this,
                            "Raspuns: " + mesajRaspuns + "\nSuma: " + suma,
                            Toast.LENGTH_LONG).show();
                    }
                }
            }
        );

        // 3. La click lansezi cu activityLauncher.launch() in loc de startActivity()
        Button btnDeschide = findViewById(R.id.btnDeschide);
        btnDeschide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("mesaj", "Salut din activitate 2!");
                bundle.putInt("valoare1", 42);
                bundle.putInt("valoare2", 100);
                intent.putExtras(bundle);

                activityLauncher.launch(intent); // <-- nu startActivity!
            }
        });
    }

    // Lifecycle logs
    @Override protected void onStart() { super.onStart(); Log.i("a2", "onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d("a2", "onResume"); }
    @Override protected void onPause() { super.onPause(); Log.d("a2", "onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d("a2", "onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d("a2", "onDestroy"); }
    @Override protected void onRestart() { super.onRestart(); Log.d("a2", "onRestart"); }
}
```

**Importuri necesare pentru SecondActivity:**
```java
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
```

---

## Rezumat AndroidManifest.xml complet

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.AppCompat.Light">

        <!-- Activitatea 1 - nu e launcher -->
        <activity android:name=".MainActivity" android:exported="false" />

        <!-- Activitatea 2 - LAUNCHER (se deschide la pornirea aplicatiei) -->
        <activity android:name=".SecondActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Activitatea 3 - lansata din SecondActivity -->
        <activity android:name=".ThirdActivity" android:exported="false" />

    </application>
</manifest>
```

---

## Flow complet al aplicatiei

```
App porneste → SecondActivity (LAUNCHER)
    ↓ user apasa buton
    Intent + Bundle { mesaj, valoare1=42, valoare2=100 }
    ↓
ThirdActivity
    → afiseaza Toast cu datele primite
    → calculeaza suma = 42 + 100 = 142
    ↓ user apasa buton "Trimite rezultat"
    setResult(RESULT_OK, intent cu { mesajRaspuns, suma=142 })
    finish()
    ↓
SecondActivity (onActivityResult)
    → afiseaza Toast cu mesajul si suma primite
```

---

## Checklist examen

- [ ] Proiect creat cu min SDK 23 sau 24
- [ ] MainActivity cu lifecycle methods (onStart, onResume, onPause, onStop)
- [ ] Fiecare metoda are Log cu nivel diferit (e, w, d, i, v)
- [ ] SecondActivity creata si setata ca LAUNCHER in Manifest
- [ ] ThirdActivity creata si declarata in Manifest
- [ ] Button in SecondActivity care deschide ThirdActivity
- [ ] Bundle cu mesaj + 2 valori int trimis catre ThirdActivity
- [ ] ThirdActivity preia Bundle si afiseaza Toast
- [ ] Button in ThirdActivity care face setResult + finish
- [ ] SecondActivity foloseste ActivityResultLauncher (nu startActivityForResult deprecated)
- [ ] SecondActivity afiseaza Toast cu mesajul si suma primite
