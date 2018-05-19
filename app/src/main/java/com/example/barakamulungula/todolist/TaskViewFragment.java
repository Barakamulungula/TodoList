package com.example.barakamulungula.todolist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.barakamulungula.todolist.MainActivity.SPINNER_LIST_TYPE;
import static com.example.barakamulungula.todolist.MainActivity.TASK_POSITION;

public class TaskViewFragment extends Fragment {
    @BindView(R.id.task_view_date_completed)
    TextView dateCompletedTextView;
    @BindView(R.id.view_task_title)
    TextView taskTitle;
    @BindView(R.id.view_task_description)
    TextView taskDesc;
    @BindView(R.id.task_view_date_due)
    TextView taskDueDate;
    @BindView(R.id.task_view_status)
    TextView taskStatus;
    @BindView(R.id.text_view_date_created)
    TextView taskDateCreated;
    @BindView(R.id.task_view_layout)
    ConstraintLayout taskViewLayout;
    @BindView(R.id.task_view_is_completed)
    CheckBox statusCheckbox;
    private int position;
    private TaskDatabase taskDatabase;
    private ActivityCallback activityCallback;
    DateFormat dateFormat;

    public static TaskViewFragment newInstance() {

        Bundle args = new Bundle();

        TaskViewFragment fragment = new TaskViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setActivityCallback(ActivityCallback activityCallback) {
        this.activityCallback = activityCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_view_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        assert getArguments() != null;
        dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.US);
        position = getArguments().getInt(TASK_POSITION);
        assert getActivity() != null;
        taskDatabase = ((TaskApplication) getActivity().getApplicationContext()).getDatabase();
        setUpView();
        statusCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Task task = activityCallback.getTaskList().get(position);
                if (isChecked) {
                    task.setCompleted(true);
                    Date dateCompleted = Calendar.getInstance().getTime();
                    task.setDateCompleted(dateCompleted);
                    taskDatabase.taskDAO().updateTask(task);
                    taskViewLayout.setBackgroundResource(R.color.to_completed_task);
                    taskStatus.setText(getString(R.string.status, getString(R.string.complete)));
                    dateCompletedTextView.setVisibility(View.VISIBLE);
                    dateCompletedTextView.setText(getString(R.string.date_completed,
                            dateFormat.format(task.getDateCompleted())));
                    Toast.makeText(getActivity(), "Task marked complete", Toast.LENGTH_SHORT).show();
                } else {
                    task.setCompleted(false);
                    taskDatabase.taskDAO().updateTask(task);
                    dateCompletedTextView.setVisibility(View.GONE);
                    taskViewLayout.setBackgroundResource(R.color.passed_due_time);
                    taskStatus.setText(getString(R.string.status, getString(R.string.incomplete)));
                    Toast.makeText(getActivity(), "Task marked incomplete", Toast.LENGTH_SHORT).show();
                }

                checkListType();
            }
        });
    }

    private void setUpView() {
        taskTitle.setText(activityCallback.getTaskList().get(position).getTitle());
        taskDesc.setText(activityCallback.getTaskList().get(position).getDescription());
        taskDueDate.setText(getString(R.string.due_date, dateFormat.format(
                activityCallback.getTaskList().get(position).getDueDate())));
        taskDateCreated.setText(getString(R.string.date_created, dateFormat.format(
                activityCallback.getTaskList().get(position).getDateCreated()
        )));

        if (activityCallback.getTaskList().get(position).isCompleted()) {
            dateCompletedTextView.setVisibility(View.VISIBLE);
            dateCompletedTextView.setText(
                    getString(R.string.date_completed,
                    dateFormat.format(activityCallback.getTaskList().get(position).getDateCompleted())));
            taskViewLayout.setBackgroundResource(R.color.to_completed_task);
            taskStatus.setText(getString(R.string.status, getString(R.string.complete)));
            statusCheckbox.setChecked(true);

        } else {
            dateCompletedTextView.setVisibility(View.GONE);
            taskViewLayout.setBackgroundResource(R.color.passed_due_time);
            taskStatus.setText(getString(R.string.status, getString(R.string.incomplete)));
            statusCheckbox.setChecked(false);
        }
    }

    @OnClick(R.id.task_view_delete)
    protected void deleteTask() {
        taskDatabase.taskDAO().deleteTask(activityCallback.getTaskList().get(position));
        assert getActivity() != null;
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.exit_to_right, R.anim.exit_to_left)
                .remove(this).commit();
        Toast.makeText(getActivity(), "Task deleted", Toast.LENGTH_SHORT).show();
        checkListType();
    }

    private void checkListType(){
        assert getArguments() != null;
        if(getArguments().getInt(SPINNER_LIST_TYPE) == 1){
            activityCallback.setTaskList(taskDatabase.taskDAO().getCompletedTask(true));
            activityCallback.updateAdapter(taskDatabase.taskDAO().getCompletedTask(true));
        }else if(getArguments().getInt(SPINNER_LIST_TYPE) == 2){
            activityCallback.setTaskList(taskDatabase.taskDAO().getinCompleteTask(false));
            activityCallback.updateAdapter(taskDatabase.taskDAO().getCompletedTask(false));
        }else{
            activityCallback.setTaskList(taskDatabase.taskDAO().getTasks());
            activityCallback.updateAdapter(taskDatabase.taskDAO().getTasks());
        }
    }

    @OnClick(R.id.task_view_edit)
    protected void editTask() {
        activityCallback.editTask(position);
    }

}
