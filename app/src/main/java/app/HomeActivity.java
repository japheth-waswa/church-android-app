package app;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.ActivityHomeBinding;

import java.util.ArrayList;

import adapters.recyclerview.NavigationRecyclerAdapter;
import model.Navigation;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding activityHomeBinding;

    private ArrayList<Navigation> navigations;
    private NavigationRecyclerAdapter navigationRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //StrictMode
        StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setVmPolicy(vmPolicy);
        /**==============**/
        super.onCreate(savedInstanceState);

        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        navigations = new ArrayList<>();

        navigationRecyclerAdapter = new NavigationRecyclerAdapter(navigations);

        activityHomeBinding.homeNavigationItems.setLayoutManager(new LinearLayoutManager(this));
        activityHomeBinding.homeNavigationItems.setAdapter(navigationRecyclerAdapter);

        //prepare navigation items
        prepareNavigationitems();
    }

    private void prepareNavigationitems() {
        navigations.clear();
        for (int i = 0; i < 20; i++) {
            Navigation navigation = new Navigation();
            navigation.setTitle("title-" + String.valueOf(i));
            navigation.setColor("color-" + String.valueOf(i));
            navigation.setDescription("desc-" + String.valueOf(i));
            navigation.setDrawable("drawable-" + String.valueOf(i));
            navigations.add(navigation);
        }
        navigationRecyclerAdapter.notifyDataSetChanged();
    }
}
