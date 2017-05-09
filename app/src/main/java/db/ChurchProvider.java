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
import static db.ChurchContract.PATH_SERMONS;
import static db.ChurchContract.PATH_EVENT_CATEGORIES;

public class ChurchProvider extends ContentProvider{
    /**
     * constants for the operation
     **/
    private static final int SERMON = 1;
    private static final int SERMON_ID = 2;

    private static final int EVENTCATEGORY = 3;
    private static final int EVENTCATEGORY_ID = 4;

    //Uri matcher
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        //sermon data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SERMONS, SERMON);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SERMONS + "/#", SERMON_ID);

        //event data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_EVENT_CATEGORIES, EVENTCATEGORY);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_EVENT_CATEGORIES + "/#", EVENTCATEGORY_ID);

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
            case SERMON:
                cursor = db.query(ChurchContract.SermonEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SERMON_ID:
                selection = ChurchContract.SermonEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ChurchContract.SermonEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case EVENTCATEGORY:
                cursor = db.query(ChurchContract.EventCategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case EVENTCATEGORY_ID:
                selection = ChurchContract.EventCategoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ChurchContract.EventCategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
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
            case SERMON:
                return insertRecord(uri, values, ChurchContract.SermonEntry.TABLE_NAME);
            case EVENTCATEGORY:
                return insertRecord(uri, values, ChurchContract.EventCategoryEntry.TABLE_NAME);
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
            case SERMON:
                return deleteRecord(uri, null, null, ChurchContract.SermonEntry.TABLE_NAME);
            case SERMON_ID:
                return deleteRecord(uri, selection, selectionArgs, ChurchContract.SermonEntry.TABLE_NAME);
            case EVENTCATEGORY:
                return deleteRecord(uri, null, null, ChurchContract.EventCategoryEntry.TABLE_NAME);
            case EVENTCATEGORY_ID:
                return deleteRecord(uri, selection, selectionArgs, ChurchContract.EventCategoryEntry.TABLE_NAME);
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
            case SERMON:
                return updateRecord(uri, values, selection, selectionArgs, ChurchContract.SermonEntry.TABLE_NAME);
            case EVENTCATEGORY:
                return updateRecord(uri, values, selection, selectionArgs, ChurchContract.EventCategoryEntry.TABLE_NAME);
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
