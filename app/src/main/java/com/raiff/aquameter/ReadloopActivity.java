package com.raiff.aquameter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class ReadloopActivity extends AppCompatActivity {

    Handler UIHandler;
    Thread thread = null;

    private ListView listView;
    private ArrayList<MyDataModel> list;
    private MyArrayAdapter adapter;

    public static final int SERVERPORT = 80;
    public static final String SERVERIP = "192.168.1.4";
    public boolean controlThread = true;
    String messageStr="send";
    int cont = 1;
    private static final int UDP_SERVER_PORT = 4200;
    private static final int MAX_UDP_DATAGRAM_LEN = 10;

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

        controlThread = true;
        runThread();

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
                        Thread.sleep(1000);
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