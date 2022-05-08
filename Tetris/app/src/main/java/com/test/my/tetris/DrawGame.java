package com.test.my.tetris;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import static com.test.my.tetris.MainActivity.fuckingLog;

import androidx.annotation.NonNull;

public class DrawGame extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener
{
    Game dt;
    Point displaySize;
    private final GestureDetector gestureDetector;

    @SuppressLint("ClickableViewAccessibility")
    public DrawGame(Context context)
    {
        super(context);
        getHolder().addCallback(this);
        gestureDetector = new GestureDetector(context, new GestureListener());
        this.setOnTouchListener(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder)
    {
        displaySize = new Point();
        getDisplay().getSize(displaySize);
        Log.e("TAG", "x: " + displaySize.x + "; y: " + displaySize.y);
        dt = new Game(surfaceHolder, displaySize.x, displaySize.y);
        dt.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder)
    {
        dt.isRunning = false;
        boolean retry = true;
        while (retry)
        {
            try
            {
                dt.join();
                retry = false;
            }
            catch (InterruptedException e) {/*do nothing XD*/}
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if(Game.theend)
            return false;
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener
    {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        float changeX;
        float changeY;

        @Override
        public boolean onDown(MotionEvent event)
        {
            changeX = 0;
            changeY = 0;
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event)
        {
            dt.rotate(event.getX() < displaySize.x / 2f);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            changeX += distanceX;
            changeY += distanceY;
            fuckingLog("" + distanceY);
            if(distanceY <= displaySize.x * 60 / -1080f)
            {
                Game.cFigure.toGround();
                changeY = 0;
                changeX = 0;
                return true;
            }
            if (changeX >= 100)
            {
                Game.cFigure.changePos(-1, 0);
                changeX = 0;
            }
            if (changeX <= -100)
            {
                Game.cFigure.changePos(1, 0);
                changeX = 0;
            }
            if (changeY < -70)
            {
                Game.cFigure.changePos(0, 1);
                changeY = 0;
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            boolean result = false;
            try
            {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY))
                {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD)
                    {
                        if (diffX > 0)
                        {
                            onSwipeRight();
                        }
                        else
                        {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD)
                    {
                        if (diffY > 0)
                        {
                            onSwipeBottom();
                        }
                        else
                        {
                            onSwipeTop();
                        }
                        result = true;
                    }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
            return result;
        }

        private void onSwipeRight()
        {
        }

        private void onSwipeLeft()
        {
        }

        private void onSwipeTop()
        {
        }

        private void onSwipeBottom()
        {
        }
    }
}