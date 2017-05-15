package adapters.recyclerview.blog;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;

import adapters.recyclerview.sermon.SermonViewHolder;


public class BlogRecyclerViewAdapter extends RecyclerView.Adapter<BlogViewHolder> {

    private Cursor cursor;

    public BlogRecyclerViewAdapter(Cursor cursor) {
        this.cursor = cursor;
    }


    @Override
    public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View customViewContainer = inflater.inflate(R.layout.item_blog, parent, false);

        return new BlogViewHolder(customViewContainer);

    }

    @Override
    public void onBindViewHolder(BlogViewHolder holder, int position) {
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
