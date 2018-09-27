package com.raiff.aquameter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;

public class MainActivity extends AppCompatActivity implements ArquivoDialogListener {

    //TextView enviarComando;
    TextView receberComando;
    TextView receberComandoCont;
    TextView grafico;
    TextView bd;
    TextView delete_db;
    TextView gsheet;
    TextView mergebd;
    //private EditText et_dataComando;
    private EditText et_data;
    public MyDBHandler dbHandler;
    String GPS = "";
    int controleBD;
    private LocationManager locationManager;
    private LocationListener listener;
    LocationListener locationListener;
    private static final int REQUEST_CODE = 3132;



    public final static String MESSAGE_KEY = "com.raiff.aquameter.message_key";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            showAlert();

        locationListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        else {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                showAlert();

            locationListener = new MyLocationListener();

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }



        loadView();
        dbHandler = new MyDBHandler(this, null, null, 1);
        tryHTTP("http://192.168.4.1/aqua/des");

//        enviarComando.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //MessageSender messageSender = new MessageSender();
//                //messageSender.execute(et_dataComando.getText().toString());
//                tryHTTP("http://192.168.4.1/aqua/"+et_dataComando.getText().toString());
//            }
//        });

        receberComando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryHTTP("http://192.168.4.1/aqua/freq");
                creatXlsx("amostras");

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
                        .putExtra(MESSAGE_KEY, getEditData()
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
        gsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        mergebd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SheetAPIActivity.class));
            }
        });

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Toast.makeText(
                        MainActivity.this,
                        location.getLongitude() + " " + location.getLatitude(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

    }

    private void addData(String data) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        assert locationManager != null;
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Dados dados = new Dados(Calendar.getInstance().getTime().toString(),GPS,data);
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
        gsheet = findViewById(R.id.gsheet);
        mergebd = findViewById(R.id.mergebd);
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
//                            new SendRequest().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Suas Configurações de Local estão DESATIVADAS.\nPor favor Ative" +
                        " o Local para usar este aplicativo!")
                .setPositiveButton("Configuração de Local", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        showAlert();
                    }
                });
        dialog.show();
    }

    private void creatXlsx(String nomeArquivo){

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Asking for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        Log.v("log", "Permission is granted");
        MyDBHandler db = new MyDBHandler(this,null,null,1);
        Cursor cursor = db.getuser();
        File sd = Environment.getExternalStorageDirectory();

        String csvFile = nomeArquivo+".xls";

        File directory = new File(sd.getAbsolutePath(),"AMOSTRAS");
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            boolean f = file.delete();
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("pt", "BR"));
            WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("userList", 0);
            // column and row
            sheet.addCell(new Label(0, 0, "ID"));
            sheet.addCell(new Label(1, 0, "TimeStamp"));
            sheet.addCell(new Label(2, 0, "GPS"));
            sheet.addCell(new Label(3, 0, "DATA"));

            ArrayList<String> dados = db.loadHandler();
            int i = 1;
            for(String aux : dados){
                Log.e("aux", aux);
                String buffer = "";
                int controle = 0;
                for(char res : aux.toCharArray()) {
                    if(res != ';')
                        buffer += res;
                    else{
                        switch (controle) {
                            case 0:
                                sheet.addCell(new Label(0, i, buffer));
                                buffer = "";
                                controle++;
                                break;
                            case 1:
                                sheet.addCell(new Label(1, i, buffer));
                                buffer = "";
                                controle++;
                                break;
                            case 2:
                                sheet.addCell(new Label(2, i, buffer));
                                buffer = "";
                                controle++;
                                break;
                            case 3:
                                sheet.addCell(new Label(3, i, buffer));
                                buffer = "";
                                controle = 0;
                                i++;
                                break;
                        }
                    }
                }
            }

            cursor.close();
            workbook.write();
            workbook.close();
            Toast.makeText(getApplication(),
                    "Dado Salvo no EXCEL", Toast.LENGTH_SHORT).show();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void openDialog(){
        MyDBHandler db = new MyDBHandler(getBaseContext(),null,null,1);
        ArrayList<String> dados = db.loadHandler();

        ArquivoDialogSheet arquivoDialogSheet = new ArquivoDialogSheet();
        arquivoDialogSheet.hint = "Delecione um ID do BD entre 1 e " + dados.size();
        arquivoDialogSheet.show(getSupportFragmentManager(), "ID Dados BD");
    }

    @Override
    public void applyTexts(int controleBD) {
        this.controleBD = controleBD;
        new SendRequest().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class SendRequest extends AsyncTask<Void, Void, String> {


        protected void onPreExecute(){
            Log.v("PreExec","Entrou na pré execução");
            Thread.currentThread().setPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);
        }

        protected String doInBackground(Void... resultss) {

            String retorno = "";
            try{
                URL url = new URL("https://script.google.com/macros/s/" +
                        "AKfycbzzhOLiYVBXcJ-SKmbUsdhVXRKqHd4iczOaUQ3jSLxzHgKU9Cfy/exec");
                JSONObject postDataParams = new JSONObject();

                String id = "1pJmWLNTwpPbB_qLE3KqukWVZuGiYw-I7yGieUEf9QxA";

                ArrayList<String> result = new ArrayList<String>();
                String buffer = "";

                MyDBHandler db = new MyDBHandler(getBaseContext(),null,null,1);
                ArrayList<String> dados = db.loadHandler();
                if(controleBD > dados.size())
                    return "Valor de ID inválido";
                String results = dados.get(controleBD-1);
//                for(String results : dados) {
                    for (char res : results.toCharArray()) {
                        if (res != ';')
                            buffer += res;
                        else {
                            result.add(buffer);
                            buffer = "";
                        }
                    }

                    postDataParams.put("id", id);
                    postDataParams.put("id2", result.get(0));
                    postDataParams.put("time", result.get(1));
                    postDataParams.put("gps", result.get(2));
                    postDataParams.put("data", result.get(3));
                    result.clear();
                    result = new ArrayList<String>();

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));

                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {

                        BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuffer sb = new StringBuffer("");
                        String line="";

                        while((line = in.readLine()) != null) {

                            sb.append(line);
                            break;
                        }

                        in.close();
                        return sb.toString();

                    }
                    else {
                        return new String("false : "+responseCode);
                    }
//                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }


    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            GPS = "";
            String longitude = "Long:" + loc.getLongitude();
            Log.v("GPS", longitude);
            String latitude = "Lati:" + loc.getLatitude();
            Log.v("GPS", latitude);

            /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            GPS = longitude + "," + latitude + ",Cidade:" + cityName;
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

}
