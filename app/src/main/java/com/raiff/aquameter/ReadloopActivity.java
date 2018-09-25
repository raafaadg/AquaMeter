package com.raiff.aquameter;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.raiff.aquameter.adapter.MyArrayAdapter;
import com.raiff.aquameter.model.MyDataModel;
import java.util.ArrayList;

public class ReadloopActivity extends AppCompatActivity implements ArquivoDialogListener{

    Handler UIHandler;
    Thread thread = null;

    private ListView listView;
    private ArrayList<MyDataModel> list;
    private MyArrayAdapter adapter;
    public CoordinatorLayout coordinatorLayout;

    public boolean controlThread = true;
    int cont = 1;
    int amostragem = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Fresco.initialize(this);

        list = new ArrayList<>();

        adapter = new MyArrayAdapter(this, list);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(findViewById(R.id.parentLayout),
                        list.get(position).getTitle() + " => " +
                                list.get(position).getData()
                        , Snackbar.LENGTH_LONG).show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                openDialog();
            }
        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_read);

        controlThread = true;
        runThread();
    }

    public void openDialog(){
        ArquivoDialog arquivoDialog = new ArquivoDialog();
        arquivoDialog.show(getSupportFragmentManager(), "Salvar nome do Arquivo");
    }

    @Override
    public void applyTexts(int amostragem) {
        this.amostragem = amostragem;
    }

    private void runThread() {
        new Thread() {
            public void run() {
                if (interrupted()){
                    controlThread = false;
                    tryHTTP("http://192.168.4.1/aqua/des");
                    return;
                }
                while (controlThread) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tryHTTP("http://192.168.4.1/aqua/data");
                                Log.v("run","RODANDO Thread!!!");
                                if (interrupted()){
                                    controlThread = false;
                                    tryHTTP("http://192.168.4.1/aqua/des");
                                    return;
                                }
                                //new ClientSendAndListen().run();
                            }
                        });
                        Thread.sleep(1000/amostragem);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void tryHTTP(String url){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest putRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        MyDataModel model = new MyDataModel();
                        model.setTitle(String.valueOf(cont++));
                        model.setData(response);
                        list.add(model);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.v("online",error.toString());
                    }
                }
        );

        queue.add(putRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
            Thread.interrupted();
            tryHTTP("http://192.168.4.1/aqua/des");

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controlThread = false;
        tryHTTP("http://192.168.4.1/aqua/des");

    }

}