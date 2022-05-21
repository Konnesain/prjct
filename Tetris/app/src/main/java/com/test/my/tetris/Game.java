package com.test.my.tetris;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.nio.file.attribute.AclEntryType;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game extends Thread
{
    SurfaceHolder holder;
    public volatile boolean isRunning = true;
    public Tile[][] map = new Tile[10][20];
    public Figure cFigure;
    private Figure nextFigure;
    private final int tileSize;
    private final int margin;
    private final int textSize;
    Random r;
    Timer timer;
    private boolean wait = false;
    private double speed = 1;
    public boolean theend = false;
    private int score = 0;
    private final boolean isFirst;
    private Activity activity;

    private class myTimerTask extends TimerTask //task for timer
    {
        @Override
        public void run()
        {
            if (cFigure == null)
                return;
            cFigure.changePos(0,1, map);
            if (cFigure.onGround(map) != 0)
            {
                if (theend)
                {
                    isRunning = false;
                    this.cancel();
                    finish();
                    return;
                }
                cFigure.isFalling = false;
                for (Tile tile : cFigure.tiles)
                {
                    map[tile.x + cFigure.dx][tile.y + cFigure.dy].isEmpty = false;
                    map[tile.x + cFigure.dx][tile.y + cFigure.dy].color = tile.color;
                }
                deleteLines();
            }
        }
    }

    public void deleteLines()
    {
        wait = true;
        int lines = 0;
        for(int i = 19; i >= 0; i--)
        {
            boolean is = true;
            for(int g = 0; g < 10; g++)
            {
                if(map[g][i].isEmpty)
                {
                    is = false;
                    break;
                }
            }
            if(is) //maybe if need to delete line
            {
                lines++;
                for(int ii = i; ii > 0; ii--)
                {
                    for(int gg = 0; gg < 10; gg++)
                    {
                        map[gg][ii] = new Tile(map[gg][ii-1]);
                    }
                }
                for(int gg = 0; gg < 10; gg++)
                {
                    map[gg][0] = new Tile(gg, 0, Color.WHITE);
                }
                i++;
            }
        }
        switch (lines)
        {
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 700;
                break;
            case 4:
                score += 1500;
                break;
        }
        wait = false;
    }

    public Game(SurfaceHolder hldr, Resources res, Activity activity)
    {
        this.activity = activity;
        this.holder = hldr;
        this.textSize = (int) res.getDimension(R.dimen.textSize);
        this.tileSize = (int) res.getDimension(R.dimen.tileSize);
        this.margin = (int) res.getDimension(R.dimen.margin);
        this.isFirst = true;
    }

    public Game(Game game)
    {
        this.activity = game.activity;
        this.holder = game.holder;
        this.textSize = game.textSize;
        this.tileSize = game.tileSize;
        this.margin = game.margin;
        this.score = game.score;
        this.speed = game.speed;
        this.wait = game.wait;
        this.cFigure = game.cFigure;
        this.nextFigure = game.nextFigure;
        this.map = game.map;
        this.isFirst = false;
    }

    private void restartTimer(double speed)
    {
        timer.cancel();
        timer.purge();
        timer = new Timer();
        timer.schedule(new myTimerTask(), 50, Math.round(500 / speed));
    }

    public void changePos(int x, int y) { cFigure.changePos(x,y, map); }
    public void toGround() { cFigure.toGround(map); restartTimer(speed); } //is it better than static?

    public void rotate(boolean right) //rotate figure
    {
        wait = true;
        cFigure.rotate(right);
        wait = false;
    }

    private void draw()
    {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null)
        {
            try
            {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //clear canvas
                Paint paint = new Paint();
                for (int i = 0; i < 10; i++) //draw map
                    for (int g = 0; g < 20; g++)
                    {
                        paint.setColor(map[i][g].color);
                        if (map[i][g].isEmpty)
                        {
                            paint.setStyle(Paint.Style.STROKE);
                        }
                        else
                            paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        canvas.drawRect(margin + map[i][g].x * tileSize, margin + map[i][g].y * tileSize,
                                margin + map[i][g].x * tileSize + tileSize, margin + map[i][g].y * tileSize + tileSize, paint);
                    }

                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.STROKE);
                for(int i = 0; i < 4; i++) //draw... what is it?
                    for(int g = 0; g < 4; g++)
                    {
                        canvas.drawRect(3 * margin + tileSize * 10 + (i * tileSize), margin * 5 + (g * tileSize),
                                3 * margin + tileSize * 10 + tileSize + (i * tileSize), margin * 5 + tileSize + (g * tileSize), paint);
                    }

                paint.setColor(nextFigure.tiles[0].color);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                for (int i = 0; i < 4; i++) //draw next figure
                {
                    Tile tile = nextFigure.tiles[i];
                    canvas.drawRect(3 * margin + tileSize * 10 + (tile.x * tileSize), margin * 5 + (tile.y * tileSize),
                            3 * margin + tileSize * 10 + tileSize + (tile.x * tileSize), margin * 5 + tileSize + (tile.y * tileSize), paint);
                }

                paint.setColor(cFigure.tiles[0].color);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                drawFigure(canvas, cFigure, paint);
                Figure pseudoFigure = new Figure(cFigure);
                paint.setAlpha(128); //50% transparent

                while(true) //trash
                {
                    int a = pseudoFigure.onGround(map);
                    if(a == 0)
                        pseudoFigure.dy++;
                    else
                    {
                        if(a == 2)
                            this.theend = true;
                        break;
                    }
                }

                //while(!pseudoFigure.onGround(map))
                //    pseudoFigure.dy++;
                drawFigure(canvas, pseudoFigure, paint); //draw figure on ground

                paint = new Paint();
                paint.setColor(Color.RED);
                paint.setTextSize(textSize);
                canvas.drawText("score: " + score, 3 * margin + tileSize * 10, margin * 3, paint); //draw text
            }
            finally
            {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawFigure(Canvas canvas, Figure figure, Paint paint)
    {
        for (int i = 0; i < 4; i++) //draw figure
        {
            Tile tile = figure.tiles[i];
            canvas.drawRect(margin + (tile.x + figure.dx) * tileSize, margin + (tile.y + figure.dy) * tileSize,
                    margin + (tile.x + figure.dx + 1) * tileSize, margin + (tile.y + figure.dy + 1) * tileSize, paint);
        }
    }

    @Override
    public void run()
    {
        super.run();
        r = new Random();
        if(isFirst)
        {
            for (int i = 0; i < 10; i++)
                for (int g = 0; g < 20; g++)
                    map[i][g] = new Tile(i, g, Color.WHITE);
            cFigure = new Figure(Figure.Type.I);
            nextFigure = getRandomFigure();
        }
        timer = new Timer();
        timer.schedule(new myTimerTask(), 1000, Math.round(500 / speed));
        while (isRunning)
        {
            if (!cFigure.isFalling) //check figure
            {
                cFigure = nextFigure;
                nextFigure = getRandomFigure();
            }
            if (!wait)
                draw();
        }
        timer.cancel();
    }

    private Figure getRandomFigure()
    {
        Figure.Type type = null;
        switch (r.nextInt(7))
        {
            case 0:
                type = Figure.Type.L;
                break;
            case 1:
                type = Figure.Type.J;
                break;
            case 2:
                type = Figure.Type.I;
                break;
            case 3:
                type = Figure.Type.T;
                break;
            case 4:
                type = Figure.Type.Z;
                break;
            case 5:
                type = Figure.Type.S;
                break;
            case 6:
                type = Figure.Type.O;
                break;
        }
        return new Figure(type);
    }

    private void finish()
    {
        Intent i = new Intent();
        i.putExtra("score", score);
        activity.setResult(1, i);
        activity.finish();
    }
}