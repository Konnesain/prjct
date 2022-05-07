package com.test.my.tetris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.SurfaceHolder;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game extends Thread
{
    SurfaceHolder holder;
    public volatile boolean isRunning = true;
    public static Tile[][] map = new Tile[10][20];
    public static Figure cFigure;
    private final int tileSize;
    private final int margin;
    Random r;
    Timer timer;
    boolean wait = false;
    double speed = 1;
    public static boolean theend = false;
    int score = 0;

    TimerTask task = new TimerTask()
    {
        @Override
        public void run()
        {
            if (cFigure != null)
                cFigure.changePos(0,1);
            if (cFigure.onGround())
            {
                if(theend)
                {
                    this.cancel();
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
    };

    public void deleteLines()
    {
        wait = true;
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
                score += 100;
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
        wait = false;
    }

    public Game(SurfaceHolder hldr, int width, int height)
    {
        this.tileSize = width * 60 / 1080;//def size - 80; now - 60
        this.margin = width * 40 / 1080;

        this.holder = hldr;
        for (int i = 0; i < 10; i++)
            for (int g = 0; g < 20; g++)
                map[i][g] = new Tile(i, g, Color.WHITE);
        r = new Random();
        timer = new Timer();
        timer.schedule(task, 1000, Math.round(500 / speed));
    }

    private void changeSpeed(double speed)
    {
        long spd = Math.round(500 / speed);
        timer.cancel();
        timer.schedule(task, 0, spd);
    }

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
                for (int i = 0; i < 10; i++) //draw map
                    for (int g = 0; g < 20; g++)
                    {
                        Paint paint = new Paint();
                        paint.setColor(map[i][g].color);
                        if (map[i][g].isEmpty)
                        {
                            paint.setStyle(Paint.Style.STROKE);
                        }
                        else
                            paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        canvas.drawRect(new Rect(margin + map[i][g].x * tileSize, margin + map[i][g].y * tileSize,
                                margin + map[i][g].x * tileSize + tileSize, margin + map[i][g].y * tileSize + tileSize), paint);
                    }
                for (int i = 0; i < 4; i++) //draw figure
                {
                    Tile tile = cFigure.tiles[i];
                    Paint paint = new Paint();
                    paint.setColor(tile.color);
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    canvas.drawRect(margin + (tile.x + cFigure.dx) * tileSize, margin + (tile.y +cFigure.dy) * tileSize,
                            margin + (tile.x + cFigure.dx + 1) * tileSize, margin + (tile.y + cFigure.dy + 1) * tileSize, paint);
                }
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setTextSize(25);
                canvas.drawText("score: " + score, margin + map[9][19].x * tileSize + tileSize + 100, margin * 3, paint); //draw text
            }
            finally
            {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void run()
    {
        super.run();
        cFigure = new Figure(Figure.Type.I);
        while (isRunning)
        {
            if (!cFigure.isFalling) //check figure
            {
                Figure.Type t = null;
                switch (r.nextInt(7))
                {
                    case 0:
                        t = Figure.Type.L;
                        break;
                    case 1:
                        t = Figure.Type.J;
                        break;
                    case 2:
                        t = Figure.Type.I;
                        break;
                    case 3:
                        t = Figure.Type.T;
                        break;
                    case 4:
                        t = Figure.Type.Z;
                        break;
                    case 5:
                        t = Figure.Type.S;
                        break;
                    case 6:
                        t = Figure.Type.O;
                        break;
                }
                cFigure = new Figure(t);
            }
            if (!wait)
                draw();
        }
        timer.cancel();
    }
}