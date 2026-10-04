package com.erickbarbosa.rupturainfinita;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/** Static rendition of the Lovable opening's central six-color rift. */
final class IntroRiftDrawable extends Drawable {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int[] gemColors;

    IntroRiftDrawable(Context context) {
        gemColors = new int[]{
                context.getColor(R.color.stone_space), context.getColor(R.color.stone_power),
                context.getColor(R.color.stone_reality), context.getColor(R.color.stone_mind),
                context.getColor(R.color.stone_time), context.getColor(R.color.stone_space)};
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.drawColor(Color.rgb(5, 7, 12));
        float centerX = bounds.exactCenterX();
        float centerY = bounds.exactCenterY();
        float radius = Math.min(bounds.width() * .55f, bounds.height() * .38f);
        paint.setShader(new RadialGradient(centerX, centerY, radius,
                new int[]{0x223B55A9, 0x1A7C3B91, 0x11741D35, 0x0005070C},
                new float[]{0f, .35f, .7f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawCircle(centerX, centerY, radius, paint);
        paint.setShader(null);

        float top = bounds.top;
        float bottom = bounds.bottom;
        int[] glowColors = new int[gemColors.length];
        for (int index = 0; index < gemColors.length; index++) {
            glowColors[index] = (gemColors[index] & 0x00FFFFFF) | 0x22000000;
        }
        paint.setShader(new LinearGradient(centerX, top, centerX, bottom,
                glowColors, null, Shader.TileMode.CLAMP));
        canvas.drawRect(centerX - 28f, top, centerX + 28f, bottom, paint);
        paint.setShader(new LinearGradient(centerX, top, centerX, bottom,
                gemColors, null, Shader.TileMode.CLAMP));
        paint.setAlpha(145);
        canvas.drawRect(centerX - 1.5f, top, centerX + 1.5f, bottom, paint);
        paint.setShader(null);
        paint.setAlpha(255);
    }

    @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); invalidateSelf(); }
    @Override public void setColorFilter(ColorFilter filter) { paint.setColorFilter(filter); invalidateSelf(); }
    @Override public int getOpacity() { return PixelFormat.OPAQUE; }
}
