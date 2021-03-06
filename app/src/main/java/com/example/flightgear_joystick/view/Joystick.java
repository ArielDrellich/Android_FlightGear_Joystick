package com.example.flightgear_joystick.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.flightgear_joystick.R;

public class Joystick extends View {
    private int baseRadius, stickRadius, startX, startY, stickXPos, stickYPos, circleBoundary;

    public Joystick(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /* Sets initial positions.
     * Not in the constructor in order to receive baseDiam after layout is initialized. */
    public void setPositions(int baseDiam) {
        // Relational sizes based on joystick image and stick image.
        baseRadius = (int) (baseDiam * 0.48);
        stickRadius = (int) (baseRadius * 0.43);
        startX = baseRadius - stickRadius;
        startY = startX;
        stickXPos = startX;
        stickYPos = startY;
        circleBoundary = baseRadius - stickRadius;
    }

    public int getStickRange() {
        return circleBoundary;
    }

    public Joystick(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public Point getJoystickPosition() {
        return new Point(stickXPos, stickYPos);
    }

    public Point getJoystickStartPosition() {
        return new Point(startX, startY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        // relative touch location.
        int xPosition = (int) event.getX() - stickRadius;
        int yPosition = (int) event.getY() - stickRadius;
        int action = event.getAction();
        if ( action == MotionEvent.ACTION_MOVE ) {
            // if within the joystick base radius.
            if (Math.sqrt(Math.pow(xPosition - startX, 2) + Math.pow(yPosition - startY, 2) ) < circleBoundary) {
                stickXPos = xPosition;
                stickYPos = yPosition;
            } else {
                // find closest point on circle to touch position.
                double xDist = xPosition - startX;
                double yDist = yPosition - startY;
                int dist = (int) Math.sqrt(xDist*xDist + yDist*yDist);
                stickXPos = (int) ((double) startX + xDist / dist * (double) circleBoundary);
                stickYPos = (int) ((double) startY + yDist / dist * (double) circleBoundary);
            }
        }
        // when releasing, returns stick to center.
        else if (action == MotionEvent.ACTION_UP) {
            this.stickXPos = startX;
            this.stickYPos = startY;
        }

        // tells the screen to update.
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /* Draws the joystick base sprite. */
        Drawable base = getResources().getDrawable(R.drawable.base2);
        base.setBounds(0, 0, baseRadius * 2, baseRadius * 2);
        base.draw(canvas);

        /* Connection between base and stick. */
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stickRadius);
        paint.setColor(Color.parseColor("#1f1f1f"));
        canvas.drawLine(baseRadius, baseRadius, stickXPos + stickRadius, stickYPos + stickRadius, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(baseRadius, baseRadius, stickRadius/2, paint);


        /* Draws the joystick stick sprite. */
        Drawable stick = getResources().getDrawable(R.drawable.stick_v2);
        // based on prior calculations.
        stick.setBounds(stickXPos, stickYPos, stickRadius*2 + stickXPos, stickRadius*2 + stickYPos);
        stick.draw(canvas);
    }

}
