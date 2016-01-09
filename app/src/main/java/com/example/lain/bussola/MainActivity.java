package com.example.lain.bussola;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // define imagem da bulssola
    private ImageView image;

    // guarda o angulo em que a bulsola está virada
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    TextView tvHeading;
    TextView txtAzimuth;
    TextView txtGetDeclination;
    TextView txtNovoAzimuth;
    TextView txtBearingTo;
    TextView txtDirection;
    TextView txtBearing;
    TextView txtPosicaoInicial;
    TextView txtPosicaoFinal;
    TextView txtDistancia;

    // inicio do location para pegar a posicao atual
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location Destino = new Location("Destino");
    Location currentLoc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();
        //    }
        //});


        image = (ImageView) findViewById(R.id.bulssola);
        tvHeading = (TextView) findViewById(R.id.cabecario);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        txtAzimuth = (TextView) findViewById(R.id.txt_azimuth);
        txtGetDeclination = (TextView) findViewById(R.id.txtGetDeclination);
        txtNovoAzimuth = (TextView) findViewById(R.id.txtNovoAzimuth);
        txtBearingTo = (TextView) findViewById(R.id.txtBearingTo);
        txtDirection = (TextView) findViewById(R.id.txtDirection);
        txtBearing = (TextView) findViewById(R.id.txtBearing);
        txtPosicaoInicial = (TextView) findViewById(R.id.txtPosicaoInicial);
        txtPosicaoFinal = (TextView) findViewById(R.id.txtPosicaoFianl);
        txtDistancia = (TextView) findViewById(R.id.txtDistancia);

        //Destino.setLatitude(-16.676795);
        //Destino.setLongitude(-49.242968);

        currentLoc = new Location("Posicao atual");
        currentLoc.setLatitude(0);
        currentLoc.setLongitude(0);

        Destino.setLatitude(-15.799718);
        Destino.setLongitude(-47.864197);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                currentLoc = location;

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }else{
                locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
            }

        }
        else{
            locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
                return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        double azimuth = 0;
        azimuth = Math.round(event.values[0]);

        //tvHeading.setText("Heading: " + Float.toString(degree) + " graus");

        txtAzimuth.setText("Azimuth: " + azimuth + " graus");

        //rotacao da imagem
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        //tempo que a animacao ira demorar
        ra.setDuration(210);
        ra.setFillAfter(true);
        //inicia a animacao
        image.startAnimation(ra);
        currentDegree = -degree;

        //aqui começa os t
        // estes

        GeomagneticField geoField = new GeomagneticField(
                (float) currentLoc.getLatitude(),
                (float) currentLoc.getLongitude(),
                (float) currentLoc.getAltitude(),
                System.currentTimeMillis());
        azimuth += geoField.getDeclination(); // converts magnetic north into true north
        float bearingTo = currentLoc.bearingTo(Destino); // (it's already in degrees)

        //altera
        float transformado = 0;
        if( bearingTo < 0){
            transformado = 360 + bearingTo;
        }else{
            transformado = bearingTo;
        }

        double myDirection = transformado - azimuth;
        if (myDirection < 0 ){
            myDirection = 360 + myDirection;
        }
        //double direction = azimuth - bearingTo;
        double direction = bearingTo - (bearingTo + azimuth);

        txtGetDeclination.setText("getDeclination: " + geoField.getDeclination() + " graus");
        txtNovoAzimuth.setText("Azimuth Verdadeiro: " + azimuth + " graus");
        txtBearingTo.setText("BearingTo: " + bearingTo + " graus");
        txtDirection.setText("Direction: " + Math.round(-direction/ 360 + 180)+ " graus");
        txtBearing.setText("Ndirecao: " + myDirection + " graus");
        txtPosicaoInicial.setText("Lat At: " + currentLoc.getLatitude() + " Long At: " + currentLoc.getLongitude());
        txtPosicaoFinal.setText("Lat dst: " + Destino.getLatitude() + " Long dst: " + Destino.getLongitude());
        txtDistancia.setText("d: " + currentLoc.distanceTo(Destino) + " m");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
