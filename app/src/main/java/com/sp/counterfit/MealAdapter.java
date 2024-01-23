package com.sp.counterfit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    // Example data structure for meal items
    private List<MealItem> mealItems;

    // Constructor
    public MealAdapter(List<MealItem> mealItems) {
        this.mealItems = mealItems;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealItem currentItem = mealItems.get(position);
        holder.mealImageView.setImageResource(currentItem.getImageResourceId());
        holder.mealNameTextView.setText(currentItem.getMealName());
        holder.calorieTextView.setText(currentItem.getCalorieCount() + " Calories");
    }

    @Override
    public int getItemCount() {
        return mealItems.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImageView;
        TextView mealNameTextView;
        TextView calorieTextView;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImageView = itemView.findViewById(R.id.mealImageView);
            mealNameTextView = itemView.findViewById(R.id.mealNameTextView);
            calorieTextView = itemView.findViewById(R.id.calorieTextView);
        }
    }
}
