package com.misrotostudio.jeparticipeapp.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.misrotostudio.jeparticipeapp.R;
import com.misrotostudio.jeparticipeapp.app.AppConfig;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Created by othmaneelmassari on 21/07/15.
 */
public class CustomArrayAdaptater extends SimpleAdapter {
        private List<? extends Map<String, ?>> data;
        private String[] from;
        private int layout;
        private int[] to;
        private LayoutInflater mInflater;
        private String url_image;
        private SQLiteHandler db;
        private ImageLoader imageLoader;
        private DisplayImageOptions options;

        public CustomArrayAdaptater(Context context, List<? extends Map<String, ?>> data,
                                     int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            layout = resource;
            this.data = data;
            this.from = from;
            this.to = to;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = ImageLoader.getInstance();

            options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(null)
                    .showImageOnFail(null)
                    .showImageOnLoading(null).build();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return this.createViewFromResource(position, convertView, parent, layout);
        }

        private View createViewFromResource(int position, View convertView,
                                            ViewGroup parent, int resource) {
            View v;
            if (convertView == null) {
                v = mInflater.inflate(resource, parent, false);
            } else {
                v = convertView;
            }

            this.bindView(position, v);

            return v;
        }


        private void bindView(int position, View view) {
            final Map<String, ?> dataSet = data.get(position);

            if (dataSet == null) {
                return;
            }

            final ViewBinder binder = super.getViewBinder();
            final int count = to.length;



            for (int i = 0; i < count; i++) {
                final View v = view.findViewById(to[i]);
                if (v != null) {
                    final Object data = dataSet.get(from[i]);
                    //final DataEventElement ev = (DataEventElement) dataSet.get(from[i]);
                    //Log.d("DATA TEST", data.toString());
                    //Log.d("CHERQI TEST", url_image.toString());
                    //nouveau get frommysqllit
                    url_image = dataSet.get(from[4]).toString();
                    String text = data == null ? "" : data.toString();

                    if (text == null) {
                        text = "";
                    }

                    //Log.d("DATA TEST IMAGE", url_image);

                    boolean bound = false;
                    if (binder != null) {
                        bound = binder.setViewValue(v, data, text);
                    }

                    if (!bound) {
                        if (v instanceof Checkable) {
                            if (data instanceof Boolean) {
                                ((Checkable) v).setChecked((Boolean) data);
                            } else if (v instanceof TextView) {
                                // Note: keep the instanceof TextView check at the bottom of these
                                // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                                setViewText((TextView) v, text);
                            } else {
                                throw new IllegalStateException(v.getClass().getName() +
                                        " should be bound to a Boolean, not a " +
                                        (data == null ? "<unknown type>" : data.getClass()));
                            }
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, text);
                        } else if (v instanceof ImageView) {
                            //new ImageDownloader((ImageView) v).execute(AppConfig.URL_IMAGE + url_image);

                            imageLoader.displayImage(AppConfig.URL_IMAGE + url_image, (ImageView) v, options);

/*

                            if (data instanceof Integer) {
                                setViewImage((ImageView) v, (Integer) data);
                            } else if (data instanceof Bitmap){
                                setViewImage((ImageView) v, (Bitmap)data);
                            } else {
                                setViewImage((ImageView) v, text);
                            }
                            */
                        } else {
                            throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                    " view that can be bounds by this SimpleAdapter");
                        }
                    }
                }
            }
        }

        private void setViewImage(ImageView v, Bitmap bmp){
            v.setImageBitmap(bmp);
        }
}
