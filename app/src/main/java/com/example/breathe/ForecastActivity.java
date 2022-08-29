package com.example.breathe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ForecastActivity extends AppCompatActivity {

    private RequestQueue queue;

    View[] V = new View[4];
    TextView[] D = new TextView[4];
    TextView[] Q = new TextView[4];
    TextView[] A = new TextView[4];
    TextView city_tv;

    Calendar c = Calendar.getInstance();
    String[] dates = new String[4];
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        city = getIntent().getExtras().getString("city");

        city_tv = findViewById(R.id.forecast_city);
        city_tv.setText(getString(R.string.forecast_city, city));

        queue = Volley.newRequestQueue(this);
        queue.start();

        V[0] = findViewById(R.id.V0);
        V[1] = findViewById(R.id.V1);
        V[2] = findViewById(R.id.V2);
        V[3] = findViewById(R.id.V3);

        for (int i = 0; i < 4; i++) {
            D[i] = V[i].findViewById(R.id.date);
            Q[i] = V[i].findViewById(R.id.quality);
            A[i] = V[i].findViewById(R.id.advice);
        }

        String year;
        String month;
        String day;

        for (int i = 0; i < 4; i++) {
            year = String.valueOf(c.get(Calendar.YEAR));
            month = String.valueOf(c.get(Calendar.MONTH) + 1);
            day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));

            if (Integer.parseInt(month) < 10) {
                month = "0" + month;
            }

            if (Integer.parseInt(day) < 10) {
                day = "0" + day;
            }

            dates[i] = year + "-" + month + "-" + day;
            if (i > 1) {
                String s = day + "-" + month + "-" + year;
                D[i].setText(s);
                D[i].setPaintFlags(D[i].getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                D[i].setTypeface(null, Typeface.BOLD);
            }
            getPrediction(i);
            c.add(Calendar.DATE, 1);
        }

        D[0].setText(R.string.today);
        D[1].setText(R.string.tomorrow);
    }

    private void getPrediction(int offset) {
        // building the url for the request
        String url = "https://api.aircheckr.com/v1.5/territory/BE/LAU2/name/" + city + "?date=" + dates[offset];

        // creating the request
        MyJsonObjectRequest request = new MyJsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) { // on successful response
                        try {
                            Q[offset].setText(getString(R.string.quality ,response.getJSONArray("data").getJSONObject(0).getJSONObject("aqi_11").getString("name")));
                            A[offset].setText(getString(R.string.advice ,response.getJSONArray("data").getJSONObject(0).getJSONObject("recommend_non_sensitive").getString("general")));
                            String color = response.getJSONArray("data").getJSONObject(0).getJSONObject("aqi_11").getString("color_hex");
                            Drawable background = V[offset].getBackground();
                            if (background instanceof ShapeDrawable) {
                                ((ShapeDrawable)background).getPaint().setColor(Color.parseColor(color));
                            } else if (background instanceof GradientDrawable) {
                                ((GradientDrawable)background).setColor(Color.parseColor(color));
                            } else if (background instanceof ColorDrawable) {
                                ((ColorDrawable)background).setColor(Color.parseColor(color));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) { // on network error
                        error.printStackTrace();
                    }
                });

        // sending the request
        queue.add(request);
    }

}