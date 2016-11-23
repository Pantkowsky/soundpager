package com.example.komputer.discogify;


import android.net.Uri;
import android.util.Log;

import com.example.komputer.discogify.Models.Artist;
import com.example.komputer.discogify.Models.ArtistReleases;
import com.example.komputer.discogify.Models.Release;
import com.example.komputer.discogify.Models.SearchQuery;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DiscogsFetchr {

    private static final String TAG = "DiscogsFetchr";
    private static final String API_TOKEN = "hmleVSrYppOAgGBspReXzZqwKheMRVtjInjOkIXn";


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException((connection.getResponseMessage() + ": with " + urlSpec));
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    public List<ArtistReleases> fetchReleases(String link){

        List<ArtistReleases> artistReleases = new ArrayList<>();

        try{
            String url = Uri.parse(link + "/releases?per_page=100")
                    .buildUpon()
                    .build()
                    .toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseReleases(artistReleases, jsonBody);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }catch (IOException ioe){
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return artistReleases;
    }

    public List<ArtistReleases> fetchRecentReleases(String link){

        List<ArtistReleases> artistReleases = new ArrayList<>();

        try{
            String url = Uri.parse(link + "/releases?per_page=100&page=1")
                    .buildUpon()
                    .build()
                    .toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseReleases(artistReleases, jsonBody);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }catch (IOException ioe){
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        List<ArtistReleases> filteredList = filter(artistReleases);
        return filteredList;
    }

    public List<Release> fetchReleaseContents(String link){

        List<Release> releases = new ArrayList<>();

        try{
            String url = Uri.parse(link)
                    .buildUpon()
                    .build()
                    .toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseReleaseContents(releases, jsonBody);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }catch (IOException ioe){
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return releases;
    }

    public Artist fetchInfo(int id, String type) {

        Artist artist = new Artist();
        String artistId = String.valueOf(id);

        try{
            String url = Uri.parse("https://api.discogs.com/" + type + "/" + artistId)
                    .buildUpon()
                    .build()
                    .toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseArtistInfo(artist, jsonBody);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }catch (IOException ioe){
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return artist;
    }

    public Release fetchReleaseInfo(String mainReleaseId){

        Release release = new Release();

        try{
            String url = Uri.parse("https://api.discogs.com/releases/" + mainReleaseId)
                    .buildUpon()
                    .build()
                    .toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseReleaseInfo(release, jsonBody);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }catch (IOException ioe){
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return release;
    }

    public Release fetchMasterReleaseInfo(String link){

        Release release = new Release();

        try{
            String url = Uri.parse(link)
                    .buildUpon()
                    .build()
                    .toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseMasterReleaseInfo(release, jsonBody);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }catch (IOException ioe){
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return release;
    }

    public List<SearchQuery> fetchSearchQuery(){

        List<SearchQuery> searchQueries = new ArrayList<>();

        try{
            String url = Uri.parse("https://api.discogs.com/database/search")
                    .buildUpon()
                    .appendQueryParameter("q", SearchFragment.getArtistName())
                    .appendQueryParameter("token", API_TOKEN)
                    .build().toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseSearchQuery(searchQueries, jsonBody);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }catch (IOException ioe){
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return searchQueries;
    }

    public List<ArtistReleases> fetchVersions(String link){

        List<ArtistReleases> artistReleases = new ArrayList<>();

        try{
            String url = Uri.parse(link + "/versions")
                    .buildUpon()
                    .build()
                    .toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseVersions(artistReleases, jsonBody);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }catch (IOException ioe){
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return artistReleases;
    }

    private List<ArtistReleases> filter(List<ArtistReleases> list) throws NumberFormatException{
        List<ArtistReleases> filteredList = new ArrayList<>();
        List<ArtistReleases> versions = new ArrayList<>();
        Release release;

        List<String> months = new ArrayList<>();
        months.add("Jan");
        months.add("Feb");
        months.add("Mar");
        months.add("Apr");
        months.add("May");
        months.add("Jun");
        months.add("Jul");
        months.add("Aug");
        months.add("Sep");
        months.add("Oct");
        months.add("Nov");
        months.add("Dec");

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        try{
            for(ArtistReleases releases: list){
                if (Integer.parseInt(releases.getYear()) >= currentYear){
                    filteredList.add(releases);
                }
            }
        }catch(NumberFormatException nfe){

        }

        List<ArtistReleases> sortedList = new ArrayList<>();
        String type = "";

        for(int i = 0; i < filteredList.size(); i++){
            if(filteredList.get(i).getMainRelease() == null){
                release = fetchReleaseInfo(filteredList.get(i).getId());
                if(format(release.getAddedDate()).substring(3, 6).equals(months.get(currentMonth))){
                    filteredList.get(i).setDateAdded(format(release.getAddedDate()));
                    filteredList.get(i).setDateReleased(release.getReleaseDate());
                    sortedList.add(filteredList.get(i));
                }
            }else{
                versions = fetchVersions(filteredList.get(i).getResource());
                type = filteredList.get(i).getType();
            }
        }
        for(int y = 0; y < versions.size(); y++){
            release = fetchMasterReleaseInfo(versions.get(y).getResource());
            if(format(release.getAddedDate()).substring(3, 6).equals(months.get(currentMonth))){
                versions.get(y).setTitle(release.getTitle());
                versions.get(y).setProducer(release.getProducer());
                versions.get(y).setDateAdded(format(release.getAddedDate()));
                versions.get(y).setDateReleased(release.getReleaseDate());
                versions.get(y).setType(type);
                sortedList.add(versions.get(y));
            }
        }
        return sortedList;
    }

    private String format(String dateString){

        String sub = dateString.substring(0, 10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try{
            date = sdf.parse(sub);
            sdf.applyPattern("dd/MMM/yyyy");
            sdf.format(date);
        }catch (ParseException pe){
            pe.printStackTrace();
        }
        String dateFormatted = date.toString();
        String monthSubstring = dateFormatted.substring(4, 7);
        String daySubstring = dateFormatted.substring(8, 10);
        String yearSubstring = dateFormatted.substring(30, 34);
        return daySubstring + " " + monthSubstring + " " + yearSubstring;
    }

    private void parseArtistInfo(Artist artist, JSONObject jsonBody) throws IOException, JSONException {

        String artistProfileJsonString = (String)jsonBody.get("profile");
        String artistNameJsonString = (String)jsonBody.get("name");
        String artistUrlJsonString = (String)jsonBody.get("resource_url");
        int artistIdJsonString = (int)jsonBody.get("id");
        artist.setProfile(artistProfileJsonString);
        artist.setName(artistNameJsonString);
        artist.setUrl(artistUrlJsonString);
        artist.setId(artistIdJsonString);
    }

    private void parseMasterReleaseInfo(Release release, JSONObject jsonBody) throws IOException, JSONException {

        String releaseTitleJsonString = (String)jsonBody.get("title");
        String releaseDateFormattedJsonString = (String)jsonBody.get("released_formatted");
        String releaseDateAddedJsonString = (String)jsonBody.get("date_added");

        JSONArray formatJsonArray = jsonBody.getJSONArray("formats");

        for (int i = 0; i < formatJsonArray.length(); i++){
            JSONObject formatJsonObject = formatJsonArray.getJSONObject(i);
            release.setFormat(formatJsonObject.getString("name"));
        }

        JSONArray artistNameJsonArray = jsonBody.getJSONArray("artists");

        for (int i = 0; i < artistNameJsonArray.length(); i++){
            JSONObject artistNameJsonObject = artistNameJsonArray.getJSONObject(i);

            release.setProducer(artistNameJsonObject.getString("name"));
        }
        release.setTitle(releaseTitleJsonString);
        release.setReleaseDate(releaseDateFormattedJsonString);
        release.setAddedDate(releaseDateAddedJsonString);
    }

    private void parseReleases(List<ArtistReleases> artistReleases, JSONObject jsonBody) throws IOException, JSONException {

        JSONArray artistReleasesJsonArray = jsonBody.getJSONArray("releases");

        for (int i = 0; i < artistReleasesJsonArray.length(); i++){
            JSONObject artistReleasesJsonObject = artistReleasesJsonArray.getJSONObject(i);

            ArtistReleases releases = new ArtistReleases();
            releases.setId(artistReleasesJsonObject.getString("id"));
            if(artistReleasesJsonObject.has("format")){
                releases.setFormat(artistReleasesJsonObject.getString("format"));
            }
            releases.setTitle(artistReleasesJsonObject.getString("title"));
            releases.setProducer(artistReleasesJsonObject.getString("artist"));
            releases.setResource(artistReleasesJsonObject.getString("resource_url"));
            if(artistReleasesJsonObject.has("year")){
                releases.setYear(artistReleasesJsonObject.getString("year"));
            }
            if(artistReleasesJsonObject.has("type")){
                releases.setType(artistReleasesJsonObject.getString("type"));
            }
            if(artistReleasesJsonObject.has("main_release")){
                releases.setMainRelease(artistReleasesJsonObject.getString("main_release"));
            }
            artistReleases.add(releases);
        }
    }

    private void parseReleaseContents(List<Release> releases, JSONObject jsonBody) throws IOException, JSONException {

        JSONArray releasesJsonArray = jsonBody.getJSONArray("tracklist");

        for (int i = 0; i < releasesJsonArray.length(); i++){
            JSONObject releaseJsonObject = releasesJsonArray.getJSONObject(i);

            Release release = new Release();
            release.setTrackTitle(releaseJsonObject.getString("title"));
            release.setDuration(releaseJsonObject.getString("duration"));
            release.setPosition(releaseJsonObject.getString("position"));
            releases.add(release);
        }
    }

    private void parseReleaseInfo(Release release, JSONObject jsonBody) throws IOException, JSONException {

        String releaseTitleJsonString = (String)jsonBody.get("title");
        String releaseDateFormattedJsonString = (String)jsonBody.get("released_formatted");
        String releaseDateAddedJsonString = (String)jsonBody.get("date_added");

        JSONArray formatJsonArray = jsonBody.getJSONArray("formats");

        for (int i = 0; i < formatJsonArray.length(); i++){
            JSONObject formatJsonObject = formatJsonArray.getJSONObject(i);

            release.setFormat(formatJsonObject.getString("name"));
        }
        release.setTitle(releaseTitleJsonString);
        release.setReleaseDate(releaseDateFormattedJsonString);
        release.setAddedDate(releaseDateAddedJsonString);
    }

    private void parseVersions(List<ArtistReleases> releases, JSONObject jsonBody) throws IOException, JSONException {

        JSONArray versionsJsonArray = jsonBody.getJSONArray("versions");

        for(int i = 0; i < versionsJsonArray.length(); i++){
            JSONObject versionsJsonObject = versionsJsonArray.getJSONObject(i);

            ArtistReleases release = new ArtistReleases();
            release.setFormat(versionsJsonObject.getString("format"));
            release.setResource(versionsJsonObject.getString("resource_url"));
            release.setLabel(versionsJsonObject.getString("label"));
            release.setId(versionsJsonObject.getString("id"));
            releases.add(release);
        }
    }

    private void parseSearchQuery(List<SearchQuery> searchQueries, JSONObject jsonBody) throws IOException, JSONException {

        JSONArray queryJsonArray = jsonBody.getJSONArray("results");

        for (int i = 0; i < queryJsonArray.length(); i++){
            JSONObject queryJsonObject = queryJsonArray.getJSONObject(i);

            SearchQuery searchQuery = new SearchQuery();
            searchQuery.setName(queryJsonObject.getString("title"));
            searchQuery.setType(queryJsonObject.getString("type"));
            searchQuery.setId(queryJsonObject.getString("id"));
            searchQuery.setResource(queryJsonObject.getString("resource_url"));
            searchQuery.setUri(queryJsonObject.getString("uri"));
            searchQueries.add(searchQuery);
        }
    }
}
