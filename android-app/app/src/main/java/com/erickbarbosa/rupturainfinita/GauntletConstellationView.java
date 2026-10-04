package com.erickbarbosa.rupturainfinita;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;

/** Decorative, state-driven rendering of the six containment sockets. */
final class GauntletConstellationView extends View {
    private static final int[] STONE_COLORS = {
            0xFF2E7BFF, 0xFFFFD84A, 0xFFE53945,
            0xFF9B5CFF, 0xFF36D17E, 0xFFFF8A3D
    };
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final boolean[] complete = new boolean[6];

    GauntletConstellationView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        setMinimumHeight(dp(300));
        describe();
    }

    void setCompleted(boolean[] state) {
        if (state == null || state.length != 6) throw new IllegalArgumentException("Six Stone states required");
        System.arraycopy(state, 0, complete, 0, 6);
        describe();
        invalidate();
    }

    private void describe() {
        int count = 0;
        for (boolean value : complete) if (value) count++;
        setContentDescription("Manopla de Contenção: " + count + " de 6 Joias completas");
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth(), h = getHeight();
        float cx = w / 2f, cy = h / 2f;
        float unit = Math.min(w / 350f, h / 300f);
        float orbit = 108f * unit;

        paint.clearShadowLayer();
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, 0, h, 0xFF0D111B, 0xFF05070C,
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(new RadialGradient(cx, cy, 155f * unit,
                new int[]{0x55E0B45C, 0x1539566A, 0x0005070C}, null, Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, 155f * unit, paint);
        paint.setShader(null);

        paint.setColor(0x244A5B6D);
        for (float x = 8f * unit; x < w; x += 8f * unit) {
            for (float y = 8f * unit; y < h; y += 8f * unit) {
                canvas.drawCircle(x, y, .55f * unit, paint);
            }
        }
        paint.setStyle(Paint.Style.STROKE);
        for (int index = 0; index < 3; index++) {
            paint.setStrokeWidth(index == 1 ? 1.3f * unit : .8f * unit);
            paint.setColor(index == 1 ? 0x66E0B45C : 0x5548E0FF);
            paint.setShadowLayer(index == 1 ? 12f * unit : 6f * unit, 0, 0, 0x66E0B45C);
            canvas.drawCircle(cx, cy, (65 + 26 * index) * unit, paint);
        }
        paint.clearShadowLayer();
        paint.setStyle(Paint.Style.FILL);

        float half = 56f * unit;
        Path center = new Path();
        center.moveTo(cx - half, cy - half);
        center.lineTo(cx + half - 15f * unit, cy - half);
        center.lineTo(cx + half, cy - half + 15f * unit);
        center.lineTo(cx + half, cy + half);
        center.lineTo(cx - half, cy + half);
        center.close();
        paint.setShader(new LinearGradient(cx, cy - half, cx, cy + half,
                0xFF1B2130, 0xFF0B0F18, Shader.TileMode.CLAMP));
        paint.setShadowLayer(30f * unit, 0, 0, 0x77E0B45C);
        canvas.drawPath(center, paint);
        paint.clearShadowLayer();
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f * unit);
        paint.setColor(0xAAE0B45C);
        canvas.drawPath(center, paint);
        paint.setStyle(Paint.Style.FILL);
        int count = 0;
        for (boolean value : complete) if (value) count++;
        paint.setTypeface(getResources().getFont(R.font.barlow_condensed_bold));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(13f * unit);
        paint.setColor(0xFFE0B45C);
        canvas.drawText(count + " / 6", cx, cy + 5f * unit, paint);

        for (int index = 0; index < 6; index++) {
            double angle = index * Math.PI / 3.0 - Math.PI / 2.0;
            float x = cx + (float) Math.cos(angle) * orbit;
            float y = cy + (float) Math.sin(angle) * orbit;
            drawSocket(canvas, x, y, STONE_COLORS[index], complete[index], unit);
        }
        paint.setShader(new LinearGradient(w - 15f * unit, h - 70f * unit,
                w - 15f * unit, h - 8f * unit, 0x009B5CFF, 0xAA9B5CFF,
                Shader.TileMode.CLAMP));
        canvas.drawRect(w - 16f * unit, h - 70f * unit,
                w - 13f * unit, h - 8f * unit, paint);
        paint.setShader(null);
    }

    private void drawSocket(Canvas canvas, float x, float y, int color, boolean filled, float unit) {
        float outer = 24f * unit;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(filled ? withAlpha(color, 30) : 0xFF0A0E16);
        if (filled) paint.setShadowLayer(22f * unit, 0, 0, color);
        canvas.drawCircle(x, y, outer, paint);
        paint.clearShadowLayer();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f * unit);
        paint.setColor(filled ? color : 0xFF344054);
        canvas.drawCircle(x, y, outer, paint);
        float radius = 11f * unit;
        Path hex = new Path();
        for (int corner = 0; corner < 6; corner++) {
            double angle = Math.PI / 3.0 * corner - Math.PI / 2.0;
            float px = x + (float) Math.cos(angle) * radius;
            float py = y + (float) Math.sin(angle) * radius;
            if (corner == 0) hex.moveTo(px, py); else hex.lineTo(px, py);
        }
        hex.close();
        paint.setStrokeWidth(2f * unit);
        paint.setColor(color);
        if (filled) {
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(12f * unit, 0, 0, color);
            canvas.drawPath(hex, paint);
            paint.clearShadowLayer();
            paint.setColor(Color.WHITE);
            canvas.drawCircle(x - 2f * unit, y - 3f * unit, 2.5f * unit, paint);
        } else {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(hex, paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private static int withAlpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + .5f);
    }
}
