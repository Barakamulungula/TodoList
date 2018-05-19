package com.example.barakamulungula.todolist;

import android.app.Application;
import android.arch.persistence.room.Room;

public class TaskApplication extends Application {
    public static String DATABASE_NAME = "TASK_DATABASE";
    private TaskDatabase taskDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        taskDatabase = Room.databaseBuilder(getApplicationContext(), TaskDatabase.class, DATABASE_NAME).
                allowMainThreadQueries().build();
    }

    public TaskDatabase getDatabase() {
        return taskDatabase;
    }
}
