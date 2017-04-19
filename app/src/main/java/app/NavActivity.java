package app;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cleveroad.loopbar.widget.OnItemClickListener;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.ActivityNavBinding;

import fragment.bible.BibleFragment;
import fragment.sermon.SermonFragment;

public class NavActivity extends AppCompatActivity {
    //todo in each fragment handle screen orientation appropriately
    private ActivityNavBinding activityNavBinding;
    private int navPosition;
    public FragmentManager fragmentManager = getSupportFragmentManager();


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
                fragmentTransaction.replace(R.id.navFragmentHolder,sermonFragment,"sermonFragment");
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
                fragmentTransaction.replace(R.id.navFragmentHolder,bibleFragment,"bibleFragment");
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


}
