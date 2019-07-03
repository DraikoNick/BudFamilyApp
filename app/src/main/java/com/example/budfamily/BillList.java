package com.example.budfamily;
import com.example.budfamily.BIllCount;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import static com.example.budfamily.DBHelper.DATABASE_NAME;

public class BillList extends AppCompatActivity {
    private DBHelper dbHelper=new DBHelper(this);
    private List<BIllCount> listOfBills=new ArrayList<BIllCount>();

    public void addBill(int id, String name, int curr, Double summ){
        BIllCount bc=new BIllCount(id, name, curr, summ);
        listOfBills.add(bc);
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

    public void makeChange(BIllCount bc1,BIllCount bc2, Double sum, Double change){
        bc1.takeMoney(sum*change);
        bc2.addMoney(sum);
    }//make change

    public void readBills() {

        SQLiteDatabase database=dbHelper.getReadableDatabase();
        Cursor cursor=database.query(DBHelper.TABLE_BILLS,null,null,null,null,null,"_id DESC");
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
    }

    public void writeBillsSummToList(){
        dbHelper=new DBHelper(this);
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
                listOfBills.get(cursor.getInt(toIndex)).setBillSumm(cursor.getDouble(sumIndex)*cursor.getDouble(curIndex));    //вписываем в лист в счет сумму поступивших средств
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
                listOfBills.get(cursor.getInt(toIndex)).setBillSumm(cursor.getDouble(sumIndex)*cursor.getDouble(curIndex));    //вписываем в лист в счет сумму поступивших средств
                makeChange(listOfBills.get(cursor.getInt(fromIndex)),listOfBills.get(cursor.getInt(toIndex)),cursor.getDouble(sumIndex),cursor.getDouble(curIndex));
            }while(cursor.moveToNext());
        }//end if
        //получаем все потраченые средства
        String[] Colum2={DBHelper.KEY_ACT_TYPE,DBHelper.KEY_ACT_FROM,DBHelper.KEY_ACT_SUM};
        Select=DBHelper.KEY_ACT_TYPE+" = 2";
        cursor=database.query(DBHelper.TABLE_ACTIVITIES,Colum2,Select,null,DBHelper.KEY_ACT_TO,null,null);
        if(cursor.moveToFirst()){
            int fromIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_FROM);
            int sumIndex=cursor.getColumnIndex(DBHelper.KEY_ACT_SUM);
            do{
                listOfBills.get(cursor.getInt(fromIndex)).takeMoney(cursor.getDouble(sumIndex));
            }while(cursor.moveToNext());
        }//end if
        cursor.close();
        for(int i=0; i<listOfBills.size();i++){
            Log.d("mLog",listOfBills.get(i).getBillName()+" "+listOfBills.get(i).getBillSumm());
        }
    }

}
