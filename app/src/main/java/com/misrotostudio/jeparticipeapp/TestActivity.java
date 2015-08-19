package com.misrotostudio.jeparticipeapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.misrotostudio.jeparticipeapp.app.AppConfig;
import com.misrotostudio.jeparticipeapp.app.AppController;
import com.misrotostudio.jeparticipeapp.helper.CustomArrayAdaptater;
import com.misrotostudio.jeparticipeapp.helper.SQLiteHandler;
import com.misrotostudio.jeparticipeapp.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class TestActivity extends FragmentActivity {

    private SQLiteHandler db;
    private SessionManager session;
    private String uid;

    private CallbackManager callbackManager;
    private LoginManager manager;


    private static final String TAG = TestActivity.class.getSimpleName();

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Create an adapter that when requested, will return a fragment representing an object in
        // the collection.
        //
        // ViewPager and its adapters use support library fragments, so we must use
        // getSupportFragmentManager.
        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());

        // Set up action bar.
        //final ActionBar actionBar = getActionBar();

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        //actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the ViewPager, attaching the adapter.


        db = new SQLiteHandler(getApplicationContext());

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            //logoutUser();
        }

        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");

        getEvents(uid);


        if (session.isFacebookLoggedIn()) {
            FacebookSdk.sdkInitialize(getApplicationContext());

            callbackManager = CallbackManager.Factory.create();

            List<String> permissionNeeds = Arrays.asList("publish_actions");
            //try{
            manager = LoginManager.getInstance();

            manager.logInWithPublishPermissions(this, permissionNeeds);

            manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    session.setShare(true);
                    Log.d("Facebook", "Sharing Success");
                }

                @Override
                public void onCancel() {
                    Log.d("Facebook", "Sharing Cancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.d("Facebook", "Sharing Error onError");
                }
            });
        }

        String name = user.get("name");
        String email = user.get("email");



        //getEvents(uid);
    }

    @Override
    protected void onResume(){
        super.onResume();
        getEvents(uid);
    }

    private void getEvents(final String uid) {

        // Tag used to cancel the request
        String tag_string_req = "req_login";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Event Response: " + response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        db.deleteDataEvent();
                        db.deleteDataParticipe();

                        JSONArray coming_events = jObj.getJSONArray("coming_event");
                        JSONArray my_event = jObj.getJSONArray("my_event");

                        //Log.d("Othmane :", coming_events.toString());
                        //Log.d("MY EVENTS :", my_event.toString());


                        JSONObject event;
                        for(int i=0; i<coming_events.length(); i++){
                            event = coming_events.getJSONObject(i);
                            //Log.d("ObjEvent", event.toString());
                            db.addDataEvent(event.getString("nom"), event.getString("type"), event.getString("date_debut"), event.getString("heure_debut"),
                                    event.getString("date_fin"), event.getString("heure_fin"), event.getString("lieu"), event.getString("description"),
                                    event.getString("image"), event.getString("event_url"), event.getString("buy_url"));
                        }

                        for(int i=0; i<my_event.length(); i++){
                            event = my_event.getJSONObject(i);
                            //Log.d("ObjParticipe", event.toString());
                            db.addDataParticipe(event.getString("nom"), event.getString("type"), event.getString("date_debut"), event.getString("heure_debut"),
                                    event.getString("date_fin"), event.getString("heure_fin"), event.getString("lieu"), event.getString("description"),
                                    event.getString("image"), event.getString("event_url"), event.getString("buy_url"));
                            Log.d(TAG, "NEv:" + db.getEventRowCount());
                        }

                        mViewPager = (ViewPager) findViewById(R.id.pager);
                        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
/*
                        //showList();
                        eventList = db.getDataEventDetails();
                        participeList = db.getDataParticipeDetails();


                        adapter = new CustomArrayAdaptater(getApplicationContext(), eventList, R.layout.list_item,
                                new String[]{"name", "date_debut", "lieu", "image", "image_url"}, new int[]{R.id.name, R.id.date_debut, R.id.lieu, R.id.image});
                        //adapter = new SimpleAdapter(MainActivity.this, eventList, R.layout.list_item,
                        //      new String[]{"name", "date_debut", "lieu"}, new int[]{R.id.name, R.id.date_debut, R.id.lieu});

                        adapterPa = new CustomArrayAdaptater(getApplicationContext(), participeList, R.layout.list_item,
                                new String[]{"name", "date_debut", "lieu", "image", "image_url"}, new int[]{R.id.name, R.id.date_debut, R.id.lieu, R.id.image});

*/

                        Log.d(TAG, "List showed");
                    }
                    else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "Event Fetch Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();


            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "events"); //Define request type in index.php
                params.put("uid", uid);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, MainActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            //Un switch pour a venir et my event
            switch (i){
                case 0:
                    Fragment fragment = new MyEventFragment();
                    //Bundle args = new Bundle();
                    //args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1); // Our object is just an integer :-P
                    //fragment.setArguments(args);
                    return fragment;
                case 1:
                    Fragment fragment1 = new AVenirFragment();
                    return fragment1;
            }
            return new DemoObjectFragment();
        }

        @Override
        public int getCount() {
            // For this contrived example, we have a 100-object collection.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Mes évenements";
                case 1:
                    return "À venir";
                default:
                    return "error";
            }
        }
    }






    public static class DemoObjectFragment extends Fragment {

        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    Integer.toString(args.getInt(ARG_OBJECT)));
            return rootView;
        }
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