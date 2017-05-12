package adapters.recyclerview.schedule;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;


public class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleViewHolder> {

    private Cursor cursor;

    public ScheduleRecyclerViewAdapter(Cursor cursor) {
        this.cursor = cursor;
    }


    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View customViewContainer = inflater.inflate(R.layout.item_schedule, parent, false);

        return new ScheduleViewHolder(customViewContainer);

    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, int position) {
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
