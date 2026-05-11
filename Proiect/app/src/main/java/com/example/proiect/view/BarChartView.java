package com.example.proiect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

public class BarChartView extends View {

    private final Paint barPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint yPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Map<String, Float> data = new LinkedHashMap<>();

    private static final int[] COLORS = {
        Color.parseColor("#1E88E5"),
        Color.parseColor("#00695C"),
        Color.parseColor("#4527A0"),
        Color.parseColor("#E65100"),
        Color.parseColor("#2E7D32"),
        Color.parseColor("#607D8B")
    };

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        axisPaint.setColor(Color.parseColor("#BDBDBD"));
        axisPaint.setStrokeWidth(2f);

        gridPaint.setColor(Color.parseColor("#E0E0E0"));
        gridPaint.setStrokeWidth(1f);

        valPaint.setColor(Color.WHITE);
        valPaint.setTextSize(28f);
        valPaint.setTextAlign(Paint.Align.CENTER);

        labelPaint.setColor(Color.parseColor("#212121"));
        labelPaint.setTextSize(24f);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        yPaint.setColor(Color.parseColor("#374151"));
        yPaint.setTextSize(22f);
        yPaint.setTextAlign(Paint.Align.RIGHT);
    }

    public void setData(Map<String, Float> data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || data.isEmpty() || getWidth() == 0) return;

        float padL = 64f, padR = 16f, padT = 20f, padB = 70f;
        float chartW = getWidth()  - padL - padR;
        float chartH = getHeight() - padT - padB;

        // Axes
        canvas.drawLine(padL, padT, padL, getHeight() - padB, axisPaint);
        canvas.drawLine(padL, getHeight() - padB,
                        getWidth() - padR, getHeight() - padB, axisPaint);

        // Y grid + labels  (0..5)
        for (int i = 0; i <= 5; i++) {
            float y = getHeight() - padB - i * chartH / 5f;
            canvas.drawLine(padL, y, getWidth() - padR, y, gridPaint);
            canvas.drawText(String.valueOf(i), padL - 6f, y + 8f, yPaint);
        }

        int n = data.size();
        if (n == 0) return;
        float slotW  = chartW / n;
        float barW   = slotW * 0.55f;
        float startX = padL + (slotW - barW) / 2f;
        int idx = 0;

        for (Map.Entry<String, Float> entry : data.entrySet()) {
            float value    = Math.min(entry.getValue(), 5f);
            float barH     = (value / 5f) * chartH;
            float left     = startX + idx * slotW;
            float top      = getHeight() - padB - barH;
            float right    = left + barW;
            float bottom   = getHeight() - padB;

            barPaint.setColor(COLORS[idx % COLORS.length]);
            canvas.drawRect(left, top, right, bottom, barPaint);

            if (barH > 36) {
                canvas.drawText(String.format("%.1f", value),
                        left + barW / 2f, top + 30f, valPaint);
            }

            // Short label
            String label = entry.getKey();
            if (label.length() > 7) label = label.substring(0, 7);
            canvas.drawText(label, left + barW / 2f,
                    getHeight() - padB + 40f, labelPaint);

            idx++;
        }
    }
}
