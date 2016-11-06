package lk.lankahomes.baman.tourchlight;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
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

public class TourchMain extends Activity {

    public static Camera cam = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourch_main);

        final ImageButton img_on = (ImageButton) findViewById(R.id.im_btn_green_light);
        final ImageButton img_off = (ImageButton) findViewById(R.id.im_btn_greendark);
        img_on.setVisibility(View.GONE);
        img_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_on.setVisibility(View.GONE);
                img_off.setVisibility(View.VISIBLE);
                offtourch();

            }
        });


        img_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ontourch();
                img_on.setVisibility(View.VISIBLE);
                img_off.setVisibility(View.GONE);

            }
        });


    }

    private void ontourch(){
//        try {
//            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
//                cam = Camera.open();
//                Parameters p = cam.getParameters();
//                p.setFlashMode(Parameters.FLASH_MODE_TORCH);
//                cam.setParameters(p);
//                cam.startPreview();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getBaseContext(), "Tourch Light ON", Toast.LENGTH_SHORT).show();
//        }



    }

    private void offtourch(){
        try {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Tourch Light OFF", Toast.LENGTH_SHORT).show();
        }
    }



}
