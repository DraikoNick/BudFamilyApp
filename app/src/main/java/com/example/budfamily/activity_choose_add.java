package com.example.budfamily;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class activity_choose_add extends AppCompatActivity implements View.OnClickListener {

    DBHelper dbHelper;

    int DIALOG_DATE=1;
    int myYear,myMonth,myDay;

    TextView etxtDate;
    Spinner spinBills;
    EditText etxtSumm;
    Spinner spinValue;
    TextView txtCurs1,txtCurs2;
    EditText etxtCursValue;
    Button btnNo,btnYes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_add);
        //ищем все элементы в активности
        etxtDate=(TextView) findViewById(R.id.etxtDate);
        spinBills=(Spinner) findViewById(R.id.spinBill1);
        etxtSumm=(EditText) findViewById(R.id.etxtSum);
        spinValue=(Spinner) findViewById(R.id.spinType);
        txtCurs1=(TextView) findViewById(R.id.txtCurs1);
        txtCurs2=(TextView) findViewById(R.id.txtCurs2);
        etxtCursValue=(EditText) findViewById(R.id.etxtCurs);
        btnNo=(Button) findViewById(R.id.btnNo);
        btnYes=(Button) findViewById(R.id.btnYes);
        dbHelper=new DBHelper(this);
        //установить начальные значения
        getCurrentTime();
        billSpinner(DBHelper.TABLE_BILLS,DBHelper.KEY_BILL_NAME, spinBills);
        billSpinner(DBHelper.TABLE_CURRENCY,DBHelper.KEY_CURR_NAME, spinValue);
        etxtSumm.setText("0");
        etxtCursValue.setText("1");
        spinnerChanger();
        //скрыть курс если валюта одинаковая

    }//end onCreate

    private void billSpinner(String tableName, String columnName, Spinner spin){
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor=database.query(tableName,null,null,null,null,null,null);
        List<String> list = new ArrayList<String>();
        if(cursor.moveToFirst()){
            int nameIndex=cursor.getColumnIndex((columnName));
            do{
                list.add(cursor.getString(nameIndex));
            }while(cursor.moveToNext());
        }//end if
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setSelection(0);
    }//end billSpinner

    private void spinnerChanger(){
        spinValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                SQLiteDatabase database=dbHelper.getReadableDatabase();
                Cursor cursor=database.query(DBHelper.TABLE_CURRENCY,null,null,null,null,null,null);
                List<String> list = new ArrayList<String>();
                if(cursor.moveToFirst()){
                    int nameIndex=cursor.getColumnIndex((DBHelper.KEY_CURR_NAME));
                    do{
                        list.add(cursor.getString(nameIndex));
                    }while(cursor.moveToNext());
                }//end if
                cursor.close();
                txtCurs1.setText("1 "+list.get(selectedItemPosition)+" = ");
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinBills.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                SQLiteDatabase database=dbHelper.getReadableDatabase();
                Cursor cursor=database.query(DBHelper.TABLE_BILLS,null,null,null,null,null,null);
                List<Integer> list = new ArrayList<Integer>();
                if(cursor.moveToFirst()){
                    int currIndex=cursor.getColumnIndex((DBHelper.KEY_BILL_CURRENCY));
                    do{
                        list.add(cursor.getInt(currIndex));
                    }while(cursor.moveToNext());
                }//end if
                cursor.close();
                String[] Colum={"_id","name"};
                String Select="_id = " + list.get(selectedItemPosition).toString();
                Log.d("LOGM",list.get(selectedItemPosition).toString()+spinBills.getSelectedItem());
                Cursor cursorT=database.query(DBHelper.TABLE_CURRENCY,Colum,Select,null,null,null,"_id DESC");
                if(cursorT.moveToFirst()){
                    int idIndexT=cursorT.getColumnIndex((DBHelper.KEY_CURR_ID));
                    int nameIndexT=cursorT.getColumnIndex((DBHelper.KEY_CURR_NAME));
                    do{
                        cursorT.getInt(idIndexT);
                        if(cursorT.getInt(idIndexT)==list.get(selectedItemPosition)) txtCurs2.setText(cursorT.getString(nameIndexT));
                    }while(cursor.moveToNext());
                }
                cursorT.close();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnYes: {
                ClickYes();
                break;
            }//end R.id.btnYes
            case R.id.btnNo: {
                ClickNo();
                break;
            }//end R.id.btnNo
            case R.id.etxtDate: {
                showDialog(DIALOG_DATE);
                break;
            }//end R.id.btnNo
        }//end switch(v.getId())
    }//end onClick(View v)

    private void getCurrentTime(){
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat formDate=new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = formDate.format(currentDate.getTime());
        etxtDate.setText(formattedDate);
        etxtDate.setTextSize(24);
        //etxtDate.setText(formattedDate.toString());
        myYear=currentDate.getYear();
        myMonth=currentDate.getMonth();
        myDay=currentDate.getDate();
        int m=myMonth+1;
        int y=myYear+1900;
        etxtDate.setText(myDay+"."+m+"."+y);
    }//end getCurrenTime()

    protected Dialog onCreateDialog(int dd) {
        if (dd == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear+1900, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(dd);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myDay=dayOfMonth;
            myMonth=monthOfYear;
            myYear=year;
            int m=myMonth+1;
            int y=myYear;
            etxtDate.setText(myDay + "." + m + "." + y);
        }
    };

    private String dateToDBformat(Date date) {
        String retval = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss"); //dlya sortirovki
        if (date == null) {
            return retval;
        }
        retval = sdf.format(date);
        return retval;
    }

    private int returnIdOf(int id){
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_BILLS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_NAME);
            int curIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_CURRENCY);
            int ownIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_OWNER);
            do {
                if(cursor.getString(nameIndex).equals(spinBills.getSelectedItem().toString())) {
                    switch (id){
                        case 1:return cursor.getInt(idIndex);
                        case 2:return cursor.getInt(curIndex);
                        case 3:return cursor.getInt(ownIndex);
                    }//end switch
                }//end if
            } while (cursor.moveToNext());
        }//end if
        return 0;
    }

    private void ClickYes(){
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ACT_USER, returnIdOf(3));    //ай ди пользователя на будущее
        Date dbDate=new Date();
        dbDate.setYear(myYear);
        dbDate.setMonth(myMonth);
        dbDate.setDate(myDay);
        contentValues.put(DBHelper.KEY_ACT_DATE, dateToDBformat(dbDate));
        contentValues.put(DBHelper.KEY_ACT_TYPE, 0);
        contentValues.put(DBHelper.KEY_ACT_FROM, 0);
        contentValues.put(DBHelper.KEY_ACT_TO, returnIdOf(1));
        contentValues.put(DBHelper.KEY_ACT_CURR, Double.parseDouble(etxtCursValue.getText().toString()));
        contentValues.put(DBHelper.KEY_ACT_SUM, Double.parseDouble(etxtSumm.getText().toString()));
        database.insert(DBHelper.TABLE_ACTIVITIES, null, contentValues);
        contentValues.clear();
        database.close();
        finish();
    }//end ClickYes()

    private void ClickNo(){
        finish();
    }//end ClickNo

    public void temp(){
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        //вывод в консоль внесенных объектов в таблицу
        Cursor cursor = database.query(DBHelper.TABLE_ACTIVITIES, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ACT_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_ACT_USER);
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_ACT_DATE);
            int typeIndex = cursor.getColumnIndex(DBHelper.KEY_ACT_TYPE);
            int fromIndex = cursor.getColumnIndex(DBHelper.KEY_ACT_FROM);
            int toIndex = cursor.getColumnIndex(DBHelper.KEY_ACT_TO);
            int currIndex = cursor.getColumnIndex(DBHelper.KEY_ACT_CURR);
            int summIndex = cursor.getColumnIndex(DBHelper.KEY_ACT_SUM);

            do {
                Log.d("mLog", " ----------"+cursor.getInt(idIndex)+" "
                        +cursor.getInt(nameIndex)+" "
                        +cursor.getInt(dateIndex)+" "
                        +cursor.getInt(typeIndex)+" "
                        +cursor.getInt(fromIndex)+" "
                        +cursor.getInt(toIndex)+" "
                        +cursor.getDouble(currIndex)+" "
                        +cursor.getDouble(summIndex));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");
        cursor.close();
        /*
        Cursor cursor2 = database.query(DBHelper.TABLE_EXP_TYPE, null, null, null, null, null, null);
        if (cursor2.moveToFirst()) {
            int idIndex = cursor2.getColumnIndex(DBHelper.KEY_EXP_TYPE_ID);
            int nameIndex = cursor2.getColumnIndex(DBHelper.KEY_EXP_TYPE_NAME);
            int curIndex = cursor2.getColumnIndex(DBHelper.KEY_EXP_TYPE_CURRENCY);
            int expcatIndex = cursor2.getColumnIndex(DBHelper.KEY_EXP_TYPE_EXPCAT);
            do {
                Log.d("mLog", "ID = " + cursor2.getInt(idIndex) +", name = " + cursor2.getString(nameIndex)+", cur = "
                        + cursor2.getString(curIndex)+", expcat = " + cursor2.getString(expcatIndex));
            } while (cursor2.moveToNext());
        } else
            Log.d("mLog","0 rows");
        cursor2.close();*/
    }//end temp()    //нужно ТОЛЬКО для проверки внесенных данных в базу

}//end class
