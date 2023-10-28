package com.licenta.monitorizareavioane.mainpage.graphs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.licenta.monitorizareavioane.R;

public class SpeedometerView extends View {

    private static final float MAX_SPEED = 1500;

    private float speed;

    public SpeedometerView(Context context, float speed) {
        super(context);
        this.speed = speed;
    }

    public SpeedometerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        setPaintDetails(paint, Paint.Style.STROKE, 7, getResources().getColor(R.color.end_color));
        RectF rectF = new RectF(75, 75, getWidth() - 75, getHeight() - 75);
        float speedAngle = speed * 270 / MAX_SPEED;
        canvas.drawArc(rectF, 135, speedAngle, false, paint);
        paint.setColor(getResources().getColor(R.color.light_grey));
        canvas.drawArc(rectF, speedAngle + 135, 270 - speedAngle, false, paint);

        for (int i = 0; i <= MAX_SPEED; i += MAX_SPEED / 10) {
            setPaintDetails(paint, Paint.Style.STROKE, 4, getResources().getColor(R.color.end_color));
            paint.setTextSize(30);
            paint.getTextBounds(String.valueOf(i), 0, String.valueOf(i).length(), new Rect());

            double angle = i * 270 / MAX_SPEED + 135;
            int x = (int) (rectF.centerX() + (getWidth() / 2 - 30) * Math.cos(angle * 3.14 / 180) - paint.measureText(String.valueOf(i)) / 2);
            int y = (int) (rectF.centerY() + (getHeight() / 2 - 30) * Math.sin(angle * 3.14 / 180) + paint.getTextSize() / 2);
            canvas.drawText(String.valueOf(i), x, y, paint);
        }
        setPaintDetails(paint, Paint.Style.FILL_AND_STROKE, 4, getResources().getColor(R.color.start_color));
        paint.setTextSize(70);
        String speedValue = String.format("%.2f", speed);
        canvas.drawText(speedValue, rectF.centerX() - 55 - (speedValue.length() - 3) * 20, rectF.centerY(), paint);
        paint.setTextSize(40);
        paint.setColor(getResources().getColor(R.color.start_color));
        canvas.drawText("km/h", rectF.centerX() - 40 - (speedValue.length() - 3) * 20, rectF.centerY() + 50, paint);

    }

    private void setPaintDetails(Paint paint, Paint.Style style, int strokeWidth, int color) {
        paint.setStyle(style);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
