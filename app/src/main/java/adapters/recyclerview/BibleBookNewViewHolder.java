package adapters.recyclerview;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.japhethwaswa.church.databinding.ItemBibleBookNewBinding;
import com.japhethwaswa.church.databinding.ItemBibleBookOldBinding;

import db.ChurchContract;
import model.BibleBook;

public class BibleBookNewViewHolder extends RecyclerView.ViewHolder{

    private ItemBibleBookNewBinding itemBibleBookNewBinding;

    public BibleBookNewViewHolder(View itemView) {
        super(itemView);
        itemBibleBookNewBinding = DataBindingUtil.bind(itemView);
    }

    public void bind(Cursor cursor){

        BibleBook bibleBook = new BibleBook();
        bibleBook.setBook_name(cursor.getString(cursor.getColumnIndex(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NAME)));
        itemBibleBookNewBinding.setBibleBook(bibleBook);
    }

}
