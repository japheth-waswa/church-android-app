package adapters.recyclerview.bible;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;

public class BibleVerseRecyclerViewAdapter extends RecyclerView.Adapter<BibleVerseViewHolder> {

    private Cursor cursor;

    public BibleVerseRecyclerViewAdapter(Cursor cursor) {
        this.cursor = cursor;
    }


    @Override
    public BibleVerseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View bibleVerseContainer = inflater.inflate(R.layout.item_bible_verse, parent, false);

        return new BibleVerseViewHolder(bibleVerseContainer);

    }


    @Override
    public void onBindViewHolder(BibleVerseViewHolder holder, int position) {
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
