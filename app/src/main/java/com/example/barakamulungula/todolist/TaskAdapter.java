package com.example.barakamulungula.todolist;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Task> taskList;
    private AdapterCallBack adapterCallBack;
    private int listType = 0;
    private List<Task> completedList = new ArrayList<>();
    private List<Task> incompleteList = new ArrayList<>();

    public TaskAdapter(List<Task> taskList, AdapterCallBack adapterCallBack) {
        this.taskList = taskList;
        this.adapterCallBack = adapterCallBack;
    }

    public void setListType(int listType) {
        this.listType = listType;
        if (!adapterCallBack.taskDatabase().taskDAO().getTasks().isEmpty()) {
            for(Task task: adapterCallBack.taskDatabase().taskDAO().getTasks()){
                if(task.isCompleted() && !completedList.contains(task)){
                    completedList.add(task);
                    if(incompleteList.contains(task)){
                        incompleteList.remove(task);
                    }
                }else if(!task.isCompleted() && !incompleteList.contains(task)){
                    incompleteList.add(task);
                    if(completedList.contains(task)){
                        completedList.remove(task);
                    }

                }
            }

        }
        if (listType == 1) {
            loadTaskList(completedList);
            Log.println(Log.WARN, "COMPLETE LIST:", "LOADED, SIZE:"+completedList.size());
        }
        if(listType == 2){
            loadTaskList(incompleteList);
            Log.println(Log.WARN, "INCOMPLETE LIST:", "LOADED, SIZE:"+incompleteList.size());
        }
        if(listType == 0){
            loadTaskList(adapterCallBack.taskDatabase().taskDAO().getTasks());
            Log.println(Log.WARN, "LIST:", "LOADED, SIZE:"+taskList.size());
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        holder.bind(taskList.get(position));
        holder.itemView.setOnClickListener(holder.onClickListener(position));
        holder.itemView.setOnLongClickListener(holder.onLongClickListener(position));

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void loadTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.task_layout_view)
        protected ConstraintLayout itemConstraintLayout;

        @BindView(R.id.title)
        protected TextView titleTextView;
        @BindView(R.id.date_created)
        protected TextView dueDateTextView;
        @BindView(R.id.status)
        protected TextView taskStatusTextView;
        @BindView(R.id.task_checkbox)
        protected CheckBox taskIsChecked;
        private Calendar calendar;


        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            taskIsChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Task task = adapterCallBack.taskDatabase().taskDAO().getTasks().get(getAdapterPosition());
                    if(isChecked){
                        task.setCompleted(true);
                        if(incompleteList.contains(task)){
                            incompleteList.remove(task);
                        }

                        adapterCallBack.taskDatabase().taskDAO().updateTask(task);
                        itemView.setBackgroundResource(R.color.to_completed_task);
                        adapterCallBack.setCompleteStatus(taskStatusTextView);
                    }else{
                        if(incompleteList.contains(task)){
                            incompleteList.remove(task);
                        }
                        task.setCompleted(false);
                        adapterCallBack.taskDatabase().taskDAO().updateTask(task);
                        itemView.setBackgroundResource(R.color.passed_due_time);
                        adapterCallBack.setinCompleteStatus(taskStatusTextView);
                    }
                }
            });
        }

        void bind(Task task) {

            titleTextView.setText(task.getTitle());
            calendar = Calendar.getInstance();
            calendar.setTime(task.getDueDate());
            Date date = calendar.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
            dueDateTextView.setText(simpleDateFormat.format(date));
            if (task.isCompleted()) {
                itemConstraintLayout.setBackgroundResource(R.color.to_completed_task);
                taskIsChecked.setChecked(true);
            } else {
                itemConstraintLayout.setBackgroundResource(R.color.passed_due_time);
                taskIsChecked.setChecked(false);
            }


        }

        public View.OnLongClickListener onLongClickListener(final int position) {
            return new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            };
        }

        public View.OnClickListener onClickListener(final int position) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        adapterCallBack.viewTask(position);
                }
            };
        }

    }
}
