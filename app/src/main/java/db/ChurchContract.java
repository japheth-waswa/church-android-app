package db;

import android.net.Uri;
import android.provider.BaseColumns;

public class ChurchContract {
    public static final String CONTENT_AUTHORITY = "com.japhethwaswa.church.db.churchprovider";
    public static final String PATH_BIBLE_BOOK = "bible_book";
    public static final String PATH_BIBLE_CHAPTER = "bible_chapter";
    public static final String PATH_BIBLE_VERSE = "bible_verse";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final class BibleBookEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_BIBLE_BOOK);

        //Table name
        public static final String TABLE_NAME = "bible_book";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BIBLE_BOOK_VERSION = "bible_book_version";
        public static final String COLUMN_BIBLE_BOOK_CODE = "bible_book_code";
        public static final String COLUMN_BIBLE_BOOK_NUMBER = "bible_book_number";
        public static final String COLUMN_BIBLE_BOOK_NAME = "bible_book_name";
    }

    public static final class BibleChapterEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_BIBLE_CHAPTER);

        //Table name
        public static final String TABLE_NAME = "bible_chapter";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_CHAPTER_NUMBER = "chapter_number";
        public static final String COLUMN_CHAPTER_BOOK_CODE = "chapter_book_code";
        public static final String COLUMN_CHAPTER_CODE = "chapter_code";
    }
    public static final class BibleVerseEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_BIBLE_VERSE);

        //Table name
        public static final String TABLE_NAME = "bible_verse";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_VERSE_NUMBER = "verse_number";
        public static final String COLUMN_VERSE_CHAPTER_CODE = "verse_chapter_code";
        public static final String COLUMN_VERSE = "verse";
    }
}
