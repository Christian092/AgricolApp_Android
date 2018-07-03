package com.example.cristianalarcon.agricolapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.cristianalarcon.agricolapp.Clases.RequestJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Cristian Alarcon on 10-07-2017.
 */

public class Notificacion_Servicio extends Service
{

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public void onCreate()
    {
        Toast.makeText(this,"onCreate Servicio",Toast.LENGTH_LONG).show();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
                                        sendAlert(outsides);
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
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    public void sendAlert(JSONArray outside)
    {

        if(outside!=null && outside.length()>=1)
        {
            int notifReCode = 1;
            Intent intent = new Intent(this, MapsActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(this, 1, intent, 0);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.alert_icon_1551)
                            .setContentTitle("Alerta")
                            .setContentText("Animales fuera del perimetro!")
                            .addAction( R.drawable.alert_icon,"Ver Ubicacion",pIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder.setVibrate(new long[] { 1000, 1000});
            mNotificationManager.notify(001, mBuilder.build());
        }
        else if(outside!=null && outside.length()==1)
        {

        }
    }






}
