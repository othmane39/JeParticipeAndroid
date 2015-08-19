package com.misrotostudio.jeparticipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.misrotostudio.jeparticipeapp.app.AppConfig;
import com.misrotostudio.jeparticipeapp.app.AppController;
import com.misrotostudio.jeparticipeapp.helper.CustomArrayAdaptater;
import com.misrotostudio.jeparticipeapp.helper.DataEventElement;
import com.misrotostudio.jeparticipeapp.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by othmaneelmassari on 18/08/15.
 */
public class AVenirFragment extends Fragment {
    private ListView listEv;
    private ArrayList<HashMap<String, ?>> eventList;
    private SQLiteHandler db;
    private ListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.avenir_fragment_object, container, false);

        db = new SQLiteHandler(getActivity().getApplicationContext());
        listEv = (ListView) rootView.findViewById(R.id.listViewAVenirEvent);
        eventList = db.getDataEventDetails();
        Log.d("Othmane", ""+ db.getEventRowCount());
        adapter = new CustomArrayAdaptater(getActivity().getApplicationContext(), eventList, R.layout.list_item,
                new String[]{"name", "date_debut", "lieu", "image", "image_url"}, new int[]{R.id.name, R.id.date_debut, R.id.lieu, R.id.image});
        listEv.setAdapter(adapter);
        justifyListViewHeightBasedOnChildren(listEv);

        listEv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),
                //        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                //        .show();
                DataEventElement ev = new DataEventElement(eventList.get(position));
                Intent intent = new Intent(getActivity(), EventActivity.class);
                intent.putExtra("eventS", ev);
                startActivity(intent);

                //Log.d("TAG", ev.toString());
            }
        });

        return rootView;
    }

    public void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));

        listView.setLayoutParams(par);
        listView.requestLayout();
    }
}
