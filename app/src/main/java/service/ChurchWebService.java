package service;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.japhethwaswa.church.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import app.LaunchActivity;
import db.ChurchContract;
import db.ChurchQueryHandler;
import db.DatabaseHelper;
import event.pojo.BibleUpdate;
import event.pojo.SermonDataRetrievedSaved;
import model.dyno.OAuth;

public class ChurchWebService {

    //sermons service
    public static void serviceGetSermons(final Context applicationContext) {

        Resources res = applicationContext.getResources();
        String relativeUrl = res.getString(R.string.app_sermon);
        String accessToken = OAuth.getClientCredentialsGrantTypeAccessToken();

        AndroidNetworking.get(getAbsoluteUrl(applicationContext, relativeUrl))
                .setPriority(Priority.HIGH)
                .setTag("sermonsApi")
                .addHeaders("Authorization", "Bearer " + accessToken)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                parseSermons(response, applicationContext);
            }

            @Override
            public void onError(ANError anError) {

            }
        });

    }

    private static void parseSermons(JSONArray response, Context applicationContext) {
        //parse and store the data in local db
        JSONArray jArray = response;

        //clear entire database of sermons
        ChurchQueryHandler handler = new ChurchQueryHandler(applicationContext.getContentResolver());
        handler.startDelete(3, null, ChurchContract.SermonEntry.CONTENT_URI, null, null);

        if (jArray.length() > 0) {
            for (int i = 0; i < jArray.length(); i++) {
                try {

                    JSONObject sermonJObject = jArray.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    values.put(ChurchContract.SermonEntry.COLUMN_SERMON_ID, sermonJObject.getString("id"));
                    values.put(ChurchContract.SermonEntry.COLUMN_SERMON_TITLE, sermonJObject.getString("title"));
                    values.put(ChurchContract.SermonEntry.COLUMN_SERMON_IMAGE_URL, sermonJObject.getString("image_url"));
                    values.put(ChurchContract.SermonEntry.COLUMN_SERMON_BRIEF_DESCRIPTION, sermonJObject.getString("brief_description"));
                    values.put(ChurchContract.SermonEntry.COLUMN_SERMON_AUDIO_URL, sermonJObject.getString("audio_url"));
                    values.put(ChurchContract.SermonEntry.COLUMN_SERMON_VIDEO_URL, sermonJObject.getString("video_url"));
                    values.put(ChurchContract.SermonEntry.COLUMN_SERMON_PDF_URL, sermonJObject.getString("pdf_url"));
                    values.put(ChurchContract.SermonEntry.COLUMN_SERMON_DATE, sermonJObject.getString("sermon_date"));
                    values.put(ChurchContract.SermonEntry.COLUMN_SERMON_VISIBLE, sermonJObject.getString("visible"));
                    values.put(ChurchContract.SermonEntry.COLUMN_SERMON_CREATED_AT, sermonJObject.getString("created_at"));
                    values.put(ChurchContract.SermonEntry.COLUMN_SERMON_UPDATED_AT, sermonJObject.getString("updated_at"));

                    handler.startInsert(5, null, ChurchContract.SermonEntry.CONTENT_URI, values);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //post event for all subscribers that data has been received and saved in local database
        EventBus.getDefault().post(new SermonDataRetrievedSaved());
    }

    //events service
    public static void serviceGetEvents(final Context applicationContext) {

        Resources res = applicationContext.getResources();
        String relativeUrl = res.getString(R.string.app_event);
        String accessToken = OAuth.getClientCredentialsGrantTypeAccessToken();

        AndroidNetworking.get(getAbsoluteUrl(applicationContext, relativeUrl))
                .setPriority(Priority.HIGH)
                .setTag("eventsApi")
                .addHeaders("Authorization", "Bearer " + accessToken)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                parseEventsData(response,applicationContext);
            }

            @Override
            public void onError(ANError anError) {
            }
        });

    }

    private static void parseEventsData(JSONObject response, Context applicationContext) {
        //parse and store the data in local db
        JSONObject jObject = response;
        JSONArray eventCategoriesJArray =  null;
        JSONArray eventsJArray =  null;
        try {
            eventCategoriesJArray = jObject.getJSONArray("eventCategories");
            eventsJArray = jObject.getJSONArray("events");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        //clear entire database of sermons
        /**ChurchQueryHandler handler = new ChurchQueryHandler(applicationContext.getContentResolver());
         handler.startDelete(3,null,ChurchContract.SermonEntry.CONTENT_URI,null,null);

         if(jArray.length() > 0) {
         for (int i = 0; i < jArray.length(); i++) {
         try {

         JSONObject sermonJObject = jArray.getJSONObject(i);
         ContentValues values = new ContentValues();
         values.put(ChurchContract.SermonEntry.COLUMN_SERMON_ID,sermonJObject.getString("id"));
         values.put(ChurchContract.SermonEntry.COLUMN_SERMON_TITLE,sermonJObject.getString("title"));
         values.put(ChurchContract.SermonEntry.COLUMN_SERMON_IMAGE_URL,sermonJObject.getString("image_url"));
         values.put(ChurchContract.SermonEntry.COLUMN_SERMON_BRIEF_DESCRIPTION,sermonJObject.getString("brief_description"));
         values.put(ChurchContract.SermonEntry.COLUMN_SERMON_AUDIO_URL,sermonJObject.getString("audio_url"));
         values.put(ChurchContract.SermonEntry.COLUMN_SERMON_VIDEO_URL,sermonJObject.getString("video_url"));
         values.put(ChurchContract.SermonEntry.COLUMN_SERMON_PDF_URL,sermonJObject.getString("pdf_url"));
         values.put(ChurchContract.SermonEntry.COLUMN_SERMON_DATE,sermonJObject.getString("sermon_date"));
         values.put(ChurchContract.SermonEntry.COLUMN_SERMON_VISIBLE,sermonJObject.getString("visible"));
         values.put(ChurchContract.SermonEntry.COLUMN_SERMON_CREATED_AT,sermonJObject.getString("created_at"));
         values.put(ChurchContract.SermonEntry.COLUMN_SERMON_UPDATED_AT,sermonJObject.getString("updated_at"));

         handler.startInsert(5,null, ChurchContract.SermonEntry.CONTENT_URI,values);
         } catch (JSONException e) {
         e.printStackTrace();
         }
         }
         }
         //post event for all subscribers that data has been received and saved in local database
         EventBus.getDefault().post(new SermonDataRetrievedSaved());**/
    }

    private static String getAbsoluteUrl(Context context, String relativeUrl) {
        Resources res = context.getResources();
        String baseUrl = res.getString(R.string.root_domain_api);
        return baseUrl + relativeUrl;
    }

    public static String getRootAbsoluteUrl(Context context, String relativeUrl) {
        Resources res = context.getResources();
        String baseUrl = res.getString(R.string.root_domain);
        return baseUrl + relativeUrl;
    }

}
