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

public class activity_choose_change extends AppCompatActivity {

    DBHelper dbHelper;

    int DIALOG_DATE=1;
    int myYear,myMonth,myDay;

    TextView etxtDate;
    Spinner [] spinBills=new Spinner[2];
    EditText etxtSumm;
    TextView txtCurs1,txtCurs2,txtVal1,txtVal2,txtVal3;
    EditText etxtCursValue;
    Button btnNo,btnYes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_change);
        //ищем все элементы в активности
        etxtDate=(TextView) findViewById(R.id.etxtDate);
        spinBills[0]=(Spinner) findViewById(R.id.spinBill1);
        spinBills[1]=(Spinner) findViewById(R.id.spinBill2);
        etxtSumm=(EditText) findViewById(R.id.etxtSum);
        //spinValue=(Spinner) findViewById(R.id.spinValue);
        txtCurs1=(TextView) findViewById(R.id.txtCurs1);
        txtVal1=(TextView) findViewById(R.id.txtVal1);
        txtCurs2=(TextView) findViewById(R.id.txtCurs2);
        txtVal2=(TextView) findViewById(R.id.txtVal2);
        txtVal3=(TextView) findViewById(R.id.txtVal3);
        etxtCursValue=(EditText) findViewById(R.id.etxtCurs);
        btnNo=(Button) findViewById(R.id.btnNo);
        btnYes=(Button) findViewById(R.id.btnYes);
        dbHelper=new DBHelper(this);
        //установить начальные значения
        getCurrentTime();
        billSpinner(DBHelper.TABLE_BILLS,DBHelper.KEY_BILL_NAME, spinBills[0]);
        billSpinner(DBHelper.TABLE_BILLS,DBHelper.KEY_BILL_NAME, spinBills[1]);
        etxtSumm.setText("0");
        etxtCursValue.setText("1");
        spinnerChanger(0);
        spinnerChanger(1);
        //скрыть курс если валюта одинаковая
    }

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

    private void spinnerChanger(final int ii){
        spinBills[ii].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                Cursor cursorT=database.query(DBHelper.TABLE_CURRENCY,Colum,Select,null,null,null,"_id DESC");
                if(cursorT.moveToFirst()){
                    int idIndexT=cursorT.getColumnIndex((DBHelper.KEY_CURR_ID));
                    int nameIndexT=cursorT.getColumnIndex((DBHelper.KEY_CURR_NAME));
                    do{
                        cursorT.getInt(idIndexT);
                        if((cursorT.getInt(idIndexT)==list.get(selectedItemPosition)) & ii==1){
                            txtCurs1.setText("1 "+cursorT.getString(nameIndexT)+" =");
                            txtVal2.setText(cursorT.getString(nameIndexT));
                            txtVal3.setText(cursorT.getString(nameIndexT));
                        }
                        else{
                            txtCurs2.setText(cursorT.getString(nameIndexT));
                            txtVal1.setText(cursorT.getString(nameIndexT));
                        }
                    }while(cursor.moveToNext());
                }
                cursorT.close();
                compareValuesOfBills();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private boolean compareValuesOfBills(){
        if(txtCurs1.getText().toString().equals(txtCurs2.getText().toString())){
            etxtCursValue.setText("1");
            /*Toast toast = Toast.makeText(getApplicationContext(),"Валюта счета, с которого переводятся средства, " +
                    "и валюта счета, на который переводятся деньги - совпадают.", Toast.LENGTH_SHORT);
            toast.show();*/
            TextView txt5=(TextView) findViewById(R.id.txt5);
            txt5.setVisibility(View.INVISIBLE);
            txtCurs1.setVisibility(View.INVISIBLE);
            txtCurs2.setVisibility(View.INVISIBLE);
            etxtCursValue.setVisibility(View.INVISIBLE);
            return true;
        } else{
            etxtCursValue.setText("1");
            /*Toast toast = Toast.makeText(getApplicationContext(),"Валюта счета, с которого переводятся средства, " +
                    "и валюта счета, на который переводятся деньги - НЕсовпадают.", Toast.LENGTH_SHORT);
            toast.show();*/
            TextView txt5=(TextView) findViewById(R.id.txt5);
            txt5.setVisibility(View.VISIBLE);
            txtCurs1.setVisibility(View.VISIBLE);
            txtCurs2.setVisibility(View.VISIBLE);
            etxtCursValue.setVisibility(View.VISIBLE);
            return false;
        }
    }//end compare

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
                    Log.d("what select", spinBills[0].getSelectedItem().toString() + " " + cursor.getString(nameIndex));
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

    private boolean compareBills(){
        if(spinBills[0].getSelectedItem().toString().equals(spinBills[1].getSelectedItem().toString())){
            Toast toast = Toast.makeText(getApplicationContext(),"Вы пытаетесь с одного счета перебросить средства на этот же счет", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        } else{

            return false;
        }
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

    private String dateToDBformat(Date date) {
        String retval = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss"); //dlya sortirovki
        if (date == null) {
            return retval;
        }
        retval = sdf.format(date);
        return retval;
    }

    private int returnIdOf(int spin, int id){
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_BILLS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_NAME);
            int curIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_CURRENCY);
            int ownIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_OWNER);
            do {
                if(cursor.getString(nameIndex).equals(spinBills[spin].getSelectedItem().toString())) {
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
        if(compareSumsOfBills() & !compareBills()){
            //если средств достаточно на счету и счета разные то записываем в базу трат
            SQLiteDatabase database=dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.KEY_ACT_USER, returnIdOf(0,3));    //ай ди пользователя на будущее
            Date dbDate=new Date();
            dbDate.setYear(myYear);
            dbDate.setMonth(myMonth);
            dbDate.setDate(myDay);
            contentValues.put(DBHelper.KEY_ACT_DATE, dateToDBformat(dbDate));
            contentValues.put(DBHelper.KEY_ACT_TYPE, 1);
            contentValues.put(DBHelper.KEY_ACT_FROM, returnIdOf(0,1));
            contentValues.put(DBHelper.KEY_ACT_TO, returnIdOf(1,1));
            contentValues.put(DBHelper.KEY_ACT_CURR, Double.parseDouble(etxtCursValue.getText().toString()));
            contentValues.put(DBHelper.KEY_ACT_SUM, Double.parseDouble(etxtSumm.getText().toString()));
            database.insert(DBHelper.TABLE_ACTIVITIES, null, contentValues);
            contentValues.clear();
            database.close();
            finish();
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "У Вас или недостаточно средств на счету, или вы пытаетесь перевести деньги со счета на этот же счет.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }//end ClickYes()

    private void ClickNo(){
        finish();
    }//end ClickNo

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
}//end class

