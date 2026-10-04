package com.erickbarbosa.rupturainfinita;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/** Native recreation of the published Nexus header's gradients, stars and portal rings. */
final class NexusPortalDrawable extends Drawable {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;

    NexusPortalDrawable(float density) {
        this.density = density;
    }

    @Override public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        float left = bounds.left;
        float top = bounds.top;
        float width = bounds.width();
        float height = bounds.height();
        canvas.drawColor(0xFF05070C);

        glow(canvas, left + width * .5f, top, width * .76f, 0x2430C7E5);
        glow(canvas, left + width * .2f, top + height, width * .5f, 0x1F9B5CFF);
        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 44; i++) {
            float x = left + width * (((i * 73 + 19) % 101) / 101f);
            float y = top + height * (((i * 47 + 13) % 97) / 97f);
            paint.setColor(i % 7 == 0 ? 0xAAFFD9A8 : 0x99CFE8FF);
            canvas.drawCircle(x, y, (i % 9 == 0 ? 1.25f : .65f) * density, paint);
        }

        float centerX = left + width / 2f;
        float centerY = top + 211f * density;
        ring(canvas, centerX, centerY, 115f * density, 0x8830C7E5, 1f * density);
        ring(canvas, centerX, centerY, 75f * density, 0x88E0B45C, 1f * density);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x409B5CFF);
        canvas.drawRoundRect(left + width - 47f * density, top + height - 160f * density,
                left + width - 19f * density, top + height - 96f * density,
                14f * density, 14f * density, paint);
    }

    private void glow(Canvas canvas, float x, float y, float radius, int color) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(x, y, radius,
                new int[]{color, 0x0005070C}, null, Shader.TileMode.CLAMP));
        canvas.drawCircle(x, y, radius, paint);
        paint.setShader(null);
    }

    private void ring(Canvas canvas, float x, float y, float radius, int color, float stroke) {
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setColor(color);
        canvas.drawCircle(x, y, radius, paint);
    }

    @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); invalidateSelf(); }
    @Override public void setColorFilter(ColorFilter colorFilter) { paint.setColorFilter(colorFilter); invalidateSelf(); }
    @Override public int getOpacity() { return PixelFormat.OPAQUE; }
}
