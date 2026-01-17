package com.example.taskmanagerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "TaskManager.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TASKS = "tasks";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_TAGS = "tags";

    // Task Table Columns
    private static final String KEY_TASK_ID = "id";
    private static final String KEY_TASK_TITLE = "title";
    private static final String KEY_TASK_DESCRIPTION = "description";
    private static final String KEY_TASK_PRIORITY = "priority";
    private static final String KEY_TASK_CATEGORY = "category";
    private static final String KEY_TASK_DUE_DATE = "due_date";
    private static final String KEY_TASK_REMINDER = "reminder_time";
    private static final String KEY_TASK_COMPLETED = "is_completed";
    private static final String KEY_TASK_CREATED_AT = "created_at";
    private static final String KEY_TASK_UPDATED_AT = "updated_at";
    private static final String KEY_TASK_TAGS = "tags";
    private static final String KEY_TASK_ESTIMATED_TIME = "estimated_time";
    private static final String KEY_TASK_ACTUAL_TIME = "actual_time";

    // Category Table Columns
    private static final String KEY_CATEGORY_ID = "id";
    private static final String KEY_CATEGORY_NAME = "name";
    private static final String KEY_CATEGORY_COLOR = "color";

    // Singleton Pattern
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Tasks Table
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + KEY_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TASK_TITLE + " TEXT NOT NULL,"
                + KEY_TASK_DESCRIPTION + " TEXT,"
                + KEY_TASK_PRIORITY + " TEXT,"
                + KEY_TASK_CATEGORY + " TEXT,"
                + KEY_TASK_DUE_DATE + " INTEGER,"
                + KEY_TASK_REMINDER + " INTEGER,"
                + KEY_TASK_COMPLETED + " INTEGER DEFAULT 0,"
                + KEY_TASK_CREATED_AT + " INTEGER NOT NULL,"
                + KEY_TASK_UPDATED_AT + " INTEGER NOT NULL,"
                + KEY_TASK_TAGS + " TEXT,"
                + KEY_TASK_ESTIMATED_TIME + " INTEGER DEFAULT 0,"
                + KEY_TASK_ACTUAL_TIME + " INTEGER DEFAULT 0"
                + ")";

        // Create Categories Table
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + KEY_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CATEGORY_NAME + " TEXT UNIQUE NOT NULL,"
                + KEY_CATEGORY_COLOR + " TEXT"
                + ")";

        db.execSQL(CREATE_TASKS_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);

        // Insert default categories
        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
            onCreate(db);
        }
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        String[] categories = {"عمل", "شخصية", "تعليم", "صحة", "اجتماعية"};
        String[] colors = {"#4A6FA5", "#28A745", "#FD7E14", "#DC3545", "#6F42C1"};

        for (int i = 0; i < categories.length; i++) {
            values.put(KEY_CATEGORY_NAME, categories[i]);
            values.put(KEY_CATEGORY_COLOR, colors[i]);
            db.insert(TABLE_CATEGORIES, null, values);
            values.clear();
        }
    }

    // ==================== CRUD Operations for Tasks ====================

    // Add a new task
    public long addTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TASK_TITLE, task.getTitle());
        values.put(KEY_TASK_DESCRIPTION, task.getDescription());
        values.put(KEY_TASK_PRIORITY, task.getPriority());
        values.put(KEY_TASK_CATEGORY, task.getCategory());
        if (task.getDueDate() != null) {
            values.put(KEY_TASK_DUE_DATE, task.getDueDate().getTime());
        }
        if (task.getReminderTime() != null) {
            values.put(KEY_TASK_REMINDER, task.getReminderTime().getTime());
        }
        values.put(KEY_TASK_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(KEY_TASK_CREATED_AT, task.getCreatedAt().getTime());
        values.put(KEY_TASK_UPDATED_AT, task.getUpdatedAt().getTime());
        values.put(KEY_TASK_TAGS, task.getTags());
        values.put(KEY_TASK_ESTIMATED_TIME, task.getEstimatedTime());
        values.put(KEY_TASK_ACTUAL_TIME, task.getActualTime());

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    // Get a single task by ID
    public Task getTask(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS,
                null,
                KEY_TASK_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Task task = cursorToTask(cursor);
            cursor.close();
            db.close();
            return task;
        }

        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    // Get all tasks
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS,
                null, null, null, null, null,
                KEY_TASK_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    // Get tasks by priority
    public List<Task> getTasksByPriority(String priority) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS,
                null,
                KEY_TASK_PRIORITY + " = ?",
                new String[]{priority},
                null, null,
                KEY_TASK_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    // Get tasks by category
    public List<Task> getTasksByCategory(String category) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS,
                null,
                KEY_TASK_CATEGORY + " = ?",
                new String[]{category},
                null, null,
                KEY_TASK_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    // Get today's tasks
    public List<Task> getTodayTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        long todayStart = getStartOfDay();
        long todayEnd = getEndOfDay();

        String query = "SELECT * FROM " + TABLE_TASKS +
                " WHERE " + KEY_TASK_DUE_DATE + " >= ?" +
                " AND " + KEY_TASK_DUE_DATE + " <= ?" +
                " ORDER BY " + KEY_TASK_PRIORITY + " DESC";

        Cursor cursor = db.rawQuery(query,
                new String[]{String.valueOf(todayStart), String.valueOf(todayEnd)});

        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    // Get completed tasks
    public List<Task> getCompletedTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS,
                null,
                KEY_TASK_COMPLETED + " = 1",
                null, null, null,
                KEY_TASK_UPDATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    // Update a task
    public int updateTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TASK_TITLE, task.getTitle());
        values.put(KEY_TASK_DESCRIPTION, task.getDescription());
        values.put(KEY_TASK_PRIORITY, task.getPriority());
        values.put(KEY_TASK_CATEGORY, task.getCategory());
        if (task.getDueDate() != null) {
            values.put(KEY_TASK_DUE_DATE, task.getDueDate().getTime());
        }
        if (task.getReminderTime() != null) {
            values.put(KEY_TASK_REMINDER, task.getReminderTime().getTime());
        }
        values.put(KEY_TASK_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(KEY_TASK_UPDATED_AT, new Date().getTime());
        values.put(KEY_TASK_TAGS, task.getTags());
        values.put(KEY_TASK_ESTIMATED_TIME, task.getEstimatedTime());
        values.put(KEY_TASK_ACTUAL_TIME, task.getActualTime());

        int rowsAffected = db.update(TABLE_TASKS, values,
                KEY_TASK_ID + " = ?",
                new String[]{String.valueOf(task.getId())});

        db.close();
        return rowsAffected;
    }

    // Delete a task
    public void deleteTask(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TASKS,
                KEY_TASK_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Delete completed tasks
    public int deleteCompletedTasks() {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_TASKS,
                KEY_TASK_COMPLETED + " = 1",
                null);
        db.close();
        return rowsDeleted;
    }

    // Get tasks count
    public int getTasksCount() {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_TASKS;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    // Get completed tasks count
    public int getCompletedTasksCount() {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_TASKS +
                " WHERE " + KEY_TASK_COMPLETED + " = 1";
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    // Get overdue tasks count
    public int getOverdueTasksCount() {
        SQLiteDatabase db = getReadableDatabase();
        long now = new Date().getTime();

        String countQuery = "SELECT COUNT(*) FROM " + TABLE_TASKS +
                " WHERE " + KEY_TASK_COMPLETED + " = 0" +
                " AND " + KEY_TASK_DUE_DATE + " < " + now;

        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    // Search tasks
    public List<Task> searchTasks(String query) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String searchQuery = "SELECT * FROM " + TABLE_TASKS +
                " WHERE " + KEY_TASK_TITLE + " LIKE ?" +
                " OR " + KEY_TASK_DESCRIPTION + " LIKE ?" +
                " OR " + KEY_TASK_TAGS + " LIKE ?" +
                " ORDER BY " + KEY_TASK_CREATED_AT + " DESC";

        String searchPattern = "%" + query + "%";
        Cursor cursor = db.rawQuery(searchQuery,
                new String[]{searchPattern, searchPattern, searchPattern});

        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    // Helper method to convert cursor to Task object
    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();

        task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TASK_ID)));
        task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK_TITLE)));
        task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK_DESCRIPTION)));
        task.setPriority(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK_PRIORITY)));
        task.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK_CATEGORY)));

        long dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_TASK_DUE_DATE));
        if (!cursor.isNull(cursor.getColumnIndexOrThrow(KEY_TASK_DUE_DATE))) {
            task.setDueDate(new Date(dueDate));
        }

        long reminderTime = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_TASK_REMINDER));
        if (!cursor.isNull(cursor.getColumnIndexOrThrow(KEY_TASK_REMINDER))) {
            task.setReminderTime(new Date(reminderTime));
        }

        task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TASK_COMPLETED)) == 1);

        long createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_TASK_CREATED_AT));
        task.setCreatedAt(new Date(createdAt));

        long updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_TASK_UPDATED_AT));
        task.setUpdatedAt(new Date(updatedAt));

        task.setTags(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK_TAGS)));
        task.setEstimatedTime(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TASK_ESTIMATED_TIME)));
        task.setActualTime(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TASK_ACTUAL_TIME)));

        return task;
    }

    // Helper methods for date calculations
    private long getStartOfDay() {
        Date date = new Date();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        return date.getTime();
    }

    private long getEndOfDay() {
        Date date = new Date();
        date.setHours(23);
        date.setMinutes(59);
        date.setSeconds(59);
        return date.getTime();
    }

    // ==================== Categories Operations ====================

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORIES,
                new String[]{KEY_CATEGORY_NAME},
                null, null, null, null,
                KEY_CATEGORY_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return categories;
    }

    // ==================== Statistics Methods ====================

    // Get tasks by priority for statistics
    public int getTaskCountByPriority(String priority) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TASKS +
                " WHERE " + KEY_TASK_PRIORITY + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{priority});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    // Get tasks by category for statistics
    public int getTaskCountByCategory(String category) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TASKS +
                " WHERE " + KEY_TASK_CATEGORY + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{category});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    // Get completion rate
    public float getCompletionRate() {
        int total = getTasksCount();
        int completed = getCompletedTasksCount();

        if (total == 0) return 0;
        return (completed * 100.0f) / total;
    }

    // Get average completion time
    public float getAverageCompletionTime() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT AVG(" + KEY_TASK_ACTUAL_TIME + ") FROM " + TABLE_TASKS +
                " WHERE " + KEY_TASK_COMPLETED + " = 1" +
                " AND " + KEY_TASK_ACTUAL_TIME + " > 0";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        float average = cursor.getFloat(0);
        cursor.close();
        db.close();
        return average;
    }
}