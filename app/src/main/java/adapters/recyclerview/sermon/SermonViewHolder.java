package adapters.recyclerview.sermon;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.japhethwaswa.church.databinding.ItemSermonBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import db.ChurchContract;
import model.Sermon;
import model.dyno.ApplicationContextProvider;
import service.ChurchWebService;

public class SermonViewHolder extends RecyclerView.ViewHolder{

    private ItemSermonBinding itemSermonBinding;

    public SermonViewHolder(View itemView) {
        super(itemView);
        itemSermonBinding = DataBindingUtil.bind(itemView);
    }

    public void bind(Cursor cursor){

        Sermon sermon =new Sermon();
        if(cursor.isClosed() == false && cursor != null){
            sermon.setSermon_title(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_TITLE)));
            sermon.setSermon_image_url(ChurchWebService.getRootAbsoluteUrl(ApplicationContextProvider.getsContext(),cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_IMAGE_URL))));
            sermon.setSermon_brief_description(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_BRIEF_DESCRIPTION)));
            sermon.setSermon_audio_url(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_AUDIO_URL)));
            sermon.setSermon_video_url(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_VIDEO_URL)));
            sermon.setSermon_pdf_url(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_PDF_URL)));

            /**date format**/
            String sermonDate = "";
            String dateString = cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_DATE));
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date date = dtFormat.parse(dateString);
                SimpleDateFormat dtFormatOutPut = new SimpleDateFormat("EEE, d MMM yyyy");
                sermonDate =  dtFormatOutPut.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sermon.setSermon_date(sermonDate);
            /****/

            sermon.setSermon_created_at(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_CREATED_AT)));

        }

        itemSermonBinding.setSermon(sermon);
    }

}
