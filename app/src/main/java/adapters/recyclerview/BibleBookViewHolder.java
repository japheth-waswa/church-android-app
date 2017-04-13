package adapters.recyclerview;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.japhethwaswa.church.databinding.ItemBibleBookBinding;

import db.ChurchContract;
import model.BibleBook;

public class BibleBookViewHolder extends RecyclerView.ViewHolder{

    private ItemBibleBookBinding itemBibleBookBinding;

    public BibleBookViewHolder(View itemView) {
        super(itemView);
        itemBibleBookBinding = DataBindingUtil.bind(itemView);
    }

    public void bind(Cursor cursor){

        BibleBook bibleBook = new BibleBook();
        bibleBook.setBook_name(cursor.getString(cursor.getColumnIndex(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NAME)));
        itemBibleBookBinding.setBibleBook(bibleBook);
    }

}
