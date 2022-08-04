package com.example.breathe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

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

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.start();

        ArrayList<String> cities = new ArrayList<>();

        MyJsonArrayRequest requestListOfCities = new MyJsonArrayRequest
                (Request.Method.GET, "https://api.aircheckr.com/v1.5/territory/BE/names", null, new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        JSONArray array = (JSONArray) response;
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONArray array2 = array.getJSONObject(i).getJSONArray("name");
                                for (int j = 0; j < array2.length(); j++) {
                                    cities.add(array2.get(j).toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                        intent.putExtra("cities", cities);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("ERROR RESPONSE !!!");
                        error.printStackTrace();
                    }
                });

        queue.add(requestListOfCities);
    }
}