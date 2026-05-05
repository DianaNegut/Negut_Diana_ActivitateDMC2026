package com.example.proiect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

public class PieChartView extends View {

    private final Paint piePaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint boxPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF pieRect    = new RectF();

    private Map<String, Integer> data = new LinkedHashMap<>();

    private static final int[] COLORS = {
        Color.parseColor("#1565C0"),
        Color.parseColor("#00695C"),
        Color.parseColor("#4527A0"),
        Color.parseColor("#E65100"),
        Color.parseColor("#2E7D32"),
        Color.parseColor("#607D8B")
    };

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        valuePaint.setColor(Color.WHITE);
        valuePaint.setTextSize(32f);
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setTextScaleX(1f);

        labelPaint.setColor(Color.parseColor("#212121"));
        labelPaint.setTextSize(30f);
    }

    public void setData(Map<String, Integer> data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || data.isEmpty() || getWidth() == 0) return;

        int w = getWidth();
        float radius  = Math.min(w, getHeight()) * 0.32f;
        float centerX = w / 2f;
        float centerY = radius + 20f;

        pieRect.set(centerX - radius, centerY - radius,
                    centerX + radius, centerY + radius);

        int total = 0;
        for (int v : data.values()) total += v;
        if (total == 0) return;

        float startAngle = -90f;
        int idx = 0;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            float sweep = (entry.getValue() * 360f) / total;
            piePaint.setColor(COLORS[idx % COLORS.length]);
            canvas.drawArc(pieRect, startAngle, sweep, true, piePaint);

            if (sweep > 18) {
                double mid = Math.toRadians(startAngle + sweep / 2f);
                float tx = centerX + (radius * 0.62f) * (float) Math.cos(mid);
                float ty = centerY + (radius * 0.62f) * (float) Math.sin(mid)
                           + valuePaint.getTextSize() / 3f;
                int pct = Math.round(entry.getValue() * 100f / total);
                canvas.drawText(pct + "%", tx, ty, valuePaint);
            }

            startAngle += sweep;
            idx++;
        }

        // Legend
        float legendX = 16f;
        float legendY = centerY + radius + 48f;
        idx = 0;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            boxPaint.setColor(COLORS[idx % COLORS.length]);
            canvas.drawRect(legendX, legendY - 24f, legendX + 28f, legendY + 4f, boxPaint);
            canvas.drawText(entry.getKey() + "  (" + entry.getValue() + ")",
                    legendX + 38f, legendY, labelPaint);
            legendY += 46f;
            idx++;
        }
    }
}
