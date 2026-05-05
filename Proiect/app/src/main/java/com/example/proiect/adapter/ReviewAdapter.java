package com.example.proiect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.proiect.R;
import com.example.proiect.database.DatabaseHelper;
import com.example.proiect.model.Restaurant;
import com.example.proiect.model.Review;

import java.util.List;

public class ReviewAdapter extends BaseAdapter {

    public interface OnReviewActionListener {
        void onEdit(Review review);
        void onDelete(Review review);
    }

    private final Context context;
    private final List<Review> ratings;
    private final boolean showProviderName;
    private final LayoutInflater inflater;
    private final DatabaseHelper db;
    private OnReviewActionListener actionListener;

    public ReviewAdapter(Context context, List<Review> ratings, boolean showProviderName) {
        this.context          = context;
        this.ratings          = ratings;
        this.showProviderName = showProviderName;
        this.inflater         = LayoutInflater.from(context);
        this.db               = DatabaseHelper.getInstance(context);
    }

    public void setOnReviewActionListener(OnReviewActionListener listener) {
        this.actionListener = listener;
    }

    @Override public int getCount()            { return ratings.size(); }
    @Override public Review getItem(int pos)   { return ratings.get(pos); }
    @Override public long getItemId(int pos)   { return ratings.get(pos).id; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_review, parent, false);
            holder = new ViewHolder();
            holder.tvProviderName = convertView.findViewById(R.id.tvRestaurantName);
            holder.ratingBar      = convertView.findViewById(R.id.ratingBarReview);
            holder.tvDate         = convertView.findViewById(R.id.tvDate);
            holder.tvComment      = convertView.findViewById(R.id.tvComment);
            holder.tvRecommend    = convertView.findViewById(R.id.tvRecommend);
            holder.layoutActions  = convertView.findViewById(R.id.layoutActions);
            holder.btnEdit        = convertView.findViewById(R.id.btnEditReview);
            holder.btnDelete      = convertView.findViewById(R.id.btnDeleteReview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Review review = ratings.get(position);
        holder.ratingBar.setRating(review.rating);
        holder.tvDate.setText(review.date);
        holder.tvComment.setText(review.comment);
        holder.tvRecommend.setText(review.recommend ? "Recomand" : "Nu recomand");
        holder.tvRecommend.setTextColor(review.recommend
                ? context.getColor(R.color.colorPrimary)
                : context.getColor(R.color.colorTextSecondary));

        if (showProviderName) {
            Restaurant provider = db.getProviderById(review.providerId);
            if (provider != null) {
                holder.tvProviderName.setVisibility(View.VISIBLE);
                holder.tvProviderName.setText(provider.name);
            }
        } else {
            holder.tvProviderName.setVisibility(View.GONE);
        }

        if (actionListener != null) {
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> actionListener.onEdit(review));
            holder.btnDelete.setOnClickListener(v -> actionListener.onDelete(review));
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvProviderName, tvDate, tvComment, tvRecommend;
        RatingBar ratingBar;
        View layoutActions;
        Button btnEdit, btnDelete;
    }
}
