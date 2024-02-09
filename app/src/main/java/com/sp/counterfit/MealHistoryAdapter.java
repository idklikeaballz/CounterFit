package com.sp.counterfit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MealHistoryAdapter extends RecyclerView.Adapter<MealHistoryAdapter.ViewHolder> {

    private List<MealHistoryItem> mealHistoryList;
    private OnMealRemovedListener listener; // Declare the listener

    // Constructor
    public MealHistoryAdapter(List<MealHistoryItem> mealHistoryList, OnMealRemovedListener listener) {
        this.mealHistoryList = mealHistoryList;
        this.listener = listener;
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

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                MealHistoryItem meal = mealHistoryList.get(adapterPosition);
                if (listener != null) {
                    listener.onMealRemoved(meal, adapterPosition); // Pass position back to activity
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealHistoryList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMealName, textViewCalories, textViewDate;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMealName = itemView.findViewById(R.id.textViewMealName);
            textViewCalories = itemView.findViewById(R.id.textViewCalories);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
    public void removeMealAt(int position) {
        if (position >= 0 && position < mealHistoryList.size()) {
            mealHistoryList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mealHistoryList.size());
        }
    }

    // Method to update the list of meal history items and notify the adapter of the change.
    public void updateMealHistoryList(List<MealHistoryItem> newMealHistoryList) {
        this.mealHistoryList.clear();
        this.mealHistoryList.addAll(newMealHistoryList);
        notifyDataSetChanged(); // Notify any registered observers that the data set has changed.
    }


    // Interface definition for a callback to be invoked when a meal is removed.
    public interface OnMealRemovedListener {
        void onMealRemoved(MealHistoryItem meal, int position);
    }

    // Method to set the listener
    public void setOnMealRemovedListener(OnMealRemovedListener listener) {
        this.listener = listener;
    }
}
