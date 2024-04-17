/*
Task Scheduling Application
TaskDetailViewModel.java
Michael Lawson
2024, March 5

Task Detail View Model provides an abstraction of the Task Repository for the Task Activities
 */
package com.lawson.taskapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.lawson.taskapp.model.Group;
import com.lawson.taskapp.repo.TaskRepository;
import com.lawson.taskapp.model.Task;

public class TaskDetailViewModel extends AndroidViewModel {

    private TaskRepository mTaskRepo;
    private final MutableLiveData<Long> taskIdLiveData = new MutableLiveData<>();
    public LiveData<Task> taskLiveData =
            Transformations.switchMap(taskIdLiveData, taskId ->
                mTaskRepo.getTask(taskId));

    public TaskDetailViewModel(@NonNull Application app) {
        super(app);
        mTaskRepo = TaskRepository.getInstance(app.getApplicationContext());
    }

    public LiveData<Task> getTask(long taskId) {
        return mTaskRepo.getTask(taskId);
    }

    public Task loadTask(long taskId) {
        taskIdLiveData.setValue(taskId);
        return null;
    }

    public LiveData<Task> getTaskLiveData() {
        return taskLiveData;
    }

    public void addTask(Task task) {
        mTaskRepo.addTask(task);
    }

    public void updateTask(Task task) {
        mTaskRepo.updateTask(task);
    }

    public void deleteTask(Task task) {
        mTaskRepo.deleteTask(task);
    }

    public LiveData<Group> getGroup(long groupId) {
        return mTaskRepo.getGroup(groupId);
    }
}
