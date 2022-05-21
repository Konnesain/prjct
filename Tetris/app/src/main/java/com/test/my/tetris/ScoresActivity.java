package com.test.my.tetris;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ScoresActivity extends AppCompatActivity
{
    int[] scores;
    int latest;
    LinearLayout l;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility ->
        {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
            {
                decorView.setSystemUiVisibility(flags);
            }
        });

        scores = getIntent().getExtras().getIntArray("scores");
        if(scores == null) //if starts from main activity
        {
            scores = new int[10];
            try
            {
                Scanner scan = new Scanner(new File("/data/data/"+getPackageName()+"/files/scores"));
                for(int i = 0; i < 10; i++)
                {
                    scores[i] = scan.nextInt();
                }
                latest = scan.nextInt();
                scan.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        else//if starts from game
        {
            latest = getIntent().getExtras().getInt("latest");
        }

        l = findViewById(R.id.scores_layout);
        TextView tv = new TextView(this);
        tv.setText("Последняя игра: " + latest);
        tv.setTextSize(20);
        l.addView(tv);
        for(int i = 0; i < 10; i++)
        {
            tv = new TextView(this);
            tv.setText(i+1 + ") " + scores[i]);
            tv.setTextSize(20);
            l.addView(tv);
        }
    }
}