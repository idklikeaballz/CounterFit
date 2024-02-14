package com.sp.counterfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class History extends AppCompatActivity implements MealHistoryAdapter.OnMealRemovedListener {
    private RecyclerView recyclerViewMealHistory;
    private LineChart chart;
    private MealHistoryAdapter mealHistoryAdapter;
    private SignUpHelper signUpHelper;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        initializeComponents();
        setupBottomNavigationView();
        setupWeightGraph();
        loadMealHistory();
    }

    private void initializeComponents() {
        recyclerViewMealHistory = findViewById(R.id.recyclerViewMealHistory);
        chart = findViewById(R.id.chart);
        signUpHelper = new SignUpHelper(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation_history);

        recyclerViewMealHistory.setLayoutManager(new LinearLayoutManager(this));
        mealHistoryAdapter = new MealHistoryAdapter(new ArrayList<>(), this);
        recyclerViewMealHistory.setAdapter(mealHistoryAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_history);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }
    }

    @Override
    public void onMealRemoved(MealHistoryItem meal, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Meal");
        builder.setMessage("Are you sure you want to remove this meal?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            int userId = signUpHelper.getCurrentUserId();
            if (signUpHelper.removeMeal(userId, meal.getId())) {
                mealHistoryAdapter.notifyItemRemoved(position);
                Toast.makeText(History.this, "Meal removed successfully", Toast.LENGTH_SHORT).show();
                mealHistoryAdapter.removeMealAt(position);
            } else {
                Toast.makeText(History.this, "Error removing meal", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupWeightGraph() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 70f));
        entries.add(new Entry(2, 71f));
        entries.add(new Entry(3, 72f));
        // Continue adding entries...

        LineDataSet dataSet = new LineDataSet(entries, "Weight (KG)");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // Refresh the chart
        customizeXAxis();
    }

    private void customizeXAxis() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // one week
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setAxisLineWidth(2f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "Week " + (int) value;
            }
        });
    }

    private void loadMealHistory() {
        int userId = signUpHelper.getCurrentUserId();
        if (userId != -1) {
            List<MealHistoryItem> mealHistoryList = signUpHelper.getMealHistoryByUserId(userId);
            mealHistoryAdapter.updateMealHistoryList(mealHistoryList);
            mealHistoryAdapter.notifyDataSetChanged(); // Notify the adapter
            // Optionally update the weight graph
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNavigationView() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.bot_home) {
                        Intent intent = new Intent(History.this, Main.class);
                        item.setCheckable(true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;
                    } else if (id==R.id.bot_gym) {
                        Intent intent = new Intent(History.this, Gym.class);
                        item.setCheckable(true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;
                    }else if (id == R.id.bot_food) {
                        Intent intent = new Intent(History.this, Food.class);
                        item.setCheckable(true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });
            unselectBottomNavigationViewItems();


        } else {
            Log.e("AboutActivity", "BottomNavigationView not found in the layout.");
        }
    }
    private void unselectBottomNavigationViewItems() {
        // We can loop through all menu items and uncheck them
        int size = bottomNavigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            bottomNavigationView.getMenu().getItem(i).setCheckable(false);
        }
    }



}

