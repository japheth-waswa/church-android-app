package adapters.recyclerview.sermon;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.japhethwaswa.church.databinding.ItemSermonBinding;

import db.ChurchContract;
import model.Sermon;

public class SermonViewHolder extends RecyclerView.ViewHolder{

    private ItemSermonBinding itemSermonBinding;

    public SermonViewHolder(View itemView) {
        super(itemView);
        itemSermonBinding = DataBindingUtil.bind(itemView);
    }

    public void bind(Cursor cursor){

        Sermon sermon =new Sermon();
        sermon.setSermon_title(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_TITLE)));
        sermon.setSermon_image_url(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_IMAGE_URL)));
        sermon.setSermon_brief_description(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_BRIEF_DESCRIPTION)));
        sermon.setSermon_audio_url(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_AUDIO_URL)));
        sermon.setSermon_video_url(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_VIDEO_URL)));
        sermon.setSermon_pdf_url(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_PDF_URL)));
        sermon.setSermon_date(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_DATE)));
        sermon.setSermon_created_at(cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_CREATED_AT)));

        itemSermonBinding.setSermon(sermon);
    }

}
