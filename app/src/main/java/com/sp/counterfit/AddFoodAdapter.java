package com.sp.counterfit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AddFoodAdapter extends RecyclerView.Adapter<AddFoodAdapter.FoodViewHolder> {
    private List<FoodItem> foodItemList;
    private Context context;

    // Constructor
    public AddFoodAdapter(Context context, List<FoodItem> foodItemList, OnFoodItemClickListener listener) {
        this.context = context;
        this.foodItemList = foodItemList;
        this.listener = listener;
    }
    public interface OnFoodItemClickListener {
        void onAddMealClick(FoodItem foodItem);
        void onUpdateMealClick(FoodItem foodItem);
    }
    private OnFoodItemClickListener listener;

    // ViewHolder class that holds references to the views for each food item
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        public ImageView foodImage;
        public TextView foodName;
        public TextView foodCalories;

        public FoodViewHolder(View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.mealImageView);
            foodName = itemView.findViewById(R.id.mealNameTextView);
            foodCalories = itemView.findViewById(R.id.caloriesTextView);
        }
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_meal, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);
        holder.foodName.setText(foodItem.getName());
        holder.foodCalories.setText(String.valueOf(foodItem.getCalories()) + " Calories");
        holder.itemView.setOnClickListener(v -> showOptionsDialog(foodItem));


        // Use Glide to load the food image
        Glide.with(context)
                .load(foodItem.getImageUrl())
                .into(holder.foodImage);
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    // Method to update the list of food items
    public void updateFoodItems(List<FoodItem> foodItems) {
        this.foodItemList = foodItems;
        notifyDataSetChanged();
    }
    private void showOptionsDialog(FoodItem foodItem) {
        CharSequence[] options = {"Add Meal", "Update"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Add Meal")) {
                listener.onAddMealClick(foodItem);
            } else if (options[item].equals("Update")) {
                listener.onUpdateMealClick(foodItem);
            }
        });
        builder.show();
    }
}
