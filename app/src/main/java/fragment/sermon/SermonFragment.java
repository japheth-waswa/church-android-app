package fragment.sermon;

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
import com.japhethwaswa.church.databinding.FragmentSermonsBinding;

import org.greenrobot.eventbus.EventBus;

import app.NavActivity;
import event.pojo.ConnectionStatus;
import job.SermonsJob;
import job.builder.MyJobsBuilder;
import model.dyno.Connectivity;


public class SermonFragment extends Fragment{

private FragmentSermonsBinding fragmentSermonsBinding;
    public NavActivity navActivity;
    private JobManager jobManager;
    private FragmentManager localFragmentManager;
    private FragmentTransaction fragmentTransaction;

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
        fragmentSermonsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sermons,container,false);

        //start bg job to get sermons from remote server
        jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getActivity().getApplicationContext()));
        jobManager.addJobInBackground(new SermonsJob());

        //fragment management

        navActivity = (NavActivity) getActivity();
        localFragmentManager = navActivity.fragmentManager;
        fragmentTransaction = localFragmentManager.beginTransaction();

        //start Fragment to display all sermons
        AllSermonsFragment allSermonsFragment = new AllSermonsFragment();
        fragmentTransaction.replace(R.id.mainSermonFragment,allSermonsFragment,"allSermonsFragment");
        fragmentTransaction.commit();


        return fragmentSermonsBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        //check for internet connection and post to subscribers
        EventBus.getDefault().post(new ConnectionStatus(Connectivity.isConnected(getActivity().getApplicationContext())));
    }
}
