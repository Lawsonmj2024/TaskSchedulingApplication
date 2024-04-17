/*
Task Scheduling Application
ScheduleListViewModel.java
Michael Lawson
2024, March 5

Schedule List View Model provides an abstraction of the Task Repository for the Schedule activity
 */
package com.lawson.taskapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lawson.taskapp.model.Group;
import com.lawson.taskapp.model.Schedule;
import com.lawson.taskapp.model.Task;
import com.lawson.taskapp.repo.TaskRepository;

import java.util.List;

public class ScheduleListViewModel extends AndroidViewModel {
    private final TaskRepository mTaskRepo;

    public ScheduleListViewModel(Application app) {
        super(app);
        mTaskRepo = TaskRepository.getInstance(app.getApplicationContext());
    }

    public LiveData<List<Schedule>> getScheduleByGroup(long groupId) {
        return mTaskRepo.getScheduleByGroup(groupId);
    }

    public LiveData<List<Schedule>> getSchedules() {
        return mTaskRepo.getSchedules();
    }

    public LiveData<Schedule> getSchedule(long taskId) {
        return mTaskRepo.getSchedule(taskId);
    }

    public void addSchedule(Schedule schedule) {
        mTaskRepo.addSchedule(schedule);
    }

    public void updateSchedule(Schedule schedule) {
        mTaskRepo.updateSchedule(schedule);
    }

    public void deleteSchedule(Schedule schedule) {
        mTaskRepo.deleteSchedule(schedule);
    }

    public LiveData<Task> getTask(long taskId) {
        return mTaskRepo.getTask(taskId);
    }

    public void addTask(Task task) {
        mTaskRepo.addTask(task);
    }

    public LiveData<List<Group>> getGroups() {
        return mTaskRepo.getGroups();
    }

    public void addGroup(Group group) {
        mTaskRepo.addGroup(group);
    }
}
