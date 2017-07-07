package com.example.xenon.mapapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private List<Marker> markers;
    private Button b5;
    private Button b6;
    int TracksQuantity=0;
    private String path;
    private String fil2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String filename = getIntent().getStringExtra("FILENAME");
        super.onCreate(savedInstanceState);
        fil2=filename;
        path=getApplicationContext().getFilesDir().toString()+"/folder";
        setContentView(R.layout.activity_maps);
        b5=(Button)findViewById(R.id.button5);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveTrack();
            }
        });
        b6=(Button)findViewById(R.id.button6);
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    if(filename!="") LoadTrack(filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                ClearMarkers();
            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markers = new ArrayList<>();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        if(fil2!="") try {
            LoadTrack(fil2);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

    }
    */

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            case R.id.mapTypeNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            case R.id.mapTypeSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            case R.id.mapTypeTerrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            case R.id.mapTypeHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    int i=0;
    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(5000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

                    //Adding marker
                    Marker m = mMap.addMarker(new MarkerOptions().position(ll).title("Track marker"));
                    if(i==0){
                        markers.add(m);
                        i=1;
                    }

                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 18);
                    //Context context=getApplicationContext();
                    //Toast.makeText(context, "text", Toast.LENGTH_SHORT).show();
                    i--;
                    mMap.animateCamera(update);
                } else {
                    Log.i("onLocationChanged", "location==null!!!!!!!");

                }
            }
        });
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    Polyline polyline;

    private void drawLine(List<Marker> markers){
        PolylineOptions opt = new PolylineOptions();
        for(Marker m : markers){
            opt.add(m.getPosition());
        }
        opt.color(Color.BLUE);
        opt.width(3);
        polyline = mMap.addPolyline(opt);

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 18);
        Context context=getApplicationContext();
        Toast.makeText(context, "Track loaded", Toast.LENGTH_SHORT).show();
        mMap.animateCamera(update);

    }

    public void SaveTrack(){
        Date d=new Date();
        int number=d.getSeconds();
        String filename="trasa "+Integer.toString(number)+".txt";
        TracksQuantity++;
        File file = new File(path, filename);
        String text = "";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            for(Marker m : markers) {
                LatLng ltlng=m.getPosition();
                text=ltlng.latitude+","+ltlng.longitude+"|";
                Log.i("LATLNG", text);
                outputStream.write(text.getBytes());
                //outputStream.flush();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Track saved", Toast.LENGTH_SHORT).show();
        Log.i("Saved as",file.getPath().toString());
    }
    public void LoadTrack(String filename) throws IOException {
        markers.clear();
        Log.i("Loading from", filename);
        FileInputStream fis = getApplicationContext().openFileInput(filename);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        int temp=0;
        while ((line = bufferedReader.readLine()) != null) {
            Log.i("Line", line);
            List<String> arr=splitLine(line, '|');
            String [] array=new String[arr.size()];
            arr.toArray(array);

            Log.i("Splited", array[0]);
            Log.i("After split", "test");
            for(int j=0; j<array.length; j++) {
                Log.i("  "+Integer.toString(j)+" wywolanie", array[j]);
                if(array[j]!="") {
                    String[] arnav = array[j].split(",");
                    double latitude = Double.parseDouble(arnav[0]);
                    double longitude = Double.parseDouble(arnav[1]);
                    String tmp = Double.toString(latitude) + " " + Double.toString(longitude);
                    Log.i("lat i long", tmp);
                    LatLng ll = new LatLng(latitude, longitude);
                    Marker m = mMap.addMarker(new MarkerOptions().position(ll).title("Marker"));
                    markers.add(m);

                }

            }

        }
        drawLine(markers);
        Toast.makeText(getApplicationContext(), "Track loaded", Toast.LENGTH_SHORT).show();
    }

    public List<String> splitLine(String line, char charakter){
        int last_char=-1;
        List<String> arr= new ArrayList<>();
        for(int i=0; i<line.length(); i++){
            if(line.charAt(i)==charakter){
                String s=line.substring(last_char+1, i);
                Log.i("split wlasny", s);
                arr.add(s);
                last_char=i;
                i++;
            }
        }
        return arr;
    }

    public void ClearMarkers(){
        mMap.clear();
        markers.clear();
    }

}
