package com.example.barakamulungula.todolist;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TaskDAO {

    @Query("SELECT * FROM TASK")
    List<Task> getTasks();

    @Delete
    void deleteTask(Task task);

    @Update
    void updateTask(Task task);

    @Insert
    void addTask(Task task);
}
