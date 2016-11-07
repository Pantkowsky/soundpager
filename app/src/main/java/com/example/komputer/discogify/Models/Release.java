package com.example.komputer.discogify.Models;

/**
 * Created by Komputer on 23/09/2016.
 */
public class Release {

    private String mTitle;
    private String mReleaseDate;
    private String mAddedDate;
    private String mTrackTitle;
    private String mFormat;
    private String mId;
    private String mDuration;
    private String mPosition;
    private String mProducer;

    public String toString(){
        return mTitle;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getTrackTitle() {
        return mTrackTitle;
    }

    public void setTrackTitle(String mTrackTitle) {
        this.mTrackTitle = mTrackTitle;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public String getPosition() {
        return mPosition;
    }

    public void setPosition(String mPosition) {
        this.mPosition = mPosition;
    }

    public String getAddedDate() {
        return mAddedDate;
    }

    public void setAddedDate(String mAddedDate) {
        this.mAddedDate = mAddedDate;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getFormat(){
        return mFormat;
    }

    public void setFormat(String format){
        this.mFormat = format;
    }

    public String getProducer(){
        return mProducer;
    }

    public void setProducer(String producer){
        this.mProducer = producer;
    }
}
