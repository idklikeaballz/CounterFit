package com.sp.counterfit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class MealHistoryAdapter extends RecyclerView.Adapter<MealHistoryAdapter.ViewHolder> {
    private List<History.MealEntry> mealEntries;

    MealHistoryAdapter(List<History.MealEntry> mealEntries) {
        this.mealEntries = mealEntries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_history, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        History.MealEntry mealEntry = mealEntries.get(position);
        holder.mealNameTextView.setText(mealEntry.getName());
        holder.caloriesTextView.setText(String.valueOf(mealEntry.getCalories()));
        holder.dateTextView.setText(mealEntry.getDate());
    }

    @Override
    public int getItemCount() {
        return mealEntries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mealNameTextView;
        TextView caloriesTextView;
        TextView dateTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mealNameTextView = itemView.findViewById(R.id.mealNameTextView);
            caloriesTextView = itemView.findViewById(R.id.caloriesTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}