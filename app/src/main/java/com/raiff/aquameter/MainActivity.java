package com.raiff.aquameter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //TextView enviarComando;
    TextView receberComando;
    TextView receberComandoCont;
    TextView grafico;
    TextView bd;
    TextView delete_db;
    //private EditText et_dataComando;
    private EditText et_data;
    public MyDBHandler dbHandler;

    public final static String MESSAGE_KEY = "com.raiff.aquameter.message_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadView();
        dbHandler = new MyDBHandler(this, null, null, 1);
        tryHTTP("http://192.168.4.1/aqua/des");

        //enviarComando.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        //MessageSender messageSender = new MessageSender();
        //        //messageSender.execute(et_dataComando.getText().toString());
        //        tryHTTP("http://192.168.4.1/aqua/"+et_dataComando.getText().toString());
        //    }
        //});

        receberComando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryHTTP("http://192.168.4.1/aqua/freq");

            }
        });

        receberComandoCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryHTTP("http://192.168.4.1/aqua/liga");

                startActivity(new Intent(MainActivity.this, ReadloopActivity.class));
            }
        });
        grafico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GraficoActivity.class)
                        .putExtra(MESSAGE_KEY,getEditData()
                        ));
                //new GraficoActivity().setData(getEditData());
            }
        });
        bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReadBD.class));
            }
        });
        delete_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyDBHandler(getApplicationContext(), null, null, 1)
                        .deleteTable();
                Toast.makeText(MainActivity.this, "Banco Deletado", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void addData(String data) {
        Dados dados = new Dados(Calendar.getInstance().getTime().toString(),data);
        dbHandler.addHandler(dados);
    }

    public String getEditData(){
        return et_data.getText().toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tryHTTP("http://192.168.4.1/aqua/des");
    }

    private void loadView() {
        //enviarComando = findViewById(R.id.enviarComando);
        receberComando = findViewById(R.id.receberComando);
        receberComandoCont = findViewById(R.id.receberComandoCont);
        et_data = findViewById(R.id.et_data);
        //et_dataComando = findViewById(R.id.et_dataComando);
        grafico = findViewById(R.id.grafico);
        bd = findViewById(R.id.BD);
        delete_db = findViewById(R.id.delete_db);
    }

    public void tryHTTP(String url){
        RequestQueue queue = Volley.newRequestQueue(this);
        final String aux = url;
        StringRequest putRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if (aux.contains("freq")) {
                            addData(response);
                            et_data.setText(response);
                        }
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
}
