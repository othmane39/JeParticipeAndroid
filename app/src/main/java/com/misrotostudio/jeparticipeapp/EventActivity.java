package com.misrotostudio.jeparticipeapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.misrotostudio.jeparticipeapp.app.AppConfig;
import com.misrotostudio.jeparticipeapp.app.AppController;
import com.misrotostudio.jeparticipeapp.helper.DataEventElement;
import com.misrotostudio.jeparticipeapp.helper.HelperFunction;
import com.misrotostudio.jeparticipeapp.helper.SQLiteHandler;
import com.misrotostudio.jeparticipeapp.helper.ImageDownloader;
import com.misrotostudio.jeparticipeapp.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
*/
/**
 * Created by othmaneelmassari on 15/07/15.
 */
public class EventActivity extends Activity {

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

    private SessionManager session;
    private SQLiteHandler db;
    private HelperFunction hf;

    private HashMap<String, String> user;

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private CallbackManager callbackManager;
    private LoginManager manager;
    private ShareDialog shareDialog;
    private boolean enableShare = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Bundle b = getIntent().getExtras();
        event = b.getParcelable("eventS");
        Log.d("Parcelable: ", event.toString());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());

/*
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

                    enableShare = true;
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

*/
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
                        pDialog.setMessage("Enregistrement ...");
                        showDialog();
                        participe(event, user);

                        if (session.isFacebookLoggedIn())
                            publishImage("Je Participe à " + event.getNom(), event.getDescription(), "www.crescendo.ma");

                        finish();

                    }
                });

    }

    private void publishImage(String title, String description, String url){
        //Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.logosmall);

        //SharePhoto photo = new SharePhoto.Builder()
        //        .setBitmap(image)
        //        .setCaption(msg)
         //       .build();
        shareDialog = new ShareDialog(this);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(title)
                    .setContentDescription(
                            description)
                    .setContentUrl(Uri.parse(url))
                    .build();

            //shareDialog.show(linkContent);
            ShareApi.share(linkContent, null);
        }
        //ShareLinkContent content = new ShareLinkContent.Builder()
        //        .setContentUrl(Uri.parse("https://developers.facebook.com"))
        //        .build();
        //SharePhotoContent content = new SharePhotoContent.Builder()
        //        .addPhoto(photo)
         //       .build();
        //ShareApi.share(content, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,    Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }


    private void participe(final DataEventElement event ,final HashMap<String, String> user){

        // Tag used to cancel the request
        String tag_string_req = "req_participe";

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
                        db.deleteDataEvent(event.getNom(), event.getDateD());
                        db.addDataParticipe(event.getNom(), event.getType(), event.getDateD(), event.getHeureD(), event.getDateF(), event.getHeureF(), event.getLieu(), event.getDescription(), event.getImage_url());

                        String errorMsg = event.getNom() + "a été ajouté a vos évenements";
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

                    Log.e(TAG, "Participation Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "participate");
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
