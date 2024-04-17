/*
Task Scheduling Application
TaskDatabase.java
Michael Lawson
2024, March 5

Task Database links data access objects to the room database
 */
package com.lawson.taskapp.repo;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.lawson.taskapp.model.Group;
import com.lawson.taskapp.model.Task;
import com.lawson.taskapp.model.Schedule;

@Database(entities = {Task.class, Group.class, Schedule.class}, version = 19)
@TypeConverters({Converters.class})
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    public abstract GroupDao groupDao();

    public abstract ScheduleDao scheduleDao();
}
