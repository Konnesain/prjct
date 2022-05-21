package com.test.my.tetris;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
    private final GestureDetector gestureDetector;
    private final Resources res;
    Activity activity;

    @SuppressLint("ClickableViewAccessibility")
    public DrawGame(Activity activity)
    {
        super(activity);
        this.activity = activity;
        this.res = getResources();
        getHolder().addCallback(this);
        gestureDetector = new GestureDetector(activity, new GestureListener());
        this.setOnTouchListener(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder)
    {
        if(game == null)
            game = new Game(surfaceHolder, res, activity);
        else
            game = new Game(game);
        game.isRunning = true;
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
        if(game.theend)
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
            game.rotate(event.getX() < res.getDisplayMetrics().widthPixels / 2f);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            changeX += distanceX;
            changeY += distanceY;
            if(distanceY <= -res.getDimension(R.dimen.dropY))
            {
                game.toGround();
                changeY = 0;
                changeX = 0;
                return true;
            }
            if (changeX >= res.getDimension(R.dimen.moveX))
            {
                game.changePos(-1, 0);
                changeX = 0;
            }
            if (changeX <= -res.getDimension(R.dimen.moveX))
            {
                game.changePos(1, 0);
                changeX = 0;
            }
            if (changeY < -res.getDimension(R.dimen.moveY))
            {
                game.changePos(0, 1);
                changeY = 0;
            }
            return true;
        }
    }
}