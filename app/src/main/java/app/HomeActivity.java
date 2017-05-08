package app;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.ActivityHomeBinding;


public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding activityHomeBinding;

    //private ArrayList<Navigation> navigations;
   // private NavigationRecyclerAdapter navigationRecyclerAdapter;


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

                //instantiate navigation items in ArrayList.
        /**navigations = new ArrayList<>();

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
        activityHomeBinding.homeNavigationItems.setItemAnimator(new DefaultItemAnimator());

        //set adapter
        activityHomeBinding.homeNavigationItems.setAdapter(navigationRecyclerAdapter);
        activityHomeBinding.homeNavigationItems.setChildDrawingOrderCallback(new FanChildDrawingOrderCallback(fanLayoutManager));

        //fan layout collapse views
        fanLayoutManager.collapseViews();**/

        activityHomeBinding.homeCircleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener(){
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                switch (menuButton.getId()){
                    case R.id.bibleMenu:
                        startNavigationActivity(0);
                        break;
                    case R.id.sermonsMenu:
                        startNavigationActivity(1);
                        break;
                    case R.id.eventsMenu:
                        startNavigationActivity(2);
                        break;
                    case R.id.scheduleMenu:
                        startNavigationActivity(3);
                        break;
                    case R.id.donateMenu:
                        startNavigationActivity(4);
                        break;
                    case R.id.galleryMenu:
                        startNavigationActivity(5);
                        break;
                    case R.id.newsFeedMenu:
                        startNavigationActivity(6);
                        break;
                    default:
                        return;
                }
            }
        });

        //add touch listener to recyclerview
         /**activityHomeBinding.homeNavigationItems.addOnItemTouchListener(new CustomRecyclerTouchListener(
                this, activityHomeBinding.homeNavigationItems, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //start new activity passing the position of the item selected
                Intent intent = new Intent(HomeActivity.this,NavActivity.class);
                intent.putExtra("navPosition",position);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));**/

        //prepare navigation items
        //prepareNavigationitems();

        //set onclick listener on brand logo
        /**activityHomeBinding.brandHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                fanLayoutManager.collapseViews();
            }
        });**/

    }

    private void startNavigationActivity(int position){
        //start new activity passing the position of the item selected
        Intent intent = new Intent(HomeActivity.this,NavActivity.class);
        intent.putExtra("navPosition",position);
        startActivity(intent);
    }

    /**private void prepareNavigationitems() {
        navigations.clear();
        for (int i = 0; i < 12; i++) {
            Navigation navigation = new Navigation();
            navigation.setTitle(NavigationItem.homeTitles(i));
            navigation.setColor(NavigationItem.getBgColor(i));
            navigation.setNavIcon(NavigationItem.homeIcon(i));
            navigations.add(navigation);
        }
        navigationRecyclerAdapter.notifyDataSetChanged();
    }**/

}
