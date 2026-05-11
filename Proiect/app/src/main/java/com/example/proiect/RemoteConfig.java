package com.example.proiect;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RemoteConfig {

    private static final String TAG = "RemoteConfig";
    private static final String GIST_URL =
            "https://gist.githubusercontent.com/DianaNegut/7540a026e6fe5375badfd4ac015ffd8b/raw/cloudpod_config.json";

    private static final Map<String, String> defaultImages = new HashMap<>();

    public interface Callback {
        void onLoaded();
    }

    public static void fetch(Callback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(GIST_URL);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(url.openStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                Log.d(TAG, "JSON primit: " + sb.toString());

                JSONObject root   = new JSONObject(sb.toString());
                JSONObject images = root.getJSONObject("defaultImages");
                for (Iterator<String> it = images.keys(); it.hasNext(); ) {
                    String region = it.next();
                    defaultImages.put(region, images.getString(region));
                    Log.d(TAG, "Incarcat: " + region + " -> " + images.getString(region));
                }
            } catch (Exception e) {
                Log.e(TAG, "Eroare fetch: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }

            new Handler(Looper.getMainLooper()).post(callback::onLoaded);
        }).start();
    }

    public static String getImageForRegion(String region) {
        return defaultImages.get(region);
    }
}
