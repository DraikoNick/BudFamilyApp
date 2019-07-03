package com.example.budfamily;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class activity_result extends AppCompatActivity {

    int DIALOG_DATE1=1;
    int DIALOG_DATE2=2;
    int myYear,myMonth,myDay;
    Date dateFrom=Calendar.getInstance().getTime();
    Date dateTo=Calendar.getInstance().getTime();

    DBHelper dbHelper;
    ScrollView scrolV;
    TableLayout tabLay;
    Button btnShowAll,btnShowBill,btnShowCost,btnDateFrom,btnDateTo;
    List<activCount> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        scrolV=(ScrollView)findViewById(R.id.scrolview);
        tabLay=(TableLayout)findViewById(R.id.tabLay);
        btnShowAll=(Button)findViewById(R.id.btnShowAll);
        btnShowBill=(Button)findViewById(R.id.btnShowBills);
        btnShowCost=(Button)findViewById(R.id.btnShowCosts);
        btnDateFrom=(Button)findViewById(R.id.btnDateFrom);
        btnDateTo=(Button)findViewById(R.id.btnDateTo);
        dbHelper=new DBHelper(this);
        showRowName();
        showBills();
        getCurrentTime();
    }

    private void showBills(){
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        String[] Colum={DBHelper.KEY_ACT_DATE,DBHelper.KEY_ACT_TYPE,DBHelper.KEY_ACT_FROM,DBHelper.KEY_ACT_TO,DBHelper.KEY_ACT_CURR,DBHelper.KEY_ACT_SUM};
        Cursor cursor=database.query(DBHelper.TABLE_ACTIVITIES,Colum,null,null,null,null,"_id DESC");
        if(cursor.moveToFirst()){
            int typeIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_TYPE);
            int dataIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_DATE);
            int fromIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_FROM);
            int toIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_TO);
            int sumIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_SUM);
            int curIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_CURR);
            do{
                String from="";
                String to="";
                switch(cursor.getInt(typeIndex)){
                    case 0:{
                        to=getName(0,Integer.parseInt(cursor.getString(toIndex)));
                        break;
                    }
                    case 1:{
                        from=getName(0,Integer.parseInt(cursor.getString(fromIndex)));
                        to=getName(0,Integer.parseInt(cursor.getString(toIndex)));
                        break;
                    }
                    case 2:{
                        from=getName(0,Integer.parseInt(cursor.getString(fromIndex)));
                        to=getName(1,Integer.parseInt(cursor.getString(toIndex)));
                        break;
                    }
                }
                showRowData(cursor.getInt(typeIndex), parseDate(cursor.getString(dataIndex)), from, to,cursor.getDouble(sumIndex)*cursor.getDouble(curIndex));
                activCount ac=new activCount(cursor.getInt(typeIndex), parseDate(cursor.getString(dataIndex)), from, to,cursor.getDouble(sumIndex)*cursor.getDouble(curIndex));
                list.add(ac);
            }while(cursor.moveToNext());
        }//end if
        database.close();
        cursor.close();
    }

    private void showRowName(){
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        //начинаем заполнять строку
        TextView txttype=new TextView(this);
        TextView txtdate=new TextView(this);
        TextView txtfrom=new TextView(this);
        TextView txtto=new TextView(this);
        TextView txtsumm=new TextView(this);
        txttype.setText(" Тип ");
        txttype.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
        txttype.setTextSize(18);
        txtdate.setText(" Дата ");
        txtdate.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
        txtdate.setTextSize(18);
        txtfrom.setText(" Со Счета ");
        txtfrom.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
        txtfrom.setTextSize(18);
        txtto.setText(" На Счет ");
        txtto.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
        txtto.setTextSize(18);
        txtsumm.setText(" Сумма ");
        txtsumm.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
        txtsumm.setTextSize(18);
        tableRow.addView(txttype);
        tableRow.addView(txtdate);
        tableRow.addView(txtfrom);
        tableRow.addView(txtto);
        tableRow.addView(txtsumm);
        //заполненную строку добьавим в таблицу
        tabLay.addView(tableRow);
    }

    private void showRowData(int type, String date, String from, String to, Double summCurr){
        ImageView img=new ImageView(this);
        switch(type){
            case 0:{
                img.setImageResource(R.drawable.ic_add_black_24dp);
                break;
            }
            case 1:{
                img.setImageResource(R.drawable.ic_import_export_black_24dp);
                break;
            }
            case 2:{
                img.setImageResource(R.drawable.ic_remove_black_24dp);
                break;
            }
        }
        //создали строку
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        //начинаем заполнять строку
        TextView txtdate=new TextView(this);
        TextView txtfrom=new TextView(this);
        TextView txtto=new TextView(this);
        TextView txtsumm=new TextView(this);
        txtdate.setText(date);
        txtdate.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
        txtfrom.setText(from);
        txtfrom.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
        txtto.setText(to);
        txtto.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
        txtsumm.setText(Double.toString(summCurr));
        txtsumm.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
        tableRow.addView(img);
        tableRow.addView(txtdate);
        tableRow.addView(txtfrom);
        tableRow.addView(txtto);
        tableRow.addView(txtsumm);
        //заполненную строку добьавим в таблицу
        tabLay.addView(tableRow);
    }

    private Date strToDate(String date){
        Date dDate=Calendar.getInstance().getTime();
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            dDate=df.parse(date);
            return dDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dDate;
    }

    private String parseDate(String date){
        String result="";
        try {
            date=date.substring(0, date.length() - 7);
            Date d=null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            d = sdf.parse(date);
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            result=df.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getName(int type, int id){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String[] ColumBill={DBHelper.KEY_BILL_NAME,DBHelper.KEY_BILL_ID};
        String[] ColumCost={DBHelper.KEY_EXP_TYPE_NAME,DBHelper.KEY_EXP_TYPE_ID};
        String SelectBill=DBHelper.KEY_BILL_ID+" = "+id;
        String SelectCost=DBHelper.KEY_EXP_TYPE_ID+" = "+id;
        Cursor cursor;
        String result="";
        if(type==0){
            cursor=db.query(DBHelper.TABLE_BILLS,ColumBill,SelectBill,null,null,null,null);
            cursor.moveToFirst();
            int index=cursor.getColumnIndex(DBHelper.KEY_BILL_NAME);
            result= cursor.getString(index);
            db.close();
            cursor.close();
        }
        if(type==1){
            cursor=db.query(DBHelper.TABLE_EXP_TYPE,ColumCost,SelectCost,null,null,null,null);
            cursor.moveToFirst();
            int index=cursor.getColumnIndex(DBHelper.KEY_EXP_TYPE_NAME);
            result= cursor.getString(index);
            db.close();
            cursor.close();
        }
        return result;
    }

    private class activCount{
        private int type;
        private String date;
        private String from;
        private String to;
        private Double summ;

        public void setType(int type) {
            this.type = type;
        }
        public void setDate(String date) {
            this.date = date;
        }
        public void setFrom(String from) {
            this.from = from;
        }
        public void setTo(String to) {
            this.to = to;
        }
        public void setSumm(Double summ) {
            this.summ = summ;
        }

        public int getType() {
            return type;
        }
        public String getDate() {
            return date;
        }
        public String getFrom() {
            return from;
        }
        public String getTo() {
            return to;
        }
        public Double getSumm() {
            return summ;
        }

        activCount(int type, String data, String from,String to, Double summ){
            setType(type);
            setDate(data);
            setFrom(from);
            setTo(to);
            setSumm(summ);
        }
    }

    private void showList(int type){
        //0-все
        //1-счета
        //2-расходы
        //3-начиная с
        //4-заканчивая
        for(int i=0;i<list.size();i++){
            switch(type){
                case 0:{
                    showRowData(list.get(i).getType(),list.get(i).getDate(),list.get(i).getFrom(),list.get(i).getTo(),list.get(i).getSumm());
                    break;
                }
                case 1:{
                    if(list.get(i).getType()==0 || list.get(i).getType()==1)
                        showRowData(list.get(i).getType(),list.get(i).getDate(),list.get(i).getFrom(),list.get(i).getTo(),list.get(i).getSumm());
                    break;
                }
                case 2:{
                    if(list.get(i).getType()==2)
                        showRowData(list.get(i).getType(),list.get(i).getDate(),list.get(i).getFrom(),list.get(i).getTo(),list.get(i).getSumm());
                    break;
                }
                case 3:{
                    if(strToDate(list.get(i).getDate()).after(dateFrom))
                        showRowData(list.get(i).getType(),list.get(i).getDate(),list.get(i).getFrom(),list.get(i).getTo(),list.get(i).getSumm());
                    break;
                }
                case 4:{
                    if(strToDate(list.get(i).getDate()).before(dateTo))
                        showRowData(list.get(i).getType(),list.get(i).getDate(),list.get(i).getFrom(),list.get(i).getTo(),list.get(i).getSumm());
                    break;
                }
                default:
                    if(strToDate(list.get(i).getDate()).before(dateTo) & strToDate(list.get(i).getDate()).after(dateFrom))
                        showRowData(list.get(i).getType(),list.get(i).getDate(),list.get(i).getFrom(),list.get(i).getTo(),list.get(i).getSumm());
                    break;
            }
        }
    }

    ///////////////////////////////////////
    private void getCurrentTime(){
        Date currentDate = Calendar.getInstance().getTime();
        dateFrom=currentDate;
        dateTo=currentDate;
        //etxtDate.setText(formattedDate.toString());
        myYear=currentDate.getYear()+1900;
        myMonth=currentDate.getMonth();
        myDay=currentDate.getDate();
    }//end getCurrenTime()
    DatePickerDialog.OnDateSetListener myCallBack1 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateFrom.setYear(year);dateFrom.setMonth(monthOfYear);dateFrom.setDate(dayOfMonth);
        }
    };
    DatePickerDialog.OnDateSetListener myCallBack2 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTo.setYear(year);dateFrom.setMonth(monthOfYear);dateFrom.setDate(dayOfMonth);
        }
    };
    protected Dialog onCreateDialog(int dd) {
        if (dd == DIALOG_DATE1) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack1, myYear, myMonth, myDay);
            return tpd;
        }
        if (dd == DIALOG_DATE2) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack2, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(dd);
    }
    ///////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case R.id.btnShowAll:
                tabLay.removeAllViews();
                showRowName();
                showList(0);
                return true;
            case R.id.btnShowBills:
                tabLay.removeAllViews();
                showRowName();
                showList(1);
                return true;
            case R.id.btnShowCosts:
                tabLay.removeAllViews();
                showRowName();
                showList(2);
                return true;
            case R.id.btnDateFrom:
                tabLay.removeAllViews();
                showDialog(DIALOG_DATE1);
                showRowName();
                showList(3);
                return true;
            case R.id.btnDateTo:
                tabLay.removeAllViews();
                showDialog(DIALOG_DATE2);
                showRowName();
                showList(4);
                return true;
            default:
                return true;
        }
    }
    ///////////////////////////////////////
}
