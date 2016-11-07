package com.example.komputer.discogify.Models;

/**
 * Created by Komputer on 14/09/2016.
 */
public class SearchQuery {

    private String mTitle;
    private String mId;
    private String mResource;
    private String mThumb;
    private String mType;
    private String mUri;

    @Override
    public String toString(){
        return mTitle;
    }

    public String getName() {
        return mTitle;
    }

    public String getId() {
        return mId;
    }

    public String getResource() {
        return mResource;
    }

    public String getThumb() {
        return mThumb;
    }

    public String getType() {
        return mType;
    }

    public void setName(String mName) {
        this.mTitle = mName;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public void setResource(String mResource) {
        this.mResource = mResource;
    }

    public void setThumb(String mThumb) {
        this.mThumb = mThumb;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String mUri) {
        this.mUri = mUri;
    }

}
