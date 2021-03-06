package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity Main = this;
        setContentView(R.layout.welcome);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Main.finish();
                Intent intent = new Intent();
                intent.setClass(Main, router.class);
                startActivity(intent);
            }
        };
        timer.schedule(task, 2000);
    }

}