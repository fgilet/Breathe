package com.example.breathe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
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

    // instantiating network requests queue and visual objects
    private RequestQueue queue;
    AutoCompleteTextView field;
    Button button;
    TextView city;
    TextView quality;
    TextView advice;
    View margins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // creating and starting network requests queue
        queue = Volley.newRequestQueue(this);
        queue.start();

        // linking java visual objects to XML components
        field = findViewById(R.id.field);
        button = findViewById(R.id.button);
        city = findViewById(R.id.city);
        quality = findViewById(R.id.quality);
        advice = findViewById(R.id.advice);
        margins = findViewById(R.id.margins);

        // retrieving list of cities from loading activity
        ArrayList<String> cities = (ArrayList<String>) getIntent().getSerializableExtra("cities");

        // setting content of the drop down list for autocomplete
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        field.setAdapter(adapter);

        //setting search button behavior
        button.setOnClickListener(view -> {
            // retrieving selected city
            String c = field.getText().toString();
            // if the name is in the list of available cities
            if(cities.contains(c)) {
                // retrieve information and update screen
                getQuality(c);
            } else { // if the name is not in the list
                // clearing info from potential previous search
                city.setText("");
                quality.setText("");
                advice.setText("");
                margins.setBackgroundColor(getResources().getColor(R.color.theme));
                // showing toast message to warn user
                Toast.makeText(MainActivity.this,R.string.toast_dropdown, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getQuality(String c) {
        /**
         * This method receives the name of a city supposedly available in the database with the spelling provided.
         * It requests data about the air quality and updates the screen accordingly.         *
         */

        // building the url for the request
        String url = "https://api.aircheckr.com/v1.5/territory/BE/LAU2/name/" + c;

        // creating the request
        MyJsonObjectRequest request = new MyJsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) { // on successful response
                        try {
                            // updating screen components with the information received
                            city.setText(getString(R.string.city, c));
                            quality.setText(getString(R.string.quality, response.getJSONArray("data").getJSONObject(0).getJSONObject("aqi_11").getString("name")));
                            advice.setText(getString(R.string.advice, response.getJSONArray("data").getJSONObject(0).getJSONObject("recommend_non_sensitive").getString("general")));
                            // retrieving color code and updating the colored frame with it
                            String color = response.getJSONArray("data").getJSONObject(0).getJSONObject("aqi_11").getString("color_hex");
                            margins.setBackgroundColor(Color.parseColor(color));
                            // closing the keyboard to enhance readability of results
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
                    public void onErrorResponse(VolleyError error) { // on network error
                        // clearing info from potential previous search
                        city.setText("");
                        quality.setText("");
                        advice.setText("");
                        margins.setBackgroundColor(getResources().getColor(R.color.theme));
                        // showing toast message to warn user of the network issue
                        Toast.makeText(MainActivity.this,R.string.toast_connection, Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });

        // sending the request
        queue.add(request);
    }
}