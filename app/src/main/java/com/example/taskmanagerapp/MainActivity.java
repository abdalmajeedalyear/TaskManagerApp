package com.example.taskmanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskUpdateListener,AddTaskBottomSheet.OnTaskCreatedListener {

    private TextView btnAll, btnHighPriority, btnToday, btnCompleted;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAdd;
    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseHelper databaseHelper;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // تهيئة قاعدة البيانات
        databaseHelper = DatabaseHelper.getInstance(this);



        initViews();
        setupRecyclerView();
        setupFilterButtons();
        setupBottomNavigation();
        setupAddTaskButton();

        fabAdd.setOnClickListener(v -> {
            showAddTaskBottomSheet();
        });
    }

    private void initViews() {
        // أزرار التصنيف
        btnAll = findViewById(R.id.btn_all);
        btnHighPriority = findViewById(R.id.btn_high_priority);
        btnToday = findViewById(R.id.btn_today);
        btnCompleted = findViewById(R.id.btn_completed);
        fabAdd = findViewById(R.id.fab_add);

        // RecyclerView
        tasksRecyclerView = findViewById(R.id.tasks_recycler_view);

        // التنقل السفلي
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // زر إضافة مهمة (سيكون في CardView الموجود)
        CardView addTaskCard = findViewById(R.id.add_task_card); // أضف ID للـ CardView في XML
        addTaskCard.setOnClickListener(v -> showAddTaskBottomSheet());




    }

    private void setupRecyclerView() {
        taskList = databaseHelper.getAllTasks();
        taskAdapter = new TaskAdapter(this, taskList);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(taskAdapter);
        tasksRecyclerView.setHasFixedSize(true);
    }



    private void setupAddTaskButton() {
        // إذا كان لديك CardView "إنشاء جاهز تماماً!"
        View addTaskView = findViewById(R.id.add_task_card); // أضف هذا ID
        if (addTaskView != null) {
            addTaskView.setOnClickListener(v -> showAddTaskBottomSheet());
        }
    }

    private void showAddTaskBottomSheet() {
        AddTaskBottomSheet bottomSheet = new AddTaskBottomSheet();
        bottomSheet.setOnTaskCreatedListener(this);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    // تنفيذ واجهة OnTaskCreatedListener
    @Override
    public void onTaskCreated(Task task) {
        // إضافة المهمة إلى قاعدة البيانات
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        long taskId = dbHelper.addTask(task);

        if (taskId != -1) {
            task.setId((int) taskId);

            // تحديث القائمة
            taskList.add(0, task); // إضافة في البداية
            taskAdapter.notifyItemInserted(0);
            tasksRecyclerView.scrollToPosition(0);

            Toast.makeText(this, "تم إضافة المهمة بنجاح", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "حدث خطأ في إضافة المهمة", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupFilterButtons() {
        // زر الكل (محدد افتراضياً)
        setFilterButtonSelected(btnAll);

        btnAll.setOnClickListener(v -> {
            resetFilterButtons();
            setFilterButtonSelected(btnAll);
            filterTasks("all");
        });

        btnHighPriority.setOnClickListener(v -> {
            resetFilterButtons();
            setFilterButtonSelected(btnHighPriority);
            filterTasks("high");
        });

        btnToday.setOnClickListener(v -> {
            resetFilterButtons();
            setFilterButtonSelected(btnToday);
            filterTasks("today");
        });

        btnCompleted.setOnClickListener(v -> {
            resetFilterButtons();
            setFilterButtonSelected(btnCompleted);
            filterTasks("completed");
        });
    }

    private void setFilterButtonSelected(TextView button) {
        button.setBackgroundColor(getResources().getColor(R.color.primary_color));
        button.setTextColor(getResources().getColor(R.color.white));
    }

    private void resetFilterButtons() {
        btnAll.setBackgroundColor(getResources().getColor(R.color.white));
        btnAll.setTextColor(getResources().getColor(R.color.filter_text_unselected));

        btnHighPriority.setBackgroundColor(getResources().getColor(R.color.white));
        btnHighPriority.setTextColor(getResources().getColor(R.color.filter_text_unselected));

        btnToday.setBackgroundColor(getResources().getColor(R.color.white));
        btnToday.setTextColor(getResources().getColor(R.color.filter_text_unselected));

        btnCompleted.setBackgroundColor(getResources().getColor(R.color.white));
        btnCompleted.setTextColor(getResources().getColor(R.color.filter_text_unselected));
    }

    private void filterTasks(String filterType) {
        List<Task> filteredList;

        switch (filterType) {
            case "all":
                filteredList = databaseHelper.getAllTasks();
                break;
            case "high":
                filteredList = databaseHelper.getTasksByPriority("عالية");
                break;
            case "today":
                filteredList = databaseHelper.getTodayTasks();
                break;
            case "completed":
                filteredList = databaseHelper.getCompletedTasks();
                break;
            default:
                filteredList = databaseHelper.getAllTasks();
                break;
        }

        taskAdapter.updateTaskList(filteredList);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // نحن بالفعل في الرئيسية
                showFab();
                return true;
            } else if (itemId == R.id.nav_tasks) {
                // الانتقال لشاشة المهام
                // Intent intent = new Intent(MainActivity.this, TasksActivity.class);
                // startActivity(intent);
                hideFab();
                return true;
            } else if (itemId == R.id.nav_statistics) {
                // الانتقال لشاشة الإحصائيات
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
               startActivity(intent);
                hideFab();
                return true;
            } else if (itemId == R.id.nav_files) {
                hideFab();
                // الانتقال لشاشة الملفات
                return true;
            }
            return false;
        });

        // تحديد العنصر النشط
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void showFab() {
        fabAdd.setVisibility(View.VISIBLE);
        // تأثير الظهور
        fabAdd.animate()
                .scaleX(1f).scaleY(1f)
                .alpha(1f)
                .setDuration(300)
                .start();
    }

    private void hideFab() {
        // تأثير الاختفاء
        fabAdd.animate()
                .scaleX(0f).scaleY(0f)
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> fabAdd.setVisibility(View.GONE))
                .start();
    }

    // تنفيذ دالة الواجهة لتحديث المهمة في قاعدة البيانات
    @Override
    public void onTaskUpdated(Task task) {
        databaseHelper.updateTask(task);
    }

    // دالة لإنشاء مهام تجريبية
    private void createSampleTasks() {
        List<Task> sampleTasks = new ArrayList<>();



        // إضافة المهام إلى قاعدة البيانات
        for (Task task : sampleTasks) {
            databaseHelper.addTask(task);
        }
    }
}