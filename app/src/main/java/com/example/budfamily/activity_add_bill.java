package com.example.budfamily;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class activity_add_bill extends AppCompatActivity {
    DBHelper dbHelper;

    Button btnYes;
    Spinner spin;
    EditText edTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);
        edTxt=(EditText) findViewById(R.id.edTxt);
        btnYes=(Button) findViewById(R.id.btnYes);
        spin=(Spinner) findViewById(R.id.spinType);
        billSpinner(DBHelper.TABLE_CURRENCY,DBHelper.KEY_CURR_NAME, spin);
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnYes: {
                ClickYes();
                break;
            }//end R.id.btnYes
            default: {
                break;
            }
        }//end switch(v.getId())
    }//end onClick(View v)

    private void ClickYes(){
        /*Bundle arguments = getIntent().getExtras();
        String name=arguments.get("name").toString();
        int code=arguments.getInt("code");
        Log.d("MESSAGE", name+" "+code);
        activity_choose.BillList bl=new activity_choose.BillList();
        bl.addBilltoListAndDB(name,code);
        bl.showList();*/
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_BILL_NAME, edTxt.getText().toString());
        contentValues.put(DBHelper.KEY_BILL_CURRENCY, spin.getSelectedItemId()+1);
        contentValues.put(DBHelper.KEY_BILL_OWNER, 0);
        contentValues.put(DBHelper.KEY_BILL_SUMM, 0.0);
        database.insert(DBHelper.TABLE_BILLS, null, contentValues);
        contentValues.clear();
        Intent intent=new Intent(this, activity_choose.class);
        //intent.putExtra("name",(String) edTxt.getText().toString());
        //intent.putExtra("code",(int) spin.getSelectedItemId()+1);
        startActivity(intent);
    }

    private void billSpinner(String tableName, String columnName, Spinner spin){
        dbHelper=new DBHelper(this);
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
}
