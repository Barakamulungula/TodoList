package com.example.barakamulungula.todolist;

import android.widget.TextView;

public interface AdapterCallBack {
    void viewTask(int position);
    TaskDatabase taskDatabase();
    void setCompleteStatus(TextView textView);
    void setinCompleteStatus(TextView textView);
}
