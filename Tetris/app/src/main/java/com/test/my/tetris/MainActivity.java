package com.test.my.tetris;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_start).setOnClickListener(this::startClick);
    }

    private void startClick(View v)
    {
        setContentView(new DrawGame(this));
    }

    public static void fuckingLog(String text)
    {
        Log.e("TAG", text);
    }
}