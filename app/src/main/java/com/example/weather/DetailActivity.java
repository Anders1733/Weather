package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

public class DetailActivity extends AppCompatActivity {

    private TextView result_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        result_info = findViewById(R.id.result_info);
        Intent intentThatStartedThisActivity = getIntent();

        // Если ввели, то формируем ссылку для получения погоды
        String city = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
        String key = "db702a1967174cd73323a8909bb2b19d";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

        if(hasConnection(this)){
            // Запускаем класс для получения погоды
            new GetURLData().execute(url);
        }else{
            Toast toast = Toast.makeText(this, "НЕТУ ИНТЕРНЕТА", Toast.LENGTH_SHORT);
            toast.show();
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.query(DBHelper.TABLE_WEATHER, null, null, null, null, null, null);

            if (cursor.moveToFirst()){
                int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                int timeIndex = cursor.getColumnIndex(DBHelper.KEY_TIME);
                int cityIndex = cursor.getColumnIndex(DBHelper.KEY_CITY);
                int temperatureIndex = cursor.getColumnIndex(DBHelper.KEY_TEMPERATURE);
                int speedIndex = cursor.getColumnIndex(DBHelper.KEY_SPEED);
                int weatherIndex = cursor.getColumnIndex(DBHelper.KEY_WEATHER);
                Integer idValue = 0;
                String timeValue = "";
                String cityValue = "";
                String temperatureValue = "";
                String speedValue = "";
                String weatherValue = "";
                do {

                    idValue = cursor.getInt(idIndex);
                    timeValue = cursor.getString(timeIndex);
                    cityValue = cursor.getString(cityIndex);
                    temperatureValue = cursor.getString(temperatureIndex);
                    speedValue = cursor.getString(speedIndex);
                    weatherValue = cursor.getString(weatherIndex);

                    Log.d("mLog", "ID = " + idValue +
                            ", time = " + timeValue +
                            ", city = " + cityValue +
                            ", temperature = " + temperatureValue +
                            ", speed = " + speedValue +
                            ", weather = " + weatherValue
                    );

                }while ((!city.equals(cityValue))&(cursor.moveToNext()));
                result_info.setText("Время: " + timeValue);
                result_info.append("\nГород: " + cityValue);
                result_info.append("\nТемпература: " + temperatureValue);
                result_info.append("\nСкорость ветра: " + speedValue);
                result_info.append("\nПогода: " + weatherValue);

            } else
                Log.d("mLog", "0 rows");


            cursor.close();
            dbHelper.close();
        }

    }

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    private class GetURLData extends AsyncTask<String, String, String> {



        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line =  "";

                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Закрываем соединения
                if(connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Конвертируем JSON формат и выводим данные в текстовом поле
            try {
                JSONObject jsonObject = new JSONObject(result);
                long unixSeconds = Integer.valueOf(jsonObject.getString("dt")); // секунды
                Integer timeShift = Integer.valueOf(jsonObject.getString("timezone"));
                timeShift = timeShift/60/60;
                Date date = new Date(unixSeconds*1000L); // *1000 получаем миллисекунды
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // какой формат нужен, выбераем
                if (timeShift>=0) {
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT+" + timeShift.toString())); // если нужно даем таймзон
                }else{
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeShift.toString())); // если нужно даем таймзон
                }
                String formattedDate = sdf.format(date);

                DBHelper dbHelper = new DBHelper(DetailActivity.this);
                SQLiteDatabase database = dbHelper.getReadableDatabase();

                Integer idValue = 0;
                String timeValue = formattedDate;
                String cityValue = jsonObject.getString("name");
                String temperatureValue = jsonObject.getJSONObject("main").getString("temp");
                String speedValue = jsonObject.getJSONObject("wind").getString("speed");
                String weatherValue = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
/*
                System.out.println("UPDATE weather" +
                        "SET time = " + "'" + timeValue + "'" +
                        ", city = " + "'" + cityValue + "'" +
                        ", temperature = " + "'" + temperatureValue + "'" +
                        ", speed = " + "'" + speedValue + "'" +
                        ", weather = " + "'" + weatherValue + "'" +
                        " WHERE city = " + "'" + cityValue + "'" + ";");
*/
                database.execSQL("UPDATE weather " +
                        "SET time = " + "'" + timeValue + "'" +
                        ", city = " + "'" + cityValue + "'" +
                        ", temperature = " + "'" + temperatureValue + "'" +
                        ", speed = " + "'" + speedValue + "'" +
                        ", weather = " + "'" + weatherValue + "'" +
                        " WHERE city = " + "'" + cityValue + "'" + ";");

                result_info.setText("Время: " + formattedDate);
                result_info.append("\nГород: " + jsonObject.getString("name"));
                result_info.append("\nТемпература: " + jsonObject.getJSONObject("main").getString("temp"));
                result_info.append("\nСкорость ветра: " + jsonObject.getJSONObject("wind").getString("speed"));
                result_info.append("\nПогода: " + jsonObject.getJSONArray("weather").getJSONObject(0).getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
