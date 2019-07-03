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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class activity_choose_pay extends AppCompatActivity {

    DBHelper dbHelper;

    int DIALOG_DATE=1;
    int myYear,myMonth,myDay;

    int [] spinId=new int[3];

    int [] IDsOfSpins=new int[3];
    int [] IDsOfValue=new int[3];

    TextView etxtDate;
    Spinner [] spinBills=new Spinner[3];
    EditText etxtSumm;
    TextView txtCurs1,txtCurs2,txtVal1,txtVal2,txtVal3;
    EditText etxtCursValue;
    Button btnNo,btnYes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pay);
        //ищем элементы активити
        etxtDate=(TextView) findViewById(R.id.etxtDate);
        spinBills[0]=(Spinner) findViewById(R.id.spinBill1);
        spinBills[1]=(Spinner) findViewById(R.id.spinBill2);
        spinBills[2]=(Spinner) findViewById(R.id.spinBill3);
        etxtSumm=(EditText) findViewById(R.id.etxtSum);
        txtCurs1=(TextView) findViewById(R.id.txtCurs1);
        txtCurs2=(TextView) findViewById(R.id.txtCurs2);
        txtVal1=(TextView) findViewById(R.id.txtVal1);
        txtVal2=(TextView) findViewById(R.id.txtVal2);
        txtVal3=(TextView) findViewById(R.id.txtVal3);
        etxtCursValue=(EditText) findViewById(R.id.etxtCurs);
        btnNo=(Button) findViewById(R.id.btnNo);
        btnYes=(Button) findViewById(R.id.btnYes);
        dbHelper=new DBHelper(this);
        //определяем день
        getCurrentTime();
        //выводи в спинеры начальные значения
        billSpinner(0,DBHelper.TABLE_BILLS,DBHelper.KEY_BILL_NAME);
        billSpinner(1,DBHelper.TABLE_EXP_CAT,DBHelper.KEY_EXP_CAT_NAME);
        billSpinner(2,DBHelper.TABLE_EXP_TYPE,DBHelper.KEY_EXP_TYPE_NAME);
        //выводим начальные значения полей
        etxtSumm.setText("0");
        etxtCursValue.setText("1");
        spinnerChanger(0);
        spinnerChanger(1);
        spinnerChanger(2);
    }

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

    private void billSpinner(int spinnerId, String tableName, String columName){
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        String Select=null;
        //ищем все типы расходов в зависимости от выбранной категории расходов
        if(spinnerId==2){
            Integer in=(int)spinBills[spinnerId-1].getSelectedItemId()+1;
            Log.d("mLog",in.toString());
            Select="expcat = "+in.toString();
        }
        Cursor cursor=database.query(tableName,null,Select,null,null,null,null);
        //создаем список найденных типов расходов
        List<String> list = new ArrayList<String>();
        //List<Integer> listID = new ArrayList<Integer>();
        if(cursor.moveToFirst()){
            int nameIndex=cursor.getColumnIndex((columName));
            do{
                list.add(cursor.getString(nameIndex));
            }while(cursor.moveToNext());
        }//end if
        cursor.close();
        //выкидываем это все в активити в спиннер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinBills[spinnerId].setAdapter(adapter);
        spinBills[spinnerId].setSelection(0);
    }//end billSpinner

    private void spinnerChanger(final int spinId){
        spinBills[spinId].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                switch(spinId){
                    case 0:{
                        SQLiteDatabase database=dbHelper.getReadableDatabase();
                        Cursor cursor=database.query(DBHelper.TABLE_BILLS,null,null,null,null,null,null);
                        List<Integer> list = new ArrayList<Integer>();
                        if(cursor.moveToFirst()){
                            int idIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_ID);
                            int currIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_CURRENCY);
                            do{
                                list.add(cursor.getInt(idIndex));
                                if((selectedItemPosition+1)==cursor.getInt(idIndex)) {
                                    IDsOfSpins[spinId]=cursor.getInt(idIndex);
                                    IDsOfValue[spinId]=cursor.getInt(currIndex);
                                    Log.d("mLog",IDsOfValue[spinId]+"-"+cursor.getInt(currIndex)+"-");
                                    //txtCurs1.setText("1 "+cursor.getString(currIndex)+" = ");
                                }
                            }while(cursor.moveToNext());
                        }//end if
                        cursor.close();
                        txtCurs1.setText("1 "+changeTextValueOfBills(IDsOfValue[spinId])+" = ");
                        txtVal1.setText(changeTextValueOfBills(IDsOfValue[spinId]));
                        txtVal3.setText(changeTextValueOfBills(IDsOfValue[spinId]));
                        compareValuesOfBills();
                        break;
                    }
                    case 1:{
                        billSpinner(2,DBHelper.TABLE_EXP_TYPE,DBHelper.KEY_EXP_TYPE_NAME);
                        break;
                    }
                    case 2:{
                        SQLiteDatabase database=dbHelper.getReadableDatabase();
                        Cursor cursor=database.query(DBHelper.TABLE_EXP_TYPE,null,null,null,null,null,null);
                        List<Integer> list = new ArrayList<Integer>();
                        if(cursor.moveToFirst()){
                            int idIndex=cursor.getColumnIndex(DBHelper.KEY_EXP_TYPE_ID);
                            int nameIndex=cursor.getColumnIndex(DBHelper.KEY_EXP_TYPE_NAME);
                            int currIndex=cursor.getColumnIndex(DBHelper.KEY_EXP_TYPE_CURRENCY);
                            do{
                                list.add(cursor.getInt(idIndex));
                                if(spinBills[2].getSelectedItem().toString().equals(cursor.getString(nameIndex))) {
                                    IDsOfSpins[spinId]=cursor.getInt(idIndex);
                                    IDsOfValue[spinId]=cursor.getInt(currIndex);
                                    //txtCurs2.setText(cursor.getString(currIndex));
                                }
                            }while(cursor.moveToNext());
                        }//end if
                        cursor.close();
                        txtCurs2.setText(changeTextValueOfBills(IDsOfValue[spinId]));
                        txtVal2.setText(changeTextValueOfBills(IDsOfValue[spinId]));
                        compareValuesOfBills();
                        break;
                    }
                }//end switch
            }//end onitemselected
            public void onNothingSelected(AdapterView<?> parent) {}
        });//end spinBills id
    }//end spinnerchanger

    private String changeTextValueOfBills(int valID){
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        String[] Colum={"_id","name"};
        String Select="_id = " + valID;
        Cursor cursorT=database.query(DBHelper.TABLE_CURRENCY,Colum,Select,null,null,null,"_id DESC"); //table_currency
        if(cursorT.moveToFirst()){
            int idIndexT=cursorT.getColumnIndex((DBHelper.KEY_CURR_ID));
            int nameIndexT=cursorT.getColumnIndex((DBHelper.KEY_CURR_NAME));
            do{
                if((cursorT.getInt(idIndexT)==valID))
                    return cursorT.getString(nameIndexT);
                else
                    return "-";
            }while(cursorT.moveToNext());
        }//end if
        cursorT.close();
        return "?";
    }

    private boolean compareValuesOfBills(){
        if(IDsOfValue[0]==IDsOfValue[2]){
            etxtCursValue.setText("1");
            Toast toast = Toast.makeText(getApplicationContext(),"Валюта счета, с которого переводятся средства, " +
                    "и валюта счета, на который переводятся деньги - совпадают.", Toast.LENGTH_SHORT);
            toast.show();
            TextView txt5=(TextView) findViewById(R.id.txt5);
            txt5.setVisibility(View.INVISIBLE);
            txtCurs1.setVisibility(View.INVISIBLE);
            txtCurs2.setVisibility(View.INVISIBLE);
            etxtCursValue.setVisibility(View.INVISIBLE);
            return true;
        } else{
            //etxtCursValue.setText("1");
            Toast toast = Toast.makeText(getApplicationContext(),"Валюта счета, с которого переводятся средства, " +
                    "и валюта счета, на который переводятся деньги - НЕсовпадают.", Toast.LENGTH_SHORT);
            toast.show();
            TextView txt5=(TextView) findViewById(R.id.txt5);
            txt5.setVisibility(View.VISIBLE);
            txtCurs1.setVisibility(View.VISIBLE);
            txtCurs2.setVisibility(View.VISIBLE);
            etxtCursValue.setVisibility(View.VISIBLE);
            return false;
        }
    }//end compare

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnYes: {
                if(compareSumsOfBills())
                    ClickYes();
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Недостаточно средств на счету.", Toast.LENGTH_SHORT);
                    toast.show();
                }
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

    protected Dialog onCreateDialog(int dd) {
        if (dd == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear+1900, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(dd);
    }

    private String dateToDBformat(Date date) {
        String retval = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss"); //dlya sortirovki
        if (date == null) {
            return retval;
        }
        retval = sdf.format(date);
        return retval;
    }

    private int returnIdOf(int spinId, String tableName, String ColumName){
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ColumName);
            do {
                if(cursor.getString(idIndex).equals(spinBills[spinId].getSelectedItem().toString())) {
                    Log.d("mLog","blhjgvkhvjghvhvohuvk"+spinBills[spinId].getSelectedItem().toString());
                    return cursor.getInt(idIndex);
                }//end if
            } while (cursor.moveToNext());
        }//end if
        return 0;
    }

    private void ClickYes(){
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ACT_USER, returnIdOf(0,DBHelper.TABLE_BILLS,DBHelper.KEY_BILL_OWNER));
        Date dbDate=new Date();
        dbDate.setYear(myYear);
        dbDate.setMonth(myMonth);
        dbDate.setDate(myDay);
        contentValues.put(DBHelper.KEY_ACT_DATE, dateToDBformat(dbDate));
        contentValues.put(DBHelper.KEY_ACT_TYPE, 2);
        //contentValues.put(DBHelper.KEY_ACT_FROM, returnIdOf(0,DBHelper.TABLE_BILLS,DBHelper.KEY_BILL_ID));
        //contentValues.put(DBHelper.KEY_ACT_TO, returnIdOf(2,DBHelper.TABLE_EXP_TYPE,DBHelper.KEY_EXP_TYPE_ID));
        contentValues.put(DBHelper.KEY_ACT_FROM, IDsOfSpins[0]);
        contentValues.put(DBHelper.KEY_ACT_TO, IDsOfSpins[2]);
        contentValues.put(DBHelper.KEY_ACT_CURR, Double.parseDouble(etxtCursValue.getText().toString()));
        contentValues.put(DBHelper.KEY_ACT_SUM, Double.parseDouble(etxtSumm.getText().toString()));
        if(!compareValuesOfBills()){
            Toast toast = Toast.makeText(getApplicationContext(),"Валюта счета, с которого переводятся средства, " +
                    "и валюта категории расходов, на который тратятся деньги - НЕСОВПАДАЮТ. ПРОВЕРЬТЕ КОЭФИЦИЕНТ!", Toast.LENGTH_SHORT);
            toast.show();
        }
        database.insert(DBHelper.TABLE_ACTIVITIES, null, contentValues);
        contentValues.clear();
        database.close();
        finish();
    }//end ClickYes()

    private void ClickNo(){
        finish();
    }//end ClickNo

    private boolean compareSumsOfBills(){
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor=database.query(DBHelper.TABLE_BILLS,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            //int idIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_ID);
            int nameIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_NAME);
            //int curIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_CURRENCY);
            int sumIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_SUMM);
            do{
                if(cursor.getString(nameIndex).equals(spinBills[0].getSelectedItem().toString())) {
                    if (cursor.getDouble(sumIndex) < (Double.parseDouble(etxtCursValue.getText().toString()) * Double.parseDouble(etxtSumm.getText().toString()))) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Недостаточно средств на счету.", Toast.LENGTH_SHORT);
                        toast.show();
                        return false;
                    } else {
                        return true;
                    }
                }
            }while(cursor.moveToNext());
        }//end if
        database.close();
        return false;
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
}
