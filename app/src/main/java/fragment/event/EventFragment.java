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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.birbit.android.jobqueue.JobManager;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentEventsBinding;
import com.japhethwaswa.church.databinding.RegisterEventDialogBinding;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;
import com.willowtreeapps.spruce.sort.LinearSort;
import com.willowtreeapps.spruce.sort.RadialSort;

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
import event.pojo.DynamicToastStatusUpdate;
import event.pojo.EventDataRetrievedSaved;
import event.pojo.SermonDataRetrievedSaved;
import job.EventsJob;
import job.RegisterEventJob;
import job.builder.MyJobsBuilder;
import model.dyno.Connectivity;
import model.dyno.FormValidation;
import model.dyno.FragDyno;


public class EventFragment extends Fragment {

    private FragmentEventsBinding fragmentEventsBinding;
    //public NavActivity navActivity;
    private JobManager jobManager;
    //private FragmentManager localFragmentManager;
    //private FragmentManager localFragmentManager;
    //private FragmentTransaction fragmentTransaction;
    private int orientationChange = -1;
    private int positionCurrentlyVisible = -1;
    private Cursor localCursor;
    private Animator spruceAnimator;
    private EventRecyclerViewAdapter eventRecyclerViewAdapter;
    private AlertDialog regDialog;
    private String fullNames;
    private String emailAddress;
    private String phone;
    private int currentDialogedItem = -1;
    private RegisterEventDialogBinding registerEventBindingDialog;


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

        jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getActivity().getApplicationContext()));
        if (savedInstanceState == null) {
            //do not start job if is orientation change
            //start bg job to get events from remote server

            jobManager.addJobInBackground(new EventsJob());
        } else {
            orientationChange = 1;
            positionCurrentlyVisible = savedInstanceState.getInt("eventPosition");
            currentDialogedItem = savedInstanceState.getInt("currentDialogedItem");
            fullNames = savedInstanceState.getString("fullNames");
            emailAddress = savedInstanceState.getString("emailAddress");
            phone = savedInstanceState.getString("phone");
        }

        //set cursor to null
        localCursor = null;


        /**recycler view adapter**/
        eventRecyclerViewAdapter = new EventRecyclerViewAdapter(localCursor);

        eventRecyclerViewAdapter.setRegisterButtonListener(new OnChurchButtonItemClickListener() {
            @Override
            public void onRegisterEventClicked(View view, int position) {
                //start dialog to register this event
                currentDialogedItem = position;
                registerForEvent(position);
            }
        });

        //set adapter
        fragmentEventsBinding.eventsRecycler.setAdapter(eventRecyclerViewAdapter);

        //get screen width and do some magic
        int scrWidth = getScreenDimensions();
        int numItems = 1;

        if(scrWidth >=800)
            numItems = 2;
        if(scrWidth >=1600)
            numItems = 3;

        if(scrWidth >=800){

            GridLayoutManager gridLayoutMgr = new GridLayoutManager(getContext(),numItems){
                @Override
                public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                    super.onLayoutChildren(recycler, state);
                    //Animate in the visible children
                    customAnimation();

                }
            };

            fragmentEventsBinding.eventsRecycler.setLayoutManager(gridLayoutMgr);


        }else{

            LinearLayoutManager linearLayoutManagerRecycler = new LinearLayoutManager(getContext()) {
                @Override
                public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                    super.onLayoutChildren(recycler, state);
                    //Animate in the visible children
                    customAnimation();
                }
            };

            fragmentEventsBinding.eventsRecycler.setLayoutManager(linearLayoutManagerRecycler);

        }



        return fragmentEventsBinding.getRoot();
    }

    //animation
    private void customAnimation() {
        if(localCursor != null && localCursor.isClosed() == false){
            spruceAnimator = new Spruce.SpruceBuilder(fragmentEventsBinding.eventsRecycler)
                    .sortWith(new DefaultSort(100))
                    .animateWith(DefaultAnimations.growAnimator(fragmentEventsBinding.eventsRecycler, 800))
                    .start();
        }
    }

    private void registerForEvent(int position) {


        //inflate dialog view
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View registerEventBindingDialogInflated = layoutInflater.inflate(R.layout.register_event_dialog, null, false);

        //binding
        registerEventBindingDialog = DataBindingUtil.bind(registerEventBindingDialogInflated);
        //set data if they are not null
        if(fullNames != null)
            registerEventBindingDialog.fullNames.setText(fullNames);
        if(emailAddress != null)
        registerEventBindingDialog.emailAddress.setText(emailAddress);
        if(phone != null)
        registerEventBindingDialog.phone.setText(phone);

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

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        regDialog =  null;
                        currentDialogedItem = -1;
                    }
                });


        //create alert dialog
        regDialog = regEventDialogBuilder.create();
        //show it

        regDialog.show();

        //override the button positive handler
        regDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get event id
                String eventId = localCursor.getString(localCursor.getColumnIndex(ChurchContract.EventsEntry.COLUMN_EVENT_ID));

                //handle form validation
                fullNames = registerEventBindingDialog.fullNames.getText().toString();
                emailAddress = registerEventBindingDialog.emailAddress.getText().toString();
                phone = registerEventBindingDialog.phone.getText().toString();
                //form validation
                boolean checkedEmailAddress = FormValidation.checkEmail(emailAddress);
                boolean checkedPhone = FormValidation.checkInt(phone, 10, 10);
                boolean checkedFullNames = FormValidation.checkString(fullNames, 0, 0);

                if (!checkedEmailAddress) {
                    registerEventBindingDialog.emailAddress.setError("Invalid Email Address");
                }
                if (!checkedPhone) {
                    registerEventBindingDialog.phone.setError("Must be 10 chars long");
                }
                if (!checkedFullNames) {
                    registerEventBindingDialog.fullNames.setError("Please Enter");
                }

                if (checkedEmailAddress && checkedPhone && checkedFullNames) {
                    //dismiss dialog
                    regDialog.dismiss();
                    regDialog =  null;
                    currentDialogedItem = -1;

                    //notify user
                    EventBus.getDefault().post(new DynamicToastStatusUpdate(0, "We are registering you."));
                    //initiate a background job to register this user after successful validation of input data(write api endpoint in church application to receive the data)
                    if(localCursor != null && !localCursor.isClosed()){
                        jobManager.addJobInBackground(new RegisterEventJob(eventId,fullNames,emailAddress,phone));
                    }


                    //set the variables null after posting a background job
                    fullNames = null;
                    emailAddress = null;
                    phone = null;
                }

            }
        });


    }

    //get screen dimensions
    public int getScreenDimensions() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        return width;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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

        String orderBy = ChurchContract.EventsEntry.COLUMN_EVENT_DATE + " ASC";
        //String orderBy = null;

        handler.startQuery(23, null, ChurchContract.EventsEntry.CONTENT_URI, projection, null, null, orderBy);
    }

    private void loadSermonListToRecyclerView() {
        if (localCursor.getCount() > 0) {
            //hide loader here
            fragmentEventsBinding.pageloader.stopProgress();
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
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("orientationChange", 1);

        outState.putInt("eventPosition", positionCurrentlyVisible);

        //save dialog data
        outState.putInt("currentDialogedItem", currentDialogedItem);

        if (regDialog != null) {
            outState.putString("fullNames", registerEventBindingDialog.fullNames.getText().toString());
            outState.putString("emailAddress", registerEventBindingDialog.emailAddress.getText().toString());
            outState.putString("phone", registerEventBindingDialog.phone.getText().toString());
        }

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


        if (currentDialogedItem != -1) {
            registerForEvent(currentDialogedItem);
        }

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

        if (regDialog != null) {
            regDialog.dismiss();
        }


    }
}
