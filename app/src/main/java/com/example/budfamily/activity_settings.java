package com.example.budfamily;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class activity_settings extends AppCompatActivity {

    Button btnOk;
    EditText edtxt_date;
    CheckBox checkNameBill, checkValSumNam, checkNameVal;
    TextView txtPeriodStart;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_APP_OPEN_COUNTER = "open_app_counter";
    public static final String APP_PREFERENCES_PERIOD_START = "period_start";
    public static final String APP_PREFERENCES_PERIOD_THIS = "period_this";
    public static final String APP_PREFERENCES_OWNER = "owner";
    public static final String APP_PREFERENCES_SHOW_INFO_CODE = "show_info_code";

    private SharedPreferences appSettings;

    private int appOpenCounter;     //от этого зависит будет ли создана база лии использоваться существующая
    private String periodStartDay;       //период за который будет выводиться на экран информация о счетах и расходах
    private String thisPeriodStart;
    private int ownerId=1;      //в будущем можно поставить обработчик пользователей.
    private int infoCode;   //0-только имя, 1-все, 2-имя и валюта

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        periodStartDay="01";
        thisPeriodStart=getFormattedDate(getFirstDateOfMonth(1));
        //ищем элементы на экране
        txtPeriodStart=(TextView) findViewById(R.id.txtFinPeriodStart);
        edtxt_date=(EditText) findViewById(R.id.edtxt_date);
        btnOk=(Button) findViewById(R.id.btnOk);
        checkNameBill=(CheckBox) findViewById(R.id.checkNameBill);      //0
        checkValSumNam=(CheckBox) findViewById(R.id.checkValSumNam);    //1
        checkNameVal=(CheckBox) findViewById(R.id.checkNameVal);        //2
        //вытаскиваем настройки
        appSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        //вытаскиваем информацию об отображении полей
        if (appSettings.contains(APP_PREFERENCES_SHOW_INFO_CODE))
            infoCode = appSettings.getInt(APP_PREFERENCES_SHOW_INFO_CODE, 0);                               //0.1.2
        if (appSettings.contains(APP_PREFERENCES_PERIOD_THIS))
            periodStartDay = appSettings.getString(APP_PREFERENCES_PERIOD_START, getFormattedDate(getFirstDateOfMonth(1)));    //01.01.2019
        if (appSettings.contains(APP_PREFERENCES_PERIOD_START))
            thisPeriodStart = appSettings.getString(APP_PREFERENCES_PERIOD_THIS, getFormattedDay(getFormattedDate(getFirstDateOfMonth(1))));      //01
        //вбиваем в активити посчитанные значения
        switch(infoCode){
            case 0:{
                checkNameBill.setChecked(true);
                break;
            }
            case 1:{
                checkValSumNam.setChecked(true);
                break;
            }
            case 2:{
                checkNameVal.setChecked(true);
                break;
            }
        }
        edtxt_date.setText(periodStartDay);
        txtPeriodStart.setText("Финансовый период начинается с заданного дня ежемесячно. В этом месяце это: "+thisPeriodStart);
    }//end onCreate

    public static String getFormattedDate(Date currentDate){
        SimpleDateFormat formDate=new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = formDate.format(currentDate.getTime());
        //int myYear=currentDate.getYear();
        //int myMonth=currentDate.getMonth();
        //int myDay=currentDate.getDate();
        //int m=myMonth+1;
        //int y=myYear+1900;
        //return (myDay+"."+m+"."+y);
        return formattedDate;
    }//end getCurrenTime()
    public static String getFormattedDay(String date){
        return date.substring(0, date.length() - 8);
    }//end getCurrenTime()
    public static Date getFirstDateOfMonth(int dayOfMonth){       //возвращает первый день текущего месяца
        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        //Log.d("today",c.getTime().toString());       // this returns java.util.Date
        return c.getTime();
    }//end getFirstDayOfMonth

    private int checkBoxesCodeReturn(){
        if(checkNameVal.isChecked() || checkValSumNam.isChecked() || checkNameBill.isChecked()){
            if(checkNameVal.isChecked()) return 2;
            if(checkValSumNam.isChecked()) return 1;
            if(checkNameBill.isChecked()) return 0;
        }
        return 0;
    }

    public void onClick(View v){
        if(Integer.parseInt(edtxt_date.getText().toString())>=1 & Integer.parseInt(edtxt_date.getText().toString())<=30){
            if((checkNameVal.isChecked() ^ checkValSumNam.isChecked() ^ checkNameBill.isChecked()) &
                    !(checkNameVal.isChecked() & checkValSumNam.isChecked() & checkNameBill.isChecked())){
                periodStartDay=edtxt_date.getText().toString();
                thisPeriodStart=getFormattedDate(getFirstDateOfMonth(Integer.parseInt(periodStartDay)));
                infoCode=checkBoxesCodeReturn();
                SharedPreferences.Editor editor = appSettings.edit();
                editor.putString(APP_PREFERENCES_PERIOD_START, periodStartDay);
                editor.putString(APP_PREFERENCES_PERIOD_THIS, thisPeriodStart);
                editor.putInt(APP_PREFERENCES_SHOW_INFO_CODE, infoCode);
                editor.apply();
                finish();
            }else {
                Toast toast = Toast.makeText(getApplicationContext(), "Сделайте только ОДИН выбор отображения счетов.", Toast.LENGTH_SHORT);
                toast.show();
            }//end if checkBoxes
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Выберите дату между 1 и 28 числом. И выберите только ОДИН вид отображения счетов и расходов.", Toast.LENGTH_SHORT);
            toast.show();
        }//end if date
    }//end onClick
    @Override
    protected void onResume() {
        super.onResume();
        // Получаем число из настроек
        if (appSettings.contains(APP_PREFERENCES_APP_OPEN_COUNTER))
            appOpenCounter = appSettings.getInt(APP_PREFERENCES_APP_OPEN_COUNTER, 0);
        if (appSettings.contains(APP_PREFERENCES_SHOW_INFO_CODE))
            infoCode = appSettings.getInt(APP_PREFERENCES_SHOW_INFO_CODE, 0);
        if (appSettings.contains(APP_PREFERENCES_PERIOD_START))
            periodStartDay = appSettings.getString(APP_PREFERENCES_PERIOD_START, periodStartDay);
        if (appSettings.contains(APP_PREFERENCES_PERIOD_THIS))
            thisPeriodStart = appSettings.getString(APP_PREFERENCES_PERIOD_THIS, thisPeriodStart);
    }//end onResume
    @Override
    protected void onPause() {
        super.onPause();
        // Запоминаем данные
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putInt(APP_PREFERENCES_APP_OPEN_COUNTER, appOpenCounter);
        editor.putString(APP_PREFERENCES_PERIOD_START, periodStartDay);
        editor.putString(APP_PREFERENCES_PERIOD_THIS, thisPeriodStart);
        editor.putInt(APP_PREFERENCES_SHOW_INFO_CODE, infoCode);
        editor.apply();
    }//end onPause
}
