package adapters.recyclerview.blog;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.japhethwaswa.church.databinding.ItemBlogBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import db.ChurchContract;
import model.Blog;
import model.dyno.ApplicationContextProvider;
import service.ChurchWebService;

public class BlogViewHolder extends RecyclerView.ViewHolder{

private ItemBlogBinding itemBlogBinding;

    public BlogViewHolder(View itemView) {
        super(itemView);
        itemBlogBinding = DataBindingUtil.bind(itemView);
    }

    public void bind(Cursor cursor){

        Blog blog = new Blog();
        if(cursor.isClosed() == false && cursor != null){
            blog.setBlog_title(cursor.getString(cursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_TITLE)));
            blog.setImage_url(ChurchWebService.getRootAbsoluteUrl(ApplicationContextProvider.getsContext(),cursor.getString(cursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_IMAGE_URL))));
            blog.setBrief_description(cursor.getString(cursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_BRIEF_DESCRIPTION)));

            /**date format**/
            String blogDate = "";
            String dateString = cursor.getString(cursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_PUBLISH_DATE));
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date date = dtFormat.parse(dateString);
                SimpleDateFormat dtFormatOutPut = new SimpleDateFormat("EEE, d MMM yyyy");
                blogDate =  dtFormatOutPut.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            blog.setPublish_date(blogDate);
            /****/

        }

        itemBlogBinding.setBlog(blog);
    }

}
