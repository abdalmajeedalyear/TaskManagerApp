package com.example.taskmanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class StatisticsActivity extends AppCompatActivity {



    private MaterialButton btnWeekly, btnYearly, btnMonthly, btnFocusTime;
    private TextView tvEfficiencyPercentage, tvFocusTime, tvIncreasePercentage;
    private TextView tvPastDays;
    private TextView tvWorkCount, tvPersonalCount, tvLearningCount;
    private TextView tvGoalPercentage;
    private MaterialButton btnViewDetails;
    private CardView cardAchievement;
    private BottomNavigationView bottomNavigationView;

    // ProgressBars لتوزيع المهام
    private ProgressBar progressWork, progressPersonal, progressLearning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // تهيئة العناصر
        initViews();

        // إعداد المستمعين للأزرار
        setupButtonListeners();

        // إعداد التنقل السفلي
        setupBottomNavigation();

        // تعيين القيم الافتراضية
        setDefaultValues();

        // تحديث توزيع المهام
        updateTaskDistribution();

        // اختيار الزر الأسبوعي كافتراضياً
        selectWeeklyButton();
    }

    private void initViews() {
        // أزرار الفترات الزمنية
        btnWeekly = findViewById(R.id.btn_weekly);
        btnYearly = findViewById(R.id.btn_yearly);
        btnMonthly = findViewById(R.id.btn_monthly);
        btnFocusTime = findViewById(R.id.btn_focus_time);

        // النصوص الجديدة
        tvEfficiencyPercentage = findViewById(R.id.tv_efficiency_percentage);
        tvFocusTime = findViewById(R.id.tv_focus_time);
        tvIncreasePercentage = findViewById(R.id.tv_increase_percentage);
        tvPastDays = findViewById(R.id.tv_past_days);

        // نصوص توزيع المهام (الأرقام)
        tvWorkCount = findViewById(R.id.tv_work_count);
        tvPersonalCount = findViewById(R.id.tv_personal_count);
        tvLearningCount = findViewById(R.id.tv_learning_count);

        // ProgressBars لتوزيع المهام
        progressWork = findViewById(R.id.progress_work);
        progressPersonal = findViewById(R.id.progress_personal);
        progressLearning = findViewById(R.id.progress_learning);

        // الأزرار الأخرى
        tvGoalPercentage = findViewById(R.id.tv_goal_percentage);
        btnViewDetails = findViewById(R.id.btn_view_details);
        cardAchievement = findViewById(R.id.card_achievement);

        // التنقل السفلي
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupButtonListeners() {
        // أزرار الفترات الزمنية
        btnWeekly.setOnClickListener(v -> selectWeeklyButton());
        btnYearly.setOnClickListener(v -> selectYearlyButton());
        btnMonthly.setOnClickListener(v -> selectMonthlyButton());
        btnFocusTime.setOnClickListener(v -> selectFocusTimeButton());

        // زر عرض التفاصيل
        btnViewDetails.setOnClickListener(v -> {
            // سيتم الانتقال لشاشة التفاصيل لاحقاً
        });
    }

    private void selectWeeklyButton() {
        btnWeekly.setBackgroundResource(R.drawable.button_outlined_selected);
        btnWeekly.setTextColor(getResources().getColor(R.color.primary_color));

        btnYearly.setBackgroundResource(R.drawable.button_transparent);
        btnYearly.setTextColor(getResources().getColor(R.color.text_secondary));

        btnMonthly.setBackgroundResource(R.drawable.button_transparent);
        btnMonthly.setTextColor(getResources().getColor(R.color.text_secondary));

        btnFocusTime.setBackgroundResource(R.drawable.button_transparent);
        btnFocusTime.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void selectYearlyButton() {
        btnYearly.setBackgroundResource(R.drawable.button_outlined_selected);
        btnYearly.setTextColor(getResources().getColor(R.color.primary_color));

        btnWeekly.setBackgroundResource(R.drawable.button_transparent);
        btnWeekly.setTextColor(getResources().getColor(R.color.text_secondary));

        btnMonthly.setBackgroundResource(R.drawable.button_transparent);
        btnMonthly.setTextColor(getResources().getColor(R.color.text_secondary));

        btnFocusTime.setBackgroundResource(R.drawable.button_transparent);
        btnFocusTime.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void selectMonthlyButton() {
        btnMonthly.setBackgroundResource(R.drawable.button_outlined_selected);
        btnMonthly.setTextColor(getResources().getColor(R.color.primary_color));

        btnWeekly.setBackgroundResource(R.drawable.button_transparent);
        btnWeekly.setTextColor(getResources().getColor(R.color.text_secondary));

        btnYearly.setBackgroundResource(R.drawable.button_transparent);
        btnYearly.setTextColor(getResources().getColor(R.color.text_secondary));

        btnFocusTime.setBackgroundResource(R.drawable.button_transparent);
        btnFocusTime.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void selectFocusTimeButton() {
        btnFocusTime.setBackgroundResource(R.drawable.button_outlined_selected);
        btnFocusTime.setTextColor(getResources().getColor(R.color.primary_color));

        btnWeekly.setBackgroundResource(R.drawable.button_transparent);
        btnWeekly.setTextColor(getResources().getColor(R.color.text_secondary));

        btnYearly.setBackgroundResource(R.drawable.button_transparent);
        btnYearly.setTextColor(getResources().getColor(R.color.text_secondary));

        btnMonthly.setBackgroundResource(R.drawable.button_transparent);
        btnMonthly.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_statistics) {
                // نحن بالفعل في الرئيسية
                return true;
            } else if (itemId == R.id.nav_tasks) {
                // الانتقال لشاشة المهام
                // Intent intent = new Intent(MainActivity.this, TasksActivity.class);
                // startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_home) {
                Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_files) {
                // الانتقال لشاشة الملفات
                return true;
            }
            return false;
        });

        // تحديد العنصر النشط
        bottomNavigationView.setSelectedItemId(R.id.nav_statistics);
    }

    private void setDefaultValues() {
        // تعيين القيم كما في التصميم الجديد
        tvEfficiencyPercentage.setText("85%");
        tvFocusTime.setText("24 ساعة");
        tvIncreasePercentage.setText("+12%");
        tvPastDays.setText("١٤٢");
        tvGoalPercentage.setText("١٥/٦٥");
    }

    private void updateTaskDistribution() {
        // قيم المهام
        int workTasks = 250;
        int personalTasks = 300;
        int learningTasks = 215;

        // تحديث شريط التقدم
        progressWork.setProgress(workTasks);
        progressPersonal.setProgress(personalTasks);
        progressLearning.setProgress(learningTasks);

        // تحديث النصوص
        tvWorkCount.setText(String.valueOf(workTasks));
        tvPersonalCount.setText(String.valueOf(personalTasks));
        tvLearningCount.setText(String.valueOf(learningTasks));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // تحديث البيانات عند العودة للتطبيق
    }
}