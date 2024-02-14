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
    private static final int NUM_WEEKS = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        recyclerViewMealHistory = findViewById(R.id.recyclerViewMealHistory);
        chart = findViewById(R.id.chart);
        signUpHelper = new SignUpHelper(this);


        recyclerViewMealHistory.setLayoutManager(new LinearLayoutManager(this));
        mealHistoryAdapter = new MealHistoryAdapter(new ArrayList<>(), this);
        recyclerViewMealHistory.setAdapter(mealHistoryAdapter);
        bottomNavigationView = findViewById(R.id.bottom_navigation_history);
        setupBottomNavigationView();

        // Set up the weight graph (LineChart)
        setupWeightGraph();

        // Load the meal history data
        loadMealHistory();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_history);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }
    }
    @Override
    public void onMealRemoved(MealHistoryItem meal, int position) {
        // Create an AlertDialog.Builder instance
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Meal"); // Set the title for the dialog
        builder.setMessage("Are you sure you want to remove this meal?"); // Set the message to show

        // Set up the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Yes" button, remove the meal
                int userId = signUpHelper.getCurrentUserId();
                if (signUpHelper.removeMeal(userId, meal.getId())) {
                    mealHistoryAdapter.notifyItemRemoved(position);
                    Toast.makeText(History.this, "Meal removed successfully", Toast.LENGTH_SHORT).show();
                    // Directly remove the item from your adapter's dataset
                    mealHistoryAdapter.removeMealAt(position);
                } else {
                    Toast.makeText(History.this, "Error removing meal", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "No" button, dismiss the dialog
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
    private void updateWeightGraph() {
        int userId = signUpHelper.getCurrentUserId();
        if (userId == -1) {
            Log.e("History", "User ID not found.");
            return;
        }

        double originalWeight = signUpHelper.fetchUserOriginalWeight(userId);

        // Fetch the start date
        String startDateString = signUpHelper.fetchUserStartDate(userId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate;
        try {
            startDate = sdf.parse(startDateString);
        } catch (ParseException e) {
            Log.e("History", "Error parsing start date", e);
            return;
        }

        long millis = System.currentTimeMillis() - startDate.getTime();
        int weeksElapsed = (int) (millis / (1000 * 60 * 60 * 24 * 7));

        // Ensure the first data point correctly represents Week 1
        List<Entry> entries = new ArrayList<>();

        // Adjust loop to start from Week 2 since Week 1 is already plotted
        for (int week = 0; week <= weeksElapsed + 1; week++) {
            double weightChange = signUpHelper.calculateWeightChangeForWeek(userId); // Pass the userId
            originalWeight += weightChange;
            entries.add(new Entry(week, (float) originalWeight));
        }


        LineDataSet dataSet = new LineDataSet(entries, "Weight Over Time");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        customizeXAxis(weeksElapsed + 1); // Adjust to correctly label the X-axis
        chart.invalidate(); // Refresh the chart
    }

    private void customizeXAxis(int totalWeeks) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // Interval of 1 week
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setAxisLineWidth(2f);

        // Start from week 1 and end at totalWeeks
        xAxis.setAxisMinimum(1f); // Start from week 1
        xAxis.setAxisMaximum(totalWeeks); // Adjust this if you want a fixed maximum

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int week = (int) value;
                return "Week " + week;
            }
        });
    }







    private void loadMealHistory() {
        int userId = signUpHelper.getCurrentUserId();
        if (userId != -1) {
            List<MealHistoryItem> mealHistoryList = signUpHelper.getMealHistoryByUserId(userId);
            mealHistoryAdapter.updateMealHistoryList(mealHistoryList);
            mealHistoryAdapter.notifyDataSetChanged(); // Notify the adapter
            updateWeightGraph(); // Update the weight graph after loading meal history
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
