package com.example.starsgallery.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.starsgallery.R;
import com.example.starsgallery.beans.Celebrity;
import com.example.starsgallery.service.CelebrityManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CelebrityDisplayAdapter extends RecyclerView.Adapter<CelebrityDisplayAdapter.CelebrityViewHolder> implements Filterable {

    private List<Celebrity> sourceList;
    private List<Celebrity> filteredList;
    private Context mContext;
    private SearchFilter searchFilter;

    public CelebrityDisplayAdapter(Context context, List<Celebrity> celebs) {
        this.mContext = context;
        this.sourceList = celebs;
        this.filteredList = new ArrayList<>(celebs);
        this.searchFilter = new SearchFilter(this);
    }

    @NonNull
    @Override
    public CelebrityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.star_item, parent, false);
        final CelebrityViewHolder holder = new CelebrityViewHolder(layoutView);

        holder.itemView.setOnClickListener(v -> {
            View editView = LayoutInflater.from(mContext).inflate(R.layout.star_edit_item, null, false);

            final ImageView profileImg = editView.findViewById(R.id.img);
            final RatingBar ratingControl = editView.findViewById(R.id.ratingBar);
            final TextView hiddenId = editView.findViewById(R.id.idss);

            Bitmap drawableBitmap = ((BitmapDrawable) ((ImageView) v.findViewById(R.id.img)).getDrawable()).getBitmap();
            profileImg.setImageBitmap(drawableBitmap);
            ratingControl.setRating(((RatingBar) v.findViewById(R.id.stars)).getRating());
            hiddenId.setText(((TextView) v.findViewById(R.id.ids)).getText().toString());

            new AlertDialog.Builder(mContext)
                    .setTitle("Update Rating")
                    .setMessage("Set a new score for this profile:")
                    .setView(editView)
                    .setPositiveButton("Save", (dialog, which) -> {
                        float newScore = ratingControl.getRating();
                        int targetId = Integer.parseInt(hiddenId.getText().toString());
                        Celebrity targetCeleb = CelebrityManager.getVault().getById(targetId);
                        if (targetCeleb != null) {
                            targetCeleb.setScore(newScore);
                            CelebrityManager.getVault().modify(targetCeleb);
                            notifyItemChanged(holder.getAdapterPosition());
                        }
                    })
                    .setNegativeButton("Dismiss", null)
                    .show();
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CelebrityViewHolder holder, int position) {
        Celebrity item = filteredList.get(position);
        
        Glide.with(mContext)
                .asBitmap()
                .load(item.getImagePath())
                .apply(new RequestOptions().override(120, 120))
                .into(holder.profilePic);

        holder.displayName.setText(item.getFullName().toUpperCase());
        holder.scoreIndicator.setRating(item.getScore());
        holder.metaId.setText(String.valueOf(item.getRecordId()));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    public static class CelebrityViewHolder extends RecyclerView.ViewHolder {
        TextView metaId, displayName;
        CircleImageView profilePic;
        RatingBar scoreIndicator;

        public CelebrityViewHolder(@NonNull View itemView) {
            super(itemView);
            metaId = itemView.findViewById(R.id.ids);
            profilePic = itemView.findViewById(R.id.img);
            displayName = itemView.findViewById(R.id.name);
            scoreIndicator = itemView.findViewById(R.id.stars);
        }
    }

    private class SearchFilter extends Filter {
        private final CelebrityDisplayAdapter adapterReference;

        public SearchFilter(CelebrityDisplayAdapter adapter) {
            this.adapterReference = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Celebrity> results = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                results.addAll(sourceList);
            } else {
                String pattern = constraint.toString().toLowerCase().trim();
                for (Celebrity c : sourceList) {
                    if (c.getFullName().toLowerCase().contains(pattern)) {
                        results.add(c);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = results;
            filterResults.count = results.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList.clear();
            filteredList.addAll((List<Celebrity>) results.values);
            adapterReference.notifyDataSetChanged();
        }
    }
}
