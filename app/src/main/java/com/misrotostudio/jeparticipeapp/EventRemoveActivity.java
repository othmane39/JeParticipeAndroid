package com.misrotostudio.jeparticipeapp;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.misrotostudio.jeparticipeapp.app.AppConfig;
import com.misrotostudio.jeparticipeapp.app.AppController;
import com.misrotostudio.jeparticipeapp.helper.DataEventElement;
import com.misrotostudio.jeparticipeapp.helper.HelperFunction;
import com.misrotostudio.jeparticipeapp.helper.ImageDownloader;
import com.misrotostudio.jeparticipeapp.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class EventRemoveActivity extends ActionBarActivity {

    private ImageView image;
    private TextView nom;
    private TextView type;
    private TextView date;
    private TextView dateDetail;
    private TextView lieu;
    private TextView description;
    private Button jeParticipe_btn;
    private DataEventElement event;

    private ProgressDialog pDialog;

    private SQLiteHandler db;
    private HelperFunction hf;

    private HashMap<String, String> user;

    private static final String TAG = RegisterActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_remove);

        Bundle b = getIntent().getExtras();
        event = b.getParcelable("eventS");
        Log.d("Parcelable: ", event.toString());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from sqlite
        user = db.getUserDetails();

        //Views
        image = (ImageView) findViewById(R.id.image);
        nom = (TextView) findViewById(R.id.nom);
        type = (TextView) findViewById(R.id.type);
        date = (TextView) findViewById(R.id.date);
        dateDetail = (TextView) findViewById(R.id.date_detail);
        lieu = (TextView) findViewById(R.id.lieu);
        description = (TextView) findViewById(R.id.description);
        jeParticipe_btn = (Button) findViewById(R.id.btnJeParticipe);

        //Set Views
        nom.setText(event.getNom());
        type.setText(event.getType());
        date.setText("Du " + event.getDateD() + " au " + event.getDateF());
        dateDetail.setText("de " + event.getHeureD() + " à " + event.getHeureF());
        lieu.setText(event.getLieu());
        description.setText(event.getDescription());

        try {
            new ImageDownloader(image).execute(AppConfig.URL_IMAGE + event.getImage_url());
        }catch(Exception e){
            e.printStackTrace();
        }

        jeParticipe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.setMessage("Veuillez patienter ...");
                showDialog();
                removeParticipe(event, user);

                finish();

            }
        });
    }

    private void removeParticipe(final DataEventElement event ,final HashMap<String, String> user){

        // Tag used to cancel the request
        String tag_string_req = "req_ne_participe";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, ": " + response.toString());

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        db.deleteDataParticipe(event.getNom(), event.getDateD());
                        db.addDataEvent(event.getNom(), event.getType(), event.getDateD(), event.getHeureD(), event.getDateF(), event.getHeureF(), event.getLieu(), event.getDescription(), event.getImage_url());

                        String errorMsg = event.getNom() + "a été supprimé de vos évenements";
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                    else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "DontParticipation Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "removeParticipate");
                params.put("id_user", user.get("uid"));
                params.put("name_event", event.getNom());
                params.put("dateD_event", event.getDateD());

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
