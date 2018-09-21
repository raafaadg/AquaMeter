package com.raiff.aquameter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.raiff.aquameter.adapter.MyArrayAdapter;
import com.raiff.aquameter.model.MyDataModel;
import com.raiff.aquameter.util.InternetConnection;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReadloopActivity extends AppCompatActivity {

    Handler UIHandler;
    Thread thread = null;

    private ListView listView;
    private ArrayList<MyDataModel> list;
    private MyArrayAdapter adapter;

    public static final int SERVERPORT = 80;
    public static final String SERVERIP = "192.168.1.4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Fresco.initialize(this);

        list = new ArrayList<>();

        adapter = new MyArrayAdapter(this, list);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ReadloopActivity.this,
                        "SEGUROU o item " + String.valueOf(position),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(findViewById(R.id.parentLayout),
                        list.get(position).getTitle() + " => " +
                                list.get(position).getData() + " => "
                        , Snackbar.LENGTH_LONG).show();
            }
        });

        new GetDataTask().execute();

    }

    class GetDataTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;
        int jIndex;
        int x;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            x=list.size();

            if(x==0)
                jIndex=0;
            else
                jIndex=x;

        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {

            MyDataModel model = new MyDataModel();


//            model.setTitle(numero);
//            model.setData(data_cap_cas);

            list.add(model);

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if(list.size() > 0) {
                adapter.notifyDataSetChanged();
            } else {
                Snackbar.make(findViewById(R.id.parentLayout), "No Data Found", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}