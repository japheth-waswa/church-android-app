package db;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;

public class ChurchQueryHandler extends AsyncQueryHandler{
    public ChurchQueryHandler(ContentResolver cr) {
        super(cr);
    }
}
