package com.erickbarbosa.rupturainfinita;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/** Calm cosmic backdrop shared by the opening and authored story scenes. */
final class IntroRiftDrawable extends Drawable {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int background;

    IntroRiftDrawable(Context context) {
        background = context.getColor(R.color.canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.drawColor(background);
        float radius = Math.max(bounds.width(), bounds.height()) * .42f;
        glow(canvas, bounds.left + bounds.width() * .08f,
                bounds.top + bounds.height() * .18f, radius, 0x39EF767A);
        glow(canvas, bounds.right - bounds.width() * .07f,
                bounds.bottom - bounds.height() * .17f, radius, 0x287BD389);
        paint.setShader(null);
    }

    private void glow(Canvas canvas, float x, float y, float radius, int color) {
        paint.setShader(new RadialGradient(x, y, radius,
                new int[]{color, Color.TRANSPARENT}, null, Shader.TileMode.CLAMP));
        canvas.drawCircle(x, y, radius, paint);
        paint.setShader(null);
    }

    @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); invalidateSelf(); }
    @Override public void setColorFilter(ColorFilter filter) { paint.setColorFilter(filter); invalidateSelf(); }
    @Override public int getOpacity() { return PixelFormat.OPAQUE; }
}
