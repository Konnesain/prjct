package com.test.my.tetris;

import android.graphics.Color;

public class Figure
{
    Tile[] tiles = new Tile[4];
    public int dx, dy;
    public boolean isFalling;
    public Type type;

    public void changePos(int x, int y, Tile[][] map)
    {
        if(y != 0)
        {
            if (onGround(map) != 0)
                return;
        }
        else
        {
            for(int i = 0; i < 4; i++)
            {
                if(this.tiles[i].x + this.dx + x > 9 || this.tiles[i].x + this.dx + x < 0 || !map[this.tiles[i].x + this.dx + x][this.tiles[i].y + this.dy].isEmpty)
                {
                    return;
                }
            }
        }
        this.dx += x;
        this.dy += y;
    }

    public void toGround(Tile[][] map)
    {
        while(onGround(map) == 0)
        {
            this.dy++;
        }
    }

    public int onGround(Tile[][] map)
    {
        for (int i = 0; i < 4; i++)
        {
            if (this.tiles[i].y + this.dy >= 19 || !map[this.tiles[i].x + this.dx][this.tiles[i].y + this.dy + 1].isEmpty)
            {
                for(int g = 0; g < 4; g++)
                    if(this.tiles[g].y + this.dy == 0)
                    {
                        return 2; //true
                    }
                return 1; //true
            }
        }
        return 0; //false
    }

    public void rotate(boolean right)
    {
        int summ1x = 0, summ2x = 0;
        int summ1y = 0, summ2y = 0;
        for(int i = 0; i < 4; i++)
        {
            int y = this.tiles[i].y;
            int x = this.tiles[i].x;
            summ1x+=x;
            summ1y+=y;
            if (right)
                x = -x;
            else
                y = -y;
            this.tiles[i].y = x;
            this.tiles[i].x = y;
            summ2x+=y;
            summ2y+=x;
        }
        if(summ2x > summ1x)
            this.dx--;
        else
            if(summ2x < summ1x)
                this.dx++;
        if(summ2y > summ1y)
            this.dy--;
        else
            if(summ2y < summ1y)
                this.dy++;
    }

    public Figure(Type type)
    {
        this.dx = 4;
        this.dy = 0;
        this.type = type;
        this.isFalling = true;
        int color;
        switch (type)
        {
            case L:
                color = Color.RED;
                this.tiles[0] = new Tile(0, 0, color);
                this.tiles[1] = new Tile(0, 1, color);
                this.tiles[2] = new Tile(0, 2, color);
                this.tiles[3] = new Tile(1, 2, color);
                break;
            case J:
                color = Color.BLUE;
                this.tiles[0] = new Tile(1, 0, color);
                this.tiles[1] = new Tile(1, 1, color);
                this.tiles[2] = new Tile(1, 2, color);
                this.tiles[3] = new Tile(0, 2, color);
                break;
            case I:
                color = Color.GREEN;
                this.tiles[0] = new Tile(0, 0, color);
                this.tiles[1] = new Tile(1, 0, color);
                this.tiles[2] = new Tile(2, 0, color);
                this.tiles[3] = new Tile(3, 0, color);
                break;
            case T:
                color = Color.YELLOW;
                this.tiles[0] = new Tile(0, 0, color);
                this.tiles[1] = new Tile(1, 0, color);
                this.tiles[2] = new Tile(2, 0, color);
                this.tiles[3] = new Tile(1, 1, color);
                break;
            case Z:
                color = Color.MAGENTA;
                this.tiles[0] = new Tile(0, 0, color);
                this.tiles[1] = new Tile(1, 0, color);
                this.tiles[2] = new Tile(1, 1, color);
                this.tiles[3] = new Tile(2, 1, color);
                break;
            case S:
                color = Color.CYAN;
                this.tiles[0] = new Tile(0, 1, color);
                this.tiles[1] = new Tile(1, 1, color);
                this.tiles[2] = new Tile(1, 0, color);
                this.tiles[3] = new Tile(2, 0, color);
                break;
            case O:
                color = Color.LTGRAY;
                this.tiles[0] = new Tile(0, 0, color);
                this.tiles[1] = new Tile(1, 0, color);
                this.tiles[2] = new Tile(0, 1, color);
                this.tiles[3] = new Tile(1, 1, color);
                break;
        }
    }

    public Figure(Figure figure)
    {
        this.dx = figure.dx;
        this.dy = figure.dy;
        this.tiles = figure.tiles;
    }

    public enum Type
    {
        L,
        J,
        I,
        T,
        Z,
        S,
        O
    }
}
