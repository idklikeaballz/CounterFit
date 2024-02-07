package com.sp.counterfit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MealHistoryAdapter extends RecyclerView.Adapter<MealHistoryAdapter.ViewHolder> {

    private List<MealHistoryItem> mealHistoryList;

    public MealHistoryAdapter(List<MealHistoryItem> mealHistoryList) {
        this.mealHistoryList = mealHistoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MealHistoryItem item = mealHistoryList.get(position);
        holder.textViewMealName.setText(item.getFoodName());
        holder.textViewCalories.setText(item.getCalories() + " Calories");
        holder.textViewDate.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return mealHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMealName, textViewCalories, textViewDate;

        public ViewHolder(View itemView) { // Corrected the constructor name here
            super(itemView);
            textViewMealName = itemView.findViewById(R.id.textViewMealName);
            textViewCalories = itemView.findViewById(R.id.textViewCalories);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }

    // Method to update the list of meal history items and notify the adapter of the change.
    public void updateMealHistoryList(List<MealHistoryItem> newMealHistoryList) {
        mealHistoryList = newMealHistoryList;
        notifyDataSetChanged();
    }
}
