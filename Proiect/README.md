# CloudPod — Android App

Aplicatie Android pentru descoperirea si evaluarea furnizorilor de stocare cloud descentralizata. Proiect universitar (Anul 4 Android).

- **Package:** `com.example.proiect`
- **Limbaj:** Java
- **minSdk:** 24
- **Tema:** Material3 NoActionBar, culoare primara albastru `#1E88E5`

---

## Structura proiect

```
app/src/main/java/com/example/proiect/
├── model/
│   ├── Restaurant.java              # POJO provider: id, name, region, nodeUrl, storageCapacity,
│   │                                #   peerId, imageUrl, lat/lng, pricePerGB, uptime, addedBy
│   └── Review.java                  # POJO rating: id, providerId, comment, rating, date, recommend, username
├── adapter/
│   ├── RestaurantGridAdapter.java   # Adapter GridView cu color-coding pe regiune, badge top 3, compare overlay
│   └── ReviewAdapter.java           # Adapter ListView ratinguri (showProviderName boolean)
├── view/
│   ├── NonScrollListView.java       # ListView care isi adapteaza inaltimea (pt ScrollView parinte)
│   ├── PieChartView.java            # Pie chart pe Canvas, fara librarii externe
│   └── BarChartView.java            # Bar chart pe Canvas, fara librarii externe
├── database/
│   └── DatabaseHelper.java          # SQLiteOpenHelper singleton, versiunea 7
├── RemoteConfig.java                # Fetch + parsare JSON de la Gist (imagini default per regiune)
├── SampleData.java                  # Neutilizat
├── LoginActivity.java
├── RegisterActivity.java
├── MainActivity.java
├── RestaurantDetailActivity.java    # Detalii provider
├── ProfileActivity.java
├── AddRestaurantActivity.java       # Adaugare provider
├── EditRestaurantActivity.java      # Editare provider (doar userul care l-a adaugat)
├── AddReviewActivity.java           # Adaugare rating
├── StatisticsActivity.java
├── SettingsActivity.java
├── MapActivity.java                 # Harta OSMDroid cu markere
├── CompareActivity.java             # Tabel comparativ 2 provideri
└── WebViewActivity.java             # Browser integrat pentru Node URL
```

---

## Flux navigare

```
Prima deschidere:
  LoginActivity → (nu are cont) → RegisterActivity → MainActivity

Urmatoarele deschideri:
  LoginActivity → (sesiune activa) → MainActivity (direct)

Logout:
  MainActivity → buton Logout → LoginActivity

Comparare:
  MainActivity (long press card) → selectie → tap al doilea card → CompareActivity

WebView:
  RestaurantDetailActivity → tap pe Node URL → WebViewActivity
```

---

## Activities

### LoginActivity *(launcher)*
- Verifica la start daca exista sesiune activa → sare direct la MainActivity
- Validare campuri goale

### RegisterActivity
- Validari: username minim 3 caractere, parola minim 6, parole identice, username unic
- Parola salvata cu hash SHA-256
- Dupa inregistrare, logheaza automat

### MainActivity
- GridView 2 coloane cu toti providerii
- **Long press** pe card → intra in **compare mode**: banner albastru cu numele selectat, overlay ✓ pe card
- **Tap pe al doilea card** in compare mode → deschide CompareActivity
- **Badge-uri top 3** pe primele 3 carduri dupa rating: 🥇🥈🥉
- SearchView, dialog filtrare/sortare (regiune, A-Z, rating, favorite)
- Sortare default: providerii din **regiunea preferata** (SharedPreferences) apar primii
- Iconita filtru se coloreaza galben cand un filtru e activ

### RestaurantDetailActivity
- Header imagine: poza locala → imagine remote (Glide) → litera regiunii ca fallback
- Afiseaza: nume, regiune, URL nod (clickabil → WebViewActivity), stocare, Peer ID, Pret/GB, Uptime%
- RatingBar cu media din DB, CheckBox Favorit
- Lista ratinguri cu butoane Editeaza / Sterge

### CompareActivity *(nou)*
- Primeste 2 `provider_id` prin Intent
- Tabel comparativ: Nume, Regiune, Capacitate stocare, Pret/GB, Uptime, Rating mediu
- Valorile mai bune (pret mic, uptime mare, rating mare) apar in **verde**
- Rand final 🏆 cu verdict ✅/❌ bazat pe scor calculat

### WebViewActivity *(nou)*
- Primeste URL si titlu prin Intent
- Browser integrat cu bara de progres
- Titlul toolbar-ului se actualizeaza cu titlul paginii
- Back → navigare inapoi in pagina (history), apoi inchidere activitate

### AddRestaurantActivity
- Fotografie: galerie / camera (FileProvider, permisiune runtime)
- Campuri: nume si URL nod (obligatorii), regiune (Spinner), Peer ID, stocare, pret/GB, uptime, lat/lng
- ProgressBar + delay 800ms la salvare

### EditRestaurantActivity
- Verificare permisiune: `provider.addedBy == loggedUser`
- Header imagine: poza locala → imagine remote (Glide) → nimic
- Sterge provider cu confirmare → stergere cascadata ratinguri + favorite

### AddReviewActivity
- Cu sau fara `provider_id` in Intent
- DatePickerDialog pentru data, Switch recomandare

### StatisticsActivity
- Card profil cu regiune preferata (din SharedPreferences)
- Top 5 Provideri (colapsabil), Pie chart regiuni, Bar chart rating/regiune
- Ratinguri recente cu paginare

### ProfileActivity
- Providerii mei + Ratingurile mele
- Schimbare username / parola

### SettingsActivity
- Regiune preferata salvata in **SharedPreferences** (`cloudpod_prefs`, cheia `preferred_region`)
- Afecteza sortarea din MainActivity si cardul din StatisticsActivity

### MapActivity
- Harta **OpenStreetMap** (OSMDroid) cu markere pentru toti cei 10 provideri
- Multi-touch zoom, click pe marker → InfoWindow cu regiune, stocare, pret
- Daca vine cu `provider_id`, centreaza pe acel provider cu zoom 8

---

## Persistenta (SQLite)

Baza de date: `cloudpod.db`, versiunea **7**.

| Tabela | Coloane |
|---|---|
| `providers` | id, name, region, node_url, storage_capacity, peer_id, image_url, latitude, longitude, price_per_gb, uptime, added_by |
| `ratings` | id, provider_id, comment, rating, date, recommend, username |
| `favorites` | provider_id |
| `user_prefs` | key, value (`logged_user`, `user_name`) |
| `users` | id, username, password_hash |

**SharedPreferences** (`cloudpod_prefs`): `preferred_region` — scrisa in SettingsActivity, citita in MainActivity si StatisticsActivity.

---

## Date remote (JSON)

**RemoteConfig.java** face fetch la pornire din Gist:
```
https://gist.githubusercontent.com/DianaNegut/.../raw/cloudpod_config.json
```
JSON-ul contine URL-uri de imagini default per regiune. Parsare cu `org.json.JSONObject`, incarcare imagini cu **Glide**.

---

## Cerinte acoperite

| # | Cerinta | Implementare |
|---|---|---|
| 1 | Minim 5 activitati | 13 activitati conectate |
| 2 | Controale simple | TextView, EditText, Button, CheckBox, Spinner, ProgressBar, RatingBar, Switch |
| 3 | Controale complexe | GridView, ListView, DatePickerDialog |
| 4 | Custom adapter | `RestaurantGridAdapter`, `ReviewAdapter` |
| 5 | SharedPreferences | `cloudpod_prefs` → `preferred_region` |
| 6 | SQLite | `cloudpod.db`, versiunea 7, 5 tabele |
| 7 | JSON/XML remote | `RemoteConfig` fetch Gist → parsare JSON → imagini Glide |
| 8 | Harta cu markere | OSMDroid (OpenStreetMap), markere pentru toti providerii |
| 9 | Grafica 2D | `PieChartView` + `BarChartView` custom pe Canvas |

---

## Dependente

```kotlin
implementation("com.github.bumptech.glide:glide:4.16.0")   // incarcare imagini remote
implementation("org.osmdroid:osmdroid-android:6.1.18")      // harta OpenStreetMap
```

---

## Internationalizare

| Folder | Limba |
|---|---|
| `values/` | Romana (default) |
| `values-en/` | Engleza |
| `values-fr/` | Franceza |

---

## Color-coding regiuni

| Regiune | Culoare |
|---|---|
| EU-West | `#1E88E5` |
| EU-East | `#00695C` |
| NA | `#4527A0` |
| Asia-Pacific | `#E65100` |
| South-America | `#2E7D32` |

---

## Seed data — 10 provideri

| ID | Nume | Regiune | Stocare | Pret/GB | Uptime |
|---|---|---|---|---|---|
| 1 | StoragePod EU-1 | EU-West | 2 TB | $0.020 | 99.9% |
| 2 | CloudVault Berlin | EU-West | 5 TB | $0.018 | 99.7% |
| 3 | IronStore Paris | EU-West | 3 TB | $0.022 | 99.5% |
| 4 | BalticNode | EU-East | 1 TB | $0.015 | 98.5% |
| 5 | EastCloud Warszawa | EU-East | 3 TB | $0.016 | 99.1% |
| 6 | NovaPod NYC | NA | 10 TB | $0.025 | 99.8% |
| 7 | SteelVault Texas | NA | 8 TB | $0.022 | 99.5% |
| 8 | NorthNode Toronto | NA | 4 TB | $0.023 | 99.3% |
| 9 | AsiaPod Singapore | Asia-Pacific | 3 TB | $0.019 | 99.6% |
| 10 | SouthStore Sao Paulo | South-America | 1 TB | $0.017 | 98.9% |

---

## TODO

- [ ] Google Maps (necesita API key — inlocuit cu OSMDroid)
