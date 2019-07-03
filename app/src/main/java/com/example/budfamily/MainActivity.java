package com.example.budfamily;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_APP_OPEN_COUNTER = "open_app_counter";
    private SharedPreferences appSettings;
    private int appOpenCounter;

    DBHelper dbHelper;

    Timer timer;    //подключаем таймер. понадобится для АВТО переключения активностей

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        onResume();
        //еслив первый раз запускаем, то выводим приветствие и создаем базу
        //иначе просто переходим на главный экран
        if (appOpenCounter==0){
            timer=new Timer();
            timer.schedule(new TimerTask(){
                @Override
                public void run(){
                    Intent intent=new Intent(MainActivity.this,activity_first_start.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        } else {

            timer=new Timer();
            timer.schedule(new TimerTask(){
                @Override
                public void run(){
                    Intent intent=new Intent(MainActivity.this,activity_choose.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }//end if else
    }//end onCreate

    @Override
    protected void onResume() {
        super.onResume();
        if (appSettings.contains(APP_PREFERENCES_APP_OPEN_COUNTER)) {
            // Получаем число из настроек
            appOpenCounter = appSettings.getInt(APP_PREFERENCES_APP_OPEN_COUNTER, 0);
            // Выводим на экран данные из настроек
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Запоминаем данные
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putInt(APP_PREFERENCES_APP_OPEN_COUNTER, appOpenCounter);
        editor.apply();
    }
}

