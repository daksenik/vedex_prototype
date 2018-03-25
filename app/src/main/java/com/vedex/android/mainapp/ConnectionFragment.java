package com.vedex.android.mainapp;

import android.support.v4.app.Fragment;

import org.json.JSONObject;


public abstract class ConnectionFragment extends Fragment {//TODO: Can we store API URL here?
    public abstract void onPostConnection(String response);
}
