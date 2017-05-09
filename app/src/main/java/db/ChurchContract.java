package db;

import android.net.Uri;
import android.provider.BaseColumns;

public class ChurchContract {
    public static final String CONTENT_AUTHORITY = "com.japhethwaswa.church.db.churchprovider";
    public static final String PATH_SERMONS = "sermons";
    public static final String PATH_EVENT_CATEGORIES = "event_categories";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);


    public static final class SermonEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_SERMONS);

        //Table name
        public static final String TABLE_NAME = "sermons";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SERMON_ID = "sermon_id";
        public static final String COLUMN_SERMON_TITLE = "sermon_title";
        public static final String COLUMN_SERMON_IMAGE_URL= "sermon_image_url";
        public static final String COLUMN_SERMON_BRIEF_DESCRIPTION= "sermon_brief_description";
        public static final String COLUMN_SERMON_AUDIO_URL= "sermon_audio_url";
        public static final String COLUMN_SERMON_VIDEO_URL= "sermon_video_url";
        public static final String COLUMN_SERMON_PDF_URL= "sermon_pdf_url";
        public static final String COLUMN_SERMON_DATE= "sermon_date";
        public static final String COLUMN_SERMON_VISIBLE= "sermon_visible";
        public static final String COLUMN_SERMON_CREATED_AT= "sermon_created_at";
        public static final String COLUMN_SERMON_UPDATED_AT= "sermon_updated_at";
    }

    public static final class EventCategoryEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_EVENT_CATEGORIES);

        //Table name
        public static final String TABLE_NAME = "event_categories";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_EVENT_CATEGORY_ID = "event_category_id";
        public static final String COLUMN__TITLE = "title";
        public static final String COLUMN_URL_KEY= "url_key";
        public static final String COLUMN_DESCRIPTION= "description";
        public static final String COLUMN_VISIBLE= "visible";
        public static final String COLUMN_CREATED_AT= "created_at";
        public static final String COLUMN_UPDATED_AT= "updated_at";
    }
}
