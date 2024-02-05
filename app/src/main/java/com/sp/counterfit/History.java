package com.sp.counterfit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {

    private LineChart weightChart;
    private RecyclerView mealHistoryRecyclerView;
    private MealHistoryAdapter mealHistoryAdapter;
    private SignUpHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        dbHelper = new SignUpHelper(this);
        weightChart = findViewById(R.id.weightChart);
        mealHistoryRecyclerView = findViewById(R.id.mealHistoryRecyclerView);

        setupWeightChart();
        setupMealHistoryList();
    }

    private void setupWeightChart() {
        // You will need to create this method in SignUpHelper to get weight entries
        List<WeightEntry> weightEntries = dbHelper.getWeightEntries();

        List<Entry> chartEntries = new ArrayList<>();
        for (WeightEntry entry : weightEntries) {
            chartEntries.add(new Entry(entry.getDate(), (float) entry.getWeight()));
        }

        LineDataSet dataSet = new LineDataSet(chartEntries, "Weight Over Time");
        LineData lineData = new LineData(dataSet);
        weightChart.setData(lineData);
        weightChart.invalidate(); // Refresh the chart
    }

    private void setupMealHistoryList() {
        // You will need to create this method in SignUpHelper to get meal entries
        List<MealEntry> mealEntries = dbHelper.getMealEntries();

        mealHistoryAdapter = new MealHistoryAdapter(mealEntries);
        mealHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mealHistoryRecyclerView.setAdapter(mealHistoryAdapter);
    }

    // Inner class for adapter (Placeholder for actual adapter implementation)
    class MealHistoryAdapter extends RecyclerView.Adapter<MealHistoryAdapter.ViewHolder> {
        private List<MealEntry> mealEntries;

        MealHistoryAdapter(List<MealEntry> mealEntries) {
            this.mealEntries = mealEntries;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate your item layout and return the ViewHolder
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // Bind your data to the views here
        }

        @Override
        public int getItemCount() {
            return mealEntries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            // Define your item views here

            ViewHolder(View itemView) {
                super(itemView);
                // Initialize your item views here
            }
        }
    }

    // Placeholder classes to represent data entries
    class WeightEntry {
        private long date; // Consider using a Date or Calendar object
        private double weight;

        public WeightEntry(long date, double weight) {
            this.date = date;
            this.weight = weight;
        }

        public long getDate() {
            return date;
        }
        public double getWeight() {
            return weight;
        }
    }

    class MealEntry {
        private String name;
        private int calories;
        private String date;

        public MealEntry(String name, int calories, String date) {
            this.name = name;
            this.calories = calories;
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public int getCalories() {
            return calories;
        }

        public String getDate() {
            return date;
        }
    }
}


