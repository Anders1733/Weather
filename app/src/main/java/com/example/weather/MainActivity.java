package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import java.sql.SQLData;

public class MainActivity extends AppCompatActivity {

    private RecyclerView numbersList;
    private NumbersAdapter numbersAdapter;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numbersList = findViewById(R.id.rv_numbers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        numbersList.setLayoutManager(layoutManager);

        numbersList.setHasFixedSize(true);

        String[] cities = { "Белгород", "Псков", "Краснодар",
                "Хабаровск", "Норильск", "Москва", "Санкт-Петербург",
                "Тбилиси", "Воронеж", "Курск", "Лондон", "Берлин",
                "Париж", "Красноярск", "Киев", "Харьков", "Нью-Йорк",
                "Анапа", "Сочи" };

        dbHelper = new DBHelper(this);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //database.execSQL("SELECT COUNT(*) FROM Weather;");
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM Weather;", null);
        int row_count=0;

        if (cursor.moveToFirst()) {
            do {
                row_count =  cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        if (row_count!=cities.length) {
            for(int i = 0; i<cities.length; i++){
                //System.out.println("INSERT INTO Weather (_id, city)\n" +
                //        "VALUES (" + Integer.toString(i) + ", '"+ cities[i] + "');");
                database.execSQL("INSERT INTO Weather (_id, city)\n" +
                        "VALUES (" + Integer.toString(i) + ", '" + cities[i] + "');");
            }
        }

        ContentValues contentValues = new ContentValues();

        numbersAdapter = new NumbersAdapter(cities, this);
        numbersList.setAdapter(numbersAdapter);
    }
}