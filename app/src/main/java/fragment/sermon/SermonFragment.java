package fragment.sermon;

import android.support.v4.app.Fragment;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import event.pojo.ConnectionStatus;

public class SermonFragment extends Fragment{


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onConnectionStatus(ConnectionStatus connectionStatus){
        Log.e("jeff-waswa-conn", String.valueOf(connectionStatus.isConnected()));
        //todo do wherever you want with the connection status
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
