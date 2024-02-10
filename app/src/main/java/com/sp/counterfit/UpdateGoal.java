package com.sp.counterfit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class UpdateGoal extends AppCompatActivity {

    private SignUpHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private String[] goals;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_goal);
        dbHelper = new SignUpHelper(this);

        ListView listView = findViewById(R.id.weekly_goal_list);
        AppCompatButton updateButton = findViewById(R.id.updateGoalButton);
        goals = new String[]{
                "Gain 0.2 kg per week",
                "Gain 0.5 kg per week",
                "Lose 0.2 kg per week",
                "Lose 0.5 kg per week",
                "Maintain Weight"
        };

        String currentGoal = dbHelper.getWeightGoalForCurrentUser();
        for (int i = 0; i < goals.length; i++) {
            if (goals[i].equals(currentGoal)) {
                selectedPosition = i;
                break;
            }
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, goals) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                if (position == selectedPosition) {
                    view.setTextColor(Color.BLUE);
                } else {
                    view.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            adapter.notifyDataSetChanged();
        });

        updateButton.setOnClickListener(v -> {
            if (selectedPosition >= 0) {
                String selectedGoal = goals[selectedPosition];
                String currentUserEmail = dbHelper.getCurrentUserEmail();
                if (currentUserEmail != null) {
                    dbHelper.updateUserWeightGoal(currentUserEmail, selectedGoal);
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(UpdateGoal.this, "No user is currently logged in", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(UpdateGoal.this, "Please select a goal", Toast.LENGTH_SHORT).show();
            }
        });

        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_title_center);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.headerbg));
        }
    }
}
