package com.example.barakamulungula.todolist;

import android.widget.TextView;

import java.util.List;

public interface AdapterCallBack {
    void viewTask(int position);

    TaskDatabase taskDatabase();

    void longPressDeleteTask(final Task task);

    void setCompleteStatus(TextView textView);

    void setinCompleteStatus(TextView textView);

    void setDueDateString(TextView textView, String dueDate);
}
