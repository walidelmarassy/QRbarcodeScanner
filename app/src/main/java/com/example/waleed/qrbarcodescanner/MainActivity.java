package com.example.waleed.qrbarcodescanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA=1;
    private ZXingScannerView mScannerView;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if (checkPermission()){
                Toast.makeText(MainActivity.this, "pemission is Granted", Toast.LENGTH_LONG).show();


            }
            else {
                requestPermission();

                }

        }

    }
    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED);


    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},REQUEST_CAMERA);


    }
    public void onRequestpermissionsResult(int requestcode,String permission[] ,int grantResults[])
    {
        switch (requestcode){
            case REQUEST_CAMERA:
                if (grantResults.length>0)
                {
                    boolean CameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (CameraAccepted){
                        Toast.makeText(MainActivity.this ,
                                "permission Granted", Toast.LENGTH_LONG).show();

                    }
                    else {
                        Toast.makeText(MainActivity.this, "permission deined", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
                            if (shouldShowRequestPermissionRationale(CAMERA)){
                                displayAlertMessage("you need to allow acess for both permissions", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{CAMERA},REQUEST_CAMERA);
                                        }

                                    }
                                });
                                return;

                            }
                        }

                    }


                }
                break;
        }


    }
    @Override
    public void onResume(){
        super.onResume();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (checkPermission()){
                if (mScannerView==null){
                    mScannerView=new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();

            }
            else {
                requestPermission();
            }
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mScannerView.stopCamera();

    }
    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(MainActivity.this).setMessage(message).setPositiveButton("ok",listener).setNegativeButton("cancel",null).create().show();


    }

    @Override
    public void handleResult(final Result result) {
        final String scanResult=result.getText();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("scan result");
        builder.setPositiveButton("ok" ,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mScannerView.resumeCameraPreview(MainActivity.this);

            }
        });
        builder.setNeutralButton("visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                startActivity(intent);


            }
        });
        builder.setMessage(scanResult);
        AlertDialog alertDialog=builder.create();
        alertDialog.show();


    }
}
