package fragment.event;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.birbit.android.jobqueue.JobManager;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentEventsBinding;
import com.japhethwaswa.church.databinding.RegisterEventDialogBinding;
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
import event.OnChurchButtonItemClickListener;
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
    private AlertDialog regDialog;
    //private int dualPane = -1;


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
        eventRecyclerViewAdapter.setRegisterButtonListener(new OnChurchButtonItemClickListener() {
            @Override
            public void onRegisterEventClicked(View view, int position) {
                //start dialog to register this event
                registerForEvent(position);
            }
        });

        //todo large screens-(change recyclerview layout)-(number of items diplayed in width)-(data placement and format)
        //todo change recyclerview layout for larger scrren devices and include appropriate animation
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


        return fragmentEventsBinding.getRoot();
    }

    private void registerForEvent(int position) {

        //todo start dialog to register this event
        //todo handle data in dialog on screen orientation and restore by also showing the dialog back
        //todo add floating hint text to dialog

        //inflate dialog view
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View registerEventBindingDialogInflated = layoutInflater.inflate(R.layout.register_event_dialog, null, false);

        //binding
        final RegisterEventDialogBinding registerEventBindingDialog = DataBindingUtil.bind(registerEventBindingDialogInflated);

        //dialog builder
        AlertDialog.Builder regEventDialogBuilder = new AlertDialog.Builder(getContext());

        //dialog title
        regEventDialogBuilder.setTitle("Register For Event");

        //dialog icon
        regEventDialogBuilder.setIcon(R.drawable.ic_register);

        //set view
        regEventDialogBuilder.setView(registerEventBindingDialogInflated);

        //set dialog message
        regEventDialogBuilder.setCancelable(false)
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("jean-data", String.valueOf(registerEventBindingDialog.fullNames.getText()));
                        //todo iniate a background job to register this user after successful validation of input data
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        //create alert dialog
        regDialog = regEventDialogBuilder.create();
        //show it
        regDialog.show();


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

        if(regDialog != null){
            regDialog.dismiss();
        }


    }
}
