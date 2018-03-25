package com.vedex.android.mainapp.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.vedex.android.mainapp.R;

import java.io.InputStream;

/**
 * Created by user on 16.09.2017.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap mIcon11 = null;
        if(urlDisplay == null || urlDisplay.equals("null")) return null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if(result == null) return;
        int width = result.getWidth(), height = result.getHeight();
        if(width > height) {
            int delta = width - height;
            result = Bitmap.createBitmap(result, delta / 2, 0, width - delta, height);
        } else {
            int delta = height - width;
            result = Bitmap.createBitmap(result, 0, delta / 2, width, height - delta);
        }
        bmImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        bmImage.setImageBitmap(result);
    }
}
