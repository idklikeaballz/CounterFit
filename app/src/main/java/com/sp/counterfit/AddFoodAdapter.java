package com.sp.counterfit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AddFoodAdapter extends RecyclerView.Adapter<AddFoodAdapter.FoodViewHolder> {
    private List<FoodItem> foodItemList; // List of food items to display
    private final Context context; // Context for inflating layouts and Glide usage
    private final OnFoodItemClickListener listener; // Listener for click events

    // Constructor
    public AddFoodAdapter(Context context, List<FoodItem> foodItemList, OnFoodItemClickListener listener) {
        this.context = context;
        this.foodItemList = foodItemList;
        this.listener = listener;
    }

    // Interface for click events
    public interface OnFoodItemClickListener {
        void onAddMealClick(FoodItem foodItem);
        void onUpdateMealClick(FoodItem foodItem);
    }

    // ViewHolder class
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        public final ImageView foodImage;
        public final TextView foodName;
        public final TextView foodCalories;

        public FoodViewHolder(View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.mealImageView);
            foodName = itemView.findViewById(R.id.mealNameTextView);
            foodCalories = itemView.findViewById(R.id.caloriesTextView);
        }
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_meal, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);
        holder.foodName.setText(foodItem.getName());
        holder.foodCalories.setText(String.format("%d Calories", foodItem.getCalories()));

        // Use Glide to load the food image. Consider adding a placeholder for better UX
        Glide.with(context)
                .load(foodItem.getImageUrl())
                .into(holder.foodImage);

        holder.itemView.setOnClickListener(v -> showOptionsDialog(foodItem));
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    // Update food items list and refresh RecyclerView
    public void updateFoodItems(List<FoodItem> foodItems) {
        this.foodItemList = foodItems;
        notifyDataSetChanged();
    }

    // Show dialog for add or update meal options
    private void showOptionsDialog(FoodItem foodItem) {
        CharSequence[] options = {"Add Meal", "Update"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(foodItem.getName());
        builder.setItems(options, (dialog, item) -> {
            if ("Add Meal".equals(options[item])) {
                listener.onAddMealClick(foodItem);
            } else if ("Update".equals(options[item])) {
                listener.onUpdateMealClick(foodItem);
            }
        });
        builder.show();
    }
}
