package com.example.taskmanagerapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskBottomSheet extends BottomSheetDialogFragment {
    private LinearLayout dateLayout;
    private LinearLayout timeLayout;



    private TextInputEditText titleEditText, descriptionEditText;
    private TextView dateText, timeText;
    private MaterialButton btnCategoryWork, btnCategoryPersonal, btnCategoryLearning;
    private MaterialButton btnCategoryHealth, btnCategoryMarketing;
    private SwitchMaterial notificationSwitch;
    private Button btnCreateTask;

    private String selectedCategory = "عمل";
    private Date selectedDate;
    private boolean notificationEnabled = true;

    private OnTaskCreatedListener taskCreatedListener;

    public interface OnTaskCreatedListener {
        void onTaskCreated(Task task);
    }

    public void setOnTaskCreatedListener(OnTaskCreatedListener listener) {
        this.taskCreatedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_task_bottom_sheet, container, false);
        initViews(view);
        setupListeners();
        setDefaultValues();
        return view;
    }

    private void initViews(View view) {
        dateLayout = view.findViewById(R.id.date_layout);
        timeLayout = view.findViewById(R.id.time_layout);
        titleEditText = view.findViewById(R.id.title_edit_text);
        descriptionEditText = view.findViewById(R.id.description_edit_text);
        dateText = view.findViewById(R.id.date_text);
        timeText = view.findViewById(R.id.time_text);
        btnCategoryWork = view.findViewById(R.id.btn_category_work);
        btnCategoryPersonal = view.findViewById(R.id.btn_category_personal);
        btnCategoryLearning = view.findViewById(R.id.btn_category_learning);
        btnCategoryHealth = view.findViewById(R.id.btn_category_health);
        btnCategoryMarketing = view.findViewById(R.id.btn_category_marketing);
        notificationSwitch = view.findViewById(R.id.notification_switch);
        btnCreateTask = view.findViewById(R.id.btn_create_task);
    }

    private void setupListeners() {
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        dateText.setOnClickListener(v -> showDatePicker());

        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        // الوقت
        timeText.setOnClickListener(v -> showTimePicker());


        // التصنيفات
        btnCategoryWork.setOnClickListener(v -> selectCategory("عمل", btnCategoryWork));
        btnCategoryPersonal.setOnClickListener(v -> selectCategory("شخصي", btnCategoryPersonal));
        btnCategoryLearning.setOnClickListener(v -> selectCategory("تعليم", btnCategoryLearning));
        btnCategoryHealth.setOnClickListener(v -> selectCategory("صحة", btnCategoryHealth));
        btnCategoryMarketing.setOnClickListener(v -> selectCategory("تسويق", btnCategoryMarketing));

        // التنبيه
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            notificationEnabled = isChecked;
        });

        // زر الإنشاء
        btnCreateTask.setOnClickListener(v -> createTask());
    }

    private void setDefaultValues() {
        // التاريخ الحالي
        selectedDate = new Date();
        updateDateDisplay();
        updateTimeDisplay();

        // التصنيف الافتراضي
        selectCategory("عمل", btnCategoryWork);
    }

    private void selectCategory(String category, MaterialButton selectedButton) {
        selectedCategory = category;

        // إعادة تعيين جميع الأزرار
        resetCategoryButtons();

        // تحديد الزر المختار
        selectedButton.setTextColor(getResources().getColor(R.color.white));
        selectedButton.setBackgroundColor(getResources().getColor(getCategoryColor(category)));
        selectedButton.setStrokeWidth(0);
    }

    private void resetCategoryButtons() {
        int textColor = getResources().getColor(R.color.text_secondary);
        int backgroundColor = getResources().getColor(R.color.white);
        int strokeColor = getResources().getColor(R.color.gray_light);

        btnCategoryWork.setTextColor(textColor);
        btnCategoryWork.setBackgroundColor(backgroundColor);
        btnCategoryWork.setStrokeColorResource(R.color.gray_light);
        btnCategoryWork.setStrokeWidth(1);

        btnCategoryPersonal.setTextColor(textColor);
        btnCategoryPersonal.setBackgroundColor(backgroundColor);
        btnCategoryPersonal.setStrokeColorResource(R.color.gray_light);
        btnCategoryPersonal.setStrokeWidth(1);

        btnCategoryLearning.setTextColor(textColor);
        btnCategoryLearning.setBackgroundColor(backgroundColor);
        btnCategoryLearning.setStrokeColorResource(R.color.gray_light);
        btnCategoryLearning.setStrokeWidth(1);


        MaterialButton[] categoryButtons = {
                btnCategoryWork,
                btnCategoryPersonal,
                btnCategoryLearning,
                btnCategoryHealth,
                btnCategoryMarketing
        };

        for (MaterialButton button : categoryButtons) {
            if (button != null) {
                button.setTextColor(getResources().getColor(R.color.text_secondary));
                button.setBackgroundColor(getResources().getColor(R.color.white));
                button.setStrokeColorResource(R.color.completed_task);
                button.setStrokeWidth(1);
            }
        }
    }

    private int getCategoryColor(String category) {
        switch (category) {
            case "عمل":
                return R.color.category_work;
            case "شخصي":
                return R.color.category_personal;
            case "تعليم":
                return R.color.category_learning;
            case "صحة":
                return R.color.category_health;
            case "تسويق":
                return R.color.category_marketing;
            default:
                return R.color.primary_color;
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDate != null) {
            calendar.setTime(selectedDate);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar newCalendar = Calendar.getInstance();
                    newCalendar.set(selectedYear, selectedMonth, selectedDay);

                    // الحفاظ على الوقت الحالي
                    Calendar timeCalendar = Calendar.getInstance();
                    if (selectedDate != null) {
                        timeCalendar.setTime(selectedDate);
                    }
                    newCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
                    newCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

                    selectedDate = newCalendar.getTime();
                    updateDateDisplay();
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDate != null) {
            calendar.setTime(selectedDate);
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, selectedHour, selectedMinute) -> {
                    Calendar newCalendar = Calendar.getInstance();
                    if (selectedDate != null) {
                        newCalendar.setTime(selectedDate);
                    }
                    newCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    newCalendar.set(Calendar.MINUTE, selectedMinute);

                    selectedDate = newCalendar.getTime();
                    updateTimeDisplay();
                },
                hour, minute, false);

        timePickerDialog.show();
    }

    private void updateDateDisplay() {
        if (selectedDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ar"));
            dateText.setText(dateFormat.format(selectedDate));
        }
    }

    private void updateTimeDisplay() {
        if (selectedDate != null) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", new Locale("ar"));
            timeText.setText(timeFormat.format(selectedDate));
        }
    }

    private void createTask() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "يرجى إدخال عنوان المهمة", Toast.LENGTH_SHORT).show();
            return;
        }

        // تحديد الأولوية بناءً على التصنيف
        String priority = "متوسطة"; // يمكنك تغيير هذا بناءً على منطقك

        // إنشاء المهمة
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setCategory(selectedCategory);
        task.setDueDate(selectedDate);

        // إعداد التنبيه إذا كان مفعلاً
        if (notificationEnabled) {
            Calendar reminderCalendar = Calendar.getInstance();
            reminderCalendar.setTime(selectedDate);
            reminderCalendar.add(Calendar.MINUTE, -15); // قبل 15 دقيقة
            task.setReminderTime(reminderCalendar.getTime());
        }

        task.setEstimatedTime(60); // الوقت المقدر افتراضياً 60 دقيقة

        // إرسال المهمة عبر المستمع
        if (taskCreatedListener != null) {
            taskCreatedListener.onTaskCreated(task);
        }

        Toast.makeText(getContext(), "تم إنشاء المهمة بنجاح", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}