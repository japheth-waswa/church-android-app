package app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cleveroad.loopbar.widget.OnItemClickListener;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.ActivityNavBinding;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import es.dmoral.toasty.Toasty;
import event.pojo.ConnectionStatus;
import event.pojo.DownloadSermonPdf;
import event.pojo.DownloadSermonPdfStatus;
import event.pojo.NavActivityColor;
import event.pojo.NavActivityHideNavigation;
import event.pojo.SermonDataRetrievedSaved;
import fragment.bible.BibleFragment;
import fragment.sermon.SermonFragment;
import model.dyno.ApplicationContextProvider;

public class NavActivity extends AppCompatActivity {
    //todo in each fragment handle screen orientation appropriately
    private ActivityNavBinding activityNavBinding;
    private int navPosition;
    public FragmentManager fragmentManager = getSupportFragmentManager();
    //public JobManager jobManager;

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
        navPosition = intent.getIntExtra("navPosition", 0);

        //inflate activity
        activityNavBinding = DataBindingUtil.setContentView(this, R.layout.activity_nav);

        //initiate job
        //jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getApplicationContext()));
        //jobManager.addJobInBackground(new SermonsJob());

        //determine if orientation change or activity creation
        if (savedInstanceState == null) {
            //set the default selected menu item
            int newNavPosition = navPosition + 1;
            activityNavBinding.mainNavigationView.setCurrentItem(newNavPosition);

            //start fragment
            handleMenuClicks(newNavPosition);

        }

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
        //ensure if home clicked you load HomeActivity ie super.backpressed
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                super.onBackPressed();
                finish();
                break;
            case 1:

                //sermons
                SermonFragment sermonFragment = new SermonFragment();
                fragmentTransaction.replace(R.id.navFragmentHolder, sermonFragment, "sermonFragment");
                fragmentTransaction.commit();
                break;

            case 2:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 3:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 4:

                //bible
                BibleFragment bibleFragment = new BibleFragment();
                fragmentTransaction.replace(R.id.navFragmentHolder, bibleFragment, "bibleFragment");
                fragmentTransaction.commit();
                break;

            case 5:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 6:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 7:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 8:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 9:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 10:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 11:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 12:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            default:
                super.onBackPressed();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("customOrientationChange", 1);
    }


    public void startPdfDownload(View view) {
        //post event
        EventBus.getDefault().post(new DownloadSermonPdf());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionStatus(ConnectionStatus connectionStatus) {
        if (connectionStatus.isConnected() == false) {
            //notify user to enable internet connection
            Toasty.error(this, "Please check your internet connection.", Toast.LENGTH_SHORT, true).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadSermonPdfStatus(DownloadSermonPdfStatus event) {

        //notify download started
        if (event.getStatus() == 0)
            Toasty.info(this, "Pdf download started.Please wait.", Toast.LENGTH_SHORT, true).show();

        //notify download success
        if (event.getStatus() == 1)
            Toasty.success(this, "Pdf download successful.", Toast.LENGTH_SHORT, true).show();

        //notify download error
        if (event.getStatus() == 2)
            Toasty.error(this, "An error occurred while downloading.", Toast.LENGTH_SHORT, true).show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNavActivityColor(NavActivityColor event) {
        //activityNavBinding.getRoot().setBackgroundColor(ContextCompat.getColor(this,event.getColor()));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNavActivityHideNav(NavActivityHideNavigation event) {
        if (event.isHide()) {
            activityNavBinding.mainNavigationView.animate()
                    .translationY(activityNavBinding.mainNavigationView.getHeight())
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            activityNavBinding.mainNavigationView.setVisibility(View.GONE);
                        }
                    });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        //register event
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        //unregister event
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //((ApplicationContextProvider)getApplication()).getRefWatcher().watch(this);
    }
}
