package lk.lankahomes.baman.tourchlight.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.io.IOException;

/**
 * Created by baman on 8/13/17.
 */

public class TorchBlinkService  extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public TorchBlinkService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TorchBlinkService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    static Camera mCam;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    public void startFlash (){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences pref = getSharedPreferences("torchfile", MODE_PRIVATE);
        int k = 1;
        int i= 0;
        try {
            while (k > 0) {
                if (pref.getBoolean("is_torchon", false)){
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

                } else {
                    k=3;
                    mCam.release();
                    stopflash();
                }
            }
        } catch (Exception e){ e.printStackTrace(); }
    }

    public void stopflash(){
        SharedPreferences sp = getSharedPreferences("torchfile", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("is_torchon", false);
        ed.commit();

    }
}
