# Food Corner — Android App

Aplicatie Android pentru descoperirea si recenzarea restaurantelor locale. Proiect universitar (Anul 4 Android).

- **Package:** `com.example.proiect`
- **Limbaj:** Java
- **minSdk:** 24
- **Tema:** Material3 NoActionBar, culoare primara rosu `#E53935`

---

## Structura proiect

```
app/src/main/java/com/example/proiect/
├── model/
│   ├── Restaurant.java              # POJO: id, name, category, address, schedule, phone, imageUrl, lat/lng
│   └── Review.java                  # POJO: id, restaurantId, comment, rating, date, recommended
├── adapter/
│   ├── RestaurantGridAdapter.java   # Adapter GridView cu color-coding pe categorie
│   └── ReviewAdapter.java           # Adapter ListView recenzii (showRestaurantName boolean)
├── view/
│   ├── NonScrollListView.java       # ListView care isi adapteaza inaltimea (pt ScrollView parinte)
│   ├── PieChartView.java            # Pie chart pe Canvas, fara librarii externe
│   └── BarChartView.java            # Bar chart pe Canvas, fara librarii externe
├── database/
│   └── DatabaseHelper.java          # SQLiteOpenHelper singleton, versiunea 6
├── SampleData.java                  # Ramas in proiect dar neutilizat (inlocuit de DB)
├── LoginActivity.java
├── RegisterActivity.java
├── MainActivity.java
├── RestaurantDetailActivity.java
├── ProfileActivity.java
├── AddRestaurantActivity.java
├── EditRestaurantActivity.java      # Editare restaurant (doar userul care l-a adaugat)
├── AddReviewActivity.java
├── StatisticsActivity.java
├── SettingsActivity.java
└── MapActivity.java
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
```

---

## Activities

### LoginActivity *(launcher)*
- Verifica la start daca exista sesiune activa (`logged_user` in DB) → sare direct la MainActivity
- Validare campuri goale
- Link catre RegisterActivity

### RegisterActivity
- Validari: username minim 3 caractere, parola minim 6 caractere, parole identice, username unic
- Parola salvata cu hash SHA-256
- Erorile de validare apar **sub camp** (setate pe `TextInputLayout`, nu pe `TextInputEditText`) si ca **Snackbar** vizibil in partea de jos — mai evident decat iconita de eroare
- Campul parola are `helperText="Minim 6 caractere"` permanent vizibil
- Dupa inregistrare, logheaza automat si navigheaza la MainActivity

### MainActivity
- Verifica sesiunea la `onCreate` — redirectioneaza la Login daca nu e logat
- GridView 2 coloane cu toate restaurantele
- Carduri restaurant redesenate (`item_restaurant_grid.xml`):
  - Header colorat per categorie (130dp) cu emoji watermark semi-transparent (🍖🍕🍔🍜🥗)
  - Gradient inchis la baza header-ului pentru adancime vizuala
  - Chip alb-rotunjit (bottom-left) cu textul categoriei in culoarea categoriei
  - Border (`strokeColor`) pe card, colorat dupa categorie (1.5dp)
  - Sectiunea info: **nume** bold + **adresa** (1 linie, trunchiat) + **stea + nota numerica** (e.g. "4.2")
  - Rating provine din DB real (`db.getAverageRating()`), nu din SampleData
- `RestaurantGridAdapter` primeste acum `DatabaseHelper db` ca parametru
- SearchView — filtrare live dupa nume
- Dialog filtrare/sortare: Spinner categorie, RadioGroup sortare (default / A-Z / rating desc), CheckBox favorite
- Iconita filtru se coloreaza galben cand un filtru e activ
- Buton **`+` in toolbar** → deschide AddRestaurantActivity (meniu `menu_main.xml`)
- Buton **Profil in toolbar** → deschide ProfileActivity
- **Setari in meniul toolbar** (`...`) → deschide SettingsActivity
- Buton **Logout** in bara de jos

### RestaurantDetailActivity
- Preia `restaurant_id` din Intent
- **Header imagine (200dp):** daca `restaurant.imageUrl` exista ca fisier, afiseaza imaginea (`centerCrop`); altfel afiseaza litera categoriei ca fallback
- Afiseaza: nume, categorie, adresa, program, telefon
- **Site web** — afisat doar daca `restaurant.website` nu e gol (`tvWebsite` e `GONE` implicit); click deschide browser-ul; daca URL-ul nu incepe cu `http` se adauga automat `https://`
- RatingBar cu media calculata din recenzii
- CheckBox Favorit — persista in tabela `favorites` din DB
- Lista recenzii cu ReviewAdapter; fiecare recenzie are butoane **Editeaza** si **Sterge**
  - **Editeaza**: dialog cu EditText (comentariu), RatingBar (nota), Switch (recomandare) — salveaza cu `db.updateReview()`
  - **Sterge**: dialog de confirmare — sterge cu `db.deleteReview()`
- Butoane catre MapActivity si AddReviewActivity

### AddReviewActivity
- Poate fi deschis cu sau fara `restaurant_id` in Intent
  - Cu `restaurant_id`: cardul de selectie restaurant e ascuns
  - Fara `restaurant_id`: apare Spinner cu toate restaurantele
- Formular: comentariu (EditText), nota (RatingBar), data (DatePickerDialog), recomandare (Switch)
- Validare: comentariu si nota sunt obligatorii
- Salvare cu ProgressBar + delay 1s (Handler), scriere in DB

### StatisticsActivity
- **Card profil utilizator** (primul) — avatar circular cu prima litera din username, fundal rosu; afiseaza numele si categoria preferata
- **Top 5 Restaurante** — sectiune colapsabila (header clickabil + sageata); query SQL cu JOIN+AVG+ORDER BY+LIMIT, nu in memorie
- **Distributia pe categorii** — pie chart colapsabil (ascuns implicit)
- **Rating mediu pe categorii** — bar chart colapsabil (ascuns implicit)
- **Recenzii recente** — afiseaza 5 initial, buton "Vezi mai multe" incarca cate 3 in plus; dispare cand s-au afisat toate
- Toate sectiunile cu chart/lista sunt restranse implicit pentru a nu incarca ecranul

### ProfileActivity
- Accesibila din toolbar-ul MainActivity (iconita Profil)
- Afiseaza avatarul (prima litera din username) si numele curent
- **Restaurantele mele:** lista restaurantelor adaugate de userul curent (`added_by`); afiseaza 3 initial, "Vezi mai multe" adauga cate 3; click pe un rand → `EditRestaurantActivity`; lista se reimprospateza la revenire cu `RESULT_OK`
- **Recenziile mele:** lista recenziilor adaugate de utilizatorul curent (cu numele restaurantului); butoane Editeaza si Sterge pe fiecare recenzie
- **Schimbare username:** validare minim 3 caractere, username unic; actualizeaza `users`, `logged_user` si `user_name` in DB
- **Schimbare parola:** verifica parola actuala, validare minim 6 caractere, confirmare parola noua; salveaza hash SHA-256

### AddRestaurantActivity
- Accesibila din toolbar-ul MainActivity (iconita `+`)
- **Fotografie:** zona clickabila (200dp) la topul formularului; dialog "Galerie foto / Camera"; imaginea e copiata in `getFilesDir()/restaurant_images/` si calea absoluta e salvata in `image_url`; dupa selectie apare butonul "✎ Schimba"
  - Camera: cere permisiunea `CAMERA` la runtime; URI generat prin `FileProvider`
  - Galerie: `ActivityResultContracts.GetContent()` — nu necesita permisiune pe Android 13+
- Campuri: nume si adresa (obligatorii), categorie (Spinner), telefon, latitudine/longitudine, **site web** (optionale)
- **Program** — doua butoane outlined ("Deschidere" / "Inchidere") care deschid `TimePickerDialog` (format 24h); dupa selectarea ambelor ore apare un preview text; salvat ca `HH:mm - HH:mm`
- Salveaza `added_by = loggedUser` in DB — necesar pentru restrictia de editare
- Validare: nume si adresa obligatorii, coordonate GPS ca numere valide
- Salveaza in tabela `restaurants` din DB cu ID generat automat
- ProgressBar + delay 800ms la salvare

### EditRestaurantActivity
- Accesibila din ProfileActivity → "Restaurantele mele" → click pe restaurant
- Primeste `restaurant_id` din Intent
- **Verificare permisiune:** la `onCreate` compara `restaurant.addedBy` cu `db.getLoggedUser()` — daca nu coincid, `finish()` imediat cu Toast
- Pre-populeaza toate campurile cu datele existente (inclusiv selectia categoriei in Spinner)
- **Fotografie:** aceeasi logica de picker ca la AddRestaurantActivity; la prefill verifica daca fisierul exista (`File.exists()`) inainte de a afisa imaginea salvata
- **Program:** parseza programul existent (`HH:mm - HH:mm`) si populeaza automat butoanele cu orele salvate; acelasi TimePicker ca la adaugare
- Face `db.updateRestaurant(restaurant)` — UPDATE, nu INSERT nou
- **Sterge restaurant:** buton rosu "Sterge restaurantul" la baza formularului; dialog de confirmare; apeleaza `db.deleteRestaurant(id)` care sterge cascadat recenziile si favoritele asociate; `setResult(RESULT_OK)` + `finish()`
- La succes (save sau delete): `setResult(RESULT_OK)` + `finish()` pentru a semnala ProfileActivity sa reimprospateze lista
- ProgressBar + delay 600ms la salvare / 400ms la stergere

### SettingsActivity
- Accesibila din toolbar-ul MainActivity (meniu `...` → Setari)
- Afiseaza categoria preferata curenta (din `user_prefs`)
- Spinner cu toate categoriile (Toate, Romanesc, Italian, Fast-Food, Asian, Vegan)
- Buton Salveaza — scrie noua valoare in DB cu `db.setUserPref("preferred_category", ...)`
- Valoarea actualizata apare imediat si in StatisticsActivity

### MapActivity
- Exista ca Activity dar Google Maps nu e integrat inca
- Primeste optional `restaurant_id` din Intent (pentru centrat pe un restaurant)
- Necesita Google Maps API key

---

## Persistenta (SQLite)

Baza de date: `food_corner.db`, stocata in `/data/data/com.example.proiect/databases/`.
Gestionata prin `database/DatabaseHelper.java` (singleton, `SQLiteOpenHelper`, versiunea curenta: **5**).

| Tabela | Coloane | Continut |
|---|---|---|
| `restaurants` | id, name, category, address, schedule, phone, image_url, latitude, longitude, website, **added_by** | 10 restaurante (seed la prima instalare) |
| `reviews` | id (AUTOINCREMENT), restaurant_id, comment, rating, date, recommend, username | 13 recenzii seed + recenzii adaugate de utilizator |
| `favorites` | restaurant_id (PK) | ID-urile restaurantelor marcate ca favorite |
| `user_prefs` | key (PK), value | `logged_user`, `user_name`, `preferred_category` |
| `users` | id (AUTOINCREMENT), username (UNIQUE), password_hash | Conturi utilizatori, parola SHA-256 |

**API-ul `DatabaseHelper`:**
- `addRestaurant(restaurant)`, `getRestaurants()`, `getRestaurantById(id)`, `getRestaurantsByUser(username)`, `updateRestaurant(restaurant)`, `deleteRestaurant(restaurantId)`, `getTopRatedRestaurants(limit)`
- `getReviewsForRestaurant(restaurantId)`, `getRecentReviews(limit)`, `getAverageRating(restaurantId)`, `addReview(review)`, `updateReview(review)`, `deleteReview(reviewId)`, `getReviewsByUser(username)`
- `isFavorite(restaurantId)`, `setFavorite(restaurantId, bool)`, `getFavoriteIds()`
- `getUserPref(key, default)`, `setUserPref(key, value)`
- `registerUser(username, password)`, `loginUser(username, password)`, `usernameExists(username)`
- `updateUsername(oldUsername, newUsername)`, `updatePassword(username, oldPassword, newPassword)`
- `getLoggedUser()`, `setLoggedUser(username)`, `logout()`

Nu se mai folosesc SharedPreferences in niciun loc.

### Istoricul versiunilor DB

| Versiune | Modificare |
|---|---|
| 1–3 | Versiuni initiale |
| 4 | Adaugat tabela `users`, parola SHA-256 |
| 5 | Adaugat coloana `website TEXT` in `restaurants` (migrare cu `ALTER TABLE`, fara DROP) |
| 6 | Adaugat coloana `added_by TEXT` in `restaurants` — stocheaza username-ul celui care a adaugat restaurantul |

> **Regula migrare:** de la v4 in sus se foloseste `ALTER TABLE` in `onUpgrade` pentru a pastra datele existente. Nu mai face DROP + recreare decat daca e absolut necesar (schimbare incompatibila de schema).

### Note arhitecturale — sortare dupa rating

**`getRestaurants()`** returneaza toate restaurantele sortate dupa nume (fara rating).

**Sortarea dupa rating in MainActivity** se face *in memorie* (comparator Java), nu la nivel de DB — apeleaza `getAverageRating(id)` per restaurant dupa ce lista e incarcata.

**`getTopRatedRestaurants(limit)`** face totul intr-un singur query SQL cu `JOIN + AVG + ORDER BY + LIMIT` — folosit in StatisticsActivity pentru Top 5. Nu folosi `getRestaurants()` + comparator pentru asta, ar face N query-uri suplimentare.

---

## Drawables notabile

| Fișier | Descriere |
|---|---|
| `bg_category_tag.xml` | Background chip categorie (folosit anterior in adapter) |
| `bg_spinner.xml` | Background Spinner custom |
| `circle_white_background.xml` | Oval alb pentru avatar litera (ProfileActivity, StatisticsActivity) |
| `ic_arrow_down.xml` | Chevron pentru sectiunile colapsabile din StatisticsActivity |
| `gradient_card_header.xml` | Gradient transparent → #66000000, overlay pe header carduri restaurant |
| `bg_chip_white.xml` | Dreptunghi alb semi-transparent (corners 12dp) — chip categorie pe card |
| `ic_star_filled.xml` | Stea galbena (#FFC107) — iconita rating in cardurile restaurant |

---

## Layouts relevante

| Layout | Folosit de |
|---|---|
| `activity_login.xml` | LoginActivity |
| `activity_register.xml` | RegisterActivity |
| `activity_main.xml` | MainActivity |
| `activity_restaurant_detail.xml` | RestaurantDetailActivity |
| `activity_add_review.xml` | AddReviewActivity |
| `activity_statistics.xml` | StatisticsActivity |
| `activity_map.xml` | MapActivity |
| `activity_profile.xml` | ProfileActivity |
| `activity_add_restaurant.xml` | AddRestaurantActivity |
| `activity_edit_restaurant.xml` | EditRestaurantActivity |
| `activity_settings.xml` | SettingsActivity |
| `dialog_filter.xml` | Dialog filtrare din MainActivity |
| `dialog_edit_review.xml` | Dialog editare recenzie din RestaurantDetailActivity |
| `item_restaurant_grid.xml` | RestaurantGridAdapter |
| `item_review.xml` | ReviewAdapter (cu butoane Editeaza/Sterge) |
| `menu_main.xml` | Toolbar MainActivity (buton Adauga Restaurant) |

---

## Ce urmeaza (TODO)

- [ ] Integrare Google Maps in MapActivity (necesita API key in `local.properties`)
- [x] Ecran Setari — modificare `preferred_category` (SettingsActivity, accesibil din toolbar)
- [x] Stergere / editare recenzie — butoane in fiecare item, dialog inline
