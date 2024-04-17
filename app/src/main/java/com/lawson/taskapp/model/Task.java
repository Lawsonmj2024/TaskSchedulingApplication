/*
Task Scheduling Application
Task.java
Michael Lawson
2024, March 5

Task class is the model for the Task table in the database
 */
package com.lawson.taskapp.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

// TODO Associate task to group
@Entity
public class Task {

    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="id")
    private long mId;

    @ColumnInfo(name="text")
    private String mText;

    @ColumnInfo(name="frequency")
    private String mFrequency;

    @ColumnInfo(name="group_id")
    private long mGroupId;

    @ColumnInfo(name="duration")
    private int mDuration;

    @ColumnInfo(name="interval")
    private int mInterval;

    @ColumnInfo(name="start_date")
    private LocalDate mStartDate;

    public Task() {
        mText = "";
        mStartDate = LocalDate.now();
        mDuration = 0;
        mInterval = 0;
        mFrequency = "";
    }

    public Task(String text, LocalDate startDate, int duration, int interval, String frequency) {
        mText = text;
        mStartDate = startDate;
        mDuration = duration;
        mInterval = interval;
        mFrequency = frequency;
    }

    public Task(String text, LocalDate startDate, int duration, int interval, String frequency, long groupId) {
        this(text, startDate, duration, interval, frequency);
        mGroupId = groupId;
    }

    // Id getter/setter

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    // Text getter/setter

    public void setText(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    // Frequency getter/setter

    public void setFrequency(String frequency) {
        mFrequency = frequency;
    }

    public String getFrequency() {
        return mFrequency;
    }

    public void setGroupId(long groupId) {
        mGroupId = groupId;
    }

    public long getGroupId() {
        return mGroupId;
    }

    // Duration getter/setter

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getDuration() {
        return mDuration;
    }

    // Interval getter/setter

    public void setInterval (int interval) {
        mInterval = interval;
    }

    public int getInterval() {
        return mInterval;
    }

    // Start Date getter/setter

    public LocalDate getStartDate() {
        return mStartDate;
    }

    public void setStartDate(LocalDate startDate) {
        mStartDate = startDate;
    }
}
