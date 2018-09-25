package com.raiff.aquameter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    String GPS = "";
    double latitude, longitude;
    private LocationManager locationManager;
    private LocationListener listener;
    Location location;
    LocationListener locationListener;


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

//        GPSTracker gps = new GPSTracker (this);
//        double latitude = gps.getLatitude();
//        double longitude= gps.getLongitude();
//        Toast.makeText(
//                MainActivity.this,
//                latitude + " " + longitude,
//                Toast.LENGTH_SHORT).show();
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

//    private boolean isLocationEnabled()
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

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

    /*private void creatXlsx(ArrayList<String> vals, String nomeArquivo){
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Asking for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        Log.v("log", "Permission is granted");
        MyDBHandler db = new MyDBHandler(this,null,null,1);
        Cursor cursor = db.getuser();
        File sd = Environment.getExternalStorageDirectory();

        String csvFile = nomeArquivo+".xls";

        File directory = new File(sd.getAbsolutePath(),"LOGS");
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("pt", "BR"));
            WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("userList", 0);
            // column and row
            sheet.addCell(new Label(0, 0, "Nome"));
            sheet.addCell(new Label(1, 0, "Idade"));
            sheet.addCell(new Label(2, 0, "Peso"));
            sheet.addCell(new Label(3, 0, "Genero"));
            sheet.addCell(new Label(4, 0, "Email"));
            sheet.addCell(new Label(5, 0, "Dados"));
            // if (cursor.moveToFirst()) {
            //do {
            cursor.moveToFirst();
            String nome = cursor.getString(cursor.getColumnIndex(db.COLUMN_NOME));
            String idade = cursor.getString(cursor.getColumnIndex(db.COLUMN_IDADE));
            String peso = cursor.getString(cursor.getColumnIndex(db.COLUMN_PESO));
            String genero = cursor.getString(cursor.getColumnIndex(db.COLUMN_GENERO));
            String email = cursor.getString(cursor.getColumnIndex(db.COLUMN_EMAIL));

            int i = cursor.getPosition() + 1;
            sheet.addCell(new Label(0, i, nome));
            sheet.addCell(new Label(1, i, idade));
            sheet.addCell(new Label(2, i, peso));
            sheet.addCell(new Label(3, i, genero));
            sheet.addCell(new Label(4, i, email));
            int c = 0, r= 0, cont = 0;

            for(String result : vals) {
                sheet.addCell(new Label(5 + c, i + r, result));
                r++;
                if(r == 60000){
                    r = 0;
                    c++;
                }
                Log.v("ValsXLS","Columns = "+ String.valueOf(c)+" Row = "+
                        String.valueOf(r+(c*60000)));
                //}
                //} //while (cursor.moveToNext());
            }
            //closing cursor
            cursor.close();
            workbook.write();
            workbook.close();
            Toast.makeText(getApplication(),
                    "Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();

        } catch(Exception e){
            e.printStackTrace();
        }finally {
            if (pd.isShowing()){
                pd.dismiss();
            }
        }
    }*/

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            GPS = "";
            Toast.makeText(
                    getBaseContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
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
