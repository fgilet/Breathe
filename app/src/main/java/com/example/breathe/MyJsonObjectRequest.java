package com.example.breathe;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class MyJsonObjectRequest extends JsonObjectRequest {

    public MyJsonObjectRequest(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        HashMap<String, String> customHeaders = new HashMap<>();

        customHeaders.put("x-access-token", "eyJhbGciOiJIUzI1NiJ9.NGZhNDdjMDAtMTMyOS0xMWVkLWJhN2QtNzc2ZjVmNWM0ZWM5.xxjmSKjK9NaAydrYtsQ7prhl5_vidJW3jsQ0JnejbEY");

        return customHeaders;
    }
}
