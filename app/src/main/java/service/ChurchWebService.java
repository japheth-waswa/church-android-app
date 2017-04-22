package service;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.japhethwaswa.church.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import db.ChurchContract;
import db.ChurchQueryHandler;
import db.DatabaseHelper;
import event.pojo.SermonDataRetrievedSaved;
import model.dyno.OAuth;

public class ChurchWebService {

    public static void servSaveBibleDataToDb(Context applicationContext) {

        //parse bible books from bible_books.txt
        parseAndSaveBibleBooks(applicationContext);

        //parse bible chapters from bible_chapters.txt
        parseAndSaveBibleChapters(applicationContext);

        //parse bible verses from bible_verses.txt
        parseAndSaveBibleVerses(applicationContext);
    }

    private static void parseAndSaveBibleBooks(Context applicationContext) {
        //parse bible books from bible_books.txt
        String json = null;

        //get data from file
        try {

            InputStream is = applicationContext.getAssets().open("bible_books.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //parse the json and save in database
        try {
            JSONObject jsonObj = new JSONObject(json);
            DatabaseHelper helper = new DatabaseHelper(applicationContext);
            SQLiteDatabase db = helper.getWritableDatabase();

            Iterator<String> iterator = jsonObj.keys();
            while (iterator.hasNext()) {

                String key = iterator.next();
                JSONObject jsonObject = new JSONObject(jsonObj.get(key).toString());

                //insert book here
                ContentValues values = new ContentValues();
                values.put(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_VERSION, jsonObject.getString(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_VERSION));
                values.put(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_CODE, jsonObject.getString(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_CODE));
                values.put(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER, jsonObject.getString(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER));
                values.put(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NAME, jsonObject.getString(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NAME));

                long id = db.insert(ChurchContract.BibleBookEntry.TABLE_NAME, null, values);

            }

            db.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private static void parseAndSaveBibleChapters(Context applicationContext) {
        //parse bible chapters from bible_chapters.txt
        String json = null;

        //get data from file
        try {

            InputStream is = applicationContext.getAssets().open("bible_chapters.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //parse the json and save in database
        try {
            JSONObject jsonObj = new JSONObject(json);
            DatabaseHelper helper = new DatabaseHelper(applicationContext);
            SQLiteDatabase db = helper.getWritableDatabase();

            Iterator<String> iterator = jsonObj.keys();
            while (iterator.hasNext()) {

                String key = iterator.next();
                JSONObject jsonObject = new JSONObject(jsonObj.get(key).toString());

                //insert book here
                ContentValues values = new ContentValues();
                values.put(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_NUMBER, jsonObject.getString(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_NUMBER));
                values.put(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_BOOK_CODE, jsonObject.getString(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_BOOK_CODE));
                values.put(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_CODE, jsonObject.getString(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_CODE));

                long id = db.insert(ChurchContract.BibleChapterEntry.TABLE_NAME, null, values);

            }

            db.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private static void parseAndSaveBibleVerses(Context applicationContext) {
        //parse bible verses from bible_verses.txt
        String json = null;

        //get data from file
        try {

            InputStream is = applicationContext.getAssets().open("bible_verses.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //parse the json and save in database
        try {
            JSONObject jsonObj = new JSONObject(json);
            DatabaseHelper helper = new DatabaseHelper(applicationContext);
            SQLiteDatabase db = helper.getWritableDatabase();

            Iterator<String> iterator = jsonObj.keys();
            while (iterator.hasNext()) {

                String key = iterator.next();
                JSONObject jsonObject = new JSONObject(jsonObj.get(key).toString());

                //insert book here
                ContentValues values = new ContentValues();
                values.put(ChurchContract.BibleVerseEntry.COLUMN_VERSE_NUMBER, jsonObject.getString(ChurchContract.BibleVerseEntry.COLUMN_VERSE_NUMBER));
                values.put(ChurchContract.BibleVerseEntry.COLUMN_VERSE_CHAPTER_CODE, jsonObject.getString(ChurchContract.BibleVerseEntry.COLUMN_VERSE_CHAPTER_CODE));
                values.put(ChurchContract.BibleVerseEntry.COLUMN_VERSE, jsonObject.getString(ChurchContract.BibleVerseEntry.COLUMN_VERSE));

                long id = db.insert(ChurchContract.BibleVerseEntry.TABLE_NAME, null, values);

            }

            db.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //todo create class that handles OAuth2 authentication and access token retrieval
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
                parseSermons(response,applicationContext);
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
        EventBus.getDefault().post(new SermonDataRetrievedSaved());
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
