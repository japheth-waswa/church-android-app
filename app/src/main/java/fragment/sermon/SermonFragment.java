package fragment.sermon;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentSermonsBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import es.dmoral.toasty.Toasty;
import event.pojo.ConnectionStatus;
import model.Connectivity;


public class SermonFragment extends Fragment{

private FragmentSermonsBinding fragmentSermonsBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        fragmentSermonsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sermons,container,false);

        //connectivity status
        //todo post this event in web service
        EventBus.getDefault().post(new ConnectionStatus(Connectivity.isConnected(getContext())));

        return fragmentSermonsBinding.getRoot();
    }

}
