/*
Task Scheduling Application
TaskEditActivity.java
Michael Lawson
2024, March 5
 */
package com.lawson.taskapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lawson.taskapp.model.Schedule;
import com.lawson.taskapp.model.Task;
import com.lawson.taskapp.viewmodel.ScheduleListViewModel;
import com.lawson.taskapp.viewmodel.TaskDetailViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TaskEditActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "com.lawson.taskapp.task_id";
    public static final String EXTRA_GROUP_ID = "com.lawson.taskapp.group_id";
    public static final String EXTRA_GROUP_NAME = "com.lawson.taskapp.group_name";

    private EditText mTaskEditText;
    private EditText mStartDateEditText;
    private EditText mDurationEditText;
    private EditText mIntervalEditText;
    private RadioGroup mFrequencyRadioGroup;
    private RadioButton mFrequencyRadioButton;

    private long mTaskId;
    private long mGroupId;
    private String mGroupName;
    private Task mTask;
    private Schedule mSchedule;

    private TaskDetailViewModel mTaskDetailViewModel;
    private ScheduleListViewModel mScheduleListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        mTaskEditText = findViewById(R.id.task_edit_text);
        mStartDateEditText = findViewById(R.id.start_date_edit_text);
        mDurationEditText = findViewById(R.id.duration_edit_text);
        mIntervalEditText = findViewById(R.id.interval_edit_text);
        mFrequencyRadioGroup = (RadioGroup) findViewById(R.id.frequency_radio_group);

        findViewById(R.id.save_button).setOnClickListener(view -> saveButtonClick());

        Intent intent = getIntent();
        mTaskId = intent.getLongExtra(EXTRA_TASK_ID, -1);
        mGroupId = intent.getLongExtra(EXTRA_GROUP_ID, -1);
        mGroupName = intent.getStringExtra(EXTRA_GROUP_NAME);
        if(mGroupName == null) {
            mGroupName = "";
        }

        mTaskDetailViewModel = new ViewModelProvider(this).get(TaskDetailViewModel.class);
        mScheduleListViewModel = new ViewModelProvider(this).get(ScheduleListViewModel.class);

        if(mTaskId == -1) {
            // Adding a new task
            mTask = new Task();
            setTitle("Add " + mGroupName + " Task");
            updateUi(true);
        } else {
            Log.d("Edit Task", "Task identified as editing");
            // Display existing task from View Model
            mTaskDetailViewModel.loadTask(mTaskId);
            mTaskDetailViewModel.taskLiveData.observe(this, task -> {
                mTask = task;
                updateUi(false);
            });
            setTitle("Edit " + mGroupName + " Task");
        }
    } // end onCreate

    private void updateUi(boolean isNewTask) {
        String startDate = TaskScheduler.getDateString(mTask.getStartDate());

        if(!isNewTask) {
            mTaskEditText.setText(mTask.getText());

            mStartDateEditText.setText(startDate);
            mDurationEditText.setText(String.valueOf(mTask.getDuration()));
            mIntervalEditText.setText(String.valueOf(mTask.getInterval()));

            String frequency = mTask.getFrequency();
            int radioButtonId = getRadioButtonId(frequency);
            mFrequencyRadioGroup.check(radioButtonId);
        }
    }

    private int getRadioButtonId(String frequency) {
        switch(frequency) {
            case "Weeks":
                return R.id.radio_button_weekly;
            case "Months":
                return R.id.radio_button_monthly;
            case "Years":
                return R.id.radio_button_yearly;
            default:
                return R.id.radio_button_daily;
        }
    }

    public void rbclick(View view) {
        int radioButtonId = mFrequencyRadioGroup.getCheckedRadioButtonId();
        mFrequencyRadioButton = (RadioButton) findViewById(radioButtonId);
    }

    private void saveButtonClick() {
        LocalDate startDate = TaskScheduler.stringToDate(mStartDateEditText.getText().toString(), "/");
        if(startDate != null) {
            // Set the start date
            mTask.setStartDate(startDate);

            String intervalString = mIntervalEditText.getText().toString();
            String durationString = mDurationEditText.getText().toString();

            int duration = 1;
            int interval = 0;

            // Check for default interval
            if(!intervalString.isEmpty()) {
                interval = Integer.parseInt(intervalString);
            }
            mTask.setInterval(interval);

            // Check for default duration
            if(!durationString.isEmpty()) {
                duration = Integer.parseInt(durationString);
            }
            mTask.setDuration(duration);


            // Set the default text
            mTask.setText(mTaskEditText.getText().toString());


            // Get the selected frequency from the radio group
            int selectedId = mFrequencyRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = findViewById(selectedId);
            String frequencyText = selectedRadioButton.getText().toString();
            mTask.setFrequency(frequencyText);

            mTask.setGroupId(mGroupId);

            if (mTaskId == -1) {
                // Save new task
                mTaskDetailViewModel.addTask(mTask);

                mSchedule = new Schedule(mTask.getId(), startDate,
                        TaskScheduler.getNextDueDate(mTask.getStartDate(), duration));

                mScheduleListViewModel.addSchedule(mSchedule);
            } else {
                // Update existing task
                mTaskDetailViewModel.updateTask(mTask);
            }

            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Bad date format: use format mm/dd/yyyy", Toast.LENGTH_SHORT).show();
        }
    }
}