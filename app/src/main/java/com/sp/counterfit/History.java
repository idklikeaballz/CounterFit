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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class History extends AppCompatActivity implements MealHistoryAdapter.OnMealRemovedListener {
    private static final int NUM_MONTHS = 12;
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

        List<Entry> entries = new ArrayList<>();


        LineDataSet dataSet = new LineDataSet(entries, "Weight (KG)");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh the chart
    }
    private void updateWeightGraph() {
        int userId = signUpHelper.getCurrentUserId();
        customizeXAxis();
        if (userId == -1) {
            Log.e("History", "User ID not found.");
            return;
        }

        double originalWeight = signUpHelper.fetchUserOriginalWeight(userId);
        String startDateString = signUpHelper.fetchUserStartDate(userId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate;
        try {
            startDate = sdf.parse(startDateString);
        } catch (ParseException e) {
            Log.e("History", "Error parsing start date", e);
            return;
        }

        int monthsElapsed = calculateMonthsElapsed(startDate);
        List<Entry> entries = new ArrayList<>();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);

        for (int month = 0; month < monthsElapsed; month++) {
            Calendar dateCalendar = (Calendar) startCalendar.clone();
            dateCalendar.add(Calendar.MONTH, month);
            float xValue = dateCalendar.getTimeInMillis();
            double weight = calculateWeightForMonth(userId, month);
            entries.add(new Entry(xValue, (float) weight));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Weight Over Time");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // Refresh the chart
    }

    private int calculateMonthsElapsed(Date startDate) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        Calendar today = Calendar.getInstance();
        int monthsBetween = today.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH)
                + (today.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)) * 12;
        return Math.max(monthsBetween, 1); // Ensure at least 1 month is considered
    }

    private double calculateWeightForMonth(int userId, int monthsAfterStart) {
        // Fetch the original weight
        double originalWeight = signUpHelper.fetchUserOriginalWeight(userId);

        // Calculate weekly weight change
        double weeklyWeightChange = signUpHelper.calculateWeightChangeForWeek(userId);

        // Convert weekly weight change to monthly. Assuming 4.33 weeks in a month for calculation
        double monthlyWeightChange = weeklyWeightChange * 4.33;

        // Calculate the weight after the specified number of months
        double weightAfterMonths = originalWeight + (monthlyWeightChange * monthsAfterStart);

        return weightAfterMonths;
    }



    private void clearChart() {
        if (chart.getData() != null) {
            chart.getData().clearValues();
            chart.notifyDataSetChanged();
            chart.invalidate();
        }
    }


    private void customizeXAxis() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(24f * 60f * 60f * 1000f); // one day in milliseconds
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setAxisLineWidth(2f);

        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                // Convert float value back to a date
                long millis = (long) value;
                return mFormat.format(new Date(millis));
            }
        });
    }



    private void loadMealHistory() {
        int userId = signUpHelper.getCurrentUserId();
        if (userId != -1) {
            clearChart(); // Clear the chart for the new user
            List<MealHistoryItem> mealHistoryList = signUpHelper.getMealHistoryByUserId(userId);
            Collections.reverse(mealHistoryList); // Reverse the list if it's not already in the desired order
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