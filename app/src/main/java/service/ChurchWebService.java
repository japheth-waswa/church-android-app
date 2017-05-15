package service;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import event.pojo.DonationDataRetrievedSaved;
import event.pojo.EventDataRetrievedSaved;
import event.pojo.ScheduleDataRetrievedSaved;
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
                parseEventsData(response, applicationContext);
            }

            @Override
            public void onError(ANError anError) {
            }
        });

    }

    private static void parseEventsData(JSONObject response, Context applicationContext) {
        //parse and store the data in local db
        JSONObject jObject = response;
        JSONArray eventCategoriesJArray = null;
        JSONArray eventsJArray = null;
        try {
            eventCategoriesJArray = jObject.getJSONArray("eventCategories");
            eventsJArray = jObject.getJSONArray("events");

            //parse the data and store in local database
            parseRealEventCategories(eventCategoriesJArray, applicationContext);
            parseRealEvents(eventsJArray, applicationContext);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private static void parseRealEventCategories(JSONArray eventCategoriesJArray, Context applicationContext) {

        JSONArray jArray = eventCategoriesJArray;

        ChurchQueryHandler handler = new ChurchQueryHandler(applicationContext.getContentResolver());
        handler.startDelete(5, null, ChurchContract.EventCategoryEntry.CONTENT_URI, null, null);

        if (jArray.length() > 0) {
            for (int i = 0; i < jArray.length(); i++) {
                try {

                    JSONObject jObj = jArray.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    values.put(ChurchContract.EventCategoryEntry.COLUMN_EVENT_CATEGORY_ID, jObj.getString("id"));
                    values.put(ChurchContract.EventCategoryEntry.COLUMN__TITLE, jObj.getString("title"));
                    values.put(ChurchContract.EventCategoryEntry.COLUMN_URL_KEY, jObj.getString("url_key"));
                    values.put(ChurchContract.EventCategoryEntry.COLUMN_DESCRIPTION, jObj.getString("description"));
                    values.put(ChurchContract.EventCategoryEntry.COLUMN_VISIBLE, jObj.getString("visible"));
                    values.put(ChurchContract.EventCategoryEntry.COLUMN_CREATED_AT, jObj.getString("created_at"));
                    values.put(ChurchContract.EventCategoryEntry.COLUMN_UPDATED_AT, jObj.getString("updated_at"));

                    handler.startInsert(5, null, ChurchContract.EventCategoryEntry.CONTENT_URI, values);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void parseRealEvents(JSONArray eventsJArray, Context applicationContext) {

        JSONArray jArray = eventsJArray;

        ChurchQueryHandler handler = new ChurchQueryHandler(applicationContext.getContentResolver());
        handler.startDelete(3, null, ChurchContract.EventsEntry.CONTENT_URI, null, null);

        if (jArray.length() > 0) {
            for (int i = 0; i < jArray.length(); i++) {
                try {

                    JSONObject jObj = jArray.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    values.put(ChurchContract.EventsEntry.COLUMN_EVENT_ID, jObj.getString("id"));
                    values.put(ChurchContract.EventsEntry.COLUMN_TITLE, jObj.getString("title"));
                    values.put(ChurchContract.EventsEntry.COLUMN_IMAGE_URL, jObj.getString("image_url"));
                    values.put(ChurchContract.EventsEntry.COLUMN_BRIEF_DESCRIPTION, jObj.getString("brief_description"));
                    values.put(ChurchContract.EventsEntry.COLUMN_CONTENT, jObj.getString("content"));
                    values.put(ChurchContract.EventsEntry.COLUMN_EVENT_DATE, jObj.getString("event_date"));
                    values.put(ChurchContract.EventsEntry.COLUMN_EVENT_LOCATION, jObj.getString("event_location"));
                    values.put(ChurchContract.EventsEntry.COLUMN_EVENT_CATEGORY_ID, jObj.getString("event_category_id"));
                    values.put(ChurchContract.EventsEntry.COLUMN_VISIBLE, jObj.getString("visible"));
                    values.put(ChurchContract.EventsEntry.COLUMN_CREATED_AT, jObj.getString("created_at"));
                    values.put(ChurchContract.EventsEntry.COLUMN_UPDATED_AT, jObj.getString("updated_at"));

                    handler.startInsert(5, null, ChurchContract.EventsEntry.CONTENT_URI, values);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        //post event for all subscribers that data has been received and saved in local database
        EventBus.getDefault().post(new EventDataRetrievedSaved());

    }



    public static void serviceRegisterForEvent(Context applicationContext, String eventId, String fullNames, String emailAddress, String phone) {
        //todo implement logic to post registration to remote server-rem to add the logic in remote server ie php endpoint
        //todo registering a user to a specific event.
    }

    //get schedule

    public static void serviceGetSchedule(final Context applicationContext) {

        Resources res = applicationContext.getResources();
        String relativeUrl = res.getString(R.string.app_schedule);
        String accessToken = OAuth.getClientCredentialsGrantTypeAccessToken();

        AndroidNetworking.get(getAbsoluteUrl(applicationContext, relativeUrl))
                .setPriority(Priority.HIGH)
                .setTag("scheduleApi")
                .addHeaders("Authorization", "Bearer " + accessToken)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                parseSchedules(response, applicationContext);
            }

            @Override
            public void onError(ANError anError) {
            }
        });

    }

    private static void parseSchedules(JSONObject custJObject, Context applicationContext) {

        JSONObject jObject = custJObject;
        JSONArray jArray = null;

        ChurchQueryHandler handler = new ChurchQueryHandler(applicationContext.getContentResolver());
        handler.startDelete(5, null, ChurchContract.SchedulesEntry.CONTENT_URI, null, null);



        try {
            //get jsonArray
            jArray = jObject.getJSONArray("sundaypages");

            ContentValues values = new ContentValues();
            values.put(ChurchContract.SchedulesEntry.COLUMN_SCHEDULE_ID, jObject.getString("id"));
            values.put(ChurchContract.SchedulesEntry.COLUMN_THEME_TITLE, jObject.getString("theme_title"));
            values.put(ChurchContract.SchedulesEntry.COLUMN_THEME_DESCRIPTION, jObject.getString("theme_description"));
            values.put(ChurchContract.SchedulesEntry.COLUMN_SUNDAY_DATE, jObject.getString("sunday_date"));
            values.put(ChurchContract.SchedulesEntry.COLUMN_COLUMN_COUNT, jObject.getString("column_count"));
            values.put(ChurchContract.SchedulesEntry.COLUMN_VISIBLE, jObject.getString("visible"));
            values.put(ChurchContract.SchedulesEntry.COLUMN_CREATED_AT, jObject.getString("created_at"));
            values.put(ChurchContract.SchedulesEntry.COLUMN_UPDATED_AT, jObject.getString("updated_at"));

            handler.startInsert(7, null, ChurchContract.SchedulesEntry.CONTENT_URI, values);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        ChurchQueryHandler handlered = new ChurchQueryHandler(applicationContext.getContentResolver());
        handlered.startDelete(17, null, ChurchContract.SchedulePagesEntry.CONTENT_URI, null, null);

        if (jArray.length() > 0) {
            for (int i = 0; i < jArray.length(); i++) {
                try {

                    JSONObject jObj = jArray.getJSONObject(i);
                    ContentValues valuesed = new ContentValues();
                    valuesed.put(ChurchContract.SchedulePagesEntry.COLUMN_SCHEDULE_PAGES_ID, jObj.getString("id"));
                    valuesed.put(ChurchContract.SchedulePagesEntry.COLUMN_PAGE_CONTENT, jObj.getString("page_content"));
                    valuesed.put(ChurchContract.SchedulePagesEntry.COLUMN_SUNDAY_SCHEDULE_ID, jObj.getString("sunday_schedule_id"));
                    valuesed.put(ChurchContract.SchedulePagesEntry.COLUMN_PAGE_ORDER, jObj.getString("page_order"));
                    valuesed.put(ChurchContract.SchedulePagesEntry.COLUMN_VISIBLE, jObj.getString("visible"));
                    valuesed.put(ChurchContract.SchedulePagesEntry.COLUMN_CREATED_AT, jObj.getString("created_at"));
                    valuesed.put(ChurchContract.SchedulePagesEntry.COLUMN_UPDATED_AT, jObj.getString("updated_at"));

                    handlered.startInsert(19, null, ChurchContract.SchedulePagesEntry.CONTENT_URI, valuesed);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //post event for all subscribers that data has been received and saved in local database
        EventBus.getDefault().post(new ScheduleDataRetrievedSaved());

    }


    //retrieve donation
    public static void serviceGetDonation(final Context applicationContext) {
        Resources res = applicationContext.getResources();
        String relativeUrl = res.getString(R.string.app_donation);
        String accessToken = OAuth.getClientCredentialsGrantTypeAccessToken();

        AndroidNetworking.get(getAbsoluteUrl(applicationContext, relativeUrl))
                .setPriority(Priority.HIGH)
                .setTag("donationApi")
                .addHeaders("Authorization", "Bearer " + accessToken)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                parseDonation(response, applicationContext);
            }

            @Override
            public void onError(ANError anError) {
            }
        });
    }

    private static void parseDonation(JSONObject response, Context applicationContext) {

        ChurchQueryHandler handler = new ChurchQueryHandler(applicationContext.getContentResolver());
        handler.startDelete(9, null, ChurchContract.DonationEntry.CONTENT_URI, null, null);

        JSONObject jObject = response;

        try {
            ContentValues values = new ContentValues();
            values.put(ChurchContract.DonationEntry.COLUMN_DONATION_ID, jObject.getString("id"));
            values.put(ChurchContract.DonationEntry.COLUMN_TITLE, jObject.getString("title"));
            values.put(ChurchContract.DonationEntry.COLUMN_IMAGE_URL, jObject.getString("image_url"));
            values.put(ChurchContract.DonationEntry.COLUMN_DESCRIPTION, jObject.getString("description"));
            values.put(ChurchContract.DonationEntry.COLUMN_CONTENT, jObject.getString("content"));
            values.put(ChurchContract.DonationEntry.COLUMN_FACEBOOK, jObject.getString("facebook_url"));
            values.put(ChurchContract.DonationEntry.COLUMN_TWITTER, jObject.getString("twitter_url"));
            values.put(ChurchContract.DonationEntry.COLUMN_YOUTUBE, jObject.getString("youtube_url"));
            values.put(ChurchContract.DonationEntry.COLUMN_VISIBLE, jObject.getString("visible"));
            values.put(ChurchContract.DonationEntry.COLUMN_CREATED_AT, jObject.getString("created_at"));
            values.put(ChurchContract.DonationEntry.COLUMN_UPDATED_AT, jObject.getString("updated_at"));

            handler.startInsert(11, null, ChurchContract.DonationEntry.CONTENT_URI, values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //post event
        EventBus.getDefault().post(new DonationDataRetrievedSaved());

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
