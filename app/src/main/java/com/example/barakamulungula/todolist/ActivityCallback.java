package com.example.barakamulungula.todolist;

import java.util.List;

public interface ActivityCallback {
    void updateAdapter(List<Task> taskList);

    void editTask(int position);

    List<Task> getTaskList();

    void setTaskList(List<Task> taskList);
}
