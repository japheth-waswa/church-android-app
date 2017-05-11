package fragment.event;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.birbit.android.jobqueue.JobManager;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentEventsBinding;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import adapters.recyclerview.event.EventRecyclerViewAdapter;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.ClickListener;
import event.CustomRecyclerTouchListener;
import event.pojo.ConnectionStatus;
import event.pojo.EventDataRetrievedSaved;
import event.pojo.SermonDataRetrievedSaved;
import job.EventsJob;
import job.builder.MyJobsBuilder;
import model.dyno.Connectivity;
import model.dyno.FragDyno;


public class EventFragment extends Fragment {

    private FragmentEventsBinding fragmentEventsBinding;
    //public NavActivity navActivity;
    private JobManager jobManager;
    //private FragmentManager localFragmentManager;
    //private FragmentManager localFragmentManager;
    //private FragmentTransaction fragmentTransaction;
    //private int orientationChange = -1;
    private int positionCurrentlyVisible = -1;
    private Cursor localCursor;
    private Animator spruceAnimator;
    private EventRecyclerViewAdapter eventRecyclerViewAdapter;
    //private int dualPane = -1;

    //todo top-priority-handle SQLiteDatabaseLockedException if clicked instantly on starting the app

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
        fragmentEventsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_events, container, false);


        if (savedInstanceState == null) {
            //do not start job if is orientation change
            //start bg job to get events from remote server
            jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getActivity().getApplicationContext()));
            jobManager.addJobInBackground(new EventsJob());
        } else {
            //orientationChange = 1;
            positionCurrentlyVisible = savedInstanceState.getInt("eventPosition");
        }


        //set cursor to null
        localCursor = null;


        /**recycler view adapter**/
        eventRecyclerViewAdapter = new EventRecyclerViewAdapter(localCursor);

        LinearLayoutManager linearLayoutManagerRecycler = new LinearLayoutManager(getContext()){
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                //Animate in the visible children
                spruceAnimator = new Spruce.SpruceBuilder(fragmentEventsBinding.eventsRecycler)
                        .sortWith(new DefaultSort(100))
                        .animateWith(DefaultAnimations.shrinkAnimator(fragmentEventsBinding.eventsRecycler,800),
                                ObjectAnimator.ofFloat(fragmentEventsBinding.eventsRecycler,
                                        "translationX",-fragmentEventsBinding.eventsRecycler.getWidth(),0f)
                                        .setDuration(800)).start();
            }
        };

        fragmentEventsBinding.eventsRecycler.setAdapter(eventRecyclerViewAdapter);
        fragmentEventsBinding.eventsRecycler.setLayoutManager(linearLayoutManagerRecycler);

        //add touch listener to recyclerview
        fragmentEventsBinding.eventsRecycler.addOnItemTouchListener(new CustomRecyclerTouchListener(
                getActivity(), fragmentEventsBinding.eventsRecycler, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                //todo show floating dialog to send registration to remote server

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }
        ));

        return fragmentEventsBinding.getRoot();
    }

    //fetch all from db.
    private void getEventFromDb() {
        if (localCursor != null) {
            localCursor.close();
            localCursor = null;
        }
        //show loader
        fragmentEventsBinding.pageloader.startProgress();

        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localCursor = cursor;
                    loadSermonListToRecyclerView();
            }
        };

        String[] projection = {
                ChurchContract.EventsEntry.COLUMN_EVENT_ID,
                ChurchContract.EventsEntry.COLUMN_TITLE,
                ChurchContract.EventsEntry.COLUMN_IMAGE_URL,
                ChurchContract.EventsEntry.COLUMN_BRIEF_DESCRIPTION,
                ChurchContract.EventsEntry.COLUMN_CONTENT,
                ChurchContract.EventsEntry.COLUMN_EVENT_DATE,
                ChurchContract.EventsEntry.COLUMN_EVENT_LOCATION,
                ChurchContract.EventsEntry.COLUMN_EVENT_CATEGORY_ID,
                ChurchContract.EventsEntry.COLUMN_VISIBLE,
                ChurchContract.EventsEntry.COLUMN_CREATED_AT,
                ChurchContract.EventsEntry.COLUMN_UPDATED_AT
        };

        String orderBy = ChurchContract.EventsEntry.COLUMN_EVENT_DATE + " DESC";
        //String orderBy = null;

        handler.startQuery(23, null, ChurchContract.EventsEntry.CONTENT_URI, projection, null, null, orderBy);
    }

    private void loadSermonListToRecyclerView() {
        if (localCursor.getCount() > 0) {
            //hide loader here
            fragmentEventsBinding.pageloader.stopProgress();
            //set recycler cursor
            eventRecyclerViewAdapter.setCursor(localCursor);

            //scroll to position if set
            if (positionCurrentlyVisible != -1) {
                fragmentEventsBinding.eventsRecycler.scrollToPosition(positionCurrentlyVisible);
            }

        }
    }

    //todo on eventposition-not necessary
    /**@Subscribe(threadMode = ThreadMode.MAIN)
    public void onBibleVesePositionEvent(SermonPositionEvent event) {
        positionCurrentlyVisible = event.getPosition();
    }**/


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("orientationChange", 1);

        //post event

        //todo save current visible position
        outState.putInt("eventPosition", positionCurrentlyVisible);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDataRetrievedSaved(EventDataRetrievedSaved event) {
        //parse the event item and display
        getEventFromDb();
    }

    @Override
    public void onStart() {
        super.onStart();

        getEventFromDb();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        //check for internet connection and post to subscribers
        EventBus.getDefault().post(new ConnectionStatus(Connectivity.isConnected(getActivity().getApplicationContext())));
    }

    @Override
    public void onPause() {
        super.onPause();
        /**close cursors**/
        if (localCursor != null) {
            localCursor.close();
        }
    }
}
