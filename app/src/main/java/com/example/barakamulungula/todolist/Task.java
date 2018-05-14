package com.example.barakamulungula.todolist;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


import java.util.Date;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private Date dateCreated;
    private Date dueDate;
    private Date dateCompleted;
    private boolean isCompleted;


    public Task(String title, String description, Date dateCreated, Date dueDate, boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.dateCreated = dateCreated;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public int getId() {
        return id;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setId(int id) {
        this.id = id;
    }
}
