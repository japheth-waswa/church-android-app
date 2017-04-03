package app;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.cleveroad.fanlayoutmanager.FanLayoutManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.ActivityHomeBinding;

import java.util.ArrayList;

import adapters.recyclerview.NavigationRecyclerAdapter;
import model.Navigation;

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
                .withViewWidthDp(200)
                .withViewHeightDp(150)
                .build();
        fanLayoutManager =  new FanLayoutManager(this,fanLayoutManagerSettings);

        //recyclerviea adapter
        activityHomeBinding.homeNavigationItems.setLayoutManager(fanLayoutManager);

        activityHomeBinding.homeNavigationItems.setAdapter(navigationRecyclerAdapter);



        //prepare navigation items
        prepareNavigationitems();
    }

    private void prepareNavigationitems() {
        navigations.clear();
        for (int i = 0; i < 12; i++) {
            Navigation navigation = new Navigation();
            navigation.setTitle("title-" + String.valueOf(i));
            navigation.setColor(getBgColor(i));
            navigation.setDescription("desc-" + String.valueOf(i));
            navigation.setNavIcon(R.drawable.ic_login_nav);
            navigations.add(navigation);
        }
        navigationRecyclerAdapter.notifyDataSetChanged();
    }

    private String getBgColor(int i) {
        switch (i){
            case 0:
                return "#B600E4";
            case 1:
                return "#E4006B";
            case 2:
                return "#659933";
            case 3:
                return "#884444";
            case 4:
                return "#CC5200";
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
                return "#0E4C00";
            default:
                return "#B600E4";
        }
    }
}
