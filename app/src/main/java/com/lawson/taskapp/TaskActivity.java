/*
Task Scheduling Application
TaskActivity.java
Michael Lawson
2024, March 5
 */
package com.lawson.taskapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lawson.taskapp.model.Schedule;
import com.lawson.taskapp.model.Task;
import com.lawson.taskapp.viewmodel.ScheduleListViewModel;
import com.lawson.taskapp.viewmodel.TaskDetailViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaskActivity extends AppCompatActivity {

    public static final String EXTRA_SCHEDULE_ID = "com.lawson.taskapp.schedule_id";

    private Task mTask;
    private Schedule mSchedule;

    private TextView mTaskTextLabelTextView;
    private TextView mTaskDueDateLabelTextView;
    private TextView mTaskStartDateLabelTextView;
    private TextView mTaskIntervalLabelTextView;
    private TextView mTaskFrequencyLabelTextView;
    private TextView mTaskStatusLabelTextView;
    private TaskDetailViewModel mTaskDetailViewModel;
    private ScheduleListViewModel mScheduleListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mTaskTextLabelTextView = findViewById(R.id.task_text_text_view);
        mTaskDueDateLabelTextView = findViewById(R.id.task_due_text_view);
        mTaskStartDateLabelTextView = findViewById(R.id.task_start_text_view);
        mTaskFrequencyLabelTextView = findViewById(R.id.task_frequency_text_view);
        mTaskIntervalLabelTextView = findViewById(R.id.task_interval_text_view);
        mTaskStatusLabelTextView = findViewById(R.id.task_state_text_view);

        findViewById(R.id.complete_button).setOnClickListener(view -> completeButtonClick());
        findViewById(R.id.skip_button).setOnClickListener(view -> skipButtonClick());

        long scheduleId = getIntent().getLongExtra(EXTRA_SCHEDULE_ID, -1);
        if (scheduleId == -1) {
            Toast.makeText(this, "Invalid Schedule ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mTaskDetailViewModel = new ViewModelProvider(this).get(TaskDetailViewModel.class);
        mScheduleListViewModel = new ViewModelProvider(this).get(ScheduleListViewModel.class);



        mScheduleListViewModel.getSchedule(scheduleId).observe(this, schedule -> {
            if (schedule != null) {
                mSchedule = schedule;
                updateScheduleUI();
            }
        });
    }

    private void updateScheduleUI() {
        String taskStatus = TaskScheduler.getStatus(mSchedule.getStartDate(), mSchedule.getDueDate());
        setTaskStatus(taskStatus);

        String dueDate = TaskScheduler.getDateString(mSchedule.getDueDate());
        String startDate = TaskScheduler.getDateString(mSchedule.getStartDate());

        mTaskDueDateLabelTextView.setText(dueDate);
        mTaskStartDateLabelTextView.setText(startDate);

        mTaskDetailViewModel.loadTask(mSchedule.getTaskId());
        mTaskDetailViewModel.getTaskLiveData().observe(this, task -> {
            mTask = task;
            updateTaskUI();
        });
    }

    private void setTaskStatus(String taskStatus) {
        int taskStatusColor;

        // Task not ready to start
        switch(taskStatus) {
            case "Ready":
                taskStatusColor = getResources().getColor(R.color.ready);
                break;

            case "Sleeping":
                taskStatusColor = getResources().getColor(R.color.sleep);
                break;

            default:
                taskStatusColor = getResources().getColor(R.color.late);
        }

        mTaskStatusLabelTextView.setText(taskStatus);
        mTaskStatusLabelTextView.setTextColor(taskStatusColor);
    }

    private void updateTaskUI() {
        if (mTask == null) {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mTaskDetailViewModel.getGroup(mTask.getGroupId()).observe(this, group -> {
            if(group == null) {
                setTitle("Task");
            } else {
                setTitle(group.getName() + " Task");
            }
        });

        mTaskTextLabelTextView.setText(mTask.getText());
        mTaskFrequencyLabelTextView.setText(mTask.getFrequency());
        mTaskIntervalLabelTextView.setText(String.valueOf(mTask.getInterval()));
    }

    private void completeButtonClick() {
        updateSchedule("Completed");
        returnToHome();
    }

    private void skipButtonClick() {
        updateSchedule("Skipped");
        returnToHome();
    }

    private void returnToHome() {

    }

    private void updateSchedule(String response) {

        String toastText = "Task " + response + ".";

        if(mSchedule != null && mTask != null) {
            // Update current schedule
            mSchedule.setResponse(response);
            mSchedule.setResponseDate(LocalDate.now());

            // Persist changes
            mScheduleListViewModel.updateSchedule(mSchedule);

            // Check for recurring task
            if(mTask.getInterval() > 0) {
                Log.d("-- Update Recurring", "Task identified as recurring");
                // Calculate next schedule dates
                LocalDate nextStartDate = TaskScheduler.getNextStartDate(mSchedule.getStartDate(),
                        mTask.getInterval(), mTask.getFrequency());
                LocalDate nextDueDate = TaskScheduler.getNextDueDate(nextStartDate,
                        mTask.getDuration());

                Log.d("-- Update Recurring", "New Start: " + nextStartDate.toString() + " from " + mSchedule.getStartDate().toString());
                Log.d("-- Update Recurring", "New Due: " + nextDueDate.toString() + " from " + mSchedule.getDueDate().toString());

                // Create a new Schedule object
                Schedule nextSchedule = new Schedule(mTask.getId(), nextStartDate, nextDueDate);

                // Persist new Schedule data
                mScheduleListViewModel.addSchedule(nextSchedule);

                // Update schedule to nextSchedule
                mSchedule = nextSchedule;

                // Add scheduling message to toast
                toastText += " Task rescheduled.";
            }

            Intent intent = new Intent(TaskActivity.this, ScheduleActivity.class);
            startActivity(intent);
        } else {
            // Add error message to toast
            toastText += " But an error occurred and your response was not saved.";
        }

        // Inform user of result
        Toast.makeText(TaskActivity.this, toastText, Toast.LENGTH_SHORT).show();
    } // end updateSchedule()



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == R.id.edit_task) {
            Toast.makeText(getApplicationContext(), "Edit task",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(TaskActivity.this, TaskEditActivity.class);
            intent.putExtra(TaskEditActivity.EXTRA_TASK_ID, mTask.getId());
            Log.d("Edit Task","Task id passed as " + mTask.getId());

            mTaskDetailViewModel.getGroup(mTask.getGroupId()).observe(this, group -> {
                if(group != null) {
                    Log.d("Edit Task", "Group found");
                    intent.putExtra(TaskEditActivity.EXTRA_GROUP_ID, group.getId());
                    intent.putExtra(TaskEditActivity.EXTRA_GROUP_NAME, group.getName());
                } else {
                    Log.d("Edit Task", "Group not found");
                    intent.putExtra(TaskEditActivity.EXTRA_GROUP_ID, -1);
                    intent.putExtra(TaskEditActivity.EXTRA_GROUP_NAME, "All");
                }
            });
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    } // end onOptionsItemSelected()
}
