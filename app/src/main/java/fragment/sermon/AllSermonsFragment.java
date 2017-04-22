package fragment.sermon;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
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

import adapters.recyclerview.sermon.SermonRecyclerViewAdapter;
import app.NavActivity;
import db.ChurchContract;
import db.ChurchQueryHandler;
import es.dmoral.toasty.Toasty;
import event.pojo.BibleBookPositionEvent;
import event.pojo.FragConfigChange;
import event.pojo.SermonDataRetrievedSaved;
import event.pojo.SermonPositionEvent;
import fragment.bible.BibleChapterFragment;
import model.dyno.FragDyno;


public class AllSermonsFragment extends Fragment {

    private FragmentSermonsAllBinding fragmentSermonsAllBinding;
    public NavActivity navActivity;
    private JobManager jobManager;
    private FragmentManager localFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private SermonRecyclerViewAdapter sermonRecyclerViewAdapter;
    private Cursor localCursor;
    private int sermonPosition = -1;
    private int orientationChange = -1;
    private int currVisiblePosition = -1;

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

        //handle orientation change since it returns to bible books
        Bundle bundle = getArguments();
        orientationChange = bundle.getInt("orientationChange");
        currVisiblePosition = bundle.getInt("positionCurrentlyVisible");

        //fragment management

        navActivity = (NavActivity) getActivity();
        localFragmentManager = navActivity.fragmentManager;
        fragmentTransaction = localFragmentManager.beginTransaction();

        //set cursor to null
        localCursor = null;


         /**sermon recycler view adapter**/
        sermonRecyclerViewAdapter = new SermonRecyclerViewAdapter(localCursor);
        fragmentSermonsAllBinding.sermonsRecycler.setAdapter(sermonRecyclerViewAdapter);
        fragmentSermonsAllBinding.sermonsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        /****/

        /****/


        return fragmentSermonsAllBinding.getRoot();
    }

    //fetch all sermons from db.
    private void getSermonFromDb() {
        if (localCursor != null) {
            localCursor.close();
            localCursor =  null;
        }
        //todo show loader
        fragmentSermonsAllBinding.pageloader.startProgress();

        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localCursor = cursor;
                int previousPosition = FragDyno.getPrevPosition(getString(R.string.preference_sermon_position));

                if (previousPosition != -1 && orientationChange != -1) {
                    sermonPosition = previousPosition;
                    showSpecificSermon(sermonPosition);
                } else {
                    if (cursor.getCount() > 0) {
                        //todo hide loader here
                        fragmentSermonsAllBinding.pageloader.stopProgress();
                        //set recycler cursor
                        sermonRecyclerViewAdapter.setCursor(localCursor);

                        //scroll to position if set
                        if (currVisiblePosition != -1) {
                            fragmentSermonsAllBinding.sermonsRecycler.scrollToPosition(currVisiblePosition);
                        }

                    }

                }


            }
        };

        String[] projection = {
                ChurchContract.SermonEntry.COLUMN_SERMON_ID,
                ChurchContract.SermonEntry.COLUMN_SERMON_TITLE,
                ChurchContract.SermonEntry.COLUMN_SERMON_IMAGE_URL,
                ChurchContract.SermonEntry.COLUMN_SERMON_BRIEF_DESCRIPTION,
                ChurchContract.SermonEntry.COLUMN_SERMON_AUDIO_URL,
                ChurchContract.SermonEntry.COLUMN_SERMON_VIDEO_URL,
                ChurchContract.SermonEntry.COLUMN_SERMON_PDF_URL,
                ChurchContract.SermonEntry.COLUMN_SERMON_DATE,
                ChurchContract.SermonEntry.COLUMN_SERMON_VISIBLE,
                ChurchContract.SermonEntry.COLUMN_SERMON_CREATED_AT,
                ChurchContract.SermonEntry.COLUMN_SERMON_UPDATED_AT
        };

        String orderBy =  ChurchContract.SermonEntry.COLUMN_SERMON_DATE + " DESC";
        //String orderBy = null;

        handler.startQuery(23, null, ChurchContract.SermonEntry.CONTENT_URI, projection, null, null, orderBy);
    }


    private void showSpecificSermon(int position) {

        //save the current position in preferences
        FragDyno.saveToPreference(getString(R.string.preference_sermon_position), position);

        localCursor.moveToPosition(position);

        //todo on click a sermon,start bg job to get the specific sermon from the server.
        //todo this job should not run if it is orientation change.
        if (orientationChange == -1) {
            //start bg job to get specific sermon from the server
            //jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getActivity().getApplicationContext()));
            //jobManager.addJobInBackground(new SermonsJob());

        }

        /**BibleChapterFragment bibleChapterFragment = new BibleChapterFragment();
         String bibleBookCode = localTestamentCursor.getString(localTestamentCursor.getColumnIndex(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_CODE));
         String bibleBookName = localTestamentCursor.getString(localTestamentCursor.getColumnIndex(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NAME));

         Bundle bundle = new Bundle();
         bundle.putString("bibleBookCode", bibleBookCode);
         bundle.putString("bibleBookName", bibleBookName);
         bundle.putInt("orientationChange",orientationChange);
         bundle.putInt("bibleChapterCurrentVisiblePosition",bibleChapterCurrentVisiblePos);
         bundle.putInt("bibleVerseCurrentVisiblePosition",bibleVerseCurrentVisiblePos);

         bibleChapterFragment.setArguments(bundle);
         fragmentTransaction.replace(R.id.mainBibleFragment, bibleChapterFragment, "bibleChapterFragment");
         //fragmentTransaction.addToBackStack(null);
         fragmentTransaction.commit();**/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSermonDataRetrieved(SermonDataRetrievedSaved event) {
        //parse the sermon item and display
        getSermonFromDb();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFragConfigChange(FragConfigChange event) {
        //save current recyclerview position for bible book
        long dtCurrentVisiblePosition;
        dtCurrentVisiblePosition = ((LinearLayoutManager) fragmentSermonsAllBinding.sermonsRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        //post event to EventBus
        EventBus.getDefault().post(new SermonPositionEvent((int) dtCurrentVisiblePosition));
    }

    @Override
    public void onPause() {
        super.onPause();

        /**close cursors**/
        if (localCursor != null) {
            localCursor.close();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //confirm if is not screen orientation change then clear all the bible,chapters,verse logs in preference file
        if (orientationChange == -1) {
            //clear all preference bible related data
            String[] itemNames = {getString(R.string.preference_sermon_position)};
            FragDyno.clearDataPreference(itemNames);
        }

        getSermonFromDb();

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
