package lk.lankahomes.baman.tourchlight;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.hardware.Camera;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.TimeZone;

import lk.lankahomes.baman.tourchlight.comman.RequestExternalResouce;
import lk.lankahomes.baman.tourchlight.comman.Utility;
import lk.lankahomes.baman.tourchlight.service.TorchBlinkService;

public class TourchMain extends Activity {
    public Context context;
    static Camera mCam;
    public Boolean isBlink = false;
    public Boolean isLightON = true;
    public Intent mServiceIntent;

    TorchBlinkService mService;
    boolean mBound = false;


    private static final int CAMERA_PERMISSIONS_REQUEST = 1;
    public  ImageButton image_button_Switch;
    public  Switch blink_switch;
    public String country_code = "";
    public String country_name = "";
    public String city = "";
    public String postal = "";
    public String latitude = "";
    public String longitude = "";
    public String IPv4 = "";
    public String state = "";
    public String remarks = "no_remarks";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourch_main);
        context= getApplicationContext();

        mServiceIntent  = new Intent(context, TorchBlinkService.class);

        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            Toast.makeText(getBaseContext(), "Your phone has a Flash", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Your phone has NO Flash", Toast.LENGTH_SHORT).show();
        }

        checkCameraPermission();

        blink_switch = (Switch) findViewById(R.id.sw_blink);
        image_button_Switch = (ImageButton) findViewById(R.id.img_button_light_switch);

        blink_switch.setChecked(false);
        blink_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isBlink = (isChecked) ? true : false;
                }
        });
        putUserLog();

        Manupulate_switch(null);
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, TorchBlinkService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void Manupulate_switch(View v){
        isLightON = (isLightON) ? false : true;
        processCamera();
    }

    private void processCamera(){
        try{
            if(isLightON == true ) {
                    if(isBlink) {
                        if (mBound) {
                            mService.startFlash();
                        }
                    } else {
                        mCam = Camera.open();
                        Camera.Parameters p = mCam.getParameters();
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCam.setParameters(p);
                        SurfaceTexture mPreviewTexture = new SurfaceTexture(0);
                        try {
                            mCam.setPreviewTexture(mPreviewTexture);
                        } catch (IOException ex) {
                            // Ignore
                        }
                        mCam.startPreview();
                        image_button_Switch.setImageResource(R.drawable.btn_switch_on);
                    }
            } else if(isLightON == false){
                if(isBlink) {
                    SharedPreferences sp = getSharedPreferences("torchfile", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putBoolean("is_torchon", true);
                    ed.commit();
                    image_button_Switch.setImageResource(R.drawable.btn_switch_off);
                    mCam.release();
                } else {
                    image_button_Switch.setImageResource(R.drawable.btn_switch_off);
                    mCam.release();
                }
            }
        } catch(Exception e) { Log.e("Error", ""+e);  }
    }

    public void checkCameraPermission(){
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale( Manifest.permission.CAMERA)) { }
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST);
            }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TorchBlinkService.LocalBinder binder = (TorchBlinkService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
             if (requestCode == CAMERA_PERMISSIONS_REQUEST) {
                    if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "FlashLight permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "FlashLight permission denied", Toast.LENGTH_SHORT).show();
                    }
        }  else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void putUserLog(){
        if(new Utility().isNetworkAvailable(context)){
            new RequestExternalResouce(context, new Utility().getGEODetailsUrl(), "", "GET", new RequestExternalResouce.OnTaskDoneListener() {
                @Override
                public void onTaskDone(String responseData) {
                        try {
                            JSONObject response  = new JSONObject(responseData);
                            country_code = response.getString("country_code").toString();
                            country_name = response.getString("country_name").toString();
                            city = response.getString("city").toString();
                            postal = response.getString("postal").toString();
                            latitude = response.getString("latitude").toString();
                            longitude = response.getString("longitude").toString();
                            IPv4 = response.getString("IPv4").toString();
                            state = response.getString("state").toString();
                        } catch (JSONException e) { e.printStackTrace(); }
                        getDeviceDetails();
                    }

                @Override
                public void onError() {
                    System.out.println("Error occurred - no geo request");
                    getDeviceDetails();
                }
            }).execute();
        }
    }

    public void  getDeviceDetails(){
        if(new Utility().isNetworkAvailable(context)){
            try {
                String time_zone = TimeZone.getDefault().getDisplayName().toString();
                String versionRelease = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
                String version = String.valueOf(android.os.Build.VERSION.SDK_INT);
                String manufacturer = Build.MANUFACTURER;
                String model = Build.MODEL;
                String deviceName = android.os.Build.MODEL;

                JSONObject requestBody = new JSONObject();
                requestBody.put("country_code", country_code);
                requestBody.put("country_name", country_name);
                requestBody.put("city", city);
                requestBody.put("postal", postal);
                requestBody.put("latitude", latitude);
                requestBody.put("longitude", longitude);
                requestBody.put("IPv4", IPv4);
                requestBody.put("time_zone", time_zone);
                requestBody.put("versionRelease", versionRelease);
                requestBody.put("version", version);
                requestBody.put("manufacturer", manufacturer);
                requestBody.put("model", model);
                requestBody.put("name", deviceName);
                requestBody.put("remarks", remarks);

                new RequestExternalResouce(context, new Utility().getAddUserLogUrl(), requestBody.toString(), "POST", new RequestExternalResouce.OnTaskDoneListener() {
                    @Override
                    public void onTaskDone(String responseData) {
                        System.out.println("Success");
                    }

                    @Override
                    public void onError() {
                        System.out.println("failed");
                    }
                }).execute();
            }catch (Exception e){ e.printStackTrace();}
        }
    }

}
