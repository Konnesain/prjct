package com.test.my.tetris;

public class Tile
{
    public int x,y;
    public int color;
    public boolean isEmpty;

    public Tile(int x, int y, int color)
    {
        this.isEmpty = true;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Tile(Tile tile)
    {
        this.x = tile.x;
        this.y = tile.y + 1;
        this.color = tile.color;
        this.isEmpty = tile.isEmpty;
    }
}