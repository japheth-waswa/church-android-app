package adapters.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;

import java.util.List;

import model.Navigation;

public class NavigationRecyclerAdapter extends RecyclerView.Adapter<NavigationItemHolder>{
    //create list of data
    List<Navigation> navigations;

    public NavigationRecyclerAdapter(List<Navigation> navigations) {
        this.navigations = navigations;
    }

    @Override
    public NavigationItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View navContainer = inflater.inflate(R.layout.item_navigation,parent,false);
        return new NavigationItemHolder(navContainer);
    }

    @Override
    public void onBindViewHolder(NavigationItemHolder holder, int position) {
        Navigation navigation = navigations.get(position);
        holder.bind(navigation);
    }

    @Override
    public int getItemCount() {
        return navigations.size();
    }
}
