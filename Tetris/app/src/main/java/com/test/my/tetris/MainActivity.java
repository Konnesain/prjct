package com.test.my.tetris;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File folder = new File("/data/data/" + getPackageName() + "/files");
        File scores = new File("/data/data/"+getPackageName()+"/files/scores");

        if(!folder.exists())
            folder.mkdir();
        if(!scores.exists())
        {
            try
            {
                scores.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            writeFile(null, 0);
        }

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

        findViewById(R.id.button_start).setOnClickListener(view ->
        {
            startActivityForResult(new Intent(getApplicationContext(), GameActivity.class), 1);
        });

        findViewById(R.id.button_scores).setOnClickListener(view ->
        {
            Intent i = new Intent(getApplicationContext(), ScoresActivity.class);
            i.putExtra("scores", (int[]) null);
            startActivity(i);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1) //game ends
        {
            int[] tmp = new int[11];
            int[] scores = new int[10];
            int latest = data.getIntExtra("score", 0);
            try
            {
                Scanner scan = new Scanner(new File("/data/data/"+getPackageName()+"/files/scores"));
                for(int i = 0; i < 10; i++)
                {
                    tmp[i] = -scan.nextInt();
                }
                scan.close();
                tmp[10] = -latest;
                Arrays.sort(tmp);
                for(int i = 0; i < 10; i++)
                    tmp[i] = -tmp[i];
                scores = Arrays.copyOf(tmp, 10);
                writeFile(scores, latest);
            }
            catch (FileNotFoundException e) {/*do nothing XD*/}
            Intent i = new Intent(getApplicationContext(), ScoresActivity.class);
            i.putExtra("scores", scores);
            i.putExtra("latest", latest);
            startActivity(i);
        }
    }

    private void writeFile(int[] scores, int latest)
    {
        try
        {
            if(scores == null)
            {
                scores = new int[10];
                Arrays.fill(scores, 0);
            }
            FileWriter fw = new FileWriter("/data/data/"+getPackageName()+"/files/scores");
            for(int i = 0; i < 10; i++)
                fw.write(scores[i] + "\n");
            fw.write("" + latest);
            fw.close();
        }
        catch (IOException e) {/*do nothing XD*/}
    }
}