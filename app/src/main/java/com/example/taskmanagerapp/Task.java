package com.example.taskmanagerapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Task {
    private int id;
    private String title;
    private String description;
    private String priority; // عالية، متوسطة، منخفضة
    private String category; // عمل، شخصية، تعليم
    private Date dueDate;
    private Date reminderTime;
    private boolean isCompleted;
    private Date createdAt;
    private Date updatedAt;
    private String tags;
    private int estimatedTime; // بالدقائق
    private int actualTime; // بالدقائق

    // Constructors
    public Task() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Task(String title, String description, String priority,
                String category, Date dueDate) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.dueDate = dueDate;
        this.isCompleted = false;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getReminderTime() { return reminderTime; }
    public void setReminderTime(Date reminderTime) { this.reminderTime = reminderTime; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public int getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(int estimatedTime) { this.estimatedTime = estimatedTime; }

    public int getActualTime() { return actualTime; }
    public void setActualTime(int actualTime) { this.actualTime = actualTime; }

    // Helper methods
    public boolean isOverdue() {
        if (dueDate == null || isCompleted) return false;
        return dueDate.before(new Date());
    }

    public boolean hasReminder() {
        return reminderTime != null;
    }

    // دالة واحدة فقط لتنسيق التاريخ - تأكد أنك لا تملك نسخة مكررة
    public String getFormattedDueDate() {
        if (dueDate == null) {
            return "لا يوجد تاريخ";
        }

        try {
            // تحقق إذا كان التاريخ هو اليوم
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            if (dateFormat.format(dueDate).equals(dateFormat.format(now))) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                return "اليوم - " + timeFormat.format(dueDate);
            } else if (dateFormat.format(dueDate).equals(
                    dateFormat.format(new Date(now.getTime() + 86400000)))) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                return "غداً - " + timeFormat.format(dueDate);
            } else {
                SimpleDateFormat fullFormat = new SimpleDateFormat("EEEE - hh:mm a", Locale.getDefault());
                return fullFormat.format(dueDate);
            }
        } catch (Exception e) {
            return "تاريخ غير محدد";
        }
    }
}