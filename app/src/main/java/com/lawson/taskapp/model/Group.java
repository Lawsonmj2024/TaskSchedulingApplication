/*
Task Scheduling Application
Group.java
Michael Lawson
2024, March 5

Group class is the model for the Group table in the database
 */
package com.lawson.taskapp.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// TODO Implement grouping feature
@Entity
public class Group {

    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="id")
    private long mId;

    @NonNull
    @ColumnInfo(name="name")
    private String mName;

    public Group(@NonNull String name) {
        mName = name;
    }

    // Id getter/setter

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    // Name getter/setter

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
