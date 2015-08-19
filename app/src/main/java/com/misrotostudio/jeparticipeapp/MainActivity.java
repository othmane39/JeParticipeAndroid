package com.misrotostudio.jeparticipeapp;

/**
 * Created by othmaneelmassari on 09/07/15.
 */


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
    import com.misrotostudio.jeparticipeapp.helper.DataEventElement;
    import com.misrotostudio.jeparticipeapp.helper.ImageDownloader;
    import com.misrotostudio.jeparticipeapp.helper.SQLiteHandler;
    import com.misrotostudio.jeparticipeapp.helper.SessionManager;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    import android.app.Activity;
    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.AdapterView;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.ListAdapter;
    import android.widget.ListView;
    import android.widget.SimpleAdapter;
    import android.widget.TextView;
    import android.widget.Toast;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;


public class MainActivity extends Activity {

    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;

    private ListView listEv;
    private ListView listPa;
    ArrayList<HashMap<String, ?>> eventList;
    ArrayList<HashMap<String, ?>> participeList;

    private SQLiteHandler db;
    private SessionManager session;

    private CallbackManager callbackManager;
    private LoginManager manager;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        listPa = (ListView) findViewById(R.id.listViewParticipeEvent);
        listEv = (ListView) findViewById(R.id.listViewComingEvent);

        btnLogout = (Button) findViewById(R.id.btnLogout);




        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();


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

        //

        //Display HashMap on layout


        String name = user.get("name");
        String email = user.get("email");
        String uid = user.get("uid");
        getEvents(uid);



        //Fetching event details from sqlite
        //HashMap<String, String> event = db.getDataEventDetails();

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);



        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        listEv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),
                //        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                //        .show();
                DataEventElement ev = new DataEventElement(eventList.get(position));
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                intent.putExtra("eventS", ev);
                startActivity(intent);
                //Log.d("TAG", ev.toString());
            }
        });


        listPa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),
                //        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                //        .show();
                DataEventElement ev = new DataEventElement(participeList.get(position));
                Intent intent = new Intent(MainActivity.this, EventRemoveActivity.class);
                intent.putExtra("eventS", ev);
                startActivity(intent);
                //Log.d("TAG", ev.toString());
            }
        });


        showList();



    }

    @Override
    protected void onResume() {
        super.onResume();

        db = new SQLiteHandler(getApplicationContext());

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        String uid = user.get("uid");

        getEvents(uid);



        listEv = (ListView) findViewById(R.id.listViewComingEvent);
        listPa = (ListView) findViewById(R.id.listViewParticipeEvent);


        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        listEv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),
                //        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                //        .show();
                DataEventElement ev = new DataEventElement(eventList.get(position));
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                intent.putExtra("eventS", ev);
                startActivity(intent);

                //Log.d("TAG", ev.toString());
            }
        });
/*
        listPa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),
                //        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                //        .show();
                DataEventElement ev = new DataEventElement(participeList.get(position));
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                intent.putExtra("eventS", ev);
                startActivity(intent);
                Log.d("TAG", ev.toString());
            }
        });
        */

    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false, false);

        db.deleteUsers();
        db.deleteDataEvent();
        db.deleteDataParticipe();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
                        }

                        showList();

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


    protected void showList(){
        eventList = db.getDataEventDetails();
        participeList = db.getDataParticipeDetails();

        ListAdapter adapter ;

            adapter = new CustomArrayAdaptater(MainActivity.this, eventList, R.layout.list_item,
                    new String[]{"name", "date_debut", "lieu", "image", "image_url"}, new int[]{R.id.name, R.id.date_debut, R.id.lieu, R.id.image});
        //adapter = new SimpleAdapter(MainActivity.this, eventList, R.layout.list_item,
          //      new String[]{"name", "date_debut", "lieu"}, new int[]{R.id.name, R.id.date_debut, R.id.lieu});

        ListAdapter adapterPa = new CustomArrayAdaptater(MainActivity.this, participeList, R.layout.list_item,
                new String[]{"name", "date_debut", "lieu", "image", "image_url"}, new int[]{R.id.name, R.id.date_debut, R.id.lieu, R.id.image});
        listPa.setAdapter(adapterPa);
        listEv.setAdapter(adapter);
        justifyListViewHeightBasedOnChildren(listPa);
        justifyListViewHeightBasedOnChildren(listEv);


        Log.d(TAG, "List showed");
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
        Log.d("Missa", "NotFrag " + par.height);
        listView.setLayoutParams(par);
        listView.requestLayout();
    }


}