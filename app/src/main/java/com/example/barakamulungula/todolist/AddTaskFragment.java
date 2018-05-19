package com.example.barakamulungula.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.barakamulungula.todolist.MainActivity.CLICK_TYPE;
import static com.example.barakamulungula.todolist.MainActivity.SPINNER_LIST_TYPE;
import static com.example.barakamulungula.todolist.MainActivity.TASK_POSITION_EDIT;

public class AddTaskFragment extends Fragment implements DateCallBack {

    @BindView(R.id.text_input_title)
    protected TextInputEditText titleInput;
    @BindView(R.id.text_input_description)
    protected TextInputEditText descriptionInput;
    @BindView(R.id.due_date)
    protected Button dueDateButton;
    @BindView(R.id.due_time)
    protected Button dueTimeButton;
    private String dueDate;
    private String dueTime;
    @BindView(R.id.save_task)
    Button saveTaskButton;
    @BindView(R.id.update_task)
    Button updateTaskButton;

    private TaskDatabase taskDatabase;
    private ActivityCallback activityCallback;
    private int position;

    public static AddTaskFragment newInstance() {

        Bundle args = new Bundle();

        AddTaskFragment fragment = new AddTaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setActivityCallback(ActivityCallback activityCallback) {
        this.activityCallback = activityCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_task_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        assert getActivity() != null;
        taskDatabase = ((TaskApplication) getActivity().getApplicationContext()).getDatabase();
        titleInput.setText("");
        descriptionInput.setText("");
        if(getArguments() != null) {
            if (!getArguments().isEmpty()) {
                if(getArguments().getInt(CLICK_TYPE) == 1) {
                    updateTaskButton.setVisibility(View.VISIBLE);
                    saveTaskButton.setVisibility(View.GONE);
                    position = getArguments().getInt(TASK_POSITION_EDIT);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(activityCallback.getTaskList().get(position).getDueDate());
                    Date date = calendar.getTime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.US);
                    dueDateButton.setText(simpleDateFormat.format(date));
                    dueTimeButton.setText(simpleTimeFormat.format(date));
                    titleInput.setText(activityCallback.getTaskList().get(position).getTitle());
                    descriptionInput.setText(activityCallback.getTaskList().get(position).getDescription());
                    dueDateButton.setTextColor(Color.BLACK);
                    dueTimeButton.setTextColor(Color.BLACK);
                    getArguments().clear();
                }
            }
        }
    }



    @OnClick(R.id.save_task)
    protected void saveTask() {
        //Todo: Add created date to Task
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        if (!title.trim().isEmpty() && !description.trim().isEmpty() && dueDate != null) {
            try {
                Date due_date = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.US).parse(dueDate+" "+dueTime);
                Calendar dateCreated = Calendar.getInstance();
                Date date_created = dateCreated.getTime();
                taskDatabase.taskDAO().addTask(new Task(title, description, date_created, due_date, false));
                assert getActivity() != null;
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                checkListType();
                activityCallback.setTaskList(activityCallback.getTaskList());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "All input fields required", Toast.LENGTH_SHORT).show();
        }

    }

    //Todo: update task
    @OnClick(R.id.update_task)
    protected void updateTask(){
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        dueDate = dueDateButton.getText().toString().concat(" "+dueTimeButton.getText().toString());
        if (!title.trim().isEmpty() && !description.trim().isEmpty() && dueDate != null) {
            try {
                Date due_date = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.US).parse(dueDate+" "+dueTime);
                Task task = taskDatabase.taskDAO().getTasks().get(position);
                task.setTitle(title);
                task.setDescription(description);
                task.setDueDate(due_date);
                taskDatabase.taskDAO().updateTask(task);
                saveTaskButton.setVisibility(View.VISIBLE);
                updateTaskButton.setVisibility(View.GONE);
                assert getActivity() != null;
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                assert getArguments() != null;
                getArguments().clear();
                checkListType();
                Toast.makeText(getActivity(), "Task updated", Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "All input fields required", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.due_date)
    protected void setDueDate() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.attachParent(this);
        assert getActivity() != null;
        datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");

    }

    @OnClick(R.id.due_time)
    protected void setDueTime(){
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setDateCallBack(this);
        assert getActivity() != null;
        timePickerFragment.show(getActivity().getSupportFragmentManager(), "timePicker");

    }

    @Override
    public void setDueDate(int year, int month, int day) {
        dueDate = month + "/" + day + "/" + year;
        dueDateButton.setText(getString(R.string.due_date_button, month, day, year));
        dueDateButton.setTextColor(Color.BLACK);
    }

    @Override
    public void setDueTime(int hour, int minute) {
        dueTime = hour+":"+minute;
        dueTimeButton.setText(dueTime);
        dueTimeButton.setTextColor(Color.BLACK);
    }

    private void checkListType(){
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

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private DateCallBack dateCallBack;

        public void attachParent(DateCallBack dateCallBack) {
            this.dateCallBack = dateCallBack;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateCallBack.setDueDate(year, month + 1, dayOfMonth);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            assert getActivity() != null;
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
        private DateCallBack dateCallBack;

        public void setDateCallBack(DateCallBack dateCallBack) {
            this.dateCallBack = dateCallBack;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateCallBack.setDueTime(hourOfDay, minute);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            assert getActivity() != null;
            return new TimePickerDialog(getActivity(), this, hour, minute,false);
        }
    }




}
