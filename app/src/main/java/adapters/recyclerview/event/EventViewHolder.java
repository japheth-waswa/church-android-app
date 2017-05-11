package adapters.recyclerview.event;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.japhethwaswa.church.databinding.ItemEventBinding;
import com.japhethwaswa.church.databinding.ItemSermonBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import db.ChurchContract;
import model.Event;
import model.dyno.ApplicationContextProvider;
import service.ChurchWebService;

public class EventViewHolder extends RecyclerView.ViewHolder{

    private ItemEventBinding itemEventBinding;

    public EventViewHolder(View itemView) {
        super(itemView);
        itemEventBinding = DataBindingUtil.bind(itemView);
    }

    public void bind(Cursor cursor){

        Event event = new Event();

        if(cursor.isClosed() == false && cursor != null){
            event.setEvent_title(cursor.getString(cursor.getColumnIndex(ChurchContract.EventsEntry.COLUMN_TITLE)));
            event.setImage_url(ChurchWebService.getRootAbsoluteUrl(ApplicationContextProvider.getsContext(),cursor.getString(cursor.getColumnIndex(ChurchContract.EventsEntry.COLUMN_IMAGE_URL))));
            event.setBrief_description(cursor.getString(cursor.getColumnIndex(ChurchContract.EventsEntry.COLUMN_BRIEF_DESCRIPTION)));

            /**date format**/
            String eventDate = "";
            String dateString = cursor.getString(cursor.getColumnIndex(ChurchContract.EventsEntry.COLUMN_EVENT_DATE));
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date date = dtFormat.parse(dateString);
                //todo add time ie pm,am
                SimpleDateFormat dtFormatOutPut = new SimpleDateFormat("EEE, d MMM yyyy");
                eventDate =  dtFormatOutPut.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            event.setEvent_date(eventDate);
            /****/

            event.setCreated_at(cursor.getString(cursor.getColumnIndex(ChurchContract.EventsEntry.COLUMN_CREATED_AT)));

        }

        itemEventBinding.setEvent(event);
    }

}
