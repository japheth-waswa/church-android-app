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
import com.japhethwaswa.church.databinding.FragmentSermonsAllBinding;
import com.japhethwaswa.church.databinding.FragmentSermonsBinding;

import org.greenrobot.eventbus.EventBus;

import app.NavActivity;
import event.pojo.ConnectionStatus;
import job.SermonsJob;
import job.builder.MyJobsBuilder;
import model.Connectivity;


public class AllSermonsFragment extends Fragment {

private FragmentSermonsAllBinding fragmentSermonsAllBinding;
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
        fragmentSermonsAllBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sermons_all, container, false);

        //start bg job to get specific sermon from the server
        //jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getActivity().getApplicationContext()));
        //jobManager.addJobInBackground(new SermonsJob());

        //fragment management

        navActivity = (NavActivity) getActivity();
        localFragmentManager = navActivity.fragmentManager;
        fragmentTransaction = localFragmentManager.beginTransaction();

        //start Fragment to display all sermons
        //AllSermonsFragment allSermonsFragment = new AllSermonsFragment();
        //fragmentTransaction.replace(R.id.mainSermonFragment,allSermonsFragment,"bibleBookFragment");
        //fragmentTransaction.commit();


        return fragmentSermonsAllBinding.getRoot();
    }
//todo on click a sermon,start bg job to get the specific sermon from the server.
}
