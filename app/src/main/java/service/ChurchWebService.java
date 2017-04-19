package service;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.japhethwaswa.church.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import db.ChurchContract;
import db.DatabaseHelper;
import event.pojo.ConnectionStatus;
import model.Connectivity;

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
            json = new String(buffer,"UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //parse the json and save in database
        try {
            JSONObject jsonObj = new JSONObject(json);
            DatabaseHelper helper = new DatabaseHelper(applicationContext);
            SQLiteDatabase db = helper.getWritableDatabase();

            Iterator<String> iterator = jsonObj.keys();
            while(iterator.hasNext()){

                String key = iterator.next();
                JSONObject jsonObject = new JSONObject(jsonObj.get(key).toString());

                //insert book here
                ContentValues values = new ContentValues();
                values.put(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_VERSION,jsonObject.getString(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_VERSION));
                values.put(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_CODE,jsonObject.getString(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_CODE));
                values.put(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER,jsonObject.getString(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER));
                values.put(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NAME,jsonObject.getString(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NAME));

                long id = db.insert(ChurchContract.BibleBookEntry.TABLE_NAME,null,values);

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
            json = new String(buffer,"UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //parse the json and save in database
        try {
            JSONObject jsonObj = new JSONObject(json);
            DatabaseHelper helper = new DatabaseHelper(applicationContext);
            SQLiteDatabase db = helper.getWritableDatabase();

            Iterator<String> iterator = jsonObj.keys();
            while(iterator.hasNext()){

                String key = iterator.next();
                JSONObject jsonObject = new JSONObject(jsonObj.get(key).toString());

                //insert book here
                ContentValues values = new ContentValues();
                values.put(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_NUMBER,jsonObject.getString(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_NUMBER));
                values.put(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_BOOK_CODE,jsonObject.getString(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_BOOK_CODE));
                values.put(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_CODE,jsonObject.getString(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_CODE));

                long id = db.insert(ChurchContract.BibleChapterEntry.TABLE_NAME,null,values);

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
            json = new String(buffer,"UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //parse the json and save in database
        try {
            JSONObject jsonObj = new JSONObject(json);
            DatabaseHelper helper = new DatabaseHelper(applicationContext);
            SQLiteDatabase db = helper.getWritableDatabase();

            Iterator<String> iterator = jsonObj.keys();
            while(iterator.hasNext()){

                String key = iterator.next();
                JSONObject jsonObject = new JSONObject(jsonObj.get(key).toString());

                //insert book here
                ContentValues values = new ContentValues();
                values.put(ChurchContract.BibleVerseEntry.COLUMN_VERSE_NUMBER,jsonObject.getString(ChurchContract.BibleVerseEntry.COLUMN_VERSE_NUMBER));
                values.put(ChurchContract.BibleVerseEntry.COLUMN_VERSE_CHAPTER_CODE,jsonObject.getString(ChurchContract.BibleVerseEntry.COLUMN_VERSE_CHAPTER_CODE));
                values.put(ChurchContract.BibleVerseEntry.COLUMN_VERSE,jsonObject.getString(ChurchContract.BibleVerseEntry.COLUMN_VERSE));

                long id = db.insert(ChurchContract.BibleVerseEntry.TABLE_NAME,null,values);

            }

            db.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

//todo create class that handles OAuth2 authentication and access token retrieval
    public static void serviceGetSermons(Context applicationContext) {

        Resources res = applicationContext.getResources();
        String relativeUrl = res.getString(R.string.app_sermon);


        AndroidNetworking.get(getAbsoluteUrl(applicationContext, relativeUrl))
                .setPriority(Priority.HIGH)
                .setTag("laravelApi")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("jeff-json",response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("jeff-json-err",anError.toString());
                    }
                });
        //todo get jsonObject,if it returns an error then ensure you get a new access_token from OAuth2
        //todo parse and store the data in local db
        //todo post event for all subscribers.

    }


    private static String getAbsoluteUrl(Context context, String relativeUrl) {
        Resources res = context.getResources();
        String baseUrl = res.getString(R.string.root_domain);
        return baseUrl + relativeUrl;
    }

}
