package com.example.barakamulungula.todolist;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterCallBack, ActivityCallback {

    public static final String TASK_POSITION = "POSITION";
    public static final String TASK_POSITION_EDIT = "TASK_POSITION";
    public static final String SPINNER_LIST_TYPE = "TYPE";
    public static final String CLICK_TYPE = "EDIT/ADD_TASK";
    private List<Task> taskList = new ArrayList<>();
    @BindView(R.id.sort_task_list)
    protected Spinner sortList;
    @BindView(R.id.task_recycler_view)
    protected RecyclerView recyclerView;
    @BindView(R.id.add_task_button)
    protected Button addTaskButton;
    private TaskAdapter taskAdapter;
    private AddTaskFragment addTaskFragment;
    private TaskViewFragment taskViewFragment;
    private TaskDatabase taskDatabase;
    private int spinnerListType= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //Get task database
        taskDatabase = ((TaskApplication) getApplicationContext()).getDatabase();
        //set on item select for Spinner
        sortList.setOnItemSelectedListener(this);

        //Add element to spinner
        ArrayAdapter<CharSequence> dropdownAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_list, android.R.layout.simple_spinner_dropdown_item);
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortList.setAdapter(dropdownAdapter);

        //set up recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        taskAdapter = new TaskAdapter(taskDatabase.taskDAO().getTasks(), this);
        recyclerView.setAdapter(taskAdapter);
        taskAdapter.notifyDataSetChanged();

        addTaskFragment = AddTaskFragment.newInstance();
        taskViewFragment = TaskViewFragment.newInstance();

    }


    @Override
    protected void onStart() {
        super.onStart();
        taskList = taskDatabase.taskDAO().getTasks();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 1) {
            taskList = taskDatabase.taskDAO().getCompletedTask(true);
            spinnerListType = position;
        } else if (position == 2) {
            taskList = taskDatabase.taskDAO().getinCompleteTask(false);
            spinnerListType = position;
        }
        else if(position == 0){
            taskList = taskDatabase.taskDAO().getTasks();
            spinnerListType = position;
        }
        taskAdapter.loadTaskList(taskList);
        taskAdapter.notifyDataSetChanged();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @OnClick(R.id.add_task_button)
    protected void addTask() {
        Bundle bundle = new Bundle();
        bundle.putInt(SPINNER_LIST_TYPE, spinnerListType);
        bundle.putInt(CLICK_TYPE, 0);bundle.putInt(SPINNER_LIST_TYPE, spinnerListType);
        addTaskFragment.setActivityCallback(this);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.fragment_container, addTaskFragment).commit();
        addTaskFragment.setArguments(bundle);
    }

    @Override
    public void onBackPressed() {
        if (taskViewFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.exit_to_left, R.anim.exit_to_right)
                    .remove(taskViewFragment).commit();
        } else if (addTaskFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.exit_to_left, R.anim.exit_to_right)
                    .remove(addTaskFragment).commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void viewTask(int position) {
        taskViewFragment.setActivityCallback(this);
        Bundle bundle = new Bundle();
        bundle.putInt(TASK_POSITION, position);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(R.id.fragment_container, taskViewFragment).commit();
        taskViewFragment.setArguments(bundle);
        sortList.setSelection(0);

    }

    @Override
    public TaskDatabase taskDatabase() {
        return ((TaskApplication) getApplicationContext()).getDatabase();
    }

    @Override
    public void longPressDeleteTask(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_game)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Update database with updated game information
                        taskDatabase.taskDAO().deleteTask(task);
                        //Let our adapter know that information in the database has changed to update our view accordingly
                        taskAdapter.loadTaskList(taskDatabase.taskDAO().getTasks());

                        Toast.makeText(MainActivity.this, R.string.delete_game, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void setCompleteStatus(TextView textView) {
        textView.setText(getString(R.string.status, getString(R.string.complete)));
    }

    @Override
    public void setinCompleteStatus(TextView textView) {
        textView.setText(getString(R.string.status, getString(R.string.incomplete)));
    }

    @Override
    public List<Task> getTaskList() {
        return taskList;
    }

    @Override
    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public void setDueDateString(TextView textView, String dueDate) {
        textView.setText(getString(R.string.due_date, dueDate));
    }


    @Override
    public void updateAdapter(List<Task> taskList) {
        taskAdapter.loadTaskList(taskList);
    }


    @Override
    public void editTask(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(TASK_POSITION_EDIT, position);
        bundle.putInt(SPINNER_LIST_TYPE, spinnerListType);
        bundle.putInt(CLICK_TYPE, 1);
        addTaskFragment.setActivityCallback(this);
        getSupportFragmentManager().beginTransaction().remove(taskViewFragment).commit();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.fragment_container, addTaskFragment).commit();
        addTaskFragment.setArguments(bundle);

    }
}
