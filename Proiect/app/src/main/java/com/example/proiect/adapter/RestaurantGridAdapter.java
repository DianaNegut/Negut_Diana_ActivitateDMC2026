package com.example.proiect.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.util.Log;
import com.bumptech.glide.Glide;
import com.example.proiect.R;
import com.example.proiect.RemoteConfig;
import com.example.proiect.database.DatabaseHelper;
import com.example.proiect.model.Restaurant;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RestaurantGridAdapter extends BaseAdapter {

    private static final String[] BADGES = {"🥇", "🥈", "🥉"};

    private final Context context;
    private List<Restaurant> providers;
    private final LayoutInflater inflater;
    private final DatabaseHelper db;
    private final Map<Integer, String> badgeMap = new HashMap<>();
    private int selectedId = -1;

    public RestaurantGridAdapter(Context context, List<Restaurant> providers, DatabaseHelper db) {
        this.context   = context;
        this.providers = providers;
        this.inflater  = LayoutInflater.from(context);
        this.db        = db;
        loadTopBadges();
    }

    private void loadTopBadges() {
        badgeMap.clear();
        List<Restaurant> top = db.getTopRatedProviders(3);
        for (int i = 0; i < top.size(); i++) {
            badgeMap.put(top.get(i).id, BADGES[i]);
        }
    }

    public void updateData(List<Restaurant> newData) {
        this.providers = newData;
        notifyDataSetChanged();
    }

    public void setSelectedId(int id) {
        this.selectedId = id;
        notifyDataSetChanged();
    }

    @Override public int getCount()              { return providers.size(); }
    @Override public Restaurant getItem(int pos) { return providers.get(pos); }
    @Override public long getItemId(int pos)     { return providers.get(pos).id; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_restaurant_grid, parent, false);
            holder = new ViewHolder();
            holder.card             = convertView.findViewById(R.id.cardView);
            holder.imagePlaceholder = convertView.findViewById(R.id.imagePlaceholder);
            holder.ivPhoto          = convertView.findViewById(R.id.ivRestaurantPhoto);
            holder.tvEmoji          = convertView.findViewById(R.id.tvCategoryEmoji);
            holder.tvChip           = convertView.findViewById(R.id.tvCategoryChip);
            holder.tvName           = convertView.findViewById(R.id.tvRestaurantName);
            holder.tvAddress        = convertView.findViewById(R.id.tvAddress);
            holder.tvRating              = convertView.findViewById(R.id.tvRatingValue);
            holder.tvBadge               = convertView.findViewById(R.id.tvBadge);
            holder.viewSelectionOverlay  = convertView.findViewById(R.id.viewSelectionOverlay);
            holder.tvSelectionCheck      = convertView.findViewById(R.id.tvSelectionCheck);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Restaurant r = providers.get(position);
        int regionColor = getRegionColor(r.region);

        holder.imagePlaceholder.setBackgroundColor(regionColor);

        boolean hasPhoto = r.imageUrl != null && !r.imageUrl.isEmpty()
                && new File(r.imageUrl).exists();
        if (hasPhoto) {
            Bitmap bmp = decodeSampledBitmap(r.imageUrl, 400, 260);
            holder.ivPhoto.setImageBitmap(bmp);
            holder.ivPhoto.setVisibility(View.VISIBLE);
            holder.tvEmoji.setVisibility(View.GONE);
        } else {
            String remoteUrl = RemoteConfig.getImageForRegion(r.region);
            Log.d("RemoteConfig", "Adapter [" + r.name + "] region=" + r.region + " remoteUrl=" + remoteUrl);
            if (remoteUrl != null) {
                holder.ivPhoto.setVisibility(View.VISIBLE);
                holder.tvEmoji.setVisibility(View.GONE);
                Glide.with(context)
                        .load(remoteUrl)
                        .centerCrop()
                        .into(holder.ivPhoto);
            } else {
                holder.ivPhoto.setImageBitmap(null);
                holder.ivPhoto.setVisibility(View.GONE);
                holder.tvEmoji.setVisibility(View.VISIBLE);
                holder.tvEmoji.setText("☁");
            }
        }

        holder.tvChip.setText(r.region);
        holder.tvChip.setTextColor(regionColor);
        holder.tvName.setText(r.name);
        holder.tvAddress.setText(r.nodeUrl);

        float avg = db.getAverageRating(r.id);
        holder.tvRating.setText(avg > 0
                ? String.format(Locale.getDefault(), "%.1f", avg) : "—");

        boolean selected = r.id == selectedId;
        holder.viewSelectionOverlay.setVisibility(selected ? View.VISIBLE : View.GONE);
        holder.tvSelectionCheck.setVisibility(selected ? View.VISIBLE : View.GONE);

        String badge = badgeMap.get(r.id);
        if (badge != null) {
            holder.tvBadge.setText(badge);
            holder.tvBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvBadge.setVisibility(View.GONE);
        }

        ((com.google.android.material.card.MaterialCardView) holder.card)
                .setStrokeColor(regionColor);

        return convertView;
    }

    private int getRegionColor(String region) {
        if (region == null) return Color.parseColor("#607D8B");
        switch (region) {
            case "EU-West":       return Color.parseColor("#1E88E5");
            case "EU-East":       return Color.parseColor("#00695C");
            case "NA":            return Color.parseColor("#4527A0");
            case "Asia-Pacific":  return Color.parseColor("#E65100");
            case "South-America": return Color.parseColor("#2E7D32");
            default:              return Color.parseColor("#607D8B");
        }
    }

    private Bitmap decodeSampledBitmap(String path, int reqW, int reqH) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int inSampleSize = 1;
        if (opts.outHeight > reqH || opts.outWidth > reqW) {
            int halfH = opts.outHeight / 2, halfW = opts.outWidth / 2;
            while ((halfH / inSampleSize) >= reqH && (halfW / inSampleSize) >= reqW)
                inSampleSize *= 2;
        }
        opts.inSampleSize = inSampleSize;
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, opts);
    }

    private static class ViewHolder {
        View card;
        View imagePlaceholder;
        View viewSelectionOverlay;
        ImageView ivPhoto;
        TextView tvEmoji, tvChip, tvName, tvAddress, tvRating, tvBadge, tvSelectionCheck;
    }
}
