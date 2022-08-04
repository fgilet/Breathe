package com.example.breathe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;
    AutoCompleteTextView field;
    Button button;
    TextView city;
    TextView quality;
    TextView advice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);
        queue.start();

        AutoCompleteTextView field = findViewById(R.id.field);
        button = findViewById(R.id.button);
        city = findViewById(R.id.city);
        quality = findViewById(R.id.quality);
        advice = findViewById(R.id.advice);

        ArrayList<String> cities = (ArrayList<String>) getIntent().getSerializableExtra("cities");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        field.setAdapter(adapter);

        button.setOnClickListener(view -> {
            String c = field.getText().toString();
            if(cities.contains(c)) {
                getQuality(c);
            } else {
                city.setText("");
                quality.setText("");
                advice.setText("");
                Toast.makeText(MainActivity.this,"Please select a city from the drop down list.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getQuality(String c) {

        String url = "https://api.aircheckr.com/v1.5/territory/BE/LAU2/name/" + c;
        System.out.println(url);

        MyJsonObjectRequest request = new MyJsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            city.setText("City : " + c);
                            quality.setText("Quality : " + response.getJSONArray("data").getJSONObject(0).getJSONObject("aqi_11").getString("name"));
                            String color = response.getJSONArray("data").getJSONObject(0).getJSONObject("aqi_11").getString("color_hex");
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(MainActivity.this.getBaseContext(), R.drawable.square);
                            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(color));
                            advice.setText("Advice : " + response.getJSONArray("data").getJSONObject(0).getJSONObject("recommend_non_sensitive").getString("general"));
                            View view = MainActivity.this.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        city.setText("");
                        quality.setText("");
                        advice.setText("");
                        Toast.makeText(MainActivity.this,"Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });

        queue.add(request);
    }
}