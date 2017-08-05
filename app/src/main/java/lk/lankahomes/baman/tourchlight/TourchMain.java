package lk.lankahomes.baman.tourchlight;

import android.Manifest;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

public class TourchMain extends Activity {

    public Camera cam = null;
    public Context context;
    static Camera mCam;
    Boolean clickOn = false;
    private static final int FLASHLIGHT_PERMISSIONS_REQUEST = 1;


    public  ImageButton img_on;
    public  ImageButton img_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourch_main);
        context= getApplicationContext();

        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            Toast.makeText(getBaseContext(), "Your phone has a Flash", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Your phone has NO Flash", Toast.LENGTH_SHORT).show();
        }

        checkCameraPermission();

        img_on = (ImageButton) findViewById(R.id.im_btn_green_light);
        img_off = (ImageButton) findViewById(R.id.im_btn_greendark);



    }

    public void lightsOn(View v){
        clickOn = true;
        processCamera();
    }
    public void lightsOff(View v){
        clickOn = false;
        processCamera();
    }



    private void processCamera(){
        try{
            if(clickOn == true) {
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

                img_on.setVisibility(View.GONE);
                img_off.setVisibility(View.VISIBLE);

            } else {
                mCam.release();
                img_on.setVisibility(View.VISIBLE);
                img_off.setVisibility(View.GONE);
            }
        } catch(Exception e) {
            Log.e("Error", ""+e);
        }
    }











    public void checkCameraPermission(){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale( Manifest.permission.CAMERA)) { }
                requestPermissions(new String[]{Manifest.permission.CAMERA}, FLASHLIGHT_PERMISSIONS_REQUEST);
            }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
             if (requestCode == FLASHLIGHT_PERMISSIONS_REQUEST) {
                    if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "FlashLight permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "FlashLight permission denied", Toast.LENGTH_SHORT).show();
                    }
        }  else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
