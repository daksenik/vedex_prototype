package com.vedex.android.mainapp.network;


import android.os.AsyncTask;
import android.util.Log;

import com.vedex.android.mainapp.ConnectionFragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class APIConnection {

    ExecutorService executor = Executors.newFixedThreadPool(10);
    Semaphore semaphore = new Semaphore(1);

    private static APIConnection instance;

    private String response;
    private ConnectionFragment curFragment;

    private CookieManager cookieManager = null;

    private APIConnection() {

    }

    public static APIConnection getInstance() {
        if(instance == null) {
            instance = new APIConnection();
        }
        return instance;
    }

    public void makeRequest(final String url, final JSONObject request, final ConnectionFragment fragment) throws Exception {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(cookieManager == null) {
                    cookieManager = new CookieManager();
                    CookieManager.setDefault(cookieManager);
                }

                curFragment = fragment;
                SendDataToServer s = new SendDataToServer();
                response = new String();
                s.execute(url, String.valueOf(request));
            }
        });

    }


    private class SendDataToServer extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();
            try {
                String url = params[0];
                String request = params[1];
                URL obj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                OutputStream os = connection.getOutputStream();
                DataOutputStream wr = new DataOutputStream(os);
                wr.writeBytes(request);
                wr.flush();
                wr.close();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            publishProgress(response.toString());
            return response.toString();
        }

        @Override
        protected void onProgressUpdate(String... s) {
            super.onProgressUpdate(s);
            response = s[0];
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            response = s;
            if (curFragment.isVisible()) {
                curFragment.onPostConnection(s);
            }
            semaphore.release();
        }
    }
}