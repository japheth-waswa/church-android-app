package app;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.cleveroad.fanlayoutmanager.FanLayoutManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
import com.cleveroad.loopbar.adapter.SimpleCategoriesAdapter;
import com.cleveroad.loopbar.model.MockedItemsFactory;
import com.cleveroad.loopbar.widget.OnItemClickListener;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.ActivityHomeBinding;
import com.japhethwaswa.church.databinding.ActivityNavBinding;

import java.util.ArrayList;

import adapters.recyclerview.NavigationRecyclerAdapter;
import event.ClickListener;
import event.CustomRecyclerTouchListener;
import model.Navigation;
import model.NavigationItem;

public class NavActivity extends AppCompatActivity {
//todo start fragment to load each specific navigation and manage it
    //todo remember to set current menu item as received from HomeActivity
    //todo in each fragment handle screen orientation appropriately
    private ActivityNavBinding activityNavBinding;

    /**private ArrayList<Navigation> navigations;
    private NavigationRecyclerAdapter navigationRecyclerAdapter;
    private FanLayoutManager fanLayoutManager;**/
    private int navPosition;
    private SimpleCategoriesAdapter simpleCategoriesAdapter;


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



        activityNavBinding = DataBindingUtil.setContentView(this, R.layout.activity_nav);

        //work with the items
        activityNavBinding.mainNavigationView.addOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                handleMenuClicks(position);
            }
        });

    }

    //handle menu clicks
    private void handleMenuClicks(int position) {
        //todo ensure if home clicked you load HomeActivity ie super.backpressed
        switch (position){
            case 0:
                super.onBackPressed();
                finish();
                return;
            default:
                Log.e("jeff-waswa",String.valueOf(position));
        }
        //todo use switch statement to load each different fragment
        //todo mark the current selected menu item
    }


}
