package com.example.breathe;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MyListAdapter extends BaseAdapter {

    private String[] cities;
    private LayoutInflater layoutInflater;
    private Context context;

    public MyListAdapter(Context context, String[] cities) {
        this.context = context;
        this.cities = cities;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cities.length;
    }

    @Override
    public Object getItem(int i) {
        return cities[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null) {
            view = layoutInflater.inflate(R.layout.favorite_item, null);
            holder = new ViewHolder();
            holder.city = view.findViewById(R.id.favorite_city);
            holder.quality = view.findViewById(R.id.favorite_quality);
            holder.star = view.findViewById(R.id.favorite_star);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String city = (String) this.getItem(i);
        holder.city.setText(city);

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.start();

        String url = "https://api.aircheckr.com/v1.5/territory/BE/LAU2/name/" + city;

        View finalView = view;
        MyJsonObjectRequest request = new MyJsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) { // on successful response
                        try {
                            // updating screen components with the information received
                            holder.quality.setText(response.getJSONArray("data").getJSONObject(0).getJSONObject("aqi_11").getString("name"));
                            // retrieving color code and updating the colored frame with it
                            String color = response.getJSONArray("data").getJSONObject(0).getJSONObject("aqi_11").getString("color_hex");
                            //finalView.setBackgroundColor(Color.parseColor(color));
                            Drawable background = finalView.getBackground();
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

        holder.star.setOnClickListener(view1 -> {
            DataAccessObject DAO = DataAccessObject.getInstance(context);
            DAO.open();
            if(DAO.isFavorite(city)) {
                holder.star.setImageResource(R.mipmap.star_empty);
                DAO.deleteFavorite(city);
            } else {
                holder.star.setImageResource(R.mipmap.star_filled);
                DAO.addFavorite(city);
            }
        });

        return view;
    }

    static class ViewHolder {
        TextView city;
        TextView quality;
        ImageView star;
    }
}
