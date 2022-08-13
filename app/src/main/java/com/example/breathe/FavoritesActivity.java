package com.example.breathe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.Arrays;

public class FavoritesActivity extends AppCompatActivity {

    ListView listView;
    DataAccessObject DAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        listView = findViewById(R.id.list_view);
        DAO = DataAccessObject.getInstance(getApplicationContext());

        listView = findViewById(R.id.list_view);
        DAO.open();
        String[] cities = DAO.getFavorites();
        listView.setAdapter(new MyListAdapter(this, cities));

        DAO.close();
    }
}