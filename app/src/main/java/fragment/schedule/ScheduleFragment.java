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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.birbit.android.jobqueue.JobManager;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentScheduleBinding;
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
import event.pojo.ScheduleDataRetrievedSaved;
import job.EventsJob;
import job.RegisterEventJob;
import job.ScheduleJob;
import job.builder.MyJobsBuilder;
import model.Schedule;
import model.dyno.Connectivity;
import model.dyno.FormValidation;


public class ScheduleFragment extends Fragment implements DiscreteScrollView.OnItemChangedListener{

    private FragmentScheduleBinding fragmentScheduleBinding;
    private JobManager jobManager;
    private ScheduleRecyclerViewAdapter scheduleRecyclerViewAdapter;
    private int orientationChange = -1;
    private int previousPosition = -1;
    private Cursor localCursor;
    private Cursor scheduleCursor;


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

            jobManager.addJobInBackground(new ScheduleJob());
        } else {
            orientationChange = 1;
            previousPosition = savedInstanceState.getInt("previousPosition");
        }

        //set cursor to null
        localCursor = null;
        scheduleCursor = null;

        //discrete scrollview here
        scheduleRecyclerViewAdapter = new ScheduleRecyclerViewAdapter(localCursor);


        fragmentScheduleBinding.schedulePagesDiscreteScrollView.setOrientation(Orientation.HORIZONTAL.HORIZONTAL);
        fragmentScheduleBinding.schedulePagesDiscreteScrollView.setOnItemChangedListener(this);
        fragmentScheduleBinding.schedulePagesDiscreteScrollView.setAdapter(scheduleRecyclerViewAdapter);
        fragmentScheduleBinding.schedulePagesDiscreteScrollView.setItemTransitionTimeMillis(150);
        fragmentScheduleBinding.schedulePagesDiscreteScrollView.setItemTransformer(new ScaleTransformer.Builder()
        .setMinScale(0.8f).build());

        return fragmentScheduleBinding.getRoot();
    }

    //fetch all from db.
    private void getScheduleFromDb() {
        if (scheduleCursor != null) {
            scheduleCursor.close();
            scheduleCursor = null;
        }
        //show loader
        fragmentScheduleBinding.pageloader.startProgress();

        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                scheduleCursor = cursor;

                if (scheduleCursor.getCount() > 0){

                    fragmentScheduleBinding.pageloader.stopProgress();

                    //move to first position
                    scheduleCursor.moveToFirst();
                    String scheduleId = scheduleCursor.getString(scheduleCursor.getColumnIndex(ChurchContract.SchedulesEntry.COLUMN_SCHEDULE_ID));
                    String scheduleTitle = scheduleCursor.getString(scheduleCursor.getColumnIndex(ChurchContract.SchedulesEntry.COLUMN_THEME_TITLE));
                    String scheduleDescription = scheduleCursor.getString(scheduleCursor.getColumnIndex(ChurchContract.SchedulesEntry.COLUMN_THEME_DESCRIPTION));
                    String scheduleDate = scheduleCursor.getString(scheduleCursor.getColumnIndex(ChurchContract.SchedulesEntry.COLUMN_SUNDAY_DATE));

                    //set data in xml view
                    Schedule schedule = new Schedule();
                    schedule.setTheme_title(scheduleTitle);
                    schedule.setTheme_description(scheduleDescription);
                    schedule.setSunday_date(scheduleDate);
                    fragmentScheduleBinding.setSchedule(schedule);

                    //get the id of the schedule and use to get
                    getSchedulePagesFromDb(scheduleId);
                }


            }
        };

        String[] projection = {
                ChurchContract.SchedulesEntry.COLUMN_SCHEDULE_ID,
                ChurchContract.SchedulesEntry.COLUMN_THEME_TITLE,
                ChurchContract.SchedulesEntry.COLUMN_THEME_DESCRIPTION,
                ChurchContract.SchedulesEntry.COLUMN_SUNDAY_DATE,
                ChurchContract.SchedulesEntry.COLUMN_COLUMN_COUNT,
                ChurchContract.SchedulesEntry.COLUMN_VISIBLE
        };

        String orderBy = ChurchContract.SchedulesEntry.COLUMN_SUNDAY_DATE + " ASC";

        handler.startQuery(23, null, ChurchContract.SchedulesEntry.CONTENT_URI, projection, null, null, orderBy);
    }

    private void getSchedulePagesFromDb(String scheduleId) {
        if (localCursor != null) {
            localCursor.close();
            localCursor = null;
        }

        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localCursor = cursor;

                loadPagesToDiscreteScrollView();
            }
        };

        String[] projection = {
                ChurchContract.SchedulePagesEntry.COLUMN_SCHEDULE_PAGES_ID,
                ChurchContract.SchedulePagesEntry.COLUMN_PAGE_CONTENT,
                ChurchContract.SchedulePagesEntry.COLUMN_SUNDAY_SCHEDULE_ID,
                ChurchContract.SchedulePagesEntry.COLUMN_PAGE_ORDER,
                ChurchContract.SchedulePagesEntry.COLUMN_VISIBLE
        };

        String orderBy = "CAST (" + ChurchContract.SchedulePagesEntry.COLUMN_PAGE_ORDER + " AS INTEGER) ASC";

        handler.startQuery(25, null, ChurchContract.SchedulePagesEntry.CONTENT_URI, projection, null, null, orderBy);
    }

    private void loadPagesToDiscreteScrollView() {

        if (localCursor.getCount() > 0) {

            //set recycler cursor
            scheduleRecyclerViewAdapter.setCursor(localCursor);

            //scroll to position
            if(previousPosition != -1)
                fragmentScheduleBinding.schedulePagesDiscreteScrollView.scrollToPosition(previousPosition);


        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("orientationChange", 1);
        outState.putInt("previousPosition", fragmentScheduleBinding.schedulePagesDiscreteScrollView.getCurrentItem());


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScheduleDataRetrievedSaved(ScheduleDataRetrievedSaved event) {
        getScheduleFromDb();
    }

    @Override
    public void onStart() {
        super.onStart();
        getScheduleFromDb();
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
if (scheduleCursor != null) {
    scheduleCursor.close();
        }


    }

    @Override
    public void onCurrentItemChanged(@NonNull RecyclerView.ViewHolder viewHolder, int adapterPosition) {

        onItemChanged(adapterPosition);
    }

    //handle item changes
    private void onItemChanged(int position) {
        if (localCursor != null && localCursor.isClosed() == false) {
            if(localCursor.moveToPosition(position)){
                String pageNum = localCursor.getString(localCursor.getColumnIndex(ChurchContract.SchedulePagesEntry.COLUMN_PAGE_ORDER));
                fragmentScheduleBinding.pageNumber.setText(pageNum);
            }


        }
    }
}
