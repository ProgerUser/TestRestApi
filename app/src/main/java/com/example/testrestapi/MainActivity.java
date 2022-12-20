package com.example.testrestapi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    /**
     * Ссылка на ресурс
     */
    String myUrl = "";
    /**
     * Просмотр
     */
    TextView resultsTextView;
    /**
     * Прогресс бар
     */
    ProgressDialog progressDialog;
    /**
     * Кнопка поиска
     */
    Button displayData;
    /**
     * Текстовое поле
     */
    EditText search;

    /**
     * При создании
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultsTextView = (TextView) findViewById(R.id.results);
        displayData = (Button) findViewById(R.id.displayData);
        search = (EditText) findViewById(R.id.SearchTxt);

        // implement setOnClickListener event on displayData button
        displayData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search.getText() != null && !search.getText().equals("")) {
                    myUrl = "http://10.0.2.2:8080/api/single_read.php/?id=" + search.getText();
                    // create object of MyAsyncTasks class and execute it
                    MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                    myAsyncTasks.execute();
                }
            }
        });
    }

    /**
     * Асинхронная задача
     * ...
     */
    public class MyAsyncTasks extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String s) {
            // dismiss the progress dialog after receiving data from API
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                //JSONArray jsonArray1 = jsonObject.getJSONArray("id");

                int index_no = 0;
                JSONObject jsonObject1 = jsonArray.getJSONObject(index_no);
                String word = jsonObject1.getString("word");
                String translate = jsonObject1.getString("translate");
                String my_users = "Слово: " + word + "\n" + "Перевод: " + translate;

                //Show the Textview after fetching data
                resultsTextView.setVisibility(View.VISIBLE);

                //Display data with the Textview
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    resultsTextView.setText(Html.fromHtml(my_users, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    resultsTextView.setText(Html.fromHtml(my_users));
                }
                //resultsTextView.setText(my_users);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            // Fetch data from the API in the background.

            String result = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(myUrl);
                    //open a URL coonnection

                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);
                    int data = isw.read();
                    while (data != -1) {
                        result += (char) data;
                        data = isw.read();

                    }
                    // return the data to onPostExecute method
                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Поиск");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }
}

