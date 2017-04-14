package adapters.recyclerview;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.japhethwaswa.church.databinding.ItemBibleChapterBinding;

import db.ChurchContract;
import model.BibleChapter;

public class BibleChapterViewHolder extends RecyclerView.ViewHolder{

    private ItemBibleChapterBinding itemBibleChapterBinding;

    public BibleChapterViewHolder(View itemView) {
        super(itemView);
        itemBibleChapterBinding = DataBindingUtil.bind(itemView);
    }

    public void bind(Cursor cursor){

        BibleChapter bibleChapter = new BibleChapter();
        bibleChapter.setChapter_number("Chapter "+cursor.getString(cursor.getColumnIndex(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_NUMBER)));
        itemBibleChapterBinding.setBibleChapter(bibleChapter);

    }

}
