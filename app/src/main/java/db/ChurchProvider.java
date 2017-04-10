package db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static db.ChurchContract.CONTENT_AUTHORITY;
import static db.ChurchContract.PATH_BIBLE_BOOK;
import static db.ChurchContract.PATH_BIBLE_CHAPTER;
import static db.ChurchContract.PATH_BIBLE_VERSE;

public class ChurchProvider extends ContentProvider{
    /**
     * constants for the operation
     **/
    private static final int BIBLE_BOOK = 1;
    private static final int BIBLE_BOOK_ID = 2;
    private static final int BIBLE_CHAPTER = 3;
    private static final int BIBLE_CHAPTER_ID = 4;
    private static final int BIBLE_VERSE = 5;
    private static final int BIBLE_VERSE_ID = 6;

    //Uri matcher
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        //bible book data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BIBLE_BOOK, BIBLE_BOOK);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BIBLE_BOOK + "/#", BIBLE_BOOK_ID);

        //bible chapter data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BIBLE_CHAPTER, BIBLE_CHAPTER);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BIBLE_CHAPTER + "/#", BIBLE_CHAPTER_ID);

        //bible verse data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BIBLE_VERSE, BIBLE_VERSE);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BIBLE_VERSE + "/#", BIBLE_VERSE_ID);

    }

    private DatabaseHelper helper;

    @Override
    public boolean onCreate() {
        helper = new DatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        //4 possible scenarios
        switch (match) {
            case BIBLE_BOOK:
                cursor = db.query(ChurchContract.BibleBookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BIBLE_BOOK_ID:
                selection = ChurchContract.BibleBookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ChurchContract.BibleBookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BIBLE_CHAPTER:
                cursor = db.query(ChurchContract.BibleChapterEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BIBLE_CHAPTER_ID:
                selection = ChurchContract.BibleChapterEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ChurchContract.BibleChapterEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BIBLE_VERSE:
                cursor = db.query(ChurchContract.BibleVerseEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BIBLE_VERSE_ID:
                selection = ChurchContract.BibleVerseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ChurchContract.BibleVerseEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case BIBLE_BOOK:
                return insertRecord(uri, values, ChurchContract.BibleBookEntry.TABLE_NAME);
            case BIBLE_CHAPTER:
                return insertRecord(uri, values, ChurchContract.BibleChapterEntry.TABLE_NAME);
            case BIBLE_VERSE:
                return insertRecord(uri, values, ChurchContract.BibleVerseEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException("Unkwown uri: " + uri);

        }
    }

    private Uri insertRecord(Uri uri, ContentValues values, String tableName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long id = db.insert(tableName, null, values);
        //db.close();
        if (id == -1) {
            Log.e("Error", "insert error for uri: " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case BIBLE_BOOK:
                return deleteRecord(uri, null, null, ChurchContract.BibleBookEntry.TABLE_NAME);
            case BIBLE_BOOK_ID:
                return deleteRecord(uri, selection, selectionArgs, ChurchContract.BibleBookEntry.TABLE_NAME);
            case BIBLE_CHAPTER:
                return deleteRecord(uri, null, null, ChurchContract.BibleChapterEntry.TABLE_NAME);
            case BIBLE_CHAPTER_ID:
                return deleteRecord(uri, selection, selectionArgs, ChurchContract.BibleChapterEntry.TABLE_NAME);
            case BIBLE_VERSE:
                return deleteRecord(uri, null, null, ChurchContract.BibleVerseEntry.TABLE_NAME);
            case BIBLE_VERSE_ID:
                return deleteRecord(uri, selection, selectionArgs, ChurchContract.BibleVerseEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException("Insert unknown URI: " + uri);
        }
    }

    private int deleteRecord(Uri uri, String selection, String[] selectionArgs, String tableName) {
        //this time we need a writable database
        SQLiteDatabase db = helper.getWritableDatabase();
        int id = db.delete(tableName, selection, selectionArgs);
        //db.close();
        if (id == -1) {
            Log.e("Error", "delete unknown URI " + uri);
            return -1;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return id;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case BIBLE_BOOK:
                return updateRecord(uri, values, selection, selectionArgs, ChurchContract.BibleBookEntry.TABLE_NAME);
            case BIBLE_CHAPTER:
                return updateRecord(uri, values, selection, selectionArgs, ChurchContract.BibleChapterEntry.TABLE_NAME);
            case BIBLE_VERSE:
                return updateRecord(uri, values, selection, selectionArgs, ChurchContract.BibleVerseEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException("Update unknown URI: " + uri);
        }
    }

    private int updateRecord(Uri uri, ContentValues values, String selection, String[] selectionArgs, String tableName) {
        //this time we need a writable database
        SQLiteDatabase db = helper.getWritableDatabase();
        int id = db.update(tableName, values, selection, selectionArgs);
        //db.close();
        if (id == 0) {
            Log.e("Error", "update error for URI " + uri);
            return -1;
        }

        return id;
    }
}
