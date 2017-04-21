package adapters.recyclerview.sermon;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;

import adapters.recyclerview.bible.BibleBookViewHolder;

public class SermonRecyclerViewAdapter extends RecyclerView.Adapter<SermonViewHolder> {

    private Cursor cursor;

    public SermonRecyclerViewAdapter(Cursor cursor) {
        this.cursor = cursor;
    }


    @Override
    public SermonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View customViewContainer = inflater.inflate(R.layout.item_sermon, parent, false);

        return new SermonViewHolder(customViewContainer);

    }

    @Override
    public void onBindViewHolder(SermonViewHolder holder, int position) {
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
