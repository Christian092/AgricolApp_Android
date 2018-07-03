package com.example.cristianalarcon.agricolapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler
{
    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
    }
    public void QrScanner(View view)
    {
        try
        {
            mScannerView = new ZXingScannerView(this);
            setContentView(mScannerView);
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }
        catch (Exception e)
        {
            System.out.println("Camara Error:  "+e);
        }

    }
    public void onPause()
    {
        super.onPause();
        mScannerView.stopCamera();
    }
    public void handleResult(Result rawResult)
    {
        Log.e("handler ",rawResult.getText());
        Log.e("handler " , rawResult.getBarcodeFormat().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
            builder.setMessage(rawResult.getText());
            AlertDialog alert1 = builder.create();
            alert1.show();
    }
}
