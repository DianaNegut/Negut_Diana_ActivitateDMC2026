package com.example.proiect.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.proiect.model.Restaurant;
import com.example.proiect.model.Review;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "cloudpod.db";
    private static final int    DB_VERSION = 7;

    // ── Providers ─────────────────────────────────────────────────────────────
    public static final String TABLE_PROVIDERS  = "providers";
    public static final String COL_P_ID         = "id";
    public static final String COL_P_NAME       = "name";
    public static final String COL_P_REGION     = "region";
    public static final String COL_P_NODE_URL   = "node_url";
    public static final String COL_P_STORAGE    = "storage_capacity";
    public static final String COL_P_PEER_ID    = "peer_id";
    public static final String COL_P_IMAGE_URL  = "image_url";
    public static final String COL_P_LAT        = "latitude";
    public static final String COL_P_LNG        = "longitude";
    public static final String COL_P_PRICE      = "price_per_gb";
    public static final String COL_P_UPTIME     = "uptime";
    public static final String COL_P_ADDED_BY   = "added_by";

    // ── Ratings ───────────────────────────────────────────────────────────────
    public static final String TABLE_RATINGS     = "ratings";
    public static final String COL_RT_ID         = "id";
    public static final String COL_RT_PROV_ID    = "provider_id";
    public static final String COL_RT_COMMENT    = "comment";
    public static final String COL_RT_RATING     = "rating";
    public static final String COL_RT_DATE       = "date";
    public static final String COL_RT_RECOMMEND  = "recommend";
    public static final String COL_RT_USERNAME   = "username";

    // ── Favorites ─────────────────────────────────────────────────────────────
    public static final String TABLE_FAVORITES   = "favorites";
    public static final String COL_F_PROV_ID     = "provider_id";

    // ── User prefs ────────────────────────────────────────────────────────────
    public static final String TABLE_USER_PREFS  = "user_prefs";
    public static final String COL_UP_KEY        = "key";
    public static final String COL_UP_VALUE      = "value";

    // ── Users ─────────────────────────────────────────────────────────────────
    public static final String TABLE_USERS       = "users";
    public static final String COL_U_ID          = "id";
    public static final String COL_U_USERNAME    = "username";
    public static final String COL_U_PASSWORD    = "password_hash";

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ── Schema ────────────────────────────────────────────────────────────────

    @Override
    public void onCreate(SQLiteDatabase db) {
        createProvidersTables(db);

        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_U_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_U_USERNAME + " TEXT UNIQUE NOT NULL, "
                + COL_U_PASSWORD + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_USER_PREFS + " ("
                + COL_UP_KEY   + " TEXT PRIMARY KEY, "
                + COL_UP_VALUE + " TEXT)");

        seedProviders(db);
        seedRatings(db);
    }

    private void createProvidersTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PROVIDERS + " ("
                + COL_P_ID        + " INTEGER PRIMARY KEY, "
                + COL_P_NAME      + " TEXT NOT NULL, "
                + COL_P_REGION    + " TEXT NOT NULL, "
                + COL_P_NODE_URL  + " TEXT, "
                + COL_P_STORAGE   + " TEXT, "
                + COL_P_PEER_ID   + " TEXT, "
                + COL_P_IMAGE_URL + " TEXT, "
                + COL_P_LAT       + " REAL, "
                + COL_P_LNG       + " REAL, "
                + COL_P_PRICE     + " TEXT, "
                + COL_P_UPTIME    + " TEXT, "
                + COL_P_ADDED_BY  + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RATINGS + " ("
                + COL_RT_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RT_PROV_ID   + " INTEGER NOT NULL, "
                + COL_RT_COMMENT   + " TEXT, "
                + COL_RT_RATING    + " REAL, "
                + COL_RT_DATE      + " TEXT, "
                + COL_RT_RECOMMEND + " INTEGER, "
                + COL_RT_USERNAME  + " TEXT, "
                + "FOREIGN KEY(" + COL_RT_PROV_ID + ") REFERENCES "
                + TABLE_PROVIDERS + "(" + COL_P_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " ("
                + COL_F_PROV_ID + " INTEGER PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 7) {
            db.execSQL("DROP TABLE IF EXISTS reviews");
            db.execSQL("DROP TABLE IF EXISTS favorites");
            db.execSQL("DROP TABLE IF EXISTS restaurants");
            db.execSQL("DROP TABLE IF EXISTS ratings");
            db.execSQL("DROP TABLE IF EXISTS providers");
            createProvidersTables(db);
            seedProviders(db);
            seedRatings(db);
        }
    }

    // ── Seed data ─────────────────────────────────────────────────────────────

    private void seedProviders(SQLiteDatabase db) {
        insertProvider(db, 1,  "StoragePod EU-1",     "EU-West",       "https://node1.storagepod.eu",   "2 TB",  "QmY7a8bKzj9xLmN3pR5tW6vX", "0.020", "99.9", 51.5074, -0.1278);
        insertProvider(db, 2,  "CloudVault Berlin",   "EU-West",       "https://vault.cloudberlin.de",  "5 TB",  "QmR3n2pLx4qK8mJ7dF9hG1sA", "0.018", "99.7", 52.5200, 13.4050);
        insertProvider(db, 3,  "IronStore Paris",     "EU-West",       "https://iron.storeparis.fr",    "3 TB",  "QmT5k8mNz1wB6cD4eE2fH3iI", "0.022", "99.5", 48.8566,  2.3522);
        insertProvider(db, 4,  "BalticNode",          "EU-East",       "https://node.baltic.ee",        "1 TB",  "QmA9x2vKq7yZ0oP1rS4tU5uV", "0.015", "98.5", 59.4370, 24.7536);
        insertProvider(db, 5,  "EastCloud Warszawa",  "EU-East",       "https://cloud.eastpod.pl",      "3 TB",  "QmB6j3wCm8xQ9nO2pL7kJ5hH", "0.016", "99.1", 52.2297, 21.0122);
        insertProvider(db, 6,  "NovaPod NYC",         "NA",            "https://pod.novanyc.io",        "10 TB", "QmN1w4sJp8rG6fE3dC9bA7mM", "0.025", "99.8", 40.7128, -74.0060);
        insertProvider(db, 7,  "SteelVault Texas",    "NA",            "https://steel.vaultx.us",       "8 TB",  "QmD6h3bRm5tK2lI9jF7eE4cC", "0.022", "99.5", 30.2672, -97.7431);
        insertProvider(db, 8,  "NorthNode Toronto",   "NA",            "https://north.nodeca.io",       "4 TB",  "QmF2k9nPt3uH6gG8iI5jJ1kK", "0.023", "99.3", 43.6532, -79.3832);
        insertProvider(db, 9,  "AsiaPod Singapore",   "Asia-Pacific",  "https://pod.asiasng.sg",        "3 TB",  "QmG7m1rQs6vW9xX2yY4zA3bB", "0.019", "99.6",  1.3521, 103.8198);
        insertProvider(db, 10, "SouthStore Sao Paulo","South-America", "https://store.southpod.br",     "1 TB",  "QmI8p5cVy2dD4eE6fF7gG9hH", "0.017", "98.9", -23.5505, -46.6333);
    }

    private void insertProvider(SQLiteDatabase db, int id, String name, String region,
                                String nodeUrl, String storage, String peerId,
                                String price, String uptime, double lat, double lng) {
        ContentValues cv = new ContentValues();
        cv.put(COL_P_ID,       id);
        cv.put(COL_P_NAME,     name);
        cv.put(COL_P_REGION,   region);
        cv.put(COL_P_NODE_URL, nodeUrl);
        cv.put(COL_P_STORAGE,  storage);
        cv.put(COL_P_PEER_ID,  peerId);
        cv.put(COL_P_IMAGE_URL, "");
        cv.put(COL_P_PRICE,    price);
        cv.put(COL_P_UPTIME,   uptime);
        cv.put(COL_P_LAT,      lat);
        cv.put(COL_P_LNG,      lng);
        db.insert(TABLE_PROVIDERS, null, cv);
    }

    private void seedRatings(SQLiteDatabase db) {
        insertRating(db, 1,  "Viteza de transfer excelenta, latenta mica!",   5.0f, "2024-01-15", true);
        insertRating(db, 1,  "Foarte stabil, uptime 99.9% confirmat.",         4.5f, "2024-01-20", true);
        insertRating(db, 2,  "Pret competitiv si suport tehnic rapid.",        4.5f, "2024-02-01", true);
        insertRating(db, 2,  "Capacitate mare, ideal pentru backup.",          4.0f, "2024-02-10", true);
        insertRating(db, 3,  "Latenta buna in Europa de Vest.",                4.0f, "2024-02-15", true);
        insertRating(db, 4,  "Pret foarte bun, dar uptime sub asteptari.",     3.5f, "2024-03-01", true);
        insertRating(db, 5,  "Cel mai rapid nod din EU-East!",                 5.0f, "2024-03-05", true);
        insertRating(db, 5,  "Uptime 99.8%, impresionant pentru regiune.",     5.0f, "2024-03-08", true);
        insertRating(db, 6,  "Costisitor, dar performanta top din NA.",        3.5f, "2024-03-10", false);
        insertRating(db, 7,  "Performanta solida, pret rezonabil.",            4.0f, "2024-03-15", true);
        insertRating(db, 8,  "Cel mai bun nod din Asia-Pacific!",              5.0f, "2024-03-20", true);
        insertRating(db, 9,  "Transfer rapid catre Tokyo hub.",                4.5f, "2024-03-25", true);
        insertRating(db, 10, "Provider emergent, promitator pentru SA.",       3.5f, "2024-04-01", true);
    }

    private void insertRating(SQLiteDatabase db, int providerId, String comment,
                              float rating, String date, boolean recommend) {
        ContentValues cv = new ContentValues();
        cv.put(COL_RT_PROV_ID,   providerId);
        cv.put(COL_RT_COMMENT,   comment);
        cv.put(COL_RT_RATING,    rating);
        cv.put(COL_RT_DATE,      date);
        cv.put(COL_RT_RECOMMEND, recommend ? 1 : 0);
        db.insert(TABLE_RATINGS, null, cv);
    }

    // ── Public API — Providers ────────────────────────────────────────────────

    public long addProvider(Restaurant r) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_P_NAME,      r.name);
            cv.put(COL_P_REGION,    r.region);
            cv.put(COL_P_NODE_URL,  r.nodeUrl);
            cv.put(COL_P_STORAGE,   r.storageCapacity);
            cv.put(COL_P_PEER_ID,   r.peerId);
            cv.put(COL_P_IMAGE_URL, r.imageUrl);
            cv.put(COL_P_LAT,       r.latitude);
            cv.put(COL_P_LNG,       r.longitude);
            cv.put(COL_P_PRICE,     r.pricePerGB);
            cv.put(COL_P_UPTIME,    r.uptime);
            cv.put(COL_P_ADDED_BY,  r.addedBy);
            return db.insert(TABLE_PROVIDERS, null, cv);
        }
    }

    public List<Restaurant> getProviders() {
        List<Restaurant> list = new ArrayList<>();
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.query(TABLE_PROVIDERS, null, null, null, null, null, COL_P_NAME)) {
            while (c.moveToNext()) list.add(cursorToProvider(c));
        }
        return list;
    }

    public Restaurant getProviderById(int id) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.query(TABLE_PROVIDERS, null,
                     COL_P_ID + "=?", new String[]{String.valueOf(id)},
                     null, null, null)) {
            if (c.moveToFirst()) return cursorToProvider(c);
        }
        return null;
    }

    public List<Restaurant> getProvidersByUser(String username) {
        List<Restaurant> list = new ArrayList<>();
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.query(TABLE_PROVIDERS, null,
                     COL_P_ADDED_BY + "=?", new String[]{username},
                     null, null, COL_P_NAME)) {
            while (c.moveToNext()) list.add(cursorToProvider(c));
        }
        return list;
    }

    public boolean updateProvider(Restaurant r) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_P_NAME,      r.name);
            cv.put(COL_P_REGION,    r.region);
            cv.put(COL_P_NODE_URL,  r.nodeUrl);
            cv.put(COL_P_STORAGE,   r.storageCapacity);
            cv.put(COL_P_PEER_ID,   r.peerId);
            cv.put(COL_P_IMAGE_URL, r.imageUrl);
            cv.put(COL_P_LAT,       r.latitude);
            cv.put(COL_P_LNG,       r.longitude);
            cv.put(COL_P_PRICE,     r.pricePerGB);
            cv.put(COL_P_UPTIME,    r.uptime);
            int rows = db.update(TABLE_PROVIDERS, cv,
                    COL_P_ID + "=?", new String[]{String.valueOf(r.id)});
            return rows > 0;
        }
    }

    public boolean deleteProvider(int providerId) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.delete(TABLE_RATINGS,   COL_RT_PROV_ID + "=?", new String[]{String.valueOf(providerId)});
            db.delete(TABLE_FAVORITES, COL_F_PROV_ID  + "=?", new String[]{String.valueOf(providerId)});
            int rows = db.delete(TABLE_PROVIDERS, COL_P_ID + "=?", new String[]{String.valueOf(providerId)});
            return rows > 0;
        }
    }

    public List<Restaurant> getTopRatedProviders(int limit) {
        List<Restaurant> list = new ArrayList<>();
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.rawQuery(
                     "SELECT p.*, AVG(r." + COL_RT_RATING + ") AS avg_rating"
                             + " FROM " + TABLE_PROVIDERS + " p"
                             + " JOIN " + TABLE_RATINGS + " r ON p." + COL_P_ID + " = r." + COL_RT_PROV_ID
                             + " GROUP BY p." + COL_P_ID
                             + " ORDER BY avg_rating DESC"
                             + " LIMIT ?",
                     new String[]{String.valueOf(limit)})) {
            while (c.moveToNext()) list.add(cursorToProvider(c));
        }
        return list;
    }

    public float getAverageRating(int providerId) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.rawQuery(
                     "SELECT AVG(" + COL_RT_RATING + ") FROM " + TABLE_RATINGS
                             + " WHERE " + COL_RT_PROV_ID + "=?",
                     new String[]{String.valueOf(providerId)})) {
            if (c.moveToFirst() && !c.isNull(0)) return c.getFloat(0);
        }
        return 0f;
    }

    // ── Public API — Ratings ──────────────────────────────────────────────────

    public void addRating(Review review) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_RT_PROV_ID,   review.providerId);
            cv.put(COL_RT_COMMENT,   review.comment);
            cv.put(COL_RT_RATING,    review.rating);
            cv.put(COL_RT_DATE,      review.date);
            cv.put(COL_RT_RECOMMEND, review.recommend ? 1 : 0);
            cv.put(COL_RT_USERNAME,  review.username);
            db.insert(TABLE_RATINGS, null, cv);
        }
    }

    public List<Review> getRatingsForProvider(int providerId) {
        List<Review> list = new ArrayList<>();
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.query(TABLE_RATINGS, null,
                     COL_RT_PROV_ID + "=?", new String[]{String.valueOf(providerId)},
                     null, null, COL_RT_DATE + " DESC")) {
            while (c.moveToNext()) list.add(cursorToRating(c));
        }
        return list;
    }

    public List<Review> getRecentRatings(int limit) {
        List<Review> list = new ArrayList<>();
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.query(TABLE_RATINGS, null, null, null, null, null,
                     COL_RT_DATE + " DESC", String.valueOf(limit))) {
            while (c.moveToNext()) list.add(cursorToRating(c));
        }
        return list;
    }

    public List<Review> getRatingsByUser(String username) {
        List<Review> list = new ArrayList<>();
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.query(TABLE_RATINGS, null,
                     COL_RT_USERNAME + "=?", new String[]{username},
                     null, null, COL_RT_DATE + " DESC")) {
            while (c.moveToNext()) list.add(cursorToRating(c));
        }
        return list;
    }

    public boolean updateRating(Review review) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_RT_COMMENT,   review.comment);
            cv.put(COL_RT_RATING,    review.rating);
            cv.put(COL_RT_RECOMMEND, review.recommend ? 1 : 0);
            int rows = db.update(TABLE_RATINGS, cv,
                    COL_RT_ID + "=?", new String[]{String.valueOf(review.id)});
            return rows > 0;
        }
    }

    public boolean deleteRating(int ratingId) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            int rows = db.delete(TABLE_RATINGS,
                    COL_RT_ID + "=?", new String[]{String.valueOf(ratingId)});
            return rows > 0;
        }
    }

    // ── Autentificare ─────────────────────────────────────────────────────────

    public boolean registerUser(String username, String password) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_U_USERNAME, username);
            cv.put(COL_U_PASSWORD, hashPassword(password));
            return db.insert(TABLE_USERS, null, cv) != -1;
        }
    }

    public boolean loginUser(String username, String password) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.query(TABLE_USERS, null,
                     COL_U_USERNAME + "=? AND " + COL_U_PASSWORD + "=?",
                     new String[]{username, hashPassword(password)},
                     null, null, null)) {
            return c.moveToFirst();
        }
    }

    public boolean usernameExists(String username) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.query(TABLE_USERS, null,
                     COL_U_USERNAME + "=?", new String[]{username},
                     null, null, null)) {
            return c.moveToFirst();
        }
    }

    public boolean updateUsername(String oldUsername, String newUsername) {
        if (usernameExists(newUsername)) return false;
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_U_USERNAME, newUsername);
            int rows = db.update(TABLE_USERS, cv,
                    COL_U_USERNAME + "=?", new String[]{oldUsername});
            if (rows > 0) {
                setUserPref("logged_user", newUsername);
                setUserPref("user_name",   newUsername);
                return true;
            }
        }
        return false;
    }

    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        if (!loginUser(username, oldPassword)) return false;
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_U_PASSWORD, hashPassword(newPassword));
            int rows = db.update(TABLE_USERS, cv,
                    COL_U_USERNAME + "=?", new String[]{username});
            return rows > 0;
        }
    }

    public String getLoggedUser() {
        return getUserPref("logged_user", "");
    }

    public void setLoggedUser(String username) {
        setUserPref("logged_user", username);
        setUserPref("user_name", username);
    }

    public void logout() {
        setUserPref("logged_user", "");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    // ── Favorites ─────────────────────────────────────────────────────────────

    public boolean isFavorite(int providerId) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.query(TABLE_FAVORITES, null,
                     COL_F_PROV_ID + "=?", new String[]{String.valueOf(providerId)},
                     null, null, null)) {
            return c.moveToFirst();
        }
    }

    public void setFavorite(int providerId, boolean isFavorite) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            if (isFavorite) {
                ContentValues cv = new ContentValues();
                cv.put(COL_F_PROV_ID, providerId);
                db.replace(TABLE_FAVORITES, null, cv);
            } else {
                db.delete(TABLE_FAVORITES,
                        COL_F_PROV_ID + "=?", new String[]{String.valueOf(providerId)});
            }
        }
    }

    public List<Integer> getFavoriteIds() {
        List<Integer> ids = new ArrayList<>();
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.query(TABLE_FAVORITES, null, null, null, null, null, null)) {
            while (c.moveToNext()) ids.add(c.getInt(0));
        }
        return ids;
    }

    // ── User prefs ────────────────────────────────────────────────────────────

    public String getUserPref(String key, String defaultValue) {
        try (SQLiteDatabase db = getReadableDatabase();
             Cursor c = db.query(TABLE_USER_PREFS, new String[]{COL_UP_VALUE},
                     COL_UP_KEY + "=?", new String[]{key},
                     null, null, null)) {
            if (c.moveToFirst()) return c.getString(0);
        }
        return defaultValue;
    }

    public void setUserPref(String key, String value) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_UP_KEY,   key);
            cv.put(COL_UP_VALUE, value);
            db.replace(TABLE_USER_PREFS, null, cv);
        }
    }

    // ── Cursor helpers ────────────────────────────────────────────────────────

    private Restaurant cursorToProvider(Cursor c) {
        Restaurant r = new Restaurant(
                c.getInt(c.getColumnIndexOrThrow(COL_P_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_P_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_P_REGION)),
                c.getString(c.getColumnIndexOrThrow(COL_P_NODE_URL)),
                c.getString(c.getColumnIndexOrThrow(COL_P_STORAGE)),
                c.getString(c.getColumnIndexOrThrow(COL_P_PEER_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_P_IMAGE_URL)),
                c.getDouble(c.getColumnIndexOrThrow(COL_P_LAT)),
                c.getDouble(c.getColumnIndexOrThrow(COL_P_LNG))
        );
        int priceIdx  = c.getColumnIndex(COL_P_PRICE);
        if (priceIdx  != -1 && !c.isNull(priceIdx))  r.pricePerGB  = c.getString(priceIdx);
        int uptimeIdx = c.getColumnIndex(COL_P_UPTIME);
        if (uptimeIdx != -1 && !c.isNull(uptimeIdx)) r.uptime      = c.getString(uptimeIdx);
        int addedIdx  = c.getColumnIndex(COL_P_ADDED_BY);
        if (addedIdx  != -1 && !c.isNull(addedIdx))  r.addedBy     = c.getString(addedIdx);
        return r;
    }

    private Review cursorToRating(Cursor c) {
        return new Review(
                c.getInt(c.getColumnIndexOrThrow(COL_RT_ID)),
                c.getInt(c.getColumnIndexOrThrow(COL_RT_PROV_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_RT_COMMENT)),
                c.getFloat(c.getColumnIndexOrThrow(COL_RT_RATING)),
                c.getString(c.getColumnIndexOrThrow(COL_RT_DATE)),
                c.getInt(c.getColumnIndexOrThrow(COL_RT_RECOMMEND)) == 1,
                c.getString(c.getColumnIndexOrThrow(COL_RT_USERNAME))
        );
    }
}
