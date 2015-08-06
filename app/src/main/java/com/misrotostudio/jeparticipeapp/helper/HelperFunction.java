package com.misrotostudio.jeparticipeapp.helper;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by othmaneelmassari on 17/07/15.
 */
public class HelperFunction {
    public boolean loadImageFromURL(String fileUrl,
                                    ImageView iv){
        try {

            URL myFileUrl = new URL(fileUrl);
            HttpURLConnection conn =
                    (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            iv.setImageBitmap(BitmapFactory.decodeStream(is));


            Log.d("IMAAAAAAAGE", "Ca passe");

            return true;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("IMAAAAAAAGE", "Ca passe pas");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("IMAAAAAAAGE", "Ca passe pas");
        }


        return false;
    }


}
