/*
Task Scheduling Application
TaskRepository.java
Michael Lawson
2024, March 5

Task Repository implements data access object functions
 */
package com.lawson.taskapp.repo;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.lawson.taskapp.TaskScheduler;
import com.lawson.taskapp.model.Group;
import com.lawson.taskapp.model.Schedule;
import com.lawson.taskapp.model.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.Arrays;

public class TaskRepository {

    public MutableLiveData<String> importedGroup = new MutableLiveData<>();
    public MutableLiveData<List<Group>> fetchedGroupList = new MutableLiveData<>();

    private static TaskRepository mTaskRepo;
    private final TaskDao mTaskDao;
    private final GroupDao mGroupDao;
    private final ScheduleDao mScheduleDao;

    public static TaskRepository getInstance(Context context) {
        if(mTaskRepo == null) {
            mTaskRepo = new TaskRepository(context);
        }
        return mTaskRepo;
    }

    private TaskRepository(Context context) {
        Log.d("Starter Data", "Entering repository...");
        TaskDatabase database = Room.databaseBuilder(context, TaskDatabase.class, "tasks.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        Log.d("Starter Data", "Entering onCreate...");
                        super.onCreate(db);
                        // Database has been created, populate initial data
                        Executors.newSingleThreadExecutor().execute(() -> {
                            // Add starter data on a background thread
                            addStarterData();
                        });
                    }
                })
                .build();


        mGroupDao = database.groupDao();
        mTaskDao = database.taskDao();
        mScheduleDao = database.scheduleDao();
    }

    private void addStarterData() {
        Log.d("Starter Data", "Adding starter data...");

        // Define starter tasks
        List<Task> starterTasks = Arrays.asList(
            new Task("Wash dishes", LocalDate.of(2024, 3, 8), 1, 1, "Days"),
            new Task("Stain fence", LocalDate.of(2024, 3, 8), 20, 0, "Days"),
            new Task("Audit quality", LocalDate.of(2024, 3, 8), 1, 1, "Weeks"),
            new Task("Clean junk drawer", LocalDate.of(2024, 2, 16), 1, 0, "Days")
        );

        // Insert starter tasks and their corresponding schedules into the database
        for (Task task : starterTasks) {
            long taskId = mTaskDao.addTask(task); // This should be synchronous

            LocalDate dueDate = TaskScheduler.getNextDueDate(task.getStartDate(), task.getDuration());
            Schedule schedule = new Schedule(taskId, task.getStartDate(), dueDate);

            mScheduleDao.addSchedule(schedule); // This should also be synchronous
        }

        Log.d("Starter Data", "Starter data added.");
    }

    // Group Database Operations

    public void addGroup(Group group) {
        long groupId = mGroupDao.addGroup(group);
        group.setId(groupId);
    }
    public LiveData<Group> getGroup(long groupId) {
        return mGroupDao.getGroup(groupId);
    }
    public LiveData<List<Group>> getGroups() {
        return mGroupDao.getGroups();
    }
    public void updateGroup(Group group) {
        mGroupDao.updateGroup(group);
    }
    public void deleteGroup(Group group) {
        mGroupDao.deleteGroup(group);
    }

    // Task Database Operations

    public void addTask(Task task) {
        long taskId = mTaskDao.addTask(task);
        task.setId(taskId);
    }
    public LiveData<Task> getTask(long taskId) {
        return mTaskDao.getTask(taskId);
    }
    public void updateTask(Task task) {
        mTaskDao.updateTask(task);
    }
    public void deleteTask(Task task) {
        mTaskDao.deleteTask(task);
    }

    // Schedule Database Operations

    public void addSchedule(Schedule schedule) {
        long scheduleId = mScheduleDao.addSchedule(schedule);
        schedule.setId(scheduleId);
    }
    public LiveData<Schedule> getSchedule(long scheduleId) {
        return mScheduleDao.getSchedule(scheduleId);
    }
    public LiveData<List<Schedule>> getScheduleByTask(long taskId) {
        return mScheduleDao.getScheduleByTask(taskId);
    }

    public LiveData<List<Schedule>> getScheduleByGroup(long groupId) {
        return mScheduleDao.getScheduleByGroup(groupId);
    }

    public LiveData<List<Schedule>> getSchedules() {
        return mScheduleDao.getSchedules();
    }
    public void updateSchedule(Schedule schedule) {
        mScheduleDao.updateSchedule(schedule);
    }
    public void deleteSchedule(Schedule schedule) {
        mScheduleDao.deleteSchedule(schedule);
    }

    // TODO Fetcher database operations start here


}
