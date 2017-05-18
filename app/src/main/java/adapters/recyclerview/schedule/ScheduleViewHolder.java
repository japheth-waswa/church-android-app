package adapters.recyclerview.schedule;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.databinding.ItemScheduleBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import db.ChurchContract;
import model.SchedulePage;
import model.Sermon;
import model.dyno.ApplicationContextProvider;
import service.ChurchWebService;

public class ScheduleViewHolder extends RecyclerView.ViewHolder{

    private ItemScheduleBinding itemScheduleBinding;

    public ScheduleViewHolder(View itemView) {
        super(itemView);
        itemScheduleBinding = DataBindingUtil.bind(itemView);



        //webview settings
        itemScheduleBinding.scheduleWebView.getSettings().setJavaScriptEnabled(true);
    }

    public void bind(Cursor cursor){

        SchedulePage schedulePage = new SchedulePage();

        if(cursor.isClosed() == false && cursor != null){
            schedulePage.setPage_order(cursor.getString(cursor.getColumnIndex(ChurchContract.SchedulePagesEntry.COLUMN_PAGE_ORDER)));
            schedulePage.setPage_content(cursor.getString(cursor.getColumnIndex(ChurchContract.SchedulePagesEntry.COLUMN_PAGE_CONTENT)));
        }

        itemScheduleBinding.setSchedulePage(schedulePage);
        //todo error-ApplicationContext is null in ApplicationStatus(happens when at home and lauching a section of the app navigation)
        String html = "<html><head><link href=\"bootstrap.min.css\" type=\"text/css\" /></head><body>" + schedulePage.getPage_content()+"<script src=\"bootstrap.min.js\" type=\"text/javascript\"></script> </body></html>";
        itemScheduleBinding.scheduleWebView.loadDataWithBaseURL("file:///android_asset/",html,"text/html", "UTF-8", "");
    }

}
