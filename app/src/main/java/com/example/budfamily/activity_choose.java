package com.example.budfamily;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.budfamily.activity_settings.getFirstDateOfMonth;
import static com.example.budfamily.activity_settings.getFormattedDate;
import static com.example.budfamily.activity_settings.getFormattedDay;

public class activity_choose extends AppCompatActivity implements View.OnClickListener {

    DBHelper dbHelper;
    private SwipeRefreshLayout mSwipeRefresh;

    Button btnAddBill;
    HorizontalScrollView hScrolBill;
        LinearLayout linLayBill;
    Button btnAddCost;
    ScrollView scrolCost;
        LinearLayout linLayCost;
    BillList billList=new BillList();
    CostList costList=new CostList();
    public static final int CENTER=0;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_APP_OPEN_COUNTER = "open_app_counter";
    public static final String APP_PREFERENCES_PERIOD_START = "period_start";           //номер дня месяца  //01
    public static final String APP_PREFERENCES_PERIOD_THIS = "period_this";             //старт периода в этом месяце //01.01.2019
    public static final String APP_PREFERENCES_OWNER = "owner";
    public static final String APP_PREFERENCES_SHOW_INFO_CODE = "show_info_code";
    private SharedPreferences appSettings;
    private int appOpenCounter;     //от этого зависит будет ли создана база лии использоваться существующая
    private String periodStartDay;       //период за который будет выводиться на экран информация о счетах и расходах
    private String thisPeriodStart;
    private int ownerId=1;      //в будущем можно поставить обработчик пользователей.
    private int infoCode;   //0-только имя, 1-все, 2-имя и валюта

    private int countOfBills=3;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_add:
                    billList.showList();
                    Intent intent=new Intent(activity_choose.this,activity_choose_add.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_change:
                    billList.showList();
                    Intent intent2=new Intent(activity_choose.this,activity_choose_change.class);
                    startActivity(intent2);
                    return true;
                case R.id.navigation_pay:
                    billList.showList();
                    Intent intent3=new Intent(activity_choose.this,activity_choose_pay.class);
                    startActivity(intent3);
                    return true;
            }
            return false;
        }
    };//end BottomNavigationView.OnNavigationItemSelectedListener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //создаем активити и нижнюю навигацию
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //ищем все элементы
        //кнопки
        btnAddBill=(Button) findViewById(R.id.btnAddBill);
        btnAddCost=(Button) findViewById(R.id.btnAddCost);
        btnAddBill.setOnClickListener(this);
        btnAddCost.setOnClickListener(this);
        //элементы счетов
        hScrolBill=(HorizontalScrollView) findViewById(R.id.horScrolViewBills);
        linLayBill=(LinearLayout) findViewById(R.id.LinLayBills);
        //элементы трат
        scrolCost=(ScrollView) findViewById(R.id.VertScrolViewCosts);
        linLayCost=(LinearLayout) findViewById(R.id.LinLayCost);
        dbHelper=new DBHelper(this);
        showViewOfBills();  //вывод на экран счета которые имеем
        showViewOfCosts();  //вывод расходов
        //загрузили настройки
        appSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        //посчитали в какой раз пользователь открыл приложение
        ++appOpenCounter;
        //вытаскиваем информацию об отображении полей
        if (appSettings.contains(APP_PREFERENCES_SHOW_INFO_CODE))
            infoCode = appSettings.getInt(APP_PREFERENCES_SHOW_INFO_CODE, 0);                               //0.1.2
        if (appSettings.contains(APP_PREFERENCES_PERIOD_THIS))
            periodStartDay = appSettings.getString(APP_PREFERENCES_PERIOD_START, getFormattedDate(getFirstDateOfMonth(1)));    //01.01.2019
        if (appSettings.contains(APP_PREFERENCES_PERIOD_START))
            thisPeriodStart = appSettings.getString(APP_PREFERENCES_PERIOD_THIS, getFormattedDay(getFormattedDate(getFirstDateOfMonth(1))));      //01
        onPause();
        billList.readBills();
        costList.readCost();
        costList.writeCostsSummToList();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnAddBill: {
                clearViewOfBills();
                Intent intent=new Intent(activity_choose.this,activity_add_bill.class);
                startActivity(intent);
                showViewOfBills();
                break;
            }//end R.id.btnAddBill
            case R.id.btnAddCost: {
                clearViewOfCosts();
                Intent intent=new Intent(activity_choose.this,activity_add_cost.class);
                startActivity(intent);
                showViewOfCosts();
                break;
            }//end R.id.btnAddCost
            default:
                //Log.d("ALARMa","BUTTON PRESSED"+v.getId());
                showInfoAboutButton(v.getId());
                break;
        }//end switch(v.getId())
    }//end onClick(View v)

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case R.id.btnShowAll:
                Intent intent=new Intent(activity_choose.this,activity_result.class);
                startActivity(intent);
                return true;
            case R.id.btnShowBills:
                Intent intent2=new Intent(activity_choose.this,activity_settings.class);
                startActivity(intent2);
                return true;
            default:
                return true;
        }
    }

    private void showViewOfBills(){
        //вывод на экран информации о счетах и расходах
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor=database.query(DBHelper.TABLE_BILLS,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            int idIndex=cursor.getColumnIndex((DBHelper.KEY_BILL_ID));
            int nameIndex=cursor.getColumnIndex((DBHelper.KEY_BILL_NAME));
            int sumIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_SUMM);
            int curIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_CURRENCY);
            //еще можно добавить владельца счета и валюту счета
            do{
                Button btnNew=new Button(this);
                //0-только имя, 1-все, 2-имя и валюта
                switch(infoCode){
                    case 0:{
                        btnNew.setText(cursor.getString(nameIndex));
                        break;
                    }
                    case 1:{
                        btnNew.setText(cursor.getString(nameIndex)+"\n"+cursor.getDouble(sumIndex)+" "+returnNameOfValueByID(cursor.getInt(curIndex)));
                        break;
                    }
                    case 2:{
                        btnNew.setText(cursor.getString(nameIndex)+"\n"+returnNameOfValueByID(cursor.getInt(curIndex)));
                        break;
                    }
                    default: {
                        btnNew.setText(cursor.getString(nameIndex));
                        break;
                    }
                }
                btnNew.setOnClickListener(this);
                btnNew.setTextSize(12);
                linLayBill.addView(btnNew);   //номер индекса будем брать тоже из базы
                //btnNew.setId(cursor.getPosition()+1);
                btnNew.setId(cursor.getInt(idIndex)+1000);
                //метод вывода на экран, который передаст ай ди и имя счета
            }while(cursor.moveToNext());
        }//end if
        countOfBills=cursor.getPosition()+1;
        database.close();
        cursor.close();
    }//end showViewOfBillsAndCosts()

    private void showViewOfCosts(){
        //вывод на экран информации о счетах и расходах
        //int stopCount=101;  //необходимо для установки АЙди кнопкам
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursorC=database.query(DBHelper.TABLE_EXP_CAT,null,null,null,null,null,null);
        if(cursorC.moveToFirst()){
            int idIndex=cursorC.getColumnIndex((DBHelper.KEY_EXP_CAT_ID));
            int nameIndex=cursorC.getColumnIndex((DBHelper.KEY_EXP_CAT_NAME));
            //еще можно добавить владельца счета и валюту счета
            do{
                //создаем горизонтальный скрол и в нем лин лэй и текст
                HorizontalScrollView horScrolNew=new HorizontalScrollView(this);
                LinearLayout linLayNew=new LinearLayout(this);
                LinearLayout.LayoutParams lparam=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,130);
                TextView textNew=new TextView(this);
                textNew.setTextSize(16);
                textNew.setText(cursorC.getString(nameIndex));
                //выводим на экран
                linLayCost.addView(horScrolNew,lparam);
                horScrolNew.addView(linLayNew,lparam);
                //String[] Colum={"_id","name","expcat"};
                String Select="expcat = " + cursorC.getString(idIndex);
                Cursor cursorT=database.query(DBHelper.TABLE_EXP_TYPE,null,Select,null,null,null,null);
                if(cursorT.moveToFirst()){
                    int idIndexT=cursorT.getColumnIndex((DBHelper.KEY_EXP_TYPE_ID));
                    int nameIndexT=cursorT.getColumnIndex((DBHelper.KEY_EXP_TYPE_NAME));
                    int curIndexT=cursorT.getColumnIndex(DBHelper.KEY_EXP_TYPE_CURRENCY);
                    do{
                        Button btnNew=new Button(this);
                        //btn.setText(billList.findBillname(buttonID)+"\n"+billList.getSumOfBillById(buttonID));
                        //0-только имя, 1-все, 2-имя и валюта
                        switch(infoCode){
                            case 0:{
                                btnNew.setText(cursorT.getString(nameIndexT));
                                break;
                            }
                            case 1:{
                                btnNew.setText(cursorT.getString(nameIndexT)+"\n"+costList.getSumOfCostById(cursorT.getInt(idIndexT))+" "+returnNameOfValueByID(cursorT.getInt(curIndexT)));
                                break;
                            }
                            case 2:{
                                btnNew.setText(cursorT.getString(nameIndexT)+"\n"+returnNameOfValueByID(cursorT.getInt(curIndexT)));
                                break;
                            }
                            default: {
                                btnNew.setText(cursorT.getString(nameIndexT));
                                break;
                            }
                        }
                        //btnNew.setText(cursorT.getString(nameIndexT));    //текст будет браться из названия счета
                        btnNew.setOnClickListener(this);
                        btnNew.setTextSize(12);
                        linLayNew.addView(btnNew,lparam);
                        //btnNew.setId(cursorT.getPosition()+stopCount);
                        btnNew.setId(cursorT.getInt(idIndexT));
                        //linLayNew.addView(btnNew, idIndexT,lparam);   //номер индекса будем брать тоже из базы*/
                    }while(cursorT.moveToNext());
                    linLayNew.addView(textNew,idIndex);
                }//end if
                //stopCount=stopCount+cursorT.getPosition();
                cursorT.close();
            }while(cursorC.moveToNext());
        }//end if
        database.close();
        cursorC.close();
    }//end showViewOfBillsAndCosts()

    private void clearViewOfBills(){
        linLayBill.removeAllViews();
    }//end clearViewOfBills()

    private void clearViewOfCosts(){
        linLayCost.removeAllViews();
    }//end clearViewOfCosts()

    private void showInfoAboutButton(int buttonID){
        if(buttonID>=1000){             //if(buttonID>1000 & buttonID<=countOfBills){
            //для счетов
            Button btn=(Button)findViewById(buttonID);
            btn.setText(billList.findBillname(buttonID-1000)+"\n"+billList.getSumOfBillById(buttonID-1000));
        }else{
            //для расходов
            Button btn=(Button)findViewById(buttonID);
            //btn.setText(costList.findCostname(buttonID-100)+"\n"+costList.getSumOfCostById(buttonID-100));
            btn.setText(costList.findCostname(buttonID)+"\n"+costList.getSumOfCostById(buttonID));
        }//end if
    }//end show info

    ////////////////////////////////////////////////////////////////////////
    public class BillList extends AppCompatActivity {
        private List<BIllCount> listOfBills=new ArrayList<BIllCount>();
        public void addBilltoListAndDB(String name, int curr){
            SQLiteDatabase database=dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.KEY_BILL_NAME, name);
            contentValues.put(DBHelper.KEY_BILL_CURRENCY, curr);
            contentValues.put(DBHelper.KEY_BILL_OWNER, ownerId);
            contentValues.put(DBHelper.KEY_BILL_SUMM, 0.0);
            database.insert(DBHelper.TABLE_BILLS, null, contentValues);
            contentValues.clear();
            Cursor cursor=database.query(DBHelper.TABLE_BILLS,null,null,null,null,null,null);
            cursor.moveToLast();
            int idIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_ID);
            BIllCount bc=new BIllCount(cursor.getInt(idIndex), name, curr, 0.0);
            listOfBills.add(bc);
            database.close();
        }//addbill
        public int findBillid(String name){
            for(int i=0; i<=listOfBills.size();i++){
                if(listOfBills.get(i).getBillName().equals(name))
                    return listOfBills.get(i).getBillID();
            }
            return 0;
        }//findbillid
        public String findBillname(int id){
            for(int i=0; i<=listOfBills.size();i++){
                if(listOfBills.get(i).getBillID()==id)
                    return listOfBills.get(i).getBillName();
            }
            return "";
        }//findbillname
        public Double getSumOfBillById(int id){
            for(int i=0; i<=listOfBills.size();i++){
                if(listOfBills.get(i).getBillID()==id)
                    return listOfBills.get(i).getBillSumm();
            }
            return 0.0;
        }
        public void makeChange(BIllCount bc1,BIllCount bc2, Double sum, Double change){
            bc1.takeMoney(sum*change);
            bc2.addMoney(sum);
        }//make change
        public void readBills() {
            SQLiteDatabase database=dbHelper.getReadableDatabase();
            Cursor cursor=database.query(DBHelper.TABLE_BILLS,null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                int idIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_ID);
                int nameIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_NAME);
                int curIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_CURRENCY);
                int sumIndex=cursor.getColumnIndex(DBHelper.KEY_BILL_SUMM);
                do{
                    BIllCount bill=new BIllCount();
                    bill.setBillID(cursor.getInt(idIndex));
                    bill.setBillName(cursor.getString(nameIndex));
                    bill.setBillCurrency(cursor.getInt(curIndex));
                    bill.setBillSumm(cursor.getDouble(sumIndex));
                    listOfBills.add(bill);
                }while(cursor.moveToNext());
            }//end if
            database.close();
        }
        public void writeBillsSummToList(){
            //Log.d("wtitebillsummtolist","");
            SQLiteDatabase database=dbHelper.getReadableDatabase();
            String[] Colum0={DBHelper.KEY_ACT_TYPE,DBHelper.KEY_ACT_TO,DBHelper.KEY_ACT_CURR,DBHelper.KEY_ACT_SUM};
            String Select=DBHelper.KEY_ACT_TYPE+" = 0";
            //получаем все когда либо поступившие средства
            Cursor cursor=database.query(DBHelper.TABLE_ACTIVITIES,Colum0,Select,null,DBHelper.KEY_ACT_TO,null,null);
            if(cursor.moveToFirst()){
                int toIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_TO);
                int sumIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_SUM);
                int curIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_CURR);
                do{
                    listOfBills.get(cursor.getInt(toIndex)-1).setBillSumm(cursor.getDouble(sumIndex)*cursor.getDouble(curIndex)+listOfBills.get(cursor.getInt(toIndex)-1).getBillSumm());    //вписываем в лист в счет сумму поступивших средств
                }while(cursor.moveToNext());
            }//end if
            //получаем все переводы средств
            String[] Colum1={DBHelper.KEY_ACT_TYPE,DBHelper.KEY_ACT_FROM,DBHelper.KEY_ACT_TO,DBHelper.KEY_ACT_CURR,DBHelper.KEY_ACT_SUM};
            Select=DBHelper.KEY_ACT_TYPE+" = 1";
            cursor=database.query(DBHelper.TABLE_ACTIVITIES,Colum1,Select,null,DBHelper.KEY_ACT_TO,null,null);
            if(cursor.moveToFirst()){
                int fromIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_FROM);
                int toIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_TO);
                int sumIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_SUM);
                int curIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_CURR);
                do{
                    makeChange(listOfBills.get(cursor.getInt(fromIndex)-1),listOfBills.get(cursor.getInt(toIndex)-1),cursor.getDouble(sumIndex),cursor.getDouble(curIndex));
                }while(cursor.moveToNext());
            }//end i
            //получаем все потраченые средства
            String[] Colum2={DBHelper.KEY_ACT_TYPE,DBHelper.KEY_ACT_FROM,DBHelper.KEY_ACT_SUM};
            Select=DBHelper.KEY_ACT_TYPE+" = 2";
            cursor=database.query(DBHelper.TABLE_ACTIVITIES,Colum2,Select,null,DBHelper.KEY_ACT_TO,null,null);
            if(cursor.moveToFirst()){
                int fromIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_FROM);
                int sumIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_SUM);
                do{
                    listOfBills.get(cursor.getInt(fromIndex)-1).setBillSumm(listOfBills.get(cursor.getInt(fromIndex)-1).getBillSumm()-cursor.getDouble(sumIndex));
                }while(cursor.moveToNext());
            }//end if
            cursor.close();
            database.close();
        }
        public void writeListToDataBase(){
            SQLiteDatabase database=dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            for(int i=0;i<listOfBills.size();i++){
                //этот фор пройдет и установит данные типов расходов в базу
                contentValues.put(DBHelper.KEY_BILL_SUMM, listOfBills.get(i).getBillSumm());
                int ids=i+1;
                String str=Integer.toString(ids);
                String[] id={str};
                database.update(DBHelper.TABLE_BILLS,contentValues,"_id = ?",id);
                //database.insert(DBHelper.TABLE_BILLS, null, contentValues);
                contentValues.clear();
            }//end for i<bills.length
            database.close();
        }
        public void showList(){
            Log.d("showlist","");
            Log.d("mLogListOfBills","СПИСОК СЧЕТОВ");
            for(int i=0;i<listOfBills.size();i++){
                Log.d("mLogListOfBills","Bill:     "+listOfBills.get(i).getBillID()+" "+listOfBills.get(i).getBillName()+" "+listOfBills.get(i).getBillCurrency()+" "+listOfBills.get(i).getBillSumm());
            }
        }//end showList
        public void updateList(){
            for(int i=0;i<listOfBills.size();i++){
                listOfBills.get(i).setBillSumm(0.0);
            }
            SQLiteDatabase database=dbHelper.getReadableDatabase();
            String Select;
            String[] Colum={DBHelper.KEY_ACT_TYPE,DBHelper.KEY_ACT_FROM,DBHelper.KEY_ACT_TO,DBHelper.KEY_ACT_CURR,DBHelper.KEY_ACT_SUM};
            Cursor cursor=database.query(DBHelper.TABLE_ACTIVITIES,Colum,null,null,DBHelper.KEY_ACT_TO,null,null);;
            for(int type=0;type<=2;type++){
                Select=DBHelper.KEY_ACT_TYPE+" = "+type;
                cursor=database.query(DBHelper.TABLE_ACTIVITIES,Colum,Select,null,null,null,null);
                if(cursor.moveToFirst()){
                    int fromIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_FROM);
                    int toIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_TO);
                    int sumIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_SUM);
                    int curIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_CURR);
                    do{
                        switch (type){
                            case 0:{    //поступление
                                listOfBills.get(cursor.getInt(toIndex)-1).setBillSumm(cursor.getDouble(sumIndex)*cursor.getDouble(curIndex)+listOfBills.get(cursor.getInt(toIndex)-1).getBillSumm());
                                break;
                            }
                            case 1:{       //перевод
                                makeChange(listOfBills.get(cursor.getInt(fromIndex)-1),listOfBills.get(cursor.getInt(toIndex)-1),cursor.getDouble(sumIndex),cursor.getDouble(curIndex));
                                break;
                            }
                            case 2:{    //траты
                                listOfBills.get(cursor.getInt(fromIndex)-1).setBillSumm(listOfBills.get(cursor.getInt(fromIndex)-1).getBillSumm()-cursor.getDouble(sumIndex));
                                break;
                            }
                        }
                    }while(cursor.moveToNext());
                }//end if
            }//end for
            database.close();
            cursor.close();
        }
    }//end BillList
    ////////////////////////////////////////////////////////////////////////
    public class BIllCount {
        private int BillID;
        private String BillName;
        private int BillCurrency;
        private Double BillSumm;
        public void setBillID(int billID) {
            BillID = billID;
        }
        public int getBillID() {
            return BillID;
        }
        public String getBillName() {
            return BillName;
        }
        public void setBillName(String billName) {
            BillName = billName;
        }
        public Double getBillSumm() {
            return BillSumm;
        }
        public void setBillSumm(Double billSumm) {
            BillSumm = billSumm;
        }
        public void setBillCurrency(int billCurrency) {
            BillCurrency = billCurrency;
        }
        public int getBillCurrency() {
            return BillCurrency;
        }
        BIllCount(){
            setBillName("");
            setBillCurrency(0);
            setBillSumm(0.0);
            setBillID(0);
        }
        BIllCount(int id, String name, int curr, Double summ){
            setBillID(id);
            setBillName(name);
            setBillCurrency(curr);
            setBillSumm(summ);
        }
        public void addMoney(Double addM){
            setBillSumm(getBillSumm()+addM);
        }
        public boolean takeMoney(Double takeM){
            if(getBillSumm()<takeM){
                return false;
            }else{
                setBillSumm(getBillSumm()-takeM);
                return true;
            }
        }
    }//end BillCount
    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    public class CostList extends AppCompatActivity {
        private List<CostCount> listOfCosts=new ArrayList<CostCount>();
        public String findCostname(int id){
            for(int i=0; i<listOfCosts.size();i++){
                if(listOfCosts.get(i).getCostID()==id)
                    return listOfCosts.get(i).getCostName();
            }
            return "";
        }//findbillname
        public void readCost() {
            costList.listOfCosts.clear();
            SQLiteDatabase database=dbHelper.getReadableDatabase();
            Cursor cursor=database.query(DBHelper.TABLE_EXP_TYPE,null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                int idIndex=cursor.getColumnIndex(DBHelper.KEY_EXP_TYPE_ID);
                int nameIndex=cursor.getColumnIndex(DBHelper.KEY_EXP_TYPE_NAME);
                int curIndex=cursor.getColumnIndex(DBHelper.KEY_EXP_TYPE_CURRENCY);
                do{
                    CostCount bill=new CostCount();
                    bill.setCostID(cursor.getInt(idIndex));
                    bill.setCostName(cursor.getString(nameIndex));
                    bill.setCostCurrency(cursor.getInt(curIndex));
                    bill.setCostSumm(0.0);
                    listOfCosts.add(bill);
                }while(cursor.moveToNext());
            }//end if
        }//end readCosts
        public void writeCostsSummToList(){
            for(int i=0;i<listOfCosts.size();i++){
                listOfCosts.get(i).setCostSumm(0.0);
            }
            SQLiteDatabase database=dbHelper.getReadableDatabase();
            //получаем все потраченые средства
            String[] Colum2={DBHelper.KEY_ACT_DATE,DBHelper.KEY_ACT_TYPE,DBHelper.KEY_ACT_TO,DBHelper.KEY_ACT_SUM,DBHelper.KEY_ACT_CURR};
            //тут идет установка, с какого периода на главном экране будут выводится значения трат.
            //если база пустая, то будет выводится все
            //если есть хоть две записи, то они уже будут выводится в зависимости от времени
            //Log.d("DATE",dateToDBFormat());
            String Select=DBHelper.KEY_ACT_TYPE+" = 2" /*+"2 AND "+DBHelper.KEY_ACT_DATE+" = "+thisPeriodStart*/;
            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM "+DBHelper.TABLE_ACTIVITIES, null);
            if (cur != null) {
                cur.moveToFirst();                       // Always one row returned.
                if (cur.getInt(0) == 0)
                    Select=DBHelper.KEY_ACT_TYPE+" = 2";
                else
                    Select=DBHelper.KEY_ACT_TYPE+" = 2 AND "+DBHelper.KEY_ACT_DATE+" > '"+dateToDBFormat()+"'";
            }
            cur.close();
            //тут заканчивается установка даты
            Cursor cursor=database.query(DBHelper.TABLE_ACTIVITIES,Colum2,Select,null,null,null,null);
            if(cursor.moveToFirst()){
                int dateIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_DATE);
                int toIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_TO);
                int sumIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_SUM);
                int curIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_CURR);
                do{
                    //Log.d("DB DATES",cursor.getString(dateIndex));
                    listOfCosts.get(cursor.getInt(toIndex)-1).setCostSumm(listOfCosts.get(cursor.getInt(toIndex)-1).getCostSumm()+cursor.getDouble(sumIndex)*cursor.getDouble(curIndex));
                }while(cursor.moveToNext());
            }//end if
            cursor.close();
        }//end writeCostsSummToList
        public Double getSumOfCostById(int id){
            for(int i=0; i<=listOfCosts.size();i++){
                if(listOfCosts.get(i).getCostID()==id)
                    return listOfCosts.get(i).getCostSumm();
            }
            return 0.0;
        }
        public void addCosttoListAndDB(String name, int curr, Double summ){
            SQLiteDatabase database=dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.KEY_EXP_TYPE_NAME, name);
            contentValues.put(DBHelper.KEY_EXP_TYPE_CURRENCY, curr);
            contentValues.put(DBHelper.KEY_EXP_TYPE_EXPCAT, ownerId);
            //contentValues.put(DBHelper.KEY_BILL_SUMM, 0.0);
            database.insert(DBHelper.TABLE_EXP_TYPE, null, contentValues);
            contentValues.clear();
            Cursor cursor=database.query(DBHelper.TABLE_EXP_TYPE,null,null,null,null,null,null);
            cursor.moveToLast();
            int idIndex=cursor.getColumnIndex(DBHelper.KEY_EXP_TYPE_ID);
            CostCount bc=new CostCount(cursor.getInt(idIndex), name, curr, summ);
            listOfCosts.add(bc);
        }//addbill
        public int findCostid(String name){
            for(int i=0; i<=listOfCosts.size();i++){
                if(listOfCosts.get(i).getCostName().equals(name))
                    return listOfCosts.get(i).getCostID();
            }
            return 0;
        }//findbillid
        public void addToDBnewCost(String name, int expCat, int cur){
            SQLiteDatabase database=dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.KEY_EXP_TYPE_NAME, name);
            contentValues.put(DBHelper.KEY_EXP_TYPE_CURRENCY, cur);
            contentValues.put(DBHelper.KEY_EXP_TYPE_EXPCAT, expCat);
            database.insert(DBHelper.TABLE_EXP_TYPE, null, contentValues);
            contentValues.clear();
            Cursor cursor=database.query(DBHelper.TABLE_EXP_TYPE,null,null,null,null,null,null);
            cursor.moveToLast();
            int idIndex=cursor.getColumnIndex(DBHelper.KEY_EXP_TYPE_ID);
            CostCount bc=new CostCount(cursor.getInt(idIndex), name, cur, 0.0, expCat);
            listOfCosts.add(bc);
        }
        public void showList(){
            writeCostsSummToList();
            Log.d("mLogListOfCosts","СПИСОК РАСХОДОВ");
            for(int i=0;i<listOfCosts.size();i++){
                Log.d("mLogListOfCosts","Cost:     "+listOfCosts.get(i).getCostID()+" "+listOfCosts.get(i).getCostName()+" "+listOfCosts.get(i).getCostCurrency()+" "+listOfCosts.get(i).getCostSumm());
            }
        }//end showList
    }//end CostList
    ////////////////////////////////////////////////////////////////////////
    public class CostCount {
        private int CostID;
        private String CostName;
        private int CostCurrency;
        private Double CostSumm;
        private int CostExpCat;
        public void setCostExpCat(int costExpCat) {
            CostExpCat = costExpCat;
        }
        public int getCostExpCat() {
            return CostExpCat;
        }
        public void setCostID(int billID) {
            CostID = billID;
        }
        public int getCostID() {
            return CostID;
        }
        public String getCostName() {
            return CostName;
        }
        public void setCostName(String billName) {
            CostName = billName;
        }
        public Double getCostSumm() {
            return CostSumm;
        }
        public void setCostSumm(Double billSumm) {
            CostSumm = billSumm;
        }
        public void setCostCurrency(int billCurrency) {
            CostCurrency = billCurrency;
        }
        public int getCostCurrency() {
            return CostCurrency;
        }
        CostCount(){
            setCostName("");
            setCostCurrency(0);
            setCostSumm(0.0);
            setCostID(0);
        }
        CostCount(int id, String name, int curr, Double summ){
            setCostID(id);
            setCostName(name);
            setCostCurrency(curr);
            setCostSumm(summ);
        }
        CostCount(int id, String name, int curr, Double summ, int expcat){
            setCostID(id);
            setCostName(name);
            setCostCurrency(curr);
            setCostSumm(summ);
            setCostExpCat(expcat);
        }
        public void addMoney(Double addM){
            setCostSumm(getCostSumm()+addM);
        }
        public boolean takeMoney(Double takeM){
            if(getCostSumm()<takeM){
                return false;
            }else{
                setCostSumm(getCostSumm()-takeM);
                return true;
            }
        }

    }//end CostCount
    ////////////////////////////////////////////////////////////////////////

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
        clearViewOfCosts();
        clearViewOfBills();
        showViewOfCosts();
        showViewOfBills();
        billList.updateList();
        billList.writeListToDataBase();
        costList.writeCostsSummToList();
    }
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
        billList.writeListToDataBase();
    }

    public String dateToDBFormat(){
        Date todayDate=Calendar.getInstance().getTime();
        Date historyDate=Calendar.getInstance().getTime();
        Date futureDate=Calendar.getInstance().getTime();
        historyDate.setDate(Integer.parseInt(periodStartDay));
        historyDate.setMonth(todayDate.getMonth()-1);
        futureDate.setDate(Integer.parseInt(periodStartDay));
        futureDate.setMonth(todayDate.getMonth());
        Date returnDate=Calendar.getInstance().getTime();
        if(todayDate.after(historyDate) && todayDate.before(futureDate)) {
            //ТУТ оставим существующую дату начала периода
            SimpleDateFormat formDate=new SimpleDateFormat("dd.MM.yyyy");
            thisPeriodStart = formDate.format(historyDate.getTime());
            returnDate=historyDate;
        }else{
            //ТУТ ОБНОВИМ дату начала периода
            SimpleDateFormat formDate=new SimpleDateFormat("dd.MM.yyyy");
            thisPeriodStart = formDate.format(futureDate.getTime());
            returnDate=futureDate;
        }
        SimpleDateFormat formDate=new SimpleDateFormat("yyyyMMdd_hhmmss");
        return formDate.format(returnDate.getTime());
    }   //необходима для перевода даты отсчета в дату понятную базе данных

    public String returnNameOfValueByID(int id){
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        String Select=DBHelper.KEY_CURR_ID+" = "+id;
        Cursor cur=database.rawQuery("SELECT * FROM "+DBHelper.TABLE_CURRENCY+" WHERE "+DBHelper.KEY_CURR_ID+" = '"+id+"'",null);
        cur.moveToFirst();
        String val=cur.getString(1);
        cur.close();
        return val;
    }

    private void temp(){ //читаем значения полей и возвращаем строку
        SQLiteDatabase database=dbHelper.getReadableDatabase();
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
                Log.d("ACTIVITIES", cursor.getInt(idIndex)+" "
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
        //SQLiteDatabase database=dbHelper.getWritableDatabase();
        //вывод в консоль внесенных объектов в таблицу
        cursor = database.query(DBHelper.TABLE_BILLS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_NAME);
            int currIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_CURRENCY);
            int summIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_SUMM);
            do {
                Log.d("BILLS", cursor.getInt(idIndex)+" "
                        +cursor.getInt(nameIndex)+" "
                        +cursor.getDouble(currIndex)+" "
                        +cursor.getDouble(summIndex));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");
        database.close();
        cursor.close();
    }//end temp()    //нужно ТОЛЬКО для проверки внесенных данных в базу
}//end activity_choose
