package com.raiff.aquameter;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPActivity extends AppCompatActivity{

    TextView enviarComando;
    TextView receberComando;
    private EditText et_data;
    Handler UIHandler;
    Thread Thread1 = null;
    Socket socket = null;

    public static final int SERVERPORT = 80;
    public static final String SERVERIP = "192.168.1.4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadView();

        Thread myThread = new Thread(new MyServerThread());
        myThread.start();

        enviarComando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        enviarComando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadView() {
        enviarComando = findViewById(R.id.enviarComando);
        receberComando = findViewById(R.id.receberComando);
        et_data = findViewById(R.id.et_data);
    }
}
