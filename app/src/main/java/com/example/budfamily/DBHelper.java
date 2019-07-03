package com.example.budfamily;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    //название базы
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME    ="budgetDB";

    //описание таблицы пользователей
    public static final String TABLE_USERS      ="users";
        //название столбцов в таблице
        public static final String KEY_USER_ID  ="_id";                     //integer   ID      владелец счета или кто провел транзакцию
        public static final String KEY_USER_NAME="name";                    //string
        public static final String KEY_USER_MAIL="mail";                    //string
        public static final String KEY_USER_CURR="currency";                    //integer

    //описание таблицы типов транзакций
    public static final String TABLE_TYPE_ACTIVITY="type_activity";
        //название столбцов в таблице
        public static final String KEY_TACT_ID    ="_id";                   //integer   ID      тип проводимой транзакции. со счета на счет или на рассход
        public static final String KEY_TACT_TYPE  ="type";                  //string

    //описание таблицы счетов и их владельцев
    public static final String TABLE_BILLS          ="bills";
        //название столбцов в таблице
        public static final String KEY_BILL_ID      ="_id";                 //integer   ID
        public static final String KEY_BILL_NAME    ="name";                //string
        public static final String KEY_BILL_CURRENCY="curr";                //integer   ID
        public static final String KEY_BILL_OWNER   ="owner";               //integer   ID
        public static final String KEY_BILL_SUMM    ="summ";                 //double            количетсво средст на счету

    //описание таблицы справочника валют
    public static final String TABLE_CURRENCY   ="currency";
        //название столбцов в таблице
        public static final String KEY_CURR_ID  ="_id";                     //integer   ID
        public static final String KEY_CURR_NAME="name";                    //string

    //описание таблицы категории рассхододв
    public static final String TABLE_EXP_CAT       ="expCategories";
        //название столбцов в таблице
        public static final String KEY_EXP_CAT_ID  ="_id";                  //integer   ID
        public static final String KEY_EXP_CAT_NAME="name";                 //string

    //описание таблицы типов расходов
    public static final String TABLE_EXP_TYPE           ="expType";
        //название столбцов в таблице
        public static final String KEY_EXP_TYPE_ID      ="_id";             //integer   ID
        public static final String KEY_EXP_TYPE_NAME    ="name";            //string
        public static final String KEY_EXP_TYPE_CURRENCY="curr";            //integer   ID
        public static final String KEY_EXP_TYPE_EXPCAT  ="expcat";          //integer   ID

    //главная таблица. результирующая
    public static final String TABLE_ACTIVITIES="activity";
        //название столбцов в таблице
        public static final String KEY_ACT_ID  ="_id";                      //integer ID
        public static final String KEY_ACT_USER="user";                     //integer ID
        public static final String KEY_ACT_DATE="date";                     //date
        public static final String KEY_ACT_TYPE="type";                     //integer ID
        public static final String KEY_ACT_FROM="fromA";                     //integer ID
        public static final String KEY_ACT_TO  ="toA";                       //integer ID
        public static final String KEY_ACT_CURR="currency";                 //integer       -коэфициент транзакции, если разные валюты
        public static final String KEY_ACT_SUM ="amount";                   //double

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=on");
        db.execSQL("create table "+TABLE_USERS+"("+
                KEY_USER_ID+" integer primary key, "+
                KEY_USER_NAME+" text not null, "+
                KEY_USER_MAIL+" text,"+
                KEY_USER_CURR+" integer not null,"+
                "foreign key ("+KEY_USER_CURR+") references "+TABLE_CURRENCY+"("+KEY_CURR_ID+"))");
        db.execSQL("create table "+TABLE_TYPE_ACTIVITY+"("+
                KEY_TACT_ID+" integer primary key, "+
                KEY_TACT_TYPE+" text not null"+")");
        db.execSQL("create table "+TABLE_BILLS+"("+
                KEY_BILL_ID+" integer primary key, "+
                KEY_BILL_NAME+" text not null, "+
                KEY_BILL_CURRENCY+" integer not null,"+
                KEY_BILL_OWNER+" integer not null,"+
                KEY_BILL_SUMM+" real not null,"+
                "foreign key ("+KEY_BILL_CURRENCY+") references "+TABLE_CURRENCY+"("+KEY_CURR_ID+"),"+
                "foreign key ("+KEY_BILL_OWNER+") references "+TABLE_USERS+"("+KEY_USER_ID+"))");
        db.execSQL("create table "+TABLE_CURRENCY+"("+
                KEY_CURR_ID+" integer primary key, "+
                KEY_CURR_NAME+" text"+")");
        db.execSQL("create table "+TABLE_EXP_CAT+"("+
                KEY_EXP_CAT_ID+" integer primary key, "+
                KEY_EXP_CAT_NAME+" text"+")");
        db.execSQL("create table "+TABLE_EXP_TYPE+"("+
                KEY_EXP_TYPE_ID+" integer primary key, "+
                KEY_EXP_TYPE_NAME+" text, "+
                KEY_EXP_TYPE_CURRENCY+" integer,"+
                KEY_EXP_TYPE_EXPCAT+" integer,"+
                "foreign key ("+KEY_EXP_TYPE_CURRENCY+") references "+TABLE_CURRENCY+"("+KEY_CURR_ID+"),"+
                "foreign key ("+KEY_EXP_TYPE_EXPCAT+") references "+TABLE_EXP_CAT+"("+KEY_EXP_CAT_ID+"))");
        db.execSQL("create table "+TABLE_ACTIVITIES+"("+
                KEY_ACT_ID+" integer primary key, "+
                KEY_ACT_USER+" integer not null, "+
                KEY_ACT_DATE+" integer not null, "+
                KEY_ACT_TYPE+" integer not null, "+
                KEY_ACT_FROM+" integer, "+
                KEY_ACT_TO+" integer not null, "+
                KEY_ACT_CURR+" real not null, "+
                KEY_ACT_SUM+" real not null,"+
                "foreign key ("+KEY_ACT_USER+") references "+TABLE_USERS+"("+KEY_USER_ID+"),"+
                "foreign key ("+KEY_ACT_TYPE+") references "+TABLE_TYPE_ACTIVITY+"("+KEY_TACT_ID+"),"+
                "foreign key ("+KEY_ACT_TO+") references "+TABLE_EXP_TYPE+"("+KEY_EXP_TYPE_ID+"),"+
                "foreign key ("+KEY_ACT_FROM+") references "+TABLE_BILLS+"("+KEY_BILL_ID+"),"+
                "foreign key ("+KEY_ACT_TO+") references "+TABLE_EXP_TYPE+"("+KEY_EXP_TYPE_ID+"))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_CURRENCY);
        onCreate(db);
    }


}
