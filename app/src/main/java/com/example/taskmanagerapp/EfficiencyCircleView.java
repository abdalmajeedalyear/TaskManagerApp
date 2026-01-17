package com.example.taskmanagerapp;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class EfficiencyCircleView extends View {

    private Paint paintBackground;
    private Paint paintProgress;
    private Paint paintText;
    private RectF rectF;

    private float progress = 85; // 85%
    private String text = "85%";

    public EfficiencyCircleView(Context context) {
        super(context);
        init();
    }

    public EfficiencyCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // خلفية الدائرة
        paintBackground = new Paint();
        paintBackground.setColor(Color.LTGRAY);
        paintBackground.setStyle(Paint.Style.STROKE);
        paintBackground.setStrokeWidth(8);
        paintBackground.setAntiAlias(true);

        // شريط التقدم
        paintProgress = new Paint();
        paintProgress.setColor(getResources().getColor(R.color.primary_color));
        paintProgress.setStyle(Paint.Style.STROKE);
        paintProgress.setStrokeWidth(8);
        paintProgress.setAntiAlias(true);

        // النص
        paintText = new Paint();
        paintText.setColor(getResources().getColor(R.color.text_primary));
        paintText.setTextSize(24);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setAntiAlias(true);

        rectF = new RectF();
    }

    public void setProgress(float progress, String text) {
        this.progress = progress;
        this.text = text;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) * 0.4f;
        float centerX = width / 2f;
        float centerY = height / 2f;

        // إعداد المستطيل المحيط
        rectF.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );

        // رسم الخلفية
        canvas.drawCircle(centerX, centerY, radius, paintBackground);

        // رسم شريط التقدم
        float sweepAngle = 360 * progress / 100;
        canvas.drawArc(rectF, -90, sweepAngle, false, paintProgress);

        // رسم النص
        Paint.FontMetrics fontMetrics = paintText.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        float textY = centerY - (fontMetrics.ascent + fontMetrics.descent) / 2;

        canvas.drawText(text, centerX, textY, paintText);
    }
}