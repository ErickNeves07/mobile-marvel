package com.erickbarbosa.rupturainfinita;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

/** Decorative six-stone portal for the opening; all character art remains editorial. */
final class PlayfulOrbitView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int[] gems;
    private final int night;
    private final int soft;
    private final int sun;
    private final int mint;

    PlayfulOrbitView(Context context) {
        super(context);
        gems = new int[]{context.getColor(R.color.stone_space),
                context.getColor(R.color.stone_mind), context.getColor(R.color.stone_reality),
                context.getColor(R.color.stone_power), context.getColor(R.color.stone_time),
                context.getColor(R.color.stone_soul)};
        night = context.getColor(R.color.play_night);
        soft = context.getColor(R.color.play_soft);
        sun = context.getColor(R.color.play_sun);
        mint = context.getColor(R.color.play_mint);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float unit = Math.min(getWidth(), getHeight()) / 240f;
        canvas.save();
        canvas.translate((getWidth() - 240f * unit) / 2f,
                (getHeight() - 240f * unit) / 2f);
        canvas.scale(unit, unit);

        canvas.save();
        canvas.rotate(7f, 120f, 120f);
        paint.setColor(0x665F425A);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(25f, 24f, 215f, 217f), 49f, 49f, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-4f, 120f, 120f);
        paint.setColor(soft);
        canvas.drawRoundRect(new RectF(35f, 36f, 205f, 207f), 43f, 43f, paint);
        paint.setColor(sun);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        canvas.drawRoundRect(new RectF(35f, 36f, 205f, 207f), 43f, 43f, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();

        paint.setColor(night);
        canvas.drawCircle(120f, 121f, 55f, paint);
        drawRocket(canvas);
        drawSparkle(canvas, 164f, 86f, 10f);
        for (int index = 0; index < gems.length; index++) {
            double angle = Math.toRadians(-90 + index * 60);
            float x = 120f + (float) Math.cos(angle) * 94f;
            float y = 121f + (float) Math.sin(angle) * 94f;
            drawGem(canvas, x, y, gems[index]);
        }
        canvas.restore();
    }

    private void drawRocket(Canvas canvas) {
        canvas.save();
        canvas.rotate(-17f, 120f, 121f);
        paint.setColor(mint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5f);
        Path hull = new Path();
        hull.moveTo(108f, 137f);
        hull.cubicTo(103f, 112f, 114f, 98f, 138f, 88f);
        hull.cubicTo(142f, 112f, 131f, 129f, 108f, 137f);
        hull.close();
        canvas.drawPath(hull, paint);
        canvas.drawCircle(124f, 110f, 6f, paint);
        canvas.drawLine(108f, 121f, 96f, 124f, paint);
        canvas.drawLine(116f, 135f, 114f, 146f, paint);
        canvas.drawLine(102f, 141f, 95f, 150f, paint);
        canvas.restore();
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawGem(Canvas canvas, float x, float y, int color) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((color & 0x00FFFFFF) | 0x44000000);
        canvas.drawCircle(x, y, 16f, paint);
        paint.setColor(color);
        Path hex = new Path();
        for (int corner = 0; corner < 6; corner++) {
            double angle = Math.toRadians(30 + corner * 60);
            float px = x + (float) Math.cos(angle) * 11f;
            float py = y + (float) Math.sin(angle) * 11f;
            if (corner == 0) hex.moveTo(px, py); else hex.lineTo(px, py);
        }
        hex.close();
        canvas.drawPath(hex, paint);
        paint.setColor(0x88FFFFFF);
        canvas.drawCircle(x - 2f, y - 3f, 2.5f, paint);
    }

    private void drawSparkle(Canvas canvas, float x, float y, float size) {
        paint.setColor(sun);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.3f);
        canvas.drawLine(x - size, y, x + size, y, paint);
        canvas.drawLine(x, y - size, x, y + size, paint);
        paint.setStyle(Paint.Style.FILL);
    }
}
