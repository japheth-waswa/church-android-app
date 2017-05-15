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
import static db.ChurchContract.PATH_EVENTS;
import static db.ChurchContract.PATH_SCHEDULES;
import static db.ChurchContract.PATH_SCHEDULE_PAGES;
import static db.ChurchContract.PATH_DONATION;
import static db.ChurchContract.PATH_BLOG_CATEGORIES;
import static db.ChurchContract.PATH_BLOG;
import static db.ChurchContract.PATH_BLOG_COMMENTS;

public class ChurchProvider extends ContentProvider{
    /**
     * constants for the operation
     **/
    private static final int SERMON = 1;
    private static final int SERMON_ID = 2;

    private static final int EVENTCATEGORY = 3;
    private static final int EVENTCATEGORY_ID = 4;

    private static final int EVENT = 5;
    private static final int EVENT_ID = 6;

    private static final int SCHEDULE = 7;
    private static final int SCHEDULE_ID = 8;

    private static final int SCHEDULEPAGES = 9;
    private static final int SCHEDULEPAGES_ID = 10;

    private static final int DONATION = 11;
    private static final int DONATION_ID = 12;

    private static final int BLOGCATEGORY = 13;
    private static final int BLOGCATEGORY_ID = 14;

    private static final int BLOG = 15;
    private static final int BLOG_ID = 16;

    private static final int BLOG_COMMENTS = 17;
    private static final int BLOG_COMMENTS_ID = 18;

    //Uri matcher
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        //sermon data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SERMONS, SERMON);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SERMONS + "/#", SERMON_ID);

        //event category data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_EVENT_CATEGORIES, EVENTCATEGORY);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_EVENT_CATEGORIES + "/#", EVENTCATEGORY_ID);

        //event data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_EVENTS, EVENT);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_EVENTS + "/#", EVENT_ID);

        //schedules data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SCHEDULES, SCHEDULE);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SCHEDULES + "/#", SCHEDULE_ID);

        //schedule pages data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SCHEDULE_PAGES, SCHEDULEPAGES);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SCHEDULE_PAGES + "/#", SCHEDULEPAGES_ID);

        //donation data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_DONATION, DONATION);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_DONATION + "/#", DONATION_ID);

        //blog category data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BLOG_CATEGORIES, BLOGCATEGORY);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BLOG_CATEGORIES + "/#", BLOGCATEGORY_ID);

        //blog data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BLOG, BLOG);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BLOG + "/#", BLOG_ID);

        //blog comments data uri
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BLOG_COMMENTS, BLOG_COMMENTS);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BLOG_COMMENTS + "/#", BLOG_COMMENTS_ID);

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
            case EVENT:
                cursor = db.query(ChurchContract.EventsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case EVENT_ID:
                selection = ChurchContract.EventsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ChurchContract.EventsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SCHEDULE:
                cursor = db.query(ChurchContract.SchedulesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SCHEDULE_ID:
                selection = ChurchContract.SchedulesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ChurchContract.SchedulesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SCHEDULEPAGES:
                cursor = db.query(ChurchContract.SchedulePagesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SCHEDULEPAGES_ID:
                selection = ChurchContract.SchedulePagesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ChurchContract.SchedulePagesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DONATION:
                cursor = db.query(ChurchContract.DonationEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DONATION_ID:
                selection = ChurchContract.DonationEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ChurchContract.DonationEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BLOGCATEGORY:
                cursor = db.query(ChurchContract.BlogCategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BLOGCATEGORY_ID:
                selection = ChurchContract.BlogCategoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ChurchContract.BlogCategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BLOG:
                cursor = db.query(ChurchContract.BlogsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BLOG_ID:
                selection = ChurchContract.BlogsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ChurchContract.BlogsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BLOG_COMMENTS:
                cursor = db.query(ChurchContract.BlogCommentsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BLOG_COMMENTS_ID:
                selection = ChurchContract.BlogCommentsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ChurchContract.BlogCommentsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
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
            case EVENT:
                return insertRecord(uri, values, ChurchContract.EventsEntry.TABLE_NAME);
            case SCHEDULE:
                return insertRecord(uri, values, ChurchContract.SchedulesEntry.TABLE_NAME);
            case SCHEDULEPAGES:
                return insertRecord(uri, values, ChurchContract.SchedulePagesEntry.TABLE_NAME);
            case DONATION:
                return insertRecord(uri, values, ChurchContract.DonationEntry.TABLE_NAME);
            case BLOGCATEGORY:
                return insertRecord(uri, values, ChurchContract.BlogCategoryEntry.TABLE_NAME);
            case BLOG:
                return insertRecord(uri, values, ChurchContract.BlogsEntry.TABLE_NAME);
            case BLOG_COMMENTS:
                return insertRecord(uri, values, ChurchContract.BlogCommentsEntry.TABLE_NAME);
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
            case EVENT:
                return deleteRecord(uri, null, null, ChurchContract.EventsEntry.TABLE_NAME);
            case EVENT_ID:
                return deleteRecord(uri, selection, selectionArgs, ChurchContract.EventsEntry.TABLE_NAME);
            case SCHEDULE:
                return deleteRecord(uri, null, null, ChurchContract.SchedulesEntry.TABLE_NAME);
            case SCHEDULE_ID:
                return deleteRecord(uri, selection, selectionArgs, ChurchContract.SchedulesEntry.TABLE_NAME);
            case SCHEDULEPAGES:
                return deleteRecord(uri, null, null, ChurchContract.SchedulePagesEntry.TABLE_NAME);
            case SCHEDULEPAGES_ID:
                return deleteRecord(uri, selection, selectionArgs, ChurchContract.SchedulePagesEntry.TABLE_NAME);
            case DONATION:
                return deleteRecord(uri, null, null, ChurchContract.DonationEntry.TABLE_NAME);
            case DONATION_ID:
                return deleteRecord(uri, selection, selectionArgs, ChurchContract.DonationEntry.TABLE_NAME);
            case BLOGCATEGORY:
                return deleteRecord(uri, null, null, ChurchContract.BlogCategoryEntry.TABLE_NAME);
            case BLOGCATEGORY_ID:
                return deleteRecord(uri, selection, selectionArgs, ChurchContract.BlogCategoryEntry.TABLE_NAME);
            case BLOG:
                return deleteRecord(uri, null, null, ChurchContract.BlogsEntry.TABLE_NAME);
            case BLOG_ID:
                return deleteRecord(uri, selection, selectionArgs, ChurchContract.BlogsEntry.TABLE_NAME);
            case BLOG_COMMENTS:
                return deleteRecord(uri, null, null, ChurchContract.BlogCommentsEntry.TABLE_NAME);
            case BLOG_COMMENTS_ID:
                return deleteRecord(uri, selection, selectionArgs, ChurchContract.BlogCommentsEntry.TABLE_NAME);
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
            case EVENT:
                return updateRecord(uri, values, selection, selectionArgs, ChurchContract.EventsEntry.TABLE_NAME);
            case SCHEDULE:
                return updateRecord(uri, values, selection, selectionArgs, ChurchContract.SchedulesEntry.TABLE_NAME);
            case SCHEDULEPAGES:
                return updateRecord(uri, values, selection, selectionArgs, ChurchContract.SchedulePagesEntry.TABLE_NAME);
            case DONATION:
                return updateRecord(uri, values, selection, selectionArgs, ChurchContract.DonationEntry.TABLE_NAME);
            case BLOGCATEGORY:
                return updateRecord(uri, values, selection, selectionArgs, ChurchContract.BlogCategoryEntry.TABLE_NAME);
            case BLOG:
                return updateRecord(uri, values, selection, selectionArgs, ChurchContract.BlogsEntry.TABLE_NAME);
            case BLOG_COMMENTS:
                return updateRecord(uri, values, selection, selectionArgs, ChurchContract.BlogCommentsEntry.TABLE_NAME);
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
