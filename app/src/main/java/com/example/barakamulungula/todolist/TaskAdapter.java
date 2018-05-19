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

    public TaskAdapter(List<Task> taskList, AdapterCallBack adapterCallBack) {
        this.taskList = taskList;
        this.adapterCallBack = adapterCallBack;
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

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Task task) {
            titleTextView.setText(task.getTitle());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.US);
            dueDateTextView.setText(simpleDateFormat.format(task.getDueDate()));
            if(task.isCompleted()){
                    itemConstraintLayout.setBackgroundResource(R.color.to_completed_task);
                    adapterCallBack.setCompleteStatus(taskStatusTextView);
            } else {
                    itemConstraintLayout.setBackgroundResource(R.color.passed_due_time);
                    adapterCallBack.setinCompleteStatus(taskStatusTextView);
            }

        }

        //Todo: Handle on onlong press event on adpater items
        public View.OnLongClickListener onLongClickListener(final int position) {
            return new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    adapterCallBack.longPressDeleteTask(adapterCallBack.taskDatabase().taskDAO().getTasks().get(position));
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
