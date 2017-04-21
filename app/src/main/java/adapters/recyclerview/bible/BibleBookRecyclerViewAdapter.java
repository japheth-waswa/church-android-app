package adapters.recyclerview.bible;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;

public class BibleBookRecyclerViewAdapter extends RecyclerView.Adapter<BibleBookViewHolder> {

    private Cursor cursor;

    public BibleBookRecyclerViewAdapter(Cursor cursor) {
        this.cursor = cursor;
    }


    @Override
    public BibleBookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View bibleBookContainer = inflater.inflate(R.layout.item_bible_book, parent, false);

        return new BibleBookViewHolder(bibleBookContainer);

    }

    @Override
    public void onBindViewHolder(BibleBookViewHolder holder, int position) {
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
