package fragment.sermon;

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
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentSermonsAllBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.NavActivity;
import es.dmoral.toasty.Toasty;
import event.pojo.SermonDataRetrievedSaved;


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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSermonDataRetrieved(SermonDataRetrievedSaved event){
        //todo parse the sermon item and display
    }

    @Override
    public void onStart() {
        super.onStart();
        //register event
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        //unregister event
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
