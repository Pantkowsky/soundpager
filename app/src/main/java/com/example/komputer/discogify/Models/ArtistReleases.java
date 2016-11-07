package com.example.komputer.discogify.Models;


/**
 * Created by Komputer on 14/09/2016.
 */
public class ArtistReleases{
    private String mTitle;
    private String mLabel;
    private String mProducer;
    private String mYear;
    private String mDateAdded;
    private String mDateReleased;
    private String mId;
    private String mFormat;
    private String mResource;
    private String mMainRelease;
    private String mType;


    @Override
    public String toString(){
        return mTitle;
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String mLabel) {
        this.mLabel = mLabel;
    }

    public String getProducer() {
        return mProducer;
    }

    public void setProducer(String mProducer) {
        this.mProducer = mProducer;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String mYear) {
        this.mYear = mYear;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getFormat() {
        return mFormat;
    }

    public void setFormat(String mFormat) {
        this.mFormat = mFormat;
    }

    public String getResource() {
        return mResource;
    }

    public void setResource(String mResource) {
        this.mResource = mResource;
    }

    public String getType(){
        return mType;
    }

    public void setType(String type){
        this.mType = type;
    }

    public String getMainRelease(){
        return mMainRelease;
    }

    public void setMainRelease(String mainRelease){
        this.mMainRelease = mainRelease;
    }

    public String getDateAdded(){
        return mDateAdded;
    }

    public void setDateAdded(String dateAdded){
        this.mDateAdded = dateAdded;
    }

    public String getDateReleased(){
        return mDateReleased;
    }

    public void setDateReleased(String dateReleased){
        this.mDateReleased = dateReleased;
    }

}
