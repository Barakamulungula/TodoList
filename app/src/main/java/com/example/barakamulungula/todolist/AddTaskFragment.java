package com.example.barakamulungula.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTaskFragment extends Fragment implements DateCallBack{

    @BindView(R.id.text_input_title)
    protected TextInputEditText titleInput;
    @BindView(R.id.text_input_description)
    protected TextInputEditText descriptionInput;
    @BindView(R.id.due_date)
    protected Button dueDateButton;
    private String dueDate;

    private TaskDatabase taskDatabase;
    private ActivityCallback activityCallback;

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
    }

    public static AddTaskFragment newInstance() {

        Bundle args = new Bundle();

        AddTaskFragment fragment = new AddTaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.save_task)
    protected void saveTask(){
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        if(!title.trim().isEmpty() && !description.trim().isEmpty() && dueDate != null){
            try {
                Date due_date = new SimpleDateFormat("MM/dd/yy", Locale.US).parse(dueDate);
                taskDatabase.taskDAO().addTask(new Task(title,description,due_date,due_date, false));
                assert getActivity() != null;
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                activityCallback.updateAdapter();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getActivity(), "All input fields required", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.due_date)
    protected void setDueDate(){
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.attachParent(this);
        DialogFragment newFragment = datePickerFragment;
        assert getActivity() != null;
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");

    }

    @Override
    public void setDueDate(int year, int month, int day) {
        dueDate = month+"/"+day+"/"+year;
        dueDateButton.setText(getString(R.string.due_date_button, month, day, year));
        dueDateButton.setTextColor(Color.BLACK);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        private DateCallBack dateCallBack;

        public void attachParent(DateCallBack dateCallBack) {
            this.dateCallBack = dateCallBack;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateCallBack.setDueDate(year, month+1, dayOfMonth);
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







}
