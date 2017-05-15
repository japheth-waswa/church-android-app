package db;

import android.net.Uri;
import android.provider.BaseColumns;

public class ChurchContract {
    public static final String CONTENT_AUTHORITY = "com.japhethwaswa.church.db.churchprovider";
    public static final String PATH_SERMONS = "sermons";
    public static final String PATH_EVENT_CATEGORIES = "event_categories";
    public static final String PATH_EVENTS = "events";
    public static final String PATH_SCHEDULES = "schedules";
    public static final String PATH_SCHEDULE_PAGES = "schedule_pages";
    public static final String PATH_DONATION = "donation";
    public static final String PATH_BLOG_CATEGORIES = "blog_categories";
    public static final String PATH_BLOG = "blogs";
    public static final String PATH_BLOG_COMMENTS = "blog_comments";
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

    public static final class EventsEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_EVENTS);

        //Table name
        public static final String TABLE_NAME = "events";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_TITLE = "event_title";
        public static final String COLUMN_IMAGE_URL= "image_url";
        public static final String COLUMN_BRIEF_DESCRIPTION= "brief_description";
        public static final String COLUMN_CONTENT= "content";
        public static final String COLUMN_EVENT_DATE= "event_date";
        public static final String COLUMN_EVENT_LOCATION= "event_location";
        public static final String COLUMN_EVENT_CATEGORY_ID= "event_category_id";
        public static final String COLUMN_VISIBLE= "visible";
        public static final String COLUMN_CREATED_AT= "created_at";
        public static final String COLUMN_UPDATED_AT= "updated_at";
    }

    public static final class SchedulesEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_SCHEDULES);

        //Table name
        public static final String TABLE_NAME = "schedules";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SCHEDULE_ID = "schedule_id";
        public static final String COLUMN_THEME_TITLE = "theme_title";
        public static final String COLUMN_THEME_DESCRIPTION = "theme_description";
        public static final String COLUMN_SUNDAY_DATE= "sunday_date";
        public static final String COLUMN_COLUMN_COUNT= "column_count";
        public static final String COLUMN_VISIBLE= "visible";
        public static final String COLUMN_CREATED_AT= "created_at";
        public static final String COLUMN_UPDATED_AT= "updated_at";
    }

    public static final class SchedulePagesEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_SCHEDULE_PAGES);

        //Table name
        public static final String TABLE_NAME = "schedule_pages";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SCHEDULE_PAGES_ID = "schedule_pages_id";
        public static final String COLUMN_PAGE_CONTENT = "page_content";
        public static final String COLUMN_SUNDAY_SCHEDULE_ID = "sunday_schedule_id";
        public static final String COLUMN_PAGE_ORDER= "page_order";
        public static final String COLUMN_VISIBLE= "visible";
        public static final String COLUMN_CREATED_AT= "created_at";
        public static final String COLUMN_UPDATED_AT= "updated_at";
    }

    public static final class DonationEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_DONATION);

        //Table name
        public static final String TABLE_NAME = "donation";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_DONATION_ID = "donation_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_DESCRIPTION= "description";
        public static final String COLUMN_CONTENT= "content";
        public static final String COLUMN_FACEBOOK= "facebook_url";
        public static final String COLUMN_TWITTER= "twitter_url";
        public static final String COLUMN_YOUTUBE= "youtube_url";
        public static final String COLUMN_VISIBLE= "visible";
        public static final String COLUMN_CREATED_AT= "created_at";
        public static final String COLUMN_UPDATED_AT= "updated_at";
    }

    public static final class BlogCategoryEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_BLOG_CATEGORIES);

        //Table name
        public static final String TABLE_NAME = "blog_categories";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BLOG_CATEGORY_ID = "blog_category_id";
        public static final String COLUMN__TITLE = "title";
        public static final String COLUMN_URL_KEY= "url_key";
        public static final String COLUMN_DESCRIPTION= "description";
        public static final String COLUMN_VISIBLE= "visible";
        public static final String COLUMN_CREATED_AT= "created_at";
        public static final String COLUMN_UPDATED_AT= "updated_at";
    }

    public static final class BlogsEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_BLOG);

        //Table name
        public static final String TABLE_NAME = "blogs";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BLOG_ID = "blog_id";
        public static final String COLUMN_TITLE = "blog_title";
        public static final String COLUMN_URL_KEY= "url_key";
        public static final String COLUMN_IMAGE_URL= "image_url";
        public static final String COLUMN_BRIEF_DESCRIPTION= "brief_description";
        public static final String COLUMN_CONTENT= "content";
        public static final String COLUMN_AUTHOR_NAME= "author_name";
        public static final String COLUMN_PUBLISH_DATE= "publish_date";
        public static final String COLUMN_BLOG_CATEGORY_ID= "blog_category_id";
        public static final String COLUMN_VISIBLE= "visible";
        public static final String COLUMN_CREATED_AT= "created_at";
        public static final String COLUMN_UPDATED_AT= "updated_at";
    }

    public static final class BlogCommentsEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_BLOG_COMMENTS);

        //Table name
        public static final String TABLE_NAME = "blog_comments";
        //column field names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAMES = "names";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHONE= "phone";
        public static final String COLUMN_MESSAGE= "message";
        public static final String COLUMN_BLOG_ID= "blog_id";
        public static final String COLUMN_VIEWED= "viewed";
        public static final String COLUMN_VISIBLE= "visible";
        public static final String COLUMN_CREATED_AT= "created_at";
        public static final String COLUMN_UPDATED_AT= "updated_at";
    }

}
