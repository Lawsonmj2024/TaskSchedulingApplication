/*
Task Scheduling Application
TaskScheduler.java
Michael Lawson
2024, March 5
 */
package com.lawson.taskapp;

import android.util.Log;

import com.lawson.taskapp.model.Schedule;
import com.lawson.taskapp.model.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TaskScheduler {

    public static LocalDate getNextStartDate(LocalDate lastStartDate, int interval, String frequency) {
        LocalDate nextStartDate = LocalDate.ofEpochDay(lastStartDate.toEpochDay());

        ChronoUnit unit;

        Log.d("-- Update Recurring", "Using frequency: " + frequency);

        switch(frequency) {
            case "Days":
                unit = ChronoUnit.DAYS;
                break;
            case "Weeks":
                unit = ChronoUnit.WEEKS;
                break;
            case "Months":
                unit = ChronoUnit.MONTHS;
                break;
            default:
                unit = ChronoUnit.YEARS;
        }

        nextStartDate = nextStartDate.plus(interval, unit);
        return nextStartDate;
    }

    public static LocalDate getNextDueDate(LocalDate startDate, int duration) {
        LocalDate nextDueDate = LocalDate.ofEpochDay(startDate.toEpochDay());
        nextDueDate = nextDueDate.plus(duration, ChronoUnit.DAYS);
        return nextDueDate;
    }

    public static String getDateString(LocalDate localDate) {
        String date; // Return variable

        // Get the numeric values of the date components
        int month = localDate.getMonth().getValue();
        int day = localDate.getDayOfMonth();
        int year = localDate.getYear();

        // Write date in proper format
        date = month + "/" + day + "/" + year;

        // Return string date
        return date;
    }

    public static LocalDate stringToDate(String dateString, String delimiter) {
        String[] parts = dateString.split(delimiter);

        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if(year < 2020) {
            return null;
        }

        return LocalDate.of(year, month, day);
    }

    public static String getStatus(LocalDate startDate, LocalDate dueDate) {
        LocalDate today = LocalDate.now();
        String result = "";

        // Task not ready to start
        if(today.isBefore(startDate)) {
            result += "Sleeping";
        }
        // Task is ready but not due
        else if(today.isBefore(dueDate)) {
            result += "Ready";
        }
        // Task is overdue
        else {
            result += "Late";
        }
        return result;
    }
}
