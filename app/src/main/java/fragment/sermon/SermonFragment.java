package fragment.sermon;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.birbit.android.jobqueue.JobManager;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentSermonsBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.NavActivity;
import event.pojo.ConnectionStatus;
import event.pojo.FragConfigChange;
import event.pojo.SermonPositionEvent;
import job.SermonsJob;
import job.builder.MyJobsBuilder;
import model.dyno.Connectivity;


public class SermonFragment extends Fragment {

    private FragmentSermonsBinding fragmentSermonsBinding;
    //public NavActivity navActivity;
    private JobManager jobManager;
    private FragmentManager localFragmentManager;
    //private FragmentManager localFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int orientationChange = -1;
    private int positionCurrentlyVisible = -1;
    private int dualPane = -1;

    //todo top-priority-handle SQLiteDatabaseLockedException if clicked instantly on starting the app

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //StrictMode
        StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setVmPolicy(vmPolicy);
        /**==============**/

        //inflate the view
        fragmentSermonsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sermons, container, false);


        if (savedInstanceState == null) {
            //do not start job if is orientation change
            //start bg job to get sermons from remote server
            jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getActivity().getApplicationContext()));
            jobManager.addJobInBackground(new SermonsJob());
        } else {
            orientationChange = 1;
            positionCurrentlyVisible = savedInstanceState.getInt("sermonPosition");
        }

        if (fragmentSermonsBinding.mainSermonSpecific != null) {
            dualPane = 1;
        }

        //fragment management
        //navActivity = (NavActivity) getActivity();
        //localFragmentManager = navActivity.fragmentManager;
        localFragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = localFragmentManager.beginTransaction();


        //start Fragment to display all sermons
        SermonAllFragment sermonAllFragment = new SermonAllFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("orientationChange", orientationChange);
        bundle.putInt("positionCurrentlyVisible", positionCurrentlyVisible);
        bundle.putInt("dualPane", dualPane);

        sermonAllFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.mainSermonFragment, sermonAllFragment, "sermonAllFragment");
        fragmentTransaction.commit();


        return fragmentSermonsBinding.getRoot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBibleVesePositionEvent(SermonPositionEvent event) {
        positionCurrentlyVisible = event.getPosition();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("orientationChange", 1);

        //post event
        EventBus.getDefault().post(new FragConfigChange());
        outState.putInt("sermonPosition", positionCurrentlyVisible);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        //check for internet connection and post to subscribers
        EventBus.getDefault().post(new ConnectionStatus(Connectivity.isConnected(getActivity().getApplicationContext())));
    }

}
