package fragment.bible;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentBibleBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.NavActivity;
import event.pojo.BibleBookPositionEvent;
import event.pojo.FragConfigChange;

//todo 2layouts ie sw600dp for tablet view

public class BibleFragment extends Fragment {
    private FragmentBibleBinding fragmentBibleBinding;
    public NavActivity navActivity;
    private FragmentManager localFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int orientationChange = -1;
    private int bibleBookCurrentVisiblePosition = -1;

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

        fragmentBibleBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bible,container,false);

        navActivity =  (NavActivity) getActivity();

        //code snippets come here
        localFragmentManager = navActivity.fragmentManager;
        fragmentTransaction = localFragmentManager.beginTransaction();

        /**start frament to display bible books**/
        BibleBookFragment bibleBookFragment =  new BibleBookFragment();

        if(savedInstanceState != null){
            orientationChange = 1;
            bibleBookCurrentVisiblePosition = savedInstanceState.getInt("bibleBookCurrentVisiblePosition");
        }

        //variable to indicate orientation change
        Bundle bundle = new Bundle();
        bundle.putInt("orientationChange", orientationChange);
        bundle.putInt("bibleBookCurrentVisiblePosition", bibleBookCurrentVisiblePosition);
        //todo bundle current chapter position
        //todo bundle current verse position

        bibleBookFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.mainBibleFragment,bibleBookFragment,"bibleBookFragment");
        fragmentTransaction.commit();

        return fragmentBibleBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("orientationChange",1);

        //post event
        EventBus.getDefault().post(new FragConfigChange());
        outState.putInt("bibleBookCurrentVisiblePosition",bibleBookCurrentVisiblePosition);
    }

    //todo subscribe to event to event for current chapter position
    //todo subscribe to event to event for current verse position

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBibleBookPositionEvent(BibleBookPositionEvent event){
        bibleBookCurrentVisiblePosition = event.getBibleBookPosition();
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
}
