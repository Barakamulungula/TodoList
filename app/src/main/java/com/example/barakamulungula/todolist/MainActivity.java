package com.example.barakamulungula.todolist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterCallBack, ActivityCallback {

    @BindView(R.id.sort_task_list)
    protected Spinner sortList;
    @BindView(R.id.task_recycler_view)
    protected RecyclerView recyclerView;
    @BindView(R.id.remove_task_button)
    protected Button removeTaskButton;
    @BindView(R.id.add_task_button)
    protected Button addTaskButton;

    private TaskAdapter taskAdapter;

    private AddTaskFragment addTaskFragment;
    private TaskViewFragment taskViewFragment;
    private TaskDatabase taskDatabase;
    public static final String TASK_POSITION = "POSITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        taskDatabase = ((TaskApplication) getApplicationContext() ).getDatabase();
        sortList.setOnItemSelectedListener(this);
        ArrayAdapter <CharSequence> dropdownAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_list, android.R.layout.simple_spinner_dropdown_item);
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortList.setAdapter(dropdownAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        taskAdapter = new TaskAdapter(taskDatabase.taskDAO().getTasks(), this);
        recyclerView.setAdapter(taskAdapter);
        taskAdapter.notifyDataSetChanged();

    }





    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        taskAdapter.setListType(position);
        taskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @OnClick(R.id.add_task_button)
    protected void addTask(){
        addTaskFragment = AddTaskFragment.newInstance();
        addTaskFragment.setActivityCallback(this);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.fragment_container, addTaskFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if(addTaskFragment != null){
            if(addTaskFragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.exit_to_left, R.anim.exit_to_right)
                        .remove(addTaskFragment).commit();
            }else {
                super.onBackPressed();
            }
        }
        if(taskViewFragment != null) {
            if (taskViewFragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.exit_to_left, R.anim.exit_to_right)
                        .remove(taskViewFragment).commit();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void viewTask(int position) {
        taskViewFragment = TaskViewFragment.newInstance();
        taskViewFragment.setActivityCallback(this);
        Bundle bundle = new Bundle();
        bundle.putInt(TASK_POSITION, position);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(R.id.fragment_container,taskViewFragment ).commit();
        taskViewFragment.setArguments(bundle);

    }

    @Override
    public TaskDatabase taskDatabase() {
        return ((TaskApplication) getApplicationContext() ).getDatabase();
    }

    @Override
    public void setCompleteStatus(TextView textView) {
        textView.setText(getString(R.string.status, getString(R.string.complete)));
        sortList.setSelection(0);
    }

    @Override
    public void setinCompleteStatus(TextView textView) {
        textView.setText(getString(R.string.status, getString(R.string.incomplete)));
        sortList.setSelection(0);
    }


    @Override
    public void updateAdapter() {
        taskAdapter.loadTaskList(taskDatabase.taskDAO().getTasks());
        taskAdapter.notifyDataSetChanged();
        sortList.setSelection(0);
    }
}
