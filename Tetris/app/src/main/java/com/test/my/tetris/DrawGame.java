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

import androidx.annotation.NonNull;

public class DrawGame extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener
{
    Game game;
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
        game = new Game(surfaceHolder, displaySize.x, displaySize.y);
        game.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder)
    {
        game.isRunning = false;
        boolean retry = true;
        while (retry)
        {
            try
            {
                game.join();
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
            game.rotate(event.getX() < displaySize.x / 2f);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            changeX += distanceX;
            changeY += distanceY;
            if(distanceY <= displaySize.x * 60 / -1080f)
            {
                game.toGround();
                changeY = 0;
                changeX = 0;
                return true;
            }
            if (changeX >= 100)
            {
                game.changePos(-1, 0);
                changeX = 0;
            }
            if (changeX <= -100)
            {
                game.changePos(1, 0);
                changeX = 0;
            }
            if (changeY < -70)
            {
                game.changePos(0, 1);
                changeY = 0;
            }
            return true;
        }
    }
}