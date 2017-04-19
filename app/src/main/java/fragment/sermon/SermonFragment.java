package fragment.sermon;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentSermonsBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.NavActivity;
import es.dmoral.toasty.Toasty;
import event.pojo.ConnectionStatus;
import job.SaveBibleDataToDb;
import job.SermonsJob;
import job.builder.MyJobsBuilder;
import model.Connectivity;


public class SermonFragment extends Fragment{

private FragmentSermonsBinding fragmentSermonsBinding;
    //public NavActivity navActivity;
    private JobManager jobManager;

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

        //navActivity = (NavActivity) getActivity();

        //start bg job to get sermons from remote server
        jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getActivity().getApplicationContext()));
        jobManager.addJobInBackground(new SermonsJob());


        return fragmentSermonsBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        //check for internet connection and post to subscribers
        EventBus.getDefault().post(new ConnectionStatus(Connectivity.isConnected(getActivity().getApplicationContext())));
    }
}
