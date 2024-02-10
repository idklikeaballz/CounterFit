package com.sp.counterfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateGoalsAdapter extends AppCompatActivity {

    private int selectedPosition = -1; // Tracks the selected goal position

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_goal);

        ListView listView = findViewById(R.id.weekly_goal_list);

        // Define the list items
        String[] goals = new String[]{
                "Gain 0.2 kg per week",
                "Gain 0.5 kg per week",
                "Lose 0.2 kg per week",
                "Lose 0.5 kg per week",
                "Maintain Weight"
        };

        // Create an ArrayAdapter using the string array and a default list item layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, goals);

        // Apply the adapter to the ListView
        listView.setAdapter(adapter);

        // Handle item clicks
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Store the selected position
                selectedPosition = position;
                // Update the goal selection here or in another method
                // For simplicity, let's just show a Toast message
                Toast.makeText(UpdateGoalsAdapter.this, "Selected: " + goals[position], Toast.LENGTH_SHORT).show();

                // Prepare data to return to parent activity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("selectedGoal", goals[position]);
                setResult(RESULT_OK, returnIntent);

                // Finish activity and return to parent
                finish();
            }
        });
    }
}
