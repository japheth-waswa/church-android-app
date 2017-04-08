package app;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
import com.cleveroad.fanlayoutmanager.callbacks.FanChildDrawingOrderCallback;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.ActivityHomeBinding;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import adapters.recyclerview.NavigationRecyclerAdapter;
import event.ClickListener;
import event.CustomRecyclerTouchListener;
import job.GetBibleData;
import job.builder.MyJobsBuilder;
import model.Navigation;
import model.NavigationItem;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding activityHomeBinding;

    private ArrayList<Navigation> navigations;
    private NavigationRecyclerAdapter navigationRecyclerAdapter;
    private FanLayoutManager fanLayoutManager;
    private JobManager jobManager;


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

        //instantiate job manager
        jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getApplicationContext()));
        jobManager.addJobInBackground(new GetBibleData());

                //instantiate navigation items in ArrayList.
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
        activityHomeBinding.homeNavigationItems.setItemAnimator(new DefaultItemAnimator());

        //set adapter
        activityHomeBinding.homeNavigationItems.setAdapter(navigationRecyclerAdapter);
        activityHomeBinding.homeNavigationItems.setChildDrawingOrderCallback(new FanChildDrawingOrderCallback(fanLayoutManager));

        //fan layout collapse views
        fanLayoutManager.collapseViews();

        //add touch listener to recyclerview
         activityHomeBinding.homeNavigationItems.addOnItemTouchListener(new CustomRecyclerTouchListener(
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
        }));

        //prepare navigation items
        prepareNavigationitems();

        //set onclick listener on brand logo
        activityHomeBinding.brandHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                fanLayoutManager.collapseViews();
            }
        });

        //performBibleMining();

    }

    /**private void performBibleMining() {
        //todo perform in background thread
        try {
            File fileName = new File(getFilesDir().getPath() + "/bible_books.txt");
            FileOutputStream fOut = new FileOutputStream(fileName,true);
            //FileOutputStream fOut = openFileOutput("bible_books", Context.MODE_PRIVATE);
            String str = "Hello world 3";
            fOut.write(str.getBytes());
            fOut.close();

            FileInputStream fin = new FileInputStream(fileName);
            int c;
            String temp = "";
            while((c = fin.read()) != -1){
                temp = temp + Character.toString((char) c);
            }
            fin.close();
            Log.e("jeff-waswa",temp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }**/

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

}
