package service;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.japhethwaswa.church.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import db.ChurchQueryHandler;
import db.ChurchContract;

public class ChurchWebService {


    public static void servSaveBibleDataToDb(Context applicationContext) {
        //todo remember to use event bus

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


    }

    /**public static void serviceGetEntireBible(final Context context) {

     //perform bible_books,bible_chapters,bible_verses file creaations and write data to them.(remove after getting data i files)

     String[] bibleBooks = {"genesis", "exodus", "leviticus", "numbers", "deuteronomy", "Joshua", "Judges", "Ruth", "1Samuel", "2Samuel",
     "1Kings", "2Kings", "1Chronicles", "2Chronicles", "Ezra", "Nehemiah", "Esther", "Job", "Psalms", "Proverbs", "Ecclesiastes", "SongofSongs",
     "Isaiah", "Jeremiah", "Lamentations", "Ezekiel", "Daniel", "Hosea", "Joel", "Amos", "Obadiah", "Jonah", "Micah", "Nahum", "Habakkuk", "Zephaniah",
     "Haggai", "Zechariah", "Malachi", "Matthew", "Mark", "Luke", "John", "Acts", "Romans", "1Corinthians", "2Corinthians", "Galatians", "Ephesians",
     "Philippians", "Colossians", "1Thessalonians", "2Thessalonians", "1Timothy", "2Timothy", "Titus", "Philemon", "Hebrews", "James", "1Peter",
     "2Peter", "1John", "2John", "3John", "Jude", "Revelation"};

     Resources res = context.getResources();
     String bibleApi = res.getString(R.string.bibleApi);
     int bookNum = 0;
     for (String bibleBook : bibleBooks) {
     bookNum++;

     String relativeUrl = bibleApi + "json?p=" + bibleBook;
     final int finalBookNum = bookNum;
     AndroidNetworking.get(relativeUrl)
     .setTag("bibleData")
     .setPriority(Priority.HIGH)
     .build()
     .getAsString(new StringRequestListener() {
    @Override public void onResponse(String response) {
    //parseBibleData(response, context);
    }

    @Override public void onError(ANError anError) {
    }
    });

     }**/

    /** try {
     File fileName = new File(context.getFilesDir().getPath() + "/bible_books.txt");
     FileOutputStream fOut = new FileOutputStream(fileName, true);
     //FileOutputStream fOut = openFileOutput("bible_books", Context.MODE_PRIVATE);
     String str = "Hello world 18";
     fOut.write(str.getBytes());
     fOut.close();

     FileInputStream fin = new FileInputStream(fileName);
     int c;
     String temp = "";
     while ((c = fin.read()) != -1) {
     temp = temp + Character.toString((char) c);
     }
     fin.close();
     Log.e("jeff-waswa", temp);
     } catch (FileNotFoundException e) {
     e.printStackTrace();
     } catch (IOException e) {
     e.printStackTrace();
     }


     }**/


    //parse bible data
    /**private static void parseBibleData(String response, Context context) {
     try {
     //Log.e("jeff-waswa",response);
     response = response.replace("(", "");
     response = response.replace(")", "");
     if (response != null) {
     //save bible collection
     //saveBibleCollection(response, context);

     JSONObject responseJsonObj = new JSONObject(response);

     //deeply parse this book
     deepParsing(responseJsonObj,context);

     }

     } catch (JSONException e) {
     e.printStackTrace();
     }

     }

     private static void deepParsing(JSONObject responseJsonObj, Context context) {
     //parse both books,chapters,verses
     try {
     String bookName = responseJsonObj.getString("book_name");
     String bookCode = bookName.replace(" ","");
     String bookNumber = responseJsonObj.getString("book_nr");
     int bookNum = Integer.valueOf(bookNumber);
     String bookVersion = "new";
     if(bookNum <= 39){
     bookVersion = "old";
     }

     //insert book
     ChurchQueryHandler handler = new ChurchQueryHandler(context.getContentResolver());

     ContentValues values = new ContentValues();
     values.put(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_VERSION,bookVersion);
     values.put(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_CODE,bookCode);
     values.put(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER,bookNumber);
     values.put(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NAME,bookName);
     handler.startInsert(1,null, ChurchContract.BibleBookEntry.CONTENT_URI,values);


     //parse chapter
     parseChapter(context,responseJsonObj,bookCode);

     } catch (JSONException e) {
     e.printStackTrace();
     }

     }

     private static void parseChapter(Context context, JSONObject responseJsonObj, String bookCode) {

     try {
     JSONObject chapterMainObj = responseJsonObj.getJSONObject("book");

     Iterator<String> iter = chapterMainObj.keys();
     while(iter.hasNext()){
     String key = iter.next();
     Log.e("chapter-key",key);
     JSONObject chapterObj = new JSONObject(chapterMainObj.get(key).toString());

     String bbBookCode = bookCode;
     String chapterNumber = chapterObj.getString("chapter_nr");
     String chapterCode = bbBookCode +"_" + chapterNumber;

     //insert chapter here
     ChurchQueryHandler handler = new ChurchQueryHandler(context.getContentResolver());

     ContentValues values = new ContentValues();
     values.put(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_NUMBER,chapterNumber);
     values.put(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_BOOK_CODE,bbBookCode);
     values.put(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_CODE,chapterCode);
     handler.startInsert(1,null, ChurchContract.BibleChapterEntry.CONTENT_URI,values);


     //parse verse from here
     parseVerse(chapterObj.getJSONObject("chapter"),chapterCode,context);

     }

     } catch (JSONException e) {
     e.printStackTrace();
     }
     }

     private static void parseVerse(JSONObject chapter, String chapterCode, Context context) {

     Iterator<String> iterator = chapter.keys();
     while(iterator.hasNext()){
     String key = iterator.next();
     Log.e("verse-key",key);
     try {
     JSONObject verseObj = new JSONObject(chapter.get(key).toString());

     String verseNumber = verseObj.getString("verse_nr");
     String verse = verseObj.getString("verse");

     //insert verse here
     ChurchQueryHandler handler = new ChurchQueryHandler(context.getContentResolver());

     ContentValues values = new ContentValues();
     values.put(ChurchContract.BibleVerseEntry.COLUMN_VERSE_NUMBER,verseNumber);
     values.put(ChurchContract.BibleVerseEntry.COLUMN_VERSE_CHAPTER_CODE,chapterCode);
     values.put(ChurchContract.BibleVerseEntry.COLUMN_VERSE,verse);
     handler.startInsert(1,null, ChurchContract.BibleVerseEntry.CONTENT_URI,values);

     } catch (JSONException e) {
     e.printStackTrace();
     }
     }

     }

     //save each bible book
     /**private static void saveBibleCollection(String response, Context context) {
     //save in database then fetch later and save in a json file on completion
     ChurchQueryHandler handler = new ChurchQueryHandler(context.getContentResolver());

     ContentValues values = new ContentValues();
     values.put(ChurchContract.BibleCollectionEntry.COLUMN_BIBLE_BOOK,response);
     handler.startInsert(1,null, ChurchContract.BibleCollectionEntry.CONTENT_URI,values);

     /** try {
     File fileName = new File(context.getFilesDir().getPath() + "/bible_collection.txt");
     FileOutputStream fOut = new FileOutputStream(fileName, true);

     fOut.write(response.getBytes());
     fOut.close();

     } catch (IOException e) {
     e.printStackTrace();
     }**/

    //}
}
