package com.example.cristianalarcon.agricolapp.Clases;


import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestJson extends AsyncTask<String, Void, String> {

    public interface AsyncResponse
    {
        void processFinish(String output);
    }

    public AsyncResponse delegate=null;
    public RequestJson(AsyncResponse delegate){this.delegate=delegate;}

    @Override
    protected String doInBackground(String... params) {
        String myurl = params[0];
        String jsonObject = params[1];
        HttpURLConnection conn = null;
        try {
            StringBuffer response = null;
            URL url = new URL(myurl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(jsonObject);
            writer.close();
            out.close();
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode" + responseCode);
            switch (responseCode) {
                case 200:
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return  response.toString();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s)
    {
        delegate.processFinish(s);

    }

}
