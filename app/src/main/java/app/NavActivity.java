package app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cleveroad.loopbar.widget.OnItemClickListener;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.ActivityNavBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import event.pojo.ConnectionStatus;
import event.pojo.DownloadSermonPdf;
import event.pojo.DownloadSermonPdfStatus;
import event.pojo.DynamicToastStatusUpdate;
import event.pojo.NavActivityColor;
import event.pojo.NavActivityHideNavigation;
import fragment.donation.DonationFragment;
import fragment.event.EventFragment;
import fragment.schedule.ScheduleFragment;
import fragment.sermon.SermonFragment;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class NavActivity extends AppCompatActivity {

    private static final int ALL_PERMISSIONS_RESULT = 100;
    private ActivityNavBinding activityNavBinding;
    private int navPosition;
    public FragmentManager fragmentManager = getSupportFragmentManager();
    private SharedPreferences sharedPreferences;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected;
    private ArrayList<String> permissions = new ArrayList<>();
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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


        //set up permissions needed
        permissionsNeeded();

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
                //event
                EventFragment eventFragment = new EventFragment();
                fragmentTransaction.replace(R.id.navFragmentHolder, eventFragment, "eventFragment");
                fragmentTransaction.commit();
                break;
            case 3:
                //schedule
                ScheduleFragment scheduleFragment = new ScheduleFragment();
                fragmentTransaction.replace(R.id.navFragmentHolder, scheduleFragment, "scheduleFragment");
                fragmentTransaction.commit();
                break;
            case 4:
                //donation
                DonationFragment donationFragment = new DonationFragment();
                fragmentTransaction.replace(R.id.navFragmentHolder, donationFragment, "donationFragment");
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
    public void onDynamicToastStatusUpdate(DynamicToastStatusUpdate event) {

        //info
        if (event.getStatus() == 0)
            Toasty.info(this, event.getStatusMessage(), Toast.LENGTH_SHORT, true).show();

        //warning
        if (event.getStatus() == 1)
            Toasty.warning(this,event.getStatusMessage(), Toast.LENGTH_SHORT, true).show();

        //success
        if (event.getStatus() == 2)
            Toasty.success(this,event.getStatusMessage(), Toast.LENGTH_SHORT, true).show();

        //error
        if (event.getStatus() == 2)
            Toasty.error(this,event.getStatusMessage(), Toast.LENGTH_SHORT, true).show();

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        //permissions
        handlePermissionRequests();
    }
/** dangerous permissions api23>**/

    /**permissions needed**/
    private void permissionsNeeded(){
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(WRITE_EXTERNAL_STORAGE);
    }

    /**handle permission requests**/
    private void handlePermissionRequests(){
        //filter permissions we have already accepted.
        permissionsToRequest = findUnAskedPermissions(permissions);

        //get the permissions we had asked for before but are not granted..
        //we will store this in a global list to access  later
        permissionsRejected = findRejectedPermissions(permissions);

        //we need to ask for permissions.But have we already asked for them ?
        if(permissionsToRequest.size() > 0){
            //request permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),ALL_PERMISSIONS_RESULT);
            }

            //mark all these as read
            for(String perm : permissionsToRequest){
                markAsAsked(perm);
            }

        }else{
            //we have permissions but be careful some could have been rejected
            if(permissionsRejected.size() > 0){
                Toasty.warning(this,String.valueOf(permissionsRejected.size()) + " permission(s) were previously rejected").show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case ALL_PERMISSIONS_RESULT:
                boolean someAccepted = false;
                boolean someRejected = false;
                for(String perms : permissionsToRequest){
                    if(hasPermission(perms)){
                        someAccepted = true;
                    }else {
                        someRejected = true;
                        permissionsRejected.add(perms);
                    }
                }

                if(permissionsRejected.size() > 0){
                    someRejected = true;
                }

                //use the statuses ie someAccepted/someRejected where appropriate

                break;
        }
    }

    /**
     * return whether the permission is accepted.By default it is true if using device below api23
     **/
    private boolean hasPermission(String permission) {

        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }

        return true;
    }



    /**
     * determine whether we have asked for this permission before...if we have,we do not want to ask again.
     * They either rejected us or later removed the permission
     **/
    private boolean shouldWeAsk(String permission) {
        return (sharedPreferences.getBoolean(permission, true));
    }

    /**
     * save that we have already asked the user
     **/
    private void markAsAsked(String permission) {
        sharedPreferences.edit().putBoolean(permission, false).apply();
    }

    /**
     * we may want to ask the user again at their request..Let's clear the marked as seen preference fo taht permission
     **/
    private void clearMarkAsAsked(String permission) {
        sharedPreferences.edit().putBoolean(permission, true).apply();
    }

    /**
     * Determine permissions we have not accepted and the ones we have not already asked the user about.
     * This comes in handy when asking for multiple permissions at once
     **/
    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {

        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm) && shouldWeAsk(perm)) {
                result.add(perm);
            }
        }

        return result;

    }

    /**
     * It returns all the permissions we had previously asked for but currently do not have permission to use.
     * This may be because they declined us or later revoked our permission.This becomes useful when you want
     * to tell the user what permissions they declined and why they cannot use a feature.
     **/
    private ArrayList<String> findRejectedPermissions(ArrayList<String> wanted) {

        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm) && !shouldWeAsk(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    /**
     * check if we have marshmallow(version 23)
     **/
    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    /**************end dangerous permissions******************/
}
