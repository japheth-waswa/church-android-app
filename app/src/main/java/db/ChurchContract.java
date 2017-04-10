package db;

import android.net.Uri;
import android.provider.BaseColumns;

public class ChurchContract {
    public static final String CONTENT_AUTHORITY = "com.japhethwaswa.church.db.churchprovider";
    public static final String PATH_BIBLECOLLECTION = "bible_collection";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final class BibleCollectionEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_BIBLECOLLECTION);

        //Table name
        public static final String TABLE_NAME = "bible_collection";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BIBLE_BOOK = "bible_book";
    }
}
