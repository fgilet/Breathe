package com.example.breathe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // hint text view iat bottom of screen
        TextView tv = findViewById(R.id.check_connection);

        // creating and starting network requests queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.start();

        // list of available cities
        ArrayList<String> cities = new ArrayList<>();

        // requesting the list of available cities
        MyJsonArrayRequest requestListOfCities = new MyJsonArrayRequest
                (Request.Method.GET, "https://api.aircheckr.com/v1.5/territory/BE/names", null, new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        JSONArray array = (JSONArray) response;
                        // iterating through every city in the Json file
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONArray array2 = array.getJSONObject(i).getJSONArray("name");
                                for (int j = 0; j < array2.length(); j++) {
                                    // adding the name of the city to the list
                                    cities.add(array2.get(j).toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // list of cities is complete
                        // launching main activity and sending it the list of cities
                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                        intent.putExtra("cities", cities);
                        startActivity(intent);
                        // closing the root activity and therefore the app on return from main activity
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // in case of error response
                        // setting it clickable
                        // changing hint text
                        tv.setClickable(true);
                        tv.setText(R.string.check_connection);
                        error.printStackTrace();
                    }
                });

        // defining hint text view behavior
        tv.setOnClickListener(view -> {
            // setting it not clickable
            // updating hint text
            // sending the request again
            tv.setClickable(false);
            tv.setText(R.string.loading);
            queue.add(requestListOfCities);
        });

        queue.add(requestListOfCities);
    }
}