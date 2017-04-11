package fragment.bible;

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

import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentBibleBinding;
import com.japhethwaswa.church.databinding.FragmentBibleBookBinding;

import app.NavActivity;

public class BibleBookFragment extends Fragment {
    private FragmentBibleBookBinding fragmentBibleBookBinding;
    public NavActivity navActivity;
    //private FragmentManager localFragmentManager;

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

        fragmentBibleBookBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bible_book,container,false);

        /**navActivity =  (NavActivity) getActivity();
        //code snippets come here
        localFragmentManager = navActivity.fragmentManager;
        FragmentTransaction fragmentTransaction = localFragmentManager.beginTransaction();**/

        return fragmentBibleBookBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
