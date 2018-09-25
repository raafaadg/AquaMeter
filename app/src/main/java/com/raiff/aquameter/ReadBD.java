package com.raiff.aquameter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.raiff.aquameter.adapter.MyArrayAdapter2;
import com.raiff.aquameter.model.MyDataModel2;


import java.util.ArrayList;

public class ReadBD extends AppCompatActivity {

    private ListView listView;
    private ArrayList<MyDataModel2> list;
    private MyArrayAdapter2 adapter;
    public final static String MESSAGE_KEY = "com.raiff.aquameter.message_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read2);
        Fresco.initialize(this);

        list = new ArrayList<>();

        adapter = new MyArrayAdapter2(this, list);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ReadBD.this,
                        "SEGUROU o item " + String.valueOf(position),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(findViewById(R.id.parentLayout),
                        list.get(position).getId() + " => " +
                                list.get(position).getTime() + " => " +
                        list.get(position).getData() + " => "
                        , Snackbar.LENGTH_LONG).show();
                startActivity(new Intent(ReadBD.this, GraficoActivity.class)
                        .putExtra(MESSAGE_KEY,list.get(position).getData()
                        ));
            }
        });

        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        ArrayList<String> dados = dbHandler.loadHandler();
        for(String aux : dados){
            Log.e("aux", aux);
            MyDataModel2 model = new MyDataModel2();
            String buffer = "";
            int controle = 0;
            for(char res : aux.toCharArray()) {
                if(res != ';')
                    buffer += res;
                else{
                    switch (controle) {
                        case 0:
                            model.setId(buffer);
                            buffer = "";
                            controle++;
                            break;
                        case 1:
                            model.setTime(buffer);
                            buffer = "";
                            controle++;
                            break;
                        case 2:
                            model.setData(buffer);
                            buffer = "";
                            controle = 0;
                            list.add(model);
                            break;
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

}