package lk.lankahomes.baman.tourchlight.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

/**
 * Created by baman on 8/13/17.
 */

public class TorchBlinkService  extends Service {

    private final IBinder mBinder = new LocalBinder();
    static Camera mCam;
    private Context mContext;

    // Random number generator
    public class LocalBinder extends Binder {
        public TorchBlinkService getService() {
            return TorchBlinkService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        this.mContext = this.getApplicationContext();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver, new IntentFilter("actionOnFlash"));
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Do Something With Received Data
            String msg = intent.getStringExtra("MESSAGE");
            System.out.print("------ Received a Broad cast");
            if (msg == "startFlash"){
                startFlash();
            } else {

            }
        }
    };

    public void startFlash (){
        int k = 1;
        int i= 0;
        try {
            while (k > 0) {
                    mCam = Camera.open();
                    Camera.Parameters p = mCam.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCam.setParameters(p);
                    SurfaceTexture mPreviewTexture = new SurfaceTexture(0);
                    if (i % 2 == 0) {
                        try {
                            mCam.setPreviewTexture(mPreviewTexture);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        mCam.startPreview();
                    } else {  mCam.release(); }
                    i++;
            }
        } catch (Exception e){ e.printStackTrace(); }
    }

    public void stopflash(){


    }




}
