package com.example.lab9;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomAdapter extends ArrayAdapter<Disciplina> {

    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public CustomAdapter(Context context, List<Disciplina> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Disciplina item = getItem(position);
        holder.textView.setText(item.getDescription());
        holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        holder.imageView.setTag(item.getImageUrl());

        final String imageUrl = item.getImageUrl();
        final ImageView imageViewRef = holder.imageView;

        executor.execute(() -> {
            Bitmap bitmap = loadBitmap(imageUrl);
            mainHandler.post(() -> {
                if (imageUrl.equals(imageViewRef.getTag()) && bitmap != null) {
                    imageViewRef.setImageBitmap(bitmap);
                }
            });
        });

        return convertView;
    }

    private Bitmap loadBitmap(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setInstanceFollowRedirects(true);
            connection.connect();
            int code = connection.getResponseCode();
            Log.d("CustomAdapter", "URL: " + urlString + " -> code: " + code);
            if (code == HttpURLConnection.HTTP_OK) {
                InputStream input = connection.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(input);
                Log.d("CustomAdapter", "Bitmap: " + bmp);
                return bmp;
            }
            return null;
        } catch (Exception e) {
            Log.e("CustomAdapter", "Error: " + urlString, e);
            return null;
        }
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
