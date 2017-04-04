package app;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.cleveroad.fanlayoutmanager.FanLayoutManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.ActivityHomeBinding;

import java.util.ArrayList;

import adapters.recyclerview.NavigationRecyclerAdapter;
import event.ClickListener;
import event.CustomRecyclerTouchListener;
import model.Navigation;
import model.NavigationItem;

public class NavActivity extends AppCompatActivity {
//todo start fragment to load each specific navigation and manage it
    /**private ActivityHomeBinding activityHomeBinding;

    private ArrayList<Navigation> navigations;
    private NavigationRecyclerAdapter navigationRecyclerAdapter;
    private FanLayoutManager fanLayoutManager;**/
    private int navPosition;


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

        //get data from HomeActivity
        Intent intent = getIntent();
        navPosition = intent.getIntExtra("navPosition",0);



        /**activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        navigations = new ArrayList<>();

        navigationRecyclerAdapter = new NavigationRecyclerAdapter(navigations);

        //fan layout manager
        FanLayoutManagerSettings fanLayoutManagerSettings = FanLayoutManagerSettings
                .newBuilder(this)
                .withFanRadius(true)
                .withAngleItemBounce(5)
                .withViewWidthDp(150)
                .withViewHeightDp(200)
                .build();
        fanLayoutManager = new FanLayoutManager(this, fanLayoutManagerSettings);

        //recyclerviea adapter
        activityHomeBinding.homeNavigationItems.setLayoutManager(fanLayoutManager);

        activityHomeBinding.homeNavigationItems.setAdapter(navigationRecyclerAdapter);

        //add touch listener to recyclerview
        activityHomeBinding.homeNavigationItems.addOnItemTouchListener(new CustomRecyclerTouchListener(
                this, activityHomeBinding.homeNavigationItems, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //prepare navigation items
        prepareNavigationitems();**/
    }

    private void prepareNavigationitems() {
        /**navigations.clear();
        for (int i = 0; i < 12; i++) {
            Navigation navigation = new Navigation();
            navigation.setTitle(NavigationItem.homeTitles(i));
            navigation.setColor(NavigationItem.getBgColor(i));
            navigation.setNavIcon(NavigationItem.homeIcon(i));
            navigations.add(navigation);
        }
        navigationRecyclerAdapter.notifyDataSetChanged();**/
    }


}
