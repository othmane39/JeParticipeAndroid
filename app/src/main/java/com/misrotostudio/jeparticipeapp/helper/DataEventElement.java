package com.misrotostudio.jeparticipeapp.helper;

import android.os.Parcelable;
import android.os.Parcel;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by othmaneelmassari on 10/07/15.
 */
public class DataEventElement implements Parcelable {

    private String nom;
    private String type;
    private String dateD;
    private String heureD;
    private String dateF;
    private String heureF;
    private String lieu;
    private String description;
    private String image_url;

    public DataEventElement(){

    }

    public DataEventElement(HashMap<String, ?> ev){
        nom = (String) ev.get("name");
        type = (String) ev.get("type");
        dateD = (String) ev.get("date_debut");
        heureD = (String) ev.get("heure_debut");
        dateF = (String) ev.get("date_fin");
        heureF = (String) ev.get("heure_fin");
        lieu = (String) ev.get("lieu");
        description = (String) ev.get("description");
        image_url = (String) ev.get("image_url");

        Log.d("DataEventElement: ", "object created with success");
    }

    public DataEventElement(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // We just need to write each field into the
        // parcel. When we read from parcel, they
        // will come back in the same order
        dest.writeString(nom);
        dest.writeString(type);
        dest.writeString(dateD);
        dest.writeString(heureD);
        dest.writeString(dateF);
        dest.writeString(heureF);
        dest.writeString(lieu);
        dest.writeString(description);
        dest.writeString(image_url);
    }

    private void readFromParcel(Parcel in) {

        // We just need to read back each
        // field in the order that it was
        // written to the parcel
        nom = in.readString();
        type = in.readString();
        dateD = in.readString();
        heureD = in.readString();
        dateF = in.readString();
        heureF = in.readString();
        lieu = in.readString();
        description = in.readString();
        image_url = in.readString();

    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public DataEventElement createFromParcel(Parcel in) {
                    return new DataEventElement(in);
                }

                public DataEventElement[] newArray(int size) {
                    return new DataEventElement[size];
                }
            };


    public String getDateD() {
        return dateD;
    }

    public String getDateF() {
        return dateF;
    }

    public String getDescription() {
        return description;
    }

    public String getHeureD() {
        return heureD;
    }

    public String getHeureF() {
        return heureF;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getLieu() {
        return lieu;
    }

    public String getNom() {
        return nom;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return nom +" " +type +" " + dateD +" " + dateF +" " + heureD +" " + heureF +" " + lieu +" " + description +" " + image_url;
    }
}
