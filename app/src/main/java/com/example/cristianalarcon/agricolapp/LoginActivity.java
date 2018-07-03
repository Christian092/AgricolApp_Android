package com.example.cristianalarcon.agricolapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cristianalarcon.agricolapp.Clases.RequestJson;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText inputRut;
    private EditText inputPassword;
    private TextView loginErorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputRut = (EditText) findViewById(R.id.txtRut);
        inputPassword = (EditText) findViewById(R.id.txtPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        loginErorMsg = (TextView) findViewById(R.id.login_error);

        btnLogin.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view)
            {
                String rut = inputRut.getText().toString();
                String password = inputPassword.getText().toString();

                JSONObject jsonRequest = new JSONObject();
                try {
                    jsonRequest.put("rut",rut);
                    jsonRequest.put("password",password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String jsonString = jsonRequest.toString();
                final String url = "dfgdfgdfgdgfdgf";

                try{
                    final RequestJson jsonReq = new RequestJson(new RequestJson.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            JSONObject json;
                            try {
                                json = new JSONObject(output);
                                int id = json.getInt("id");
                                String nombre = json.getString("nombre");
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    });

                    jsonReq.execute(url, jsonString);
                }catch (Exception ex)
                {
                    String result = ex.getMessage();
                }
            }
        });

    }

}
