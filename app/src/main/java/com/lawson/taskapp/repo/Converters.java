/*
Task Scheduling Application
Converters.java
Michael Lawson
2024, March 5

Converters are used to store certain objects into a database-friendly format
 */
package com.lawson.taskapp.repo;

import androidx.room.TypeConverter;
import java.time.LocalDate;

public class Converters {
    @TypeConverter
    public static LocalDate fromTimestamp(Long value) {
        return value == null ? null : LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }
}
