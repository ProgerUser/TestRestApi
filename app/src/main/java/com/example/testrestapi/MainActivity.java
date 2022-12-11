package com.example.testrestapi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String myUrl = "http://10.0.2.2:8080/api/single_read.php/?id=%D1%83%D0%B3%D0%BE%D0%BB";
    TextView resultsTextView;
    ProgressDialog progressDialog;
    Button displayData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultsTextView = (TextView) findViewById(R.id.results);
        displayData = (Button) findViewById(R.id.displayData);

        // implement setOnClickListener event on displayData button
        displayData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create object of MyAsyncTasks class and execute it
                MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                myAsyncTasks.execute();
            }
        });
    }

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
                resultsTextView.setText(my_users);

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
            progressDialog.setMessage("processing results");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }
}

