package lk.lankahomes.baman.tourchlight.comman;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by baman on 8/11/17.
 */

public class RequestExternalResouce extends AsyncTask<String, Void, String> {

    private Context mContext;
    private OnTaskDoneListener onTaskDoneListener;
    private String urlStr = "";
    private String  requestBody = "";
    private Boolean bearer = true;
    private String requestType="";

    public RequestExternalResouce(Context context, String url, String body, String requestType,  OnTaskDoneListener onTaskDoneListener) {
        this.mContext = context;
        this.urlStr = url;
        this.requestBody= body;
        this.onTaskDoneListener = onTaskDoneListener;
        this.bearer = bearer;
        this.requestType = requestType;

    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL mUrl = new URL(urlStr);
            HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();

            if (requestType.equals("POST")){
                httpConnection.setRequestMethod("POST");
            }  else {
                httpConnection.setRequestMethod("GET");
            }

            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");


            httpConnection.setUseCaches(false);

            if (requestType.equals("POST")) { httpConnection.setDoOutput(true); }
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setConnectTimeout(1000000);
            httpConnection.setReadTimeout(1000000);

            if (requestType.equals("POST")) {
                DataOutputStream localDataOutputStream = new DataOutputStream(httpConnection.getOutputStream());
                localDataOutputStream.writeBytes(requestBody.toString());
                localDataOutputStream.flush();
                localDataOutputStream.close();
            }


            int responseCode = httpConnection.getResponseCode();
            System.out.println("Connection response code : "+ responseCode);
            httpConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();


            if (responseCode == HttpURLConnection.HTTP_OK) {
                return sb.toString();
            }

        } catch (IOException e) { e.printStackTrace();
        } catch (Exception ex) { ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (onTaskDoneListener != null && s != null) {
            onTaskDoneListener.onTaskDone(s);
        } else
            onTaskDoneListener.onError();
    }


    public interface OnTaskDoneListener {
        void onTaskDone(String responseData);
        void onError();
    }

}