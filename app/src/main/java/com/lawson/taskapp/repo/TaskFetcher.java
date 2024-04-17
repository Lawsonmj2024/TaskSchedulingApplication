/*
Task Scheduling Application
TaskDatabase.java
Michael Lawson
2024, March 5

Task Fetcher retrieves data from external sources.
Not yet implemented.
 */
package com.lawson.taskapp.repo;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lawson.taskapp.model.Group;
import com.lawson.taskapp.model.Schedule;
import com.lawson.taskapp.model.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskFetcher {
    public interface OnTaskDataReceivedListener {
        void onGroupsReceived(List<Group> groupList);
        void onTasksReceived(Group group, List<Task> taskList);
        void onSchedulesReceived(List<Schedule> scheduleList);
        void onErrorResponse(VolleyError error);
    }

    // TODO Add ability to fetch tasks externally
}
