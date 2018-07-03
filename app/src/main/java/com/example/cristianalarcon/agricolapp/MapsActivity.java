package com.example.cristianalarcon.agricolapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.cristianalarcon.agricolapp.Clases.RequestJson;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends MainActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<LatLng> arrayPoints = null;
    PolygonOptions polygonOptions = new PolygonOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        arrayPoints = new ArrayList<LatLng>();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //ejecutando el servicio
        startService(new Intent(this, Notificacion_Servicio.class));


        JSONObject jsonRequest = new JSONObject();

        final String jsonString = jsonRequest.toString();
        final String url = "http://localhost:32755/Gps/IsPointInPolygon";


        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            final RequestJson jsonReq = new RequestJson(new RequestJson.AsyncResponse()
                            {
                                @Override
                                public void processFinish(String output)
                                {
                                    JSONObject json;
                                    JSONArray insides,outsides;
                                    try
                                    {
                                        json = new JSONObject(output);
                                        insides=json.getJSONArray("inside");
                                        outsides=json.getJSONArray("outside");
                                        //mMap.clear();
                                        loadLatLng(insides,outsides);
                                        System.out.println("holaaaaaaaaaaaaa"+insides);
                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            jsonReq.execute(url, jsonString);
                        }
                        catch (Exception ex)
                        {
                            String result = ex.getMessage();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask,0,5000);

        //sendNotification();
    }
    public void sendNotification()
    {

        //Get an instance of NotificationManager//
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");


        // Gets an instance of the NotificationManager service//
        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(001, mBuilder.build());
    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }
    public void loadLatLng(JSONArray insides,JSONArray outsides)
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        try
        {
            for (int i=0; i<insides.length();i++)
            {
                JSONObject inside;
                inside=insides.getJSONObject(i);
                double lat=inside.getDouble("latitud");
                double lng=inside.getDouble("longitud");
                LatLng in = new LatLng(lat,lng);
                builder.include(in);
                mMap.addMarker(new MarkerOptions()
                        .position(in)
                        .title("animal")
                        .snippet(inside.getString("animal_Id"))
                        .draggable(true));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(in));
            }
            for (int i=0; i<outsides.length();i++)
            {
                JSONObject outside;
                outside=outsides.getJSONObject(i);
                double lat=outside.getDouble("latitud");
                double lng=outside.getDouble("longitud");
                LatLng out = new LatLng(lat,lng);
                builder.include(out);
                int height = 70;
                int width = 70;
                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.alert_icon);
                Bitmap b=bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                mMap.addMarker(new MarkerOptions()
                        .position(out)
                        .title("Animal")
                        .snippet(outside.getString("animal_Id"))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        .draggable(true));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(out));
            }

        }
        catch (Exception e)
        {

        }
        LatLngBounds bounds = builder.build();
        int padding = 200; // Zoom over markers
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);

        JSONObject jsonRequest = new JSONObject();

        final String jsonString = jsonRequest.toString();
        final String url = "http://localhost:32755/Gps/GetTerreno";

        try
        {
            final RequestJson jsonReq = new RequestJson(new RequestJson.AsyncResponse()
            {
                @Override
                public void processFinish(String output)
                {
                    JSONArray jsonArray;
                    try
                    {
                        jsonArray = new JSONArray(output);
                        System.out.println(""+jsonArray);
                        loadPolygon(jsonArray);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            jsonReq.execute(url, jsonString);
        }
        catch (Exception ex)
        {
            String result = ex.getMessage();
        }

    }
    public void loadPolygon(JSONArray polygon)
    {
        try
        {
            for (int i = 0 ; i < polygon.length(); i++)
            {
                JSONObject point;
                point=polygon.getJSONObject(i);
                double lat=point.getDouble("latitud");
                double lng=point.getDouble("longitud");
                arrayPoints.add(new LatLng(lat,lng));
            }
            if (arrayPoints.size() >= 3)
            {

                polygonOptions.addAll(arrayPoints);
                polygonOptions.strokeColor(Color.RED);
                polygonOptions.strokeWidth(2);
                polygonOptions.fillColor(Color.GRAY);
                Polygon poly = mMap.addPolygon(polygonOptions);
            }
        }
        catch (Exception e)
        {
            String result = e.getMessage();
        }


    }
}
