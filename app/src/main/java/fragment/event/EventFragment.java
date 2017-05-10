package fragment.event;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.birbit.android.jobqueue.JobManager;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentEventsBinding;
import com.japhethwaswa.church.databinding.FragmentSermonsBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import event.pojo.ConnectionStatus;
import event.pojo.FragConfigChange;
import event.pojo.SermonPositionEvent;
import fragment.sermon.SermonAllFragment;
import job.EventsJob;
import job.SermonsJob;
import job.builder.MyJobsBuilder;
import model.dyno.Connectivity;


public class EventFragment extends Fragment {

    private FragmentEventsBinding fragmentEventsBinding;
    //public NavActivity navActivity;
    private JobManager jobManager;
    //private FragmentManager localFragmentManager;
    //private FragmentManager localFragmentManager;
    //private FragmentTransaction fragmentTransaction;
    //private int orientationChange = -1;
    private int positionCurrentlyVisible = -1;
    //private int dualPane = -1;

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
        fragmentEventsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_events, container, false);


        if (savedInstanceState == null) {
            //do not start job if is orientation change
            //start bg job to get events from remote server
            jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getActivity().getApplicationContext()));
            jobManager.addJobInBackground(new EventsJob());
        } else {
            //orientationChange = 1;
            positionCurrentlyVisible = savedInstanceState.getInt("eventPosition");
        }


        return fragmentEventsBinding.getRoot();
    }

    //todo on eventposition-not necessary
    /**@Subscribe(threadMode = ThreadMode.MAIN)
    public void onBibleVesePositionEvent(SermonPositionEvent event) {
        positionCurrentlyVisible = event.getPosition();
    }**/


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("orientationChange", 1);

        //post event

        //todo save current visible position
        outState.putInt("eventPosition", positionCurrentlyVisible);
    }

    @Override
    public void onStart() {
        super.onStart();
        //EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        //EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        //check for internet connection and post to subscribers
        EventBus.getDefault().post(new ConnectionStatus(Connectivity.isConnected(getActivity().getApplicationContext())));
    }

}
