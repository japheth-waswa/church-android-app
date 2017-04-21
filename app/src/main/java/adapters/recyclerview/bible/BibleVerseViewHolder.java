package adapters.recyclerview.bible;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.japhethwaswa.church.databinding.ItemBibleChapterBinding;
import com.japhethwaswa.church.databinding.ItemBibleVerseBinding;

import db.ChurchContract;
import model.BibleChapter;
import model.BibleVerse;

public class BibleVerseViewHolder extends RecyclerView.ViewHolder{

    private ItemBibleVerseBinding itemBibleVerseBinding;

    public BibleVerseViewHolder(View itemView) {
        super(itemView);
        itemBibleVerseBinding = DataBindingUtil.bind(itemView);
    }

    public void bind(Cursor cursor){

        BibleVerse bibleVerse = new BibleVerse();
        bibleVerse.setVerse(cursor.getString(cursor.getColumnIndex(ChurchContract.BibleVerseEntry.COLUMN_VERSE)));
        bibleVerse.setVerse_number(cursor.getString(cursor.getColumnIndex(ChurchContract.BibleVerseEntry.COLUMN_VERSE_NUMBER)));
        itemBibleVerseBinding.setBibleVerse(bibleVerse);

    }

}
