package com.example.komputer.discogify.Models;

import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by Komputer on 24/09/2016.
 */
public class Artist {


    private String mProfile;
    private String mName;
    private String mType;
    private String mUrl;
    private int mId; // ID fetched from JSON >> used only for ArtistFetchTask(id) to fetch JSON for particular artist
    private UUID mUuid; // ID of the artist put to database

    public Artist(){

    }

    public Artist(String name, String type, String url, int id){
        this(UUID.randomUUID());
        mName = name;
        mType = type;
        mUrl = url;
        mId = id;
    }

    public Artist(UUID uuid){
        mUuid = uuid;
    }


    public String toString(){
        return mName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getType(){
        return mType;
    }

    public void setType(String type){
        this.mType = type;
    }

    public String getUrl(){
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getProfile() {
        return mProfile;
    }

    public void setProfile(String mProfile) {
        this.mProfile = mProfile;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public UUID getUuid() {
        return mUuid;
    }

    public void setUuid(UUID mUuid) {
        this.mUuid = mUuid;
    }

    public String getFormattedName(){
        return this.getName().replaceAll(" ", "-");
    }


}
