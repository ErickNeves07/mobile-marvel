package com.erickbarbosa.rupturainfinita;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** Static, decorative comic-panel surface for the Deadpool placeholder hero. */
final class ComicPanelDrawable extends Drawable {
    private final Paint surfacePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint cyanPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint goldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path clipPath = new Path();
    private final Path panelMark = new Path();
    private final RectF surfaceRect = new RectF();
    private final Rect shaderBounds = new Rect();
    private final float cornerRadius;
    private final int startColor;
    private final int endColor;
    private Shader surfaceShader;
    private int alpha = 255;

    ComicPanelDrawable(@NonNull Context context) {
        startColor = context.getColor(R.color.surface_elevated);
        endColor = context.getColor(R.color.surface_primary);
        cornerRadius = context.getResources().getDimension(R.dimen.radius_card);
        cyanPaint.setColor(withAlpha(context.getColor(R.color.accent_cyan), 42));
        cyanPaint.setStyle(Paint.Style.STROKE);
        cyanPaint.setStrokeWidth(context.getResources().getDimension(R.dimen.border_width) * 1.5f);
        goldPaint.setColor(withAlpha(context.getColor(R.color.accent_gold), 32));
        goldPaint.setStyle(Paint.Style.STROKE);
        goldPaint.setStrokeWidth(context.getResources().getDimension(R.dimen.border_width));
        borderPaint.setColor(context.getColor(R.color.border_subtle));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(context.getResources().getDimension(R.dimen.border_width));
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds.isEmpty()) return;

        int save = alpha == 255
                ? canvas.save()
                : canvas.saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, alpha);
        if (surfaceShader == null || !shaderBounds.equals(bounds)) {
            surfaceShader = new LinearGradient(bounds.left, bounds.top, bounds.right, bounds.bottom,
                    startColor, endColor, Shader.TileMode.CLAMP);
            shaderBounds.set(bounds);
            surfacePaint.setShader(surfaceShader);
        }
        surfaceRect.set(bounds);
        clipPath.reset();
        clipPath.addRoundRect(surfaceRect, cornerRadius, cornerRadius, Path.Direction.CW);
        canvas.drawRoundRect(surfaceRect, cornerRadius, cornerRadius, surfacePaint);

        int clipped = canvas.save();
        canvas.clipPath(clipPath);
        float width = bounds.width();
        float height = bounds.height();
        panelMark.reset();
        panelMark.moveTo(bounds.left + width * 0.63f, bounds.top - height * 0.03f);
        panelMark.lineTo(bounds.left + width * 0.83f, bounds.top - height * 0.03f);
        panelMark.lineTo(bounds.left + width * 0.76f, bounds.top + height * 0.09f);
        panelMark.lineTo(bounds.left + width * 0.98f, bounds.top + height * 0.17f);
        panelMark.lineTo(bounds.left + width * 0.91f, bounds.top + height * 0.24f);
        panelMark.lineTo(bounds.left + width * 0.69f, bounds.top + height * 0.16f);
        panelMark.close();
        canvas.drawPath(panelMark, cyanPaint);

        panelMark.reset();
        panelMark.moveTo(bounds.left + width * 0.73f, bounds.top + height * 0.03f);
        panelMark.lineTo(bounds.left + width * 0.88f, bounds.top + height * 0.19f);
        canvas.drawPath(panelMark, goldPaint);
        panelMark.reset();
        panelMark.moveTo(bounds.left + width * 0.82f, bounds.top + height * 0.02f);
        panelMark.lineTo(bounds.left + width * 0.97f, bounds.top + height * 0.13f);
        canvas.drawPath(panelMark, cyanPaint);
        canvas.restoreToCount(clipped);

        canvas.drawRoundRect(surfaceRect, cornerRadius, cornerRadius, borderPaint);
        canvas.restoreToCount(save);
    }

    private int withAlpha(int color, int componentAlpha) {
        return Color.argb(componentAlpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    @Override
    public void setAlpha(int value) {
        alpha = Math.max(0, Math.min(255, value));
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        surfacePaint.setColorFilter(colorFilter);
        cyanPaint.setColorFilter(colorFilter);
        goldPaint.setColorFilter(colorFilter);
        borderPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
