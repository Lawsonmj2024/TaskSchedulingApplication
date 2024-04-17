/*
Task Scheduling Application
Schedule.java
Michael Lawson
2024, March 5

Schedule class is the model for the Schedule table in the database
 */
package com.lawson.taskapp.model;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.NO_ACTION;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(foreignKeys=@ForeignKey(entity=Task.class, parentColumns="id",
        childColumns="task_id", onDelete=NO_ACTION))
public class Schedule {

    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="id")
    private long mId;

    @ColumnInfo(name="task_id")
    private long mTaskId;

    @ColumnInfo(name="due_date")
    private LocalDate mDueDate;

    @ColumnInfo(name="start_date")
    private LocalDate mStartDate;

    @ColumnInfo(name="response")
    private String mResponse;

    @ColumnInfo(name="response_date")
    private LocalDate mResponseDate;

    public Schedule(long taskId, LocalDate startDate, LocalDate dueDate) {
        mTaskId = taskId;
        mStartDate = startDate;
        mDueDate = dueDate;
    }

    // Id getter/setter

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    // TaskId getter/setter

    public long getTaskId() {
        return mTaskId;
    }

    public void setTaskId(long taskId) {
        mTaskId = taskId;
    }

    // DueDate getter/setter

    public LocalDate getDueDate() {
        return mDueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        mDueDate = dueDate;
    }

    // StartDate getter/setter

    public LocalDate getStartDate() {
        return mStartDate;
    }

    public void setStartDate(LocalDate StartDate) {
        mStartDate = StartDate;
    }

    // Response getter/setter

    public String getResponse() {
        return mResponse;
    }

    public void setResponse(String response) {
        mResponse = response;
    }

    // ResponseDate getter/setter

    public LocalDate getResponseDate() {
        return mResponseDate;
    }

    public void setResponseDate(LocalDate responseDate) {
        mResponseDate = responseDate;
    }
}
