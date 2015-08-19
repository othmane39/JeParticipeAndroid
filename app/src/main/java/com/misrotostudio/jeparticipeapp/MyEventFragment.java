package com.misrotostudio.jeparticipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.misrotostudio.jeparticipeapp.helper.CustomArrayAdaptater;
import com.misrotostudio.jeparticipeapp.helper.DataEventElement;
import com.misrotostudio.jeparticipeapp.helper.SQLiteHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by othmaneelmassari on 18/08/15.
 */
public class MyEventFragment extends Fragment{
    private ListView listPa;
    ArrayList<HashMap<String, ?>> participeList;

    private SQLiteHandler db;
    private ListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.myevent_fragment_object, container, false);

        listPa = (ListView) rootView.findViewById(R.id.listViewMyEvent);

        db = new SQLiteHandler(getActivity().getApplicationContext());
        listPa = (ListView) rootView.findViewById(R.id.listViewMyEvent);
        participeList = db.getDataParticipeDetails();
        Log.d("Othmane", "" + db.getEventRowCount());
        adapter = new CustomArrayAdaptater(getActivity().getApplicationContext(), participeList, R.layout.list_item,
                new String[]{"name", "date_debut", "lieu", "image", "image_url"}, new int[]{R.id.name, R.id.date_debut, R.id.lieu, R.id.image});


        listPa.setAdapter(adapter);
        justifyListViewHeightBasedOnChildren(listPa);

        listPa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),
                //        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                //        .show();
                DataEventElement ev = new DataEventElement(participeList.get(position));
                Intent intent = new Intent(getActivity(), EventRemoveActivity.class);
                intent.putExtra("eventS", ev);
                startActivity(intent);
                //Log.d("TAG", ev.toString());
            }
        });



        //listPa.setAdapter(adapterPa);
        //justifyListViewHeightBasedOnChildren(listPa);

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
