package adapters.recyclerview.bible;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;

public class BibleChapterRecyclerViewAdapter extends RecyclerView.Adapter<BibleChapterViewHolder> {

    private Cursor cursor;

    public BibleChapterRecyclerViewAdapter(Cursor cursor) {
        this.cursor = cursor;
    }


    @Override
    public BibleChapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View bibleChapterContainer = inflater.inflate(R.layout.item_bible_chapter, parent, false);

        return new BibleChapterViewHolder(bibleChapterContainer);

    }


    @Override
    public void onBindViewHolder(BibleChapterViewHolder holder, int position) {
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
