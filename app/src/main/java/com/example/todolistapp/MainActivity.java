package com.example.todolistapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.widget.AdapterView;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText editTask;
    Button btnAdd;
    ListView listViewTasks;
    TextView taskCount, completedCount, timeText, dateText;


    ArrayList<String> taskList;
    ArrayAdapter<String> adapter;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTask = findViewById(R.id.editTask);
        btnAdd = findViewById(R.id.btnAdd);
        listViewTasks = findViewById(R.id.listViewTasks);
        taskCount = findViewById(R.id.taskCount);
        completedCount = findViewById(R.id.completedCount);
        timeText = findViewById(R.id.timeText);
        dateText = findViewById(R.id.dateText);
        preferences = getSharedPreferences("tasks", MODE_PRIVATE);

        String currentTime = new SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
        ).format(new Date());

        timeText.setText("🕒 " + currentTime);

        String currentDate = new SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
        ).format(new Date());

        dateText.setText("📅 " + currentDate);

        Set<String> savedTasks =
                preferences.getStringSet("taskList", new HashSet<>());

        taskList = new ArrayList<>(savedTasks);


        adapter = new ArrayAdapter<>(
                this,
                R.layout.task_item,
                R.id.taskText,
                taskList
        );

        listViewTasks.setAdapter(adapter);
        updateTaskCount();


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String task = editTask.getText().toString();

                if(!task.isEmpty()) {

                    taskList.add(task);
                    adapter.notifyDataSetChanged();
                    updateTaskCount();
                    updateCompletedCount();
                    preferences.edit()
                            .putStringSet("taskList", new HashSet<>(taskList))
                            .apply();

                    editTask.setText("");
                    Toast.makeText(MainActivity.this,
                            "Task Added Successfully",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        listViewTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                taskList.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,
                        "Task Deleted",
                        Toast.LENGTH_SHORT).show();
                updateTaskCount();
                updateCompletedCount();
                preferences.edit()
                        .putStringSet("taskList", new HashSet<>(taskList))
                        .apply();

                return true;
            }
        });
        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String task = taskList.get(position);

                if(!task.startsWith("✓ ")) {
                    taskList.set(position, "✓ " + task);
                }

                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,
                        "Task Completed",
                        Toast.LENGTH_SHORT).show();
                updateTaskCount();
                updateCompletedCount();
                preferences.edit()
                        .putStringSet("taskList", new HashSet<>(taskList))
                        .apply();
            }
        });
    }
    private void updateTaskCount() {
        taskCount.setText("Total Tasks: " + taskList.size());
    }
    private void updateCompletedCount() {

        int completed = 0;

        for(String task : taskList) {

            if(task.startsWith("✓ ")) {
                completed++;
            }
        }

        completedCount.setText("✅ Completed: " + completed);
    }
}