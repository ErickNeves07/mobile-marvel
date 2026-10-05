package com.erickbarbosa.rupturainfinita;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/** Rounded layered panel shared by cards and controls in the playful theme. */
final class AngularPanelDrawable extends Drawable {
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path outline = new Path();
    private final int topColor;
    private final int bottomColor;
    private final boolean large;
    private final float cut;

    AngularPanelDrawable(int topColor, int bottomColor, int borderColor, float cut, boolean large) {
        this.topColor = topColor;
        this.bottomColor = bottomColor;
        this.cut = cut;
        this.large = large;
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(1.5f);
        border.setColor(borderColor);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        float left = bounds.left + 1f;
        float top = bounds.top + 1f;
        float right = bounds.right - 1f;
        float bottom = bounds.bottom - 1f;
        float radius = Math.min(cut * (large ? 1.2f : 1f),
                Math.min((right - left) / 2f, (bottom - top) / 2f));
        outline.reset();
        outline.addRoundRect(left, top, right, bottom, radius, radius, Path.Direction.CW);
        fill.setShader(new LinearGradient(left, top, right, bottom, topColor, bottomColor,
                Shader.TileMode.CLAMP));
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(outline, fill);
        canvas.drawPath(outline, border);
    }

    @Override
    public void setAlpha(int alpha) {
        fill.setAlpha(alpha);
        border.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        fill.setColorFilter(colorFilter);
        border.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
