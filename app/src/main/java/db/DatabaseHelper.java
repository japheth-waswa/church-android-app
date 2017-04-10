package db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import db.ChurchContract.BibleCollectionEntry;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "churchapp.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_BIBLECOLLECTION_CREATE=
            "CREATE TABLE " + BibleCollectionEntry.TABLE_NAME + " (" +
                    BibleCollectionEntry._ID + " INTEGER PRIMARY KEY, " +
                    BibleCollectionEntry.COLUMN_BIBLE_BOOK + " TEXT " +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_BIBLECOLLECTION_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BibleCollectionEntry.TABLE_NAME);
    }
}
