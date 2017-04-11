package adapters.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;

import fragment.bible.BibleBookFragment;

public class BibleBookOldRecyclerViewAdapter extends RecyclerView.Adapter<BibleBookOldViewHolder> {

    private Cursor cursor;

    public BibleBookOldRecyclerViewAdapter(Cursor cursor) {
        this.cursor = cursor;
    }


    @Override
    public BibleBookOldViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View bibleBookContainer = inflater.inflate(R.layout.item_bible_book_old, parent, false);

        return new BibleBookOldViewHolder(bibleBookContainer);

    }

    @Override
    public void onBindViewHolder(BibleBookOldViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.bind(cursor);
    }

    @Override
    public int getItemCount() {
        if(cursor == null){
            return 0;
        }
        return cursor.getCount();
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
