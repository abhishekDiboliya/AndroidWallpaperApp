package com.developerabhi123example.allinonewallpapers.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developerabhi123example.allinonewallpapers.R;
import com.developerabhi123example.allinonewallpapers.activities.WallpapersActivity;
import com.developerabhi123example.allinonewallpapers.models.category;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private Context mctx;
    private List<category>categoryList;

    public CategoriesAdapter(Context mctx, List<category> categoryList) {
        this.mctx = mctx;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mctx).inflate(R.layout.recyclerview_categories,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {

        category c = categoryList.get(position);
        holder.textView.setText(c.name);
        Glide.with(mctx)
                .load(c.thumb)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        ImageView imageView;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_view_cat_name);
            imageView = itemView.findViewById(R.id.image_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            category c = categoryList.get(position);

            Intent intent = new Intent(mctx,WallpapersActivity.class);
            intent.putExtra("category",c.name);

            mctx.startActivity(intent);
        }
    }

}
