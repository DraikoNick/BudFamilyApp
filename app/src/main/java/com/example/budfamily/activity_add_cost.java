package com.example.budfamily;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class activity_add_cost extends AppCompatActivity {

    DBHelper dbHelper;

    Button btnYes;
    Spinner spinValue, spinType;
    EditText edTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cost);
        edTxt=(EditText) findViewById(R.id.edTxt);
        btnYes=(Button) findViewById(R.id.btnYes);
        spinValue=(Spinner) findViewById(R.id.spinValue);
        spinType=(Spinner) findViewById(R.id.spinType);
        billSpinner(DBHelper.TABLE_CURRENCY,DBHelper.KEY_CURR_NAME, spinValue);
        billSpinner(DBHelper.TABLE_EXP_CAT,DBHelper.KEY_EXP_CAT_NAME, spinType);
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
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_EXP_TYPE_NAME, edTxt.getText().toString());
        contentValues.put(DBHelper.KEY_EXP_TYPE_CURRENCY, spinValue.getSelectedItemId()+1);
        contentValues.put(DBHelper.KEY_EXP_TYPE_EXPCAT, spinType.getSelectedItemId()+1);
        database.insert(DBHelper.TABLE_EXP_TYPE, null, contentValues);
        contentValues.clear();
        Intent intent=new Intent(this, activity_choose.class);
        //intent.putExtra("nameC",edTxt.getText().toString());
        //intent.putExtra("codeCvalue",(int) spinValue.getSelectedItemId()+1);
        //intent.putExtra("codeCtype",(int) spinType.getSelectedItemId()+1);
        startActivity(intent);
    }

}
