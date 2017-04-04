package app;

import android.animation.Animator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.widget.Toast;

import com.cleveroad.fanlayoutmanager.FanLayoutManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.ActivityHomeBinding;

import java.util.ArrayList;

import adapters.recyclerview.NavigationRecyclerAdapter;
import model.Navigation;
import model.NavigationItem;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding activityHomeBinding;

    private ArrayList<Navigation> navigations;
    private NavigationRecyclerAdapter navigationRecyclerAdapter;
    private FanLayoutManager fanLayoutManager;


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

        //add click event to recyclerview
        //prepare navigation items
        prepareNavigationitems();
    }

    private void prepareNavigationitems() {
        navigations.clear();
        for (int i = 0; i < 12; i++) {
            Navigation navigation = new Navigation();
            navigation.setTitle(NavigationItem.homeTitles(i));
            navigation.setColor(NavigationItem.getBgColor(i));
            navigation.setNavIcon(NavigationItem.homeIcon(i));
            navigations.add(navigation);
        }
        navigationRecyclerAdapter.notifyDataSetChanged();
    }

    private String getBgColor(int i) {
        switch (i) {
            case 0:
                return "#CC5200";
            case 1:
                return "#E4006B";
            case 2:
                return "#659933";
            case 3:
                return "#884444";
            case 4:
                return "#0E4C00";
            case 5:
                return "#00A3CC";
            case 6:
                return "#B0B200";
            case 7:
                return "#980000";
            case 8:
                return "#985D00";
            case 9:
                return "#400098";
            case 10:
                return "#724C26";
            case 11:
                return "#B600E4";
            default:
                return "#B600E4";
        }
    }
}
