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

public class MainActivity extends AppCompatActivity {

    TextView enviarComando;
    TextView receberComando;
    TextView receberComandoCont;
    TextView grafico;
    private EditText et_dataComando;
    private EditText et_data;
    Handler UIHandler;
    Thread Thread1 = null;
    Socket socket = null;

    public static final int SERVERPORT = 80;
    public static final String SERVERIP = "192.168.1.4";
    public final static String MESSAGE_KEY = "com.raiff.aquameter.message_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadView();
//        Thread myThread = new Thread(new MyServerThread());
//        myThread.start();
        //UIHandler = new Handler();

        //this.Thread1 = new Thread(new Thread1());
        //this.Thread1.start();

        //new readData().execute("http://192.168.4.1/mestrado/edit");
        enviarComando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MessageSender messageSender = new MessageSender();
                //messageSender.execute(et_dataComando.getText().toString());
                tryHTTP("http://192.168.4.1/aqua/"+et_dataComando.getText().toString());
            }
        });

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
                new GraficoActivity().setData(getEditData());
            }
        });
        tryHTTP("http://192.168.4.1/aqua/des");
    }

    public String getEditData(){
        return et_data.getText().toString();
    }
    @Override
    protected void onResume() {
        super.onResume();
        tryHTTP("http://192.168.4.1/aqua/liga");
    }

    private void loadView() {
        enviarComando = findViewById(R.id.enviarComando);
        receberComando = findViewById(R.id.receberComando);
        receberComandoCont = findViewById(R.id.receberComandoCont);
        et_data = findViewById(R.id.et_data);
        et_dataComando = findViewById(R.id.et_dataComando);
        grafico = findViewById(R.id.grafico);
    }

    public void tryHTTP(String url){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest putRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        et_data.setText(response);
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

    class MyServerThread implements Runnable{

        Socket s;
        ServerSocket ss;
        InputStreamReader isr;
        BufferedReader bufferedReader;
        Handler h = new Handler();
        String message;

        @Override
        public void run() {
            try {
                ss = new ServerSocket(SERVERPORT);
                while (true){
                    s = ss.accept();
                    isr = new InputStreamReader(s.getInputStream());
                    bufferedReader = new BufferedReader(isr);
                    message = bufferedReader.readLine();
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            et_data.setText(message);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class readData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {
            Socket clientSocket;
            BufferedReader input;

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVERIP);
                socket = new Socket(serverAddr,SERVERPORT);

                Thread2 commThread = new Thread2(socket);
                new Thread(commThread).start();
            }catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


        }
    }

    class Thread1 implements Runnable{
        public void run(){
            Socket socket = null;

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVERIP);
                socket = new Socket(serverAddr,SERVERPORT);

                Thread2 commThread = new Thread2(socket);
                new Thread(commThread).start();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    class Thread2 implements Runnable{
        private Socket clientSocket;
        private BufferedReader input;

        public Thread2(Socket clientSocket){
            this.clientSocket = clientSocket;
            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        public void run(){

            while (!Thread.currentThread().isInterrupted()){
                try {
                    String read = input.readLine();
                    if(read != null){
                        UIHandler.post(new updateUIThread(read));
                    }
                    else {
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    class updateUIThread implements Runnable{
        private String msg;

        public updateUIThread(String str){this.msg = str;}

        @Override
        public void run() {
            et_data.setText(msg);
        }
    }

}
