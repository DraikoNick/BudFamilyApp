package com.example.budfamily;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import static com.example.budfamily.DBHelper.DATABASE_NAME;

public class activity_first_start extends AppCompatActivity {

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

    //имеющиеся валюты
    String[] currencies = {"BYN", "RUB", "USD", "EUR", "UAH"};
    //типы расходов по умолчанию
    String[] expType = {"ЖКХ","Связь","Продукты","Услуги","Дом","Транспорт","Досуг","Обучение","Здоровье","Подарки","Работа","Одежда","Банки","Другое"};
    String[][] expCateg={
            {"Электричество","Вода","Отопление","КвартПлата","Домофон","Газ","Гараж"},
            {"Интернет","Сотовый"},
            {"Питание","ФастФуд","Моющие","Гигиена","Животные","Сигареты"},
            {"Сантехник","Электрик","Мебельщик","Доставка","Парихмахер","ГосУслуги","Косметология"},
            {"Мебель","Техника","Электрика","Интерьер","Животные","Медиа","Стройматериалы"},
            {"Запчасти","ГосТО","Страховка","СТО","ТО","Такси","Общ.Транспорт","Топливо","Авто","Налог","Мойка","Инструмент"},
            {"Кафе","Кино","Путешествие","Хобби"},
            {"Обучение","Курсы","Канцелярия","Литература"},
            {"Аптечка","Обследование","Анализы","Лечение"},
            {"Родителям","Знакомым","Друзьям"},
            {"Корпоратив","Командировка","Помощь","Указание"},
            {"Верхняя","Нижняя","Обувь","Аксесуары","Косметика","Рабочая","Спортивная","Официальная"},
            {"Кредит","Рассрочка","Долг"},
            {"Где деньги Лебовски?"},
    };  //end expCateg[][]
    String[] bills={"Наличные","Зарплатная карточка","Дебетовая карта"};
    String[] types={"Поступление средств","Перевод со счета на счет","Трата средств"};
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //при запуске создает активность и базу
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start);
        initSpinner();
        dbHelper=new DBHelper(this);
        deleteDatabase(DATABASE_NAME);
        appSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSettings.edit();
        appOpenCounter=0;
        periodStartDay="01";
        thisPeriodStart="01.01.2019";
        infoCode=0;
        ownerId=1;
        editor.putInt(APP_PREFERENCES_APP_OPEN_COUNTER, appOpenCounter);
        editor.putString(APP_PREFERENCES_PERIOD_START, periodStartDay);
        editor.putString(APP_PREFERENCES_PERIOD_THIS, thisPeriodStart);
        editor.putInt(APP_PREFERENCES_SHOW_INFO_CODE, infoCode);
        editor.putInt(APP_PREFERENCES_OWNER, ownerId);
        editor.apply();
    }//end onCreate(Bundle savedInstanceState)

    private void initSpinner(){ //подключение и настройка выпадающего списка
        // создаем адаптер выпадающего списка валюты
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencies);
        //указываем как будет выглядеть выпадающий список
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner1 = (Spinner) findViewById(R.id.spinValue1);
        spinner1.setAdapter(adapter);
        // заголовок выпадающего списка
        //spinner1.setPrompt("Выберите валюту");
        // выделяем элемент по умолчанию
        spinner1.setSelection(0);
        // устанавливаем обработчик нажатия, нужно для тестирования
        //spinnerListener();
    }//end initSpinner()

    public void onButtonClick (View v){ //по нажатии на кнопку читаем значения полей и передаем далее, переключаем активити
        v.setClickable(false);      //запрет на нажатие кнопки. МАЛО ЛИ ЧО
        setCurrency();  //создали справочник валют
        setUserData();  //записали информацию о пользователе. НЕТ АКТИВНОСТИ ДЛЯ ВВОДА ДАННЫХ ПОЛЬЗОВАТЕЛЯ. НЕОБХОДИМ ДОБАВИТЬ
        Intent intent=new Intent(activity_first_start.this,activity_choose.class);
        startActivity(intent);
        finish();
    }//end onButtonClick (View v)

    public void tempp(){
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        //вывод в консоль внесенных объектов в таблицу
        Cursor cursor = database.query(DBHelper.TABLE_BILLS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_OWNER);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_ID);
            int qIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_NAME);
            do {
                Log.d("mLog", "ID = " + cursor.getInt(idIndex) +
                        ", name = " + cursor.getString(qIndex)+", name = " + cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");
        cursor.close();

        Cursor cursor2 = database.query(DBHelper.TABLE_USERS, null, null, null, null, null, null);
        if (cursor2.moveToFirst()) {
            int idIndex = cursor2.getColumnIndex(DBHelper.KEY_USER_ID);
            int nameIndex = cursor2.getColumnIndex(DBHelper.KEY_USER_NAME);
            int curIndex = cursor2.getColumnIndex(DBHelper.KEY_USER_CURR);
           //int expcatIndex = cursor2.getColumnIndex(DBHelper.KEY_EXP_TYPE_EXPCAT);
            do {
                Log.d("mLog", "ID = " + cursor2.getInt(idIndex) +", name = " + cursor2.getString(nameIndex)+", cur = "
                        + cursor2.getInt(curIndex));
            } while (cursor2.moveToNext());
        } else
            Log.d("mLog","0 rows");
        cursor2.close();
    }//end temp()    //нужно ТОЛЬКО для проверки внесенных данных в базу

    private void setUserData(){     //записываем в базу пользователей информацию о пользователе
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //сравнение выбранной валюты и имеющейся в базе. и внесение ИД этой валюты в БД
        Spinner spinner1 = (Spinner) findViewById(R.id.spinValue1);
        Cursor cursor = database.query(DBHelper.TABLE_CURRENCY, null, null, null, null, null, null);
        int curr=0;
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_CURR_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_CURR_NAME);
            do {
                if(cursor.getString(nameIndex).equals(spinner1.getSelectedItem().toString())) {
                    curr=cursor.getInt(idIndex);
                    break;
                }
            } while (cursor.moveToNext());
        }//end if
        cursor.close();
        contentValues.put(DBHelper.KEY_USER_NAME,"USER");
        contentValues.put(DBHelper.KEY_USER_MAIL,"USER@test.com");
        contentValues.put(DBHelper.KEY_USER_CURR, curr);
        database.insert(DBHelper.TABLE_USERS, null, contentValues);
        setCategAndTypesOfExpenses(curr);   //передаем валюту по умолчанию в базу и заполняем базу стандартными типами расходов
        setBills(curr,0);       //id user = 0   -это будет первый пользователь в базе. соответственно и id =0 записи
        setTypeOfActivity();
    }// end setUserData()

    private void setCurrency(){              //заполняет базу валютами
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //внесение в базу валют
        for(int i=0; i<currencies.length;i++){
            contentValues.put(DBHelper.KEY_CURR_NAME, currencies[i]);
            database.insert(DBHelper.TABLE_CURRENCY, null, contentValues);
        }//end for
    }//end setCurrency()

    private void setCategAndTypesOfExpenses(int cur){          //заполняет базу типами расходов, а также валютой по умолчанию
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int [] idsOfTypes = new int[30];
        for(int i=0;i<expType.length;i++){
            //этот фор пройдет и установит данные типов расходов в базу
            contentValues.put(DBHelper.KEY_EXP_CAT_NAME, expType[i]);
            database.insert(DBHelper.TABLE_EXP_CAT, null, contentValues);
            contentValues.clear();
            //узнаем ай ди последней вставленной записи. НУЖНО ЛИ?
            Cursor cursorT = database.query(DBHelper.TABLE_EXP_CAT, null, null, null, null, null, null);
            if(cursorT.moveToLast()){
                int currentID=cursorT.getInt(cursorT.getColumnIndex(DBHelper.KEY_EXP_CAT_ID));
                idsOfTypes[i]=currentID;
            }//end if cursor move to last
            cursorT.close();
                for(int j=0;j<expCateg[i].length;j++){
                    //этот фор поможет записать сами расходы в другую таблицу и привязать их к ай ди типов категорий расходов
                    contentValues.put(DBHelper.KEY_EXP_TYPE_NAME, expCateg[i][j]);
                    contentValues.put(DBHelper.KEY_EXP_TYPE_CURRENCY, cur);
                    contentValues.put(DBHelper.KEY_EXP_TYPE_EXPCAT, idsOfTypes[i]);
                    database.insert(DBHelper.TABLE_EXP_TYPE, null, contentValues);
                    contentValues.clear();
                }//end for j<expcateg
        }//end for i<expType
    }   //end setCategAndTypesOfExpenses(int cur)

    private void setBills(int cur, int id){
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for(int i=0;i<bills.length;i++){
            //этот фор пройдет и установит данные типов расходов в базу
            contentValues.put(DBHelper.KEY_BILL_NAME, bills[i]);
            contentValues.put(DBHelper.KEY_BILL_CURRENCY, cur);
            contentValues.put(DBHelper.KEY_BILL_OWNER, ownerId);
            contentValues.put(DBHelper.KEY_BILL_SUMM, 0.0);
            database.insert(DBHelper.TABLE_BILLS, null, contentValues);
            contentValues.clear();
        }//end for i<bills.length
    }//end setBills()

    private void setTypeOfActivity(){
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for(int i=0;i<types.length;i++){
            //этот фор пройдет и установит данные типов расходов в базу
            contentValues.put(DBHelper.KEY_TACT_TYPE, types[i]);
            database.insert(DBHelper.TABLE_TYPE_ACTIVITY, null, contentValues);
            contentValues.clear();
        }//end for i<bills.length
    }//end setTypeOfActivity()

}//end activity_firstStart


