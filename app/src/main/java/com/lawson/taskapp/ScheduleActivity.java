/*
Task Scheduling Application
ScheduleActivity.java
Michael Lawson
2024, March 5

Schedule activity controls the home screen of the application
    TODO Lists Groups in a List View
    Lists Scheduled Tasks in a Recycler View sorted by Due Date
    TODO Selecting a group filters scheduled tasks
 */
package com.lawson.taskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lawson.taskapp.model.Group;
import com.lawson.taskapp.model.Schedule;
import com.lawson.taskapp.model.Task;
import com.lawson.taskapp.viewmodel.ScheduleListViewModel;
import com.lawson.taskapp.viewmodel.TaskDetailViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleActivity extends AppCompatActivity implements GroupDialogFragment.OnGroupEnteredListener {

    public enum ScheduleSortOrder {
        DUE_DATE, START_DATE
    }

    private Boolean mLoadScheduleList = true;
    private ScheduleAdapter mScheduleAdapter;
    private RecyclerView mRecyclerView;
    private Spinner mGroupSpinner;
    private Button mAddGroupButton;
    private ScheduleListViewModel mScheduleListViewModel;
    private Schedule mSelectedSchedule;
    private List<Group> mGroupList;
    private int mSelectedSchedulePosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;
    private Group mSelectedGroup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        mScheduleListViewModel = new ViewModelProvider(this)
                .get(ScheduleListViewModel.class);

        mGroupSpinner = findViewById(R.id.group_spinner);

        findViewById(R.id.add_schedule_button).setOnClickListener(view -> addScheduleClick());
        findViewById(R.id.add_group_button).setOnClickListener(view -> addGroupButtonClick());

        // Create 2 grid layout columns
        mRecyclerView = findViewById(R.id.schedule_recycler_view);
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // Call updateUI() when the schedule list changes
        mScheduleListViewModel.getSchedules().observe(this, schedules -> {
            if(mLoadScheduleList) {
                updateUI(schedules);
            }
        });

        mScheduleListViewModel.getGroups().observe(this, this::updateSpinnerGroups);

        // add starter data if schedules is empty
        addStarterDataIfNeeded();

    } // end onCreate


    public void onGroupEntered(String groupText) {
        if(groupText.length() > 0) {
            Group group = new Group(groupText);
            mScheduleListViewModel.addGroup(group);
            mScheduleListViewModel.getGroups().observe(this, this::updateSpinnerGroups);
        }
    }

    private void addGroupButtonClick() {
        Toast.makeText(this, "Add a new group", Toast.LENGTH_SHORT)
                .show();
        GroupDialogFragment dialog = new GroupDialogFragment();
        dialog.show(getSupportFragmentManager(), "groupDialog");
    }



    private void addStarterDataIfNeeded() {
        mScheduleListViewModel.getSchedules().observe(this, schedules -> {
            if (schedules == null || schedules.isEmpty()) {
                Log.d("Starter Data", "Adding starter data...");
                // Define starter groups
                Group workGroup = new Group("Work");
                Group personalGroup = new Group("Personal");

                mScheduleListViewModel.addGroup(workGroup);
                mScheduleListViewModel.addGroup(personalGroup);

                // Define starter tasks
                List<Task> starterTasks = Arrays.asList(
                        new Task("Wash dishes", LocalDate.of(2024, 3, 8), 1, 1, "Days", personalGroup.getId()),
                        new Task("Stain fence", LocalDate.of(2024, 3, 8), 20, 0, "Days", personalGroup.getId()),
                        new Task("Quality KPI Audit", LocalDate.of(2024, 3, 26), 1, 1, "Weeks", workGroup.getId()),
                        new Task("Clean junk drawer", LocalDate.of(2024, 2, 16), 1, 0, "Days", personalGroup.getId())
                );
                for (Task task : starterTasks) {
                    mScheduleListViewModel.addTask(task);

                    LocalDate dueDate = TaskScheduler.getNextDueDate(task.getStartDate(), task.getDuration());
                    mScheduleListViewModel.addSchedule(new Schedule(task.getId(), task.getStartDate(), dueDate));
                }
                Log.d("Starter Data", "Starter data added.");
            }
        });
    }

    private void updateSpinnerGroups(List<Group> groups) {
        // Keep a reference to the list of groups
        mGroupList = groups;

        // Add a group for seeing all tasks
        mGroupList.add(0, new Group("All"));

        // Extract group names
        List<String> groupNames = groups.stream().map(Group::getName).collect(Collectors.toList());

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, groupNames);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        mGroupSpinner.setAdapter(adapter);

        // Set item selected listener
        mGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0) {
                    onNothingSelected(parentView);
                } else {

                    // Your code here for handling group selection
                    mSelectedGroup = mGroupList.get(position);

                    // Assuming your Group object has an getId() method to retrieve groupId
                    long groupId = mSelectedGroup.getId();

                    // Use ViewModel to fetch schedules for the selected group
                    mScheduleListViewModel.getScheduleByGroup(groupId).observe(ScheduleActivity.this,
                            ScheduleActivity.this::updateUI);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Your code here if nothing is selected
                mSelectedGroup = null;
                mScheduleListViewModel.getSchedules().observe(ScheduleActivity.this, ScheduleActivity.this::updateUI);
            }
        });
    }

    private ScheduleSortOrder getSettingsSortOrder() {
        // Set sort order from settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrderPref = sharedPrefs.getString("schedule_order", "due_date");
        switch(sortOrderPref) {
            case "due_date":
                return ScheduleSortOrder.DUE_DATE;
            default:
                return ScheduleSortOrder.START_DATE;
        }
    }

    private void observeTaskDetails(List<Schedule> scheduleList) {
        for(Schedule schedule : scheduleList) {
            TaskDetailViewModel taskDetailViewModel = new ViewModelProvider(this)
                    .get(TaskDetailViewModel.class);
            taskDetailViewModel.getTask(schedule.getTaskId()).observe(this, task -> {
                if(task != null) {
                    updateAdapterWithTask(schedule, task);
                }
            });
        }
    }

    private void updateAdapterWithTask(Schedule schedule, Task task) {
        Pair<Schedule, Task> combinedData = new Pair<>(schedule, task);
        if(mScheduleAdapter != null) {
            mScheduleAdapter.updateScheduleWithTask(combinedData);
        }
    }


    private void updateUI(List<Schedule> scheduleList) {

        // Sort list in descending order
        List<Schedule> sortedList = scheduleList.stream()
                .sorted(Comparator.comparing(Schedule::getDueDate))
                .collect(Collectors.toList());

        List<Pair<Schedule, Task>> pairedList = new ArrayList<>();
        for(Schedule schedule : sortedList) {
            pairedList.add(new Pair<>(schedule, null));
        }

        if(mScheduleAdapter == null) {
            mScheduleAdapter = new ScheduleAdapter(pairedList);
            mRecyclerView.setAdapter(mScheduleAdapter);
        } else {
            mScheduleAdapter.setSchedules(pairedList);
        }
        observeTaskDetails(sortedList);
    }

    private void addScheduleClick() {
        Intent intent = new Intent(ScheduleActivity.this, TaskEditActivity.class);

        // Pass the group id and name if a group was selected
        if(mSelectedGroup != null) {
            intent.putExtra(TaskEditActivity.EXTRA_GROUP_ID, mSelectedGroup.getId());
            intent.putExtra(TaskEditActivity.EXTRA_GROUP_NAME, mSelectedGroup.getName());
        } else {
            long val = -1;
            intent.putExtra(TaskEditActivity.EXTRA_GROUP_ID, val);
            intent.putExtra(TaskEditActivity.EXTRA_GROUP_NAME, "All");
        }

        // Start activity
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == R.id.settings) {
            Toast.makeText(getApplicationContext(), "Adjust app settings",
                    Toast.LENGTH_SHORT).show();

            // TODO start SettingsActivity
            /*
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            */

            return true;
        }
        else if(item.getItemId() == R.id.share_schedule) {
            Toast.makeText(getApplicationContext(), "Share schedule with others",
                    Toast.LENGTH_SHORT).show();

            // TODO start ShareActivity
            /*
            Intent intent = new Intent(this, ShareActivity.class);
            startActivity(intent
             */

            return true;
        }

        return super.onOptionsItemSelected(item);
    } // end onOptionsItemSelected()

    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Provide context menu for CAB
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // Process action item selection
            if(item.getItemId() == R.id.delete) {
                // Stop updateUI() from being called
                mLoadScheduleList = false;

                // Delete from ViewModel
                mScheduleListViewModel.deleteSchedule(mSelectedSchedule);
                mScheduleListViewModel.deleteSchedule(mSelectedSchedule);

                // Remove from RecyclerView
                mScheduleAdapter.removeSchedule(mSelectedSchedule);

                // Close the CAB
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;

            // CAB closing, need to deselect item if not deleted
            mScheduleAdapter.notifyItemChanged(mSelectedSchedulePosition);
            mSelectedSchedulePosition = RecyclerView.NO_POSITION;
        }

    }; // end ActionMode.Callback


    private class ScheduleHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private Schedule mSchedule;
        private Task mTask;
        private final TextView mScheduleTextView;

        public ScheduleHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            mScheduleTextView = itemView.findViewById(R.id.schedule_text_view);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Start TaskActivity for the selected item
            Intent intent = new Intent(ScheduleActivity.this, TaskActivity.class);
            intent.putExtra(TaskActivity.EXTRA_SCHEDULE_ID, mSchedule.getId());

            // Pass the group id and name if a group was selected
            if(mSelectedGroup != null) {
                intent.putExtra(TaskEditActivity.EXTRA_GROUP_NAME, mSelectedGroup.getName());
            } else {
                intent.putExtra(TaskEditActivity.EXTRA_GROUP_NAME, "All");
            }

            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            if(mActionMode != null) {
                return false;
            }

            mSelectedSchedule = mSchedule;
            mSelectedSchedulePosition = getAdapterPosition();

            // Re-bind the selected item
            mScheduleAdapter.notifyItemChanged(mSelectedSchedulePosition);

            // Show the CAB
            mActionMode = ScheduleActivity.this.startActionMode(mActionModeCallback);

            return true;
        }

        public void bind(Pair<Schedule, Task> combinedData, int position) {
            // Set schedule and task members
            mSchedule = combinedData.first;
            mTask = combinedData.second;

            // Declare due date and start date as strings
            String stringDueDate = TaskScheduler.getDateString(mSchedule.getDueDate());
            String stringStartDate = TaskScheduler.getDateString(mSchedule.getStartDate());


            LocalDate startDate = mSchedule.getStartDate();
            LocalDate dueDate = mSchedule.getDueDate();
            LocalDate today = LocalDate.now();

            // Declare card text and color variables
            String cardText = "";
            int cardColor;

            // Append task text to card if available
            if(mTask != null) {
                cardText += mTask.getText() + "\n";
            }

            // Task not ready to start
            if(today.isBefore(startDate)) {
                cardColor = getResources().getColor(R.color.sleep);
                cardText += "Start: " + stringStartDate;
            }
            // Task is ready but not due
            else if(today.isBefore(dueDate)) {
                cardColor = getResources().getColor(R.color.ready);
                cardText += "Due: " + stringDueDate;
            }
            // Task is overdue
            else {
                cardColor = getResources().getColor(R.color.late);
                cardText += "Due: " + stringDueDate;
            }

            if(mSelectedSchedulePosition == position) {
                // Make selected schedule stand out
                cardColor = Color.rgb(204, 204, 0);
            }

            mScheduleTextView.setText(cardText);
            mScheduleTextView.setBackgroundColor(cardColor);
        }

    } // end ScheduleHolder

    private class ScheduleAdapter extends RecyclerView.Adapter<ScheduleHolder> {
        private List<Pair<Schedule, Task>> mScheduleList;
        public ScheduleAdapter(List<Pair<Schedule, Task>> scheduledTasks) {
            mScheduleList = scheduledTasks;
        }

        @NonNull
        @Override
        public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new ScheduleHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ScheduleHolder holder, int position) {
            holder.bind(mScheduleList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mScheduleList.size();
        }

        public void addSchedule(Pair<Schedule, Task> scheduledTask) {
            // Add the new schedule at beginning of list
            mScheduleList.add(0, scheduledTask);

            // Notify the adapter that item was added to beginning of list
            notifyItemInserted(0);

            // Scroll to top
            mRecyclerView.scrollToPosition(0);
        }

        public void removeSchedule(Schedule scheduledTask) {
            int index = mScheduleList.indexOf(scheduledTask);
            if(index >= 0) {
                // Remove the schedule
                mScheduleList.remove(index);

                // Notify adapter of schedule removal
                notifyItemRemoved(index);
            }
        }

        public void updateScheduleWithTask(Pair<Schedule, Task> combinedData) {
            int index = -1;
            for(int i = 0; i < mScheduleList.size(); i++) {
                if(mScheduleList.get(i).first.getId() == combinedData.first.getId()) {
                    index = i;
                    break;
                }
            }
            if(index != -1) {
                mScheduleList.set(index, combinedData);
                notifyItemChanged(index);
            } else {
                mScheduleList.add(combinedData);
                notifyItemInserted(mScheduleList.size() - 1);
            }
        }

        public void setSchedules(List<Pair<Schedule, Task>> pairedList) {
            this.mScheduleList = pairedList;
            notifyDataSetChanged();
        }
    } // end ScheduleAdapter
}