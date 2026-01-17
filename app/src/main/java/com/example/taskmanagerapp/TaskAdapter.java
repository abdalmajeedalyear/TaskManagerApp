package com.example.taskmanagerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private Context context;
    private OnTaskUpdateListener taskUpdateListener;

    public interface OnTaskUpdateListener {
        void onTaskUpdated(Task task);
    }

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;

        if (context instanceof OnTaskUpdateListener) {
            this.taskUpdateListener = (OnTaskUpdateListener) context;
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.taskTitle.setText(task.getTitle());

        // استخدام دالة التنسيق من Task
        holder.taskTime.setText(task.getFormattedDueDate());

        holder.taskPriority.setText(task.getPriority());

        // تحديد لون الشريط الجانبي
        int priorityColor = getPriorityColor(task.getPriority());
        holder.priorityStrip.setBackgroundColor(priorityColor);
        holder.taskPriority.setTextColor(priorityColor);

        // تحديث حالة CheckBox
        holder.checkBox.setChecked(task.isCompleted());

        // إظهار/إخفاء الخط المنقط للمهام المكتملة
        if (task.isCompleted()) {
            holder.completedLine.setVisibility(View.VISIBLE);
            holder.taskTitle.setTextColor(context.getResources().getColor(R.color.completed_task));
            holder.taskTime.setTextColor(context.getResources().getColor(R.color.completed_task));
        } else {
            holder.completedLine.setVisibility(View.GONE);
            holder.taskTitle.setTextColor(context.getResources().getColor(R.color.text_primary));
            holder.taskTime.setTextColor(context.getResources().getColor(R.color.text_secondary));
        }

        // إزالة المستمع القديم لمنع التكرار
        holder.checkBox.setOnCheckedChangeListener(null);

        // إضافة مستمع جديد
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.setCompleted(isChecked);

                // تحديث المهمة في قاعدة البيانات عبر المستمع
                if (taskUpdateListener != null) {
                    taskUpdateListener.onTaskUpdated(task);
                }

                // تحديث المظهر
                if (isChecked) {
                    holder.completedLine.setVisibility(View.VISIBLE);
                    holder.taskTitle.setTextColor(context.getResources().getColor(R.color.completed_task));
                    holder.taskTime.setTextColor(context.getResources().getColor(R.color.completed_task));
                } else {
                    holder.completedLine.setVisibility(View.GONE);
                    holder.taskTitle.setTextColor(context.getResources().getColor(R.color.text_primary));
                    holder.taskTime.setTextColor(context.getResources().getColor(R.color.text_secondary));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    private int getPriorityColor(String priority) {
        if (priority == null) {
            return context.getResources().getColor(R.color.text_secondary);
        }

        switch (priority) {
            case "عالية":
                return context.getResources().getColor(R.color.high_priority);
            case "متوسطة":
                return context.getResources().getColor(R.color.medium_priority);
            case "منخفضة":
                return context.getResources().getColor(R.color.low_priority);
            default:
                return context.getResources().getColor(R.color.text_secondary);
        }
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView taskTitle, taskTime, taskPriority;
        View priorityStrip, completedLine;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_box);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskTime = itemView.findViewById(R.id.task_time);
            taskPriority = itemView.findViewById(R.id.task_priority);
            priorityStrip = itemView.findViewById(R.id.priority_strip);
            completedLine = itemView.findViewById(R.id.completed_line);
        }
    }

    // دالة لتحديث القائمة
    public void updateTaskList(List<Task> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }
}