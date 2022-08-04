package com.example.breathe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);
        queue.start();

        EditText field = findViewById(R.id.edit_text);
        Button button = findViewById(R.id.button);
        TextView city = findViewById(R.id.city);
        TextView quality = findViewById(R.id.quality);
        TextView advice = findViewById(R.id.advice);

        final JSONObject[] resp = new JSONObject[1];

        MyJsonObjectRequest request = new MyJsonObjectRequest
                (Request.Method.GET, "https://api.aircheckr.com/territory/BE/LAU2/names", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("RESPONSE RECEIVED !!!");
                        resp[0] = response;
                        System.out.println(response.names().toString());
                        try {
                            System.out.println(response.get("success"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        queue.add(request);

    }

    /*
    private String[] getListOfCities() {

        final String[][] listOfCities = new String[1][1];

        System.out.println("here2");

        MyJsonObjectRequest request = new MyJsonObjectRequest
                (Request.Method.GET, "https://api.aircheckr.com/territory/countries", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("here3");
                        try {
                            listOfCities[0] = (String[]) response.get("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        queue.add(request);

        return listOfCities[0];
    }*/
}