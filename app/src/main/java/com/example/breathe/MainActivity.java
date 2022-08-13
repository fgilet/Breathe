package com.example.breathe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // instantiating network requests queue and visual objects
    private RequestQueue queue;
    AutoCompleteTextView field;
    Button search;
    Button favorites;
    TextView city;
    TextView quality;
    TextView advice;
    ImageView star;
    View shape;
    DataAccessObject DAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // creating and starting network requests queue
        queue = Volley.newRequestQueue(this);
        queue.start();

        DAO = DataAccessObject.getInstance(getApplicationContext());

        // linking java visual objects to XML components
        field = findViewById(R.id.field);
        star = findViewById(R.id.star_button);
        search = findViewById(R.id.button_search);
        favorites = findViewById(R.id.button_favorites);
        city = findViewById(R.id.city);
        quality = findViewById(R.id.quality);
        advice = findViewById(R.id.advice);
        shape = findViewById(R.id.quality_shape);

        // retrieving list of cities from loading activity
        ArrayList<String> cities = (ArrayList<String>) getIntent().getSerializableExtra("cities");

        // setting content of the drop down list for autocomplete
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        field.setAdapter(adapter);

        // making the star icon go empty when the user changes the input
        // this way the star is only filled after the user clicked on the search button for a favorite city
        // the star goes back to empty when the user stars deleting the name of the city, hinting that clicking it will not do anything until another city is selected from the drop down list
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                star.setImageResource(R.mipmap.star_empty);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // setting search button behavior
        search.setOnClickListener(view -> {
            // retrieving selected city
            String c = field.getText().toString();
            // if the name is in the list of available cities
            if(cities.contains(c)) {
                // retrieve information and update screen
                getQuality(c);
                // update star icon
                DAO.open();
                if(DAO.isFavorite(c)) {
                    star.setImageResource(R.mipmap.star_filled);
                } else {
                    star.setImageResource(R.mipmap.star_empty);
                }
                DAO.close();
            } else { // if the name is not in the list
                // clearing info from potential previous search
                city.setText("");
                quality.setText("");
                advice.setText("");
                shape.setVisibility(View.INVISIBLE);
                DAO.open();
                star.setImageResource(R.mipmap.star_empty);
                DAO.close();
                // showing toast message to warn user
                Toast.makeText(MainActivity.this,R.string.toast_dropdown, Toast.LENGTH_LONG).show();
            }
        });

        star.setOnClickListener(view -> {
            // retrieving selected city
            String c = field.getText().toString();
            // if the name is in the list of available cities
            if(cities.contains(c)) {
                DAO.open();
                if(DAO.isFavorite(c)) {
                    DAO.deleteFavorite(c);
                    star.setImageResource(R.mipmap.star_empty);
                } else {
                    DAO.addFavorite(c);
                    star.setImageResource(R.mipmap.star_filled);
                }
                System.out.println(Arrays.toString(DAO.getFavorites()));
                DAO.close();
            }
        });

        // setting favorites button behavior
        favorites.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
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
                            shape.setVisibility(View.VISIBLE);
                            setShapeColor(color);
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
                        shape.setVisibility(View.INVISIBLE);
                        // showing toast message to warn user of the network issue
                        Toast.makeText(MainActivity.this,R.string.toast_connection, Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });

        // sending the request
        queue.add(request);
    }

    private void setShapeColor(String color) {
        System.out.println("setting color to : " + color);
        System.out.println(shape.getVisibility());
        Drawable background = shape.getBackground();
        System.out.println(background.getClass());
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable)background).getPaint().setColor(Color.parseColor(color));
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable)background).setColor(Color.parseColor(color));
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable)background).setColor(Color.parseColor(color));
        }
    }
}