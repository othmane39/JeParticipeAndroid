package com.misrotostudio.jeparticipeapp.helper;

/**
 * Created by othmaneelmassari on 09/07/15.
 */


    import android.content.Context;
    import android.content.SharedPreferences;
    import android.content.SharedPreferences.Editor;
    import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String KEY_FB_LOGIN = "isFbLogIn";

    private static final String KEY_FB_SHARE = "isFbShare";


    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }


    public void setLogin(boolean isLoggedIn, boolean isFbLogin) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putBoolean(KEY_FB_LOGIN, isFbLogin);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!" + "isFB: "+ isFbLogin);
    }

    public void setShare(boolean isShare){
        editor.putBoolean(KEY_FB_SHARE, isShare);

        editor.commit();
    }


    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public boolean isFacebookLoggedIn(){
        return pref.getBoolean(KEY_FB_LOGIN, false);
    }

    public boolean isFacebookShare(){
        return pref.getBoolean(KEY_FB_SHARE, false);
    }
}