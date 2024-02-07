package com.sp.counterfit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {
    private RecyclerView recyclerViewMealHistory;
    private LineChart chart;
    private MealHistoryAdapter mealHistoryAdapter;
    private SignUpHelper signUpHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        recyclerViewMealHistory = findViewById(R.id.recyclerViewMealHistory);
        chart = findViewById(R.id.chart);
        signUpHelper = new SignUpHelper(this);


        recyclerViewMealHistory.setLayoutManager(new LinearLayoutManager(this));
        mealHistoryAdapter = new MealHistoryAdapter(new ArrayList<>());
        recyclerViewMealHistory.setAdapter(mealHistoryAdapter);

        // Set up the weight graph (LineChart)
        setupWeightGraph();

        // Load the meal history data
        loadMealHistory();
    }

    private void setupWeightGraph() {
        // Set up the LineChart using MPAndroidChart
        // (Fake data for demonstration. Replace with your own data source)
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 70f));
        entries.add(new Entry(2, 71f));
        entries.add(new Entry(3, 72f));
        // ... Add more entries

        LineDataSet dataSet = new LineDataSet(entries, "Weight (KG)");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh the chart
    }

    private void loadMealHistory() {
        int userId = signUpHelper.getCurrentUserId();
        if (userId != -1) {
            List<MealHistoryItem> mealHistoryList = signUpHelper.getMealHistoryByUserId(userId);
            Log.d("History", "Loaded meal history with " + mealHistoryList.size() + " items");
            mealHistoryAdapter.updateMealHistoryList(mealHistoryList);
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }

}

