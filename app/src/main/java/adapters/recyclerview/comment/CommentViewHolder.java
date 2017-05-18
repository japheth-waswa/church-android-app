package adapters.recyclerview.comment;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.japhethwaswa.church.databinding.ItemCommentBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import db.ChurchContract;
import model.BlogComment;


public class CommentViewHolder extends RecyclerView.ViewHolder{

    private ItemCommentBinding itemCommentBinding;

    public CommentViewHolder(View itemView) {
        super(itemView);
        itemCommentBinding = DataBindingUtil.bind(itemView);
    }

    public void bind(Cursor cursor){

        BlogComment blogComment = new BlogComment();
        if(cursor.isClosed() == false && cursor != null){
            blogComment.setNames(cursor.getString(cursor.getColumnIndex(ChurchContract.BlogCommentsEntry.COLUMN_NAMES)));
            blogComment.setMessage(cursor.getString(cursor.getColumnIndex(ChurchContract.BlogCommentsEntry.COLUMN_MESSAGE)));
            blogComment.setUploaded(cursor.getString(cursor.getColumnIndex(ChurchContract.BlogCommentsEntry.COLUMN_UPLOADED)));

            /**date format**/
            String customDate = "";
            String dateString = cursor.getString(cursor.getColumnIndex(ChurchContract.BlogCommentsEntry.COLUMN_CREATED_AT));
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date date = dtFormat.parse(dateString);
                SimpleDateFormat dtFormatOutPut = new SimpleDateFormat("EEE, d MMM yyyy");
                customDate =  dtFormatOutPut.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            blogComment.setCreated_at(customDate);
            /****/

        }

        itemCommentBinding.setBlogComment(blogComment);
    }

}
