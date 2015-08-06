package com.misrotostudio.jeparticipeapp.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by othmaneelmassari on 17/07/15.
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    Bitmap b;

    public ImageDownloader(){
        this.bmImage = null;
    }

    public ImageDownloader(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon = BitmapFactory.decodeStream(in);
            b = mIcon;

        } catch (Exception e) {
            //Log.e("Error", e.getMessage());
            Log.d("ImageDownloader", "No Image found: " + url);
        }
        return mIcon;
    }

    public Bitmap getB(){
        return b;
    }

    protected void onPostExecute(Bitmap result) {
            if(bmImage!=null){
                bmImage.setImageBitmap(result);
            }
    }
}