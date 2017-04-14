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

//todo 2layouts ie sw600dp for tablet view

public class BibleFragment extends Fragment {
    private FragmentBibleBinding fragmentBibleBinding;
    public NavActivity navActivity;
    private FragmentManager localFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int orientationChange = -1;

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
        }

        //variable to indicate orientation change
        Bundle bundle = new Bundle();
        bundle.putInt("orientationChange", orientationChange);

        bibleBookFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.mainBibleFragment,bibleBookFragment,"bibleBookFragment");
        fragmentTransaction.commit();

        return fragmentBibleBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("orientationChange",1);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBibleBookPositionEvent(BibleBookPositionEvent event){
        Log.e("jeff-book-pos", String.valueOf(event.getBibleBookPosition()));
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
