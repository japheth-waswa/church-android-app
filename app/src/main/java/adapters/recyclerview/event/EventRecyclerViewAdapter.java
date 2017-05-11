package adapters.recyclerview.event;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;


public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private Cursor cursor;

    public EventRecyclerViewAdapter(Cursor cursor) {
        this.cursor = cursor;
    }


    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View customViewContainer = inflater.inflate(R.layout.item_event, parent, false);

        return new EventViewHolder(customViewContainer);

    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        if(cursor.isClosed() == false && cursor != null){
            cursor.moveToPosition(position);
            holder.bind(cursor);
        }
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
