package adapters.recyclerview.schedule;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.japhethwaswa.church.databinding.ItemScheduleBinding;
import com.japhethwaswa.church.databinding.ItemSermonBinding;

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
        String html = "<html><body>" + schedulePage.getPage_content()+"</body></html>";
        Log.e("jean-html",html);
        //itemScheduleBinding.scheduleWebView.loadData(html, "text/html", "utf-8");
        itemScheduleBinding.scheduleWebView.loadDataWithBaseURL(null,html,"text/html", "UTF-8", "");
    }

}
