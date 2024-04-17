/*
Task Scheduling Application
ScheduleDao.java
Michael Lawson
2024, March 5

Schedule Data Access Object links java methods to Room database operations
 */
package com.lawson.taskapp.repo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import com.lawson.taskapp.model.Schedule;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Query("SELECT * FROM Schedule WHERE id = :id")
    LiveData<Schedule> getSchedule(long id);

    @Query("SELECT * FROM Schedule WHERE task_id = :taskId AND response ISNULL")
    LiveData<List<Schedule>> getScheduleByTask(long taskId);

    @Query( "SELECT * " +
            "FROM Schedule " +
            "INNER JOIN Task ON Schedule.task_id = Task.id " +
            "WHERE Task.group_id = :groupId " +
            "AND response ISNULL " +
            "ORDER BY due_date " +
            "COLLATE NOCASE" )
    LiveData<List<Schedule>> getScheduleByGroup(long groupId);

    @Query("SELECT * FROM Schedule WHERE response ISNULL")
    LiveData<List<Schedule>> getSchedules();

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    long addSchedule(Schedule schedule);

    @Update
    void updateSchedule(Schedule schedule);

    @Delete
    void deleteSchedule(Schedule schedule);
}
