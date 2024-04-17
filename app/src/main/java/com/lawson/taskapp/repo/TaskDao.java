/*
Task Scheduling Application
TaskDao.java
Michael Lawson
2024, March 5

Task Data Access Object links java methods to Room database operations
 */
package com.lawson.taskapp.repo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import com.lawson.taskapp.model.Task;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM Task WHERE id = :id")
    LiveData<Task> getTask(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);
}
