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

/** The clipped, layered panel shape used by the published Lovable prototype. */
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
        border.setStrokeWidth(1f);
        border.setColor(borderColor);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        float left = bounds.left + 1f;
        float top = bounds.top + 1f;
        float right = bounds.right - 1f;
        float bottom = bounds.bottom - 1f;
        float safeCut = Math.min(cut, Math.min((right - left) / 3f, (bottom - top) / 3f));
        outline.reset();
        outline.moveTo(left, large ? top + safeCut : top);
        outline.lineTo(large ? left + safeCut : right, top);
        if (large) {
            outline.lineTo(right - safeCut, top);
            outline.lineTo(right, top + safeCut);
            outline.lineTo(right, bottom);
            outline.lineTo(left + safeCut, bottom);
            outline.lineTo(left, bottom - safeCut);
        } else {
            outline.lineTo(right, bottom - safeCut);
            outline.lineTo(right - safeCut, bottom);
            outline.lineTo(left, bottom);
        }
        outline.close();
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
