package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> list;
    ListView listFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listFood = findViewById(R.id.listFood);

        //danh sách các danh mục
        list = new ArrayList<>();
        list.add("Proteins");
        list.add("Grains and Starches");
        list.add("Vitamins");
        list.add("Nutraceuticals");
        list.add("Fats and Oils");
        list.add("Amino Acids");
        list.add("Fibers and Legumes");
        list.add("Minerals");
        list.add("Processing functional ingredients");
        list.add("Preservatives");


        //Create the array adapter to bind the array to the listView
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listFood.setAdapter(adapter);

        listFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("numFood", i); //truyền dữ liệu qua class mới(key,value)
                startActivity(intent);
            }
        });
    }
}