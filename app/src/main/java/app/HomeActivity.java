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


        activityHomeBinding.homeCircleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener(){
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                switch (menuButton.getId()){

                    case R.id.sermonsMenu:
                        startNavigationActivity(0);
                        break;
                    case R.id.eventsMenu:
                        startNavigationActivity(1);
                        break;
                    case R.id.scheduleMenu:
                        startNavigationActivity(2);
                        break;
                    case R.id.donateMenu:
                        startNavigationActivity(3);
                        break;
                    case R.id.newsFeedMenu:
                        startNavigationActivity(4);
                        break;
                    default:
                        return;
                }
            }
        });
        

    }

    private void startNavigationActivity(int position){
        //start new activity passing the position of the item selected
        Intent intent = new Intent(HomeActivity.this,NavActivity.class);
        intent.putExtra("navPosition",position);
        startActivity(intent);
    }

}
