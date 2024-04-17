/*
Task Scheduling Application
GroupDao.java
Michael Lawson
2024, March 5

Group Data Access Object links java methods to Room database operations
 */
package com.lawson.taskapp.repo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import com.lawson.taskapp.model.Group;

import java.util.List;

@Dao
public interface GroupDao {

    @Query("SELECT * FROM `Group` WHERE id = :id")
    LiveData<Group> getGroup(long id);

    @Query("SELECT * FROM `Group` ORDER BY name COLLATE NOCASE")
    LiveData<List<Group>> getGroups();

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    long addGroup(Group group);

    @Update
    void updateGroup(Group group);

    @Delete
    void deleteGroup(Group group);

}
