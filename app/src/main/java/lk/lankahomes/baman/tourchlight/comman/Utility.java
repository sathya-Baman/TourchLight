package lk.lankahomes.baman.tourchlight.comman;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by baman on 8/11/17.
 */

public class Utility {


    public String getGEODetailsUrl(){
        return "https://geoip-db.com/json/";
    }

    public String getAddUserLogUrl(){
        return "http://178.62.43.112/1torchlight/tourchlog.php";
    }

    public void addDataToSharedPreferences(String key, String value, Context ctx){
        SharedPreferences.Editor editor = ctx.getSharedPreferences("torch_shared_preferences", ctx.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String  getDataFromSharedPreferences(String key, Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences("torch_shared_preferences", ctx.MODE_PRIVATE);
        return prefs.getString(key, "no_data");
    }
    
    public boolean isNetworkAvailable(Context Ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Ctx.getSystemService(Ctx.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
