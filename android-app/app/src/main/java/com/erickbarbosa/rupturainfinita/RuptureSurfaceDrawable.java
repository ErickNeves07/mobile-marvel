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

/** Decorative Nexus hero surface. Geometry scales to its bounds and exposes no accessibility node. */
final class RuptureSurfaceDrawable extends Drawable {
    private final Paint surfacePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint cyanPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint goldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path clipPath = new Path();
    private final Path fracturePath = new Path();
    private final RectF surfaceRect = new RectF();
    private final Rect shaderBounds = new Rect();
    private final float cornerRadius;
    private final int startColor;
    private final int endColor;
    private Shader surfaceShader;
    private int alpha = 255;

    RuptureSurfaceDrawable(@NonNull Context context) {
        startColor = context.getColor(R.color.surface_elevated);
        endColor = context.getColor(R.color.surface_primary);
        int cyan = context.getColor(R.color.accent_cyan);
        int gold = context.getColor(R.color.accent_gold);
        int border = context.getColor(R.color.border_subtle);
        cornerRadius = context.getResources().getDimension(R.dimen.radius_card);

        cyanPaint.setColor(withAlpha(cyan, 44));
        cyanPaint.setStyle(Paint.Style.STROKE);
        cyanPaint.setStrokeWidth(context.getResources().getDimension(R.dimen.border_width) * 1.5f);
        goldPaint.setColor(withAlpha(gold, 34));
        goldPaint.setStyle(Paint.Style.STROKE);
        goldPaint.setStrokeWidth(context.getResources().getDimension(R.dimen.border_width));
        borderPaint.setColor(border);
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
        float centerX = bounds.left + width * 0.89f;
        float centerY = bounds.top + height * 0.08f;
        float radius = Math.min(width * 0.2f, Math.max(height * 0.22f, cornerRadius * 1.7f));

        canvas.drawCircle(centerX, centerY, radius, cyanPaint);
        canvas.drawCircle(centerX, centerY, radius * 0.72f, goldPaint);
        fracturePath.reset();
        fracturePath.moveTo(bounds.left + width * 0.67f, bounds.top - height * 0.04f);
        fracturePath.lineTo(bounds.left + width * 0.79f, bounds.top + height * 0.08f);
        fracturePath.lineTo(bounds.left + width * 0.72f, bounds.top + height * 0.16f);
        fracturePath.lineTo(bounds.left + width * 0.91f, bounds.top + height * 0.24f);
        canvas.drawPath(fracturePath, cyanPaint);
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
