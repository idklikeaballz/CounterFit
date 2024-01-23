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
    private List<MealItem> mealItems;
    private OnMealClickListener listener;

    // Interface for click events
    public interface OnMealClickListener {
        void onMealClick(MealItem mealItem);
    }

    // Constructor with listener
    public MealAdapter(List<MealItem> mealItems, OnMealClickListener listener) {
        this.mealItems = mealItems;
        this.listener = listener;
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
        holder.bind(currentItem, listener);
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

        public void bind(final MealItem mealItem, final OnMealClickListener listener) {
            mealImageView.setImageResource(mealItem.getImageResourceId());
            mealNameTextView.setText(mealItem.getMealName());
            calorieTextView.setText(mealItem.getCalorieCount() + " Calories");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onMealClick(mealItem);
                    }
                }
            });
        }
    }
}