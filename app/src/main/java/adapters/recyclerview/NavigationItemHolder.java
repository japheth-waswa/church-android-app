package adapters.recyclerview;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.japhethwaswa.church.databinding.ItemNavigationBinding;

import model.Navigation;

public class NavigationItemHolder extends RecyclerView.ViewHolder {

    private ItemNavigationBinding itemNavigationBinding;

    public NavigationItemHolder(View view) {
        super(view);
        itemNavigationBinding = DataBindingUtil.bind(view);
    }

    public void bind(Navigation navigation) {
        itemNavigationBinding.setNavigation(navigation);
    }
}
