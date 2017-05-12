package fragment.schedule;

import android.animation.Animator;
import android.content.DialogInterface;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.birbit.android.jobqueue.JobManager;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentEventsBinding;
import com.japhethwaswa.church.databinding.FragmentScheduleBinding;
import com.japhethwaswa.church.databinding.RegisterEventDialogBinding;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import adapters.recyclerview.event.EventRecyclerViewAdapter;
import adapters.recyclerview.schedule.ScheduleRecyclerViewAdapter;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.OnChurchButtonItemClickListener;
import event.pojo.ConnectionStatus;
import event.pojo.DynamicToastStatusUpdate;
import event.pojo.EventDataRetrievedSaved;
import job.EventsJob;
import job.RegisterEventJob;
import job.builder.MyJobsBuilder;
import model.dyno.Connectivity;
import model.dyno.FormValidation;


public class ScheduleFragment extends Fragment implements DiscreteScrollView.OnItemChangedListener{

    private FragmentScheduleBinding fragmentScheduleBinding;
    private JobManager jobManager;
    private ScheduleRecyclerViewAdapter scheduleRecyclerViewAdapter;
    private int orientationChange = -1;
    private Cursor localCursor;
    private String fullNames;
    private String emailAddress;
    private String phone;


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
        fragmentScheduleBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false);

        jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getActivity().getApplicationContext()));
        if (savedInstanceState == null) {
            //do not start job if is orientation change
            //start bg job to get schedule from remote server

            jobManager.addJobInBackground(new EventsJob());
        } else {
            orientationChange = 1;
            fullNames = savedInstanceState.getString("fullNames");
            emailAddress = savedInstanceState.getString("emailAddress");
            phone = savedInstanceState.getString("phone");
        }

        //set cursor to null
        localCursor = null;

        //discrete scrollview here
        scheduleRecyclerViewAdapter = new ScheduleRecyclerViewAdapter(localCursor);


        fragmentScheduleBinding.schedulePagesDiscreteScrollView.setOrientation(Orientation.HORIZONTAL.HORIZONTAL);
        fragmentScheduleBinding.schedulePagesDiscreteScrollView.setOnItemChangedListener(this);
        fragmentScheduleBinding.schedulePagesDiscreteScrollView.setAdapter(scheduleRecyclerViewAdapter);
        fragmentScheduleBinding.schedulePagesDiscreteScrollView.setItemTransitionTimeMillis(150);
        fragmentScheduleBinding.schedulePagesDiscreteScrollView.setItemTransformer(new ScaleTransformer.Builder()
        .setMinScale(0.8f).build());

        /**recycler view adapter**/
        //eventRecyclerViewAdapter = new EventRecyclerViewAdapter(localCursor);


        //set adapter
        //fragmentEventsBinding.eventsRecycler.setAdapter(eventRecyclerViewAdapter);

        //todo do not set layout manager for discrete scrollview



        return fragmentScheduleBinding.getRoot();
    }


    //todo get both schedule entry and pages-schedule entry
    //fetch all from db.
    private void getSchedulePagesFromDb() {
        if (localCursor != null) {
            localCursor.close();
            localCursor = null;
        }
        //show loader
        fragmentScheduleBinding.pageloader.startProgress();

        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localCursor = cursor;
                //loadSermonListToRecyclerView();
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

        String orderBy = ChurchContract.EventsEntry.COLUMN_EVENT_DATE + " ASC";
        //String orderBy = null;

        handler.startQuery(23, null, ChurchContract.EventsEntry.CONTENT_URI, projection, null, null, orderBy);
    }

    /**private void loadSermonListToRecyclerView() {
        if (localCursor.getCount() > 0) {
            //hide loader here
            fragmentScheduleBinding.pageloader.stopProgress();
            //set recycler cursor
            eventRecyclerViewAdapter.setCursor(localCursor);

            if (currentDialogedItem != -1) {
                fragmentEventsBinding.eventsRecycler.scrollToPosition(currentDialogedItem);
            } else {

                if (positionCurrentlyVisible != -1) {
                    fragmentEventsBinding.eventsRecycler.scrollToPosition(positionCurrentlyVisible);
                }
            }


        }
    }**/


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("orientationChange", 1);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDataRetrievedSaved(EventDataRetrievedSaved event) {
        //parse the event item and display
        //todo 2-schedules and schedule pages
        //getEventFromDb();
    }

    @Override
    public void onStart() {
        super.onStart();

        //todo 2-schedules and schedule pages
        //getEventFromDb();


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

    @Override
    public void onCurrentItemChanged(@NonNull RecyclerView.ViewHolder viewHolder, int adapterPosition) {

        onItemChanged(adapterPosition);
    }

    //handle item changes
    private void onItemChanged(int position) {
        if (localCursor != null && localCursor.isClosed() == false) {
            //todo perform logic here ie change color of button-refer from github
            //localCursor.moveToPosition(position);
        }
    }
}
