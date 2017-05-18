package fragment.donation;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.birbit.android.jobqueue.JobManager;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentDonationBinding;
import com.japhethwaswa.church.databinding.FragmentScheduleBinding;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import adapters.recyclerview.schedule.ScheduleRecyclerViewAdapter;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.DataBindingCustomListener;
import event.DtbCustomListenerPresenter;
import event.pojo.ConnectionStatus;
import event.pojo.DonationDataRetrievedSaved;
import event.pojo.ScheduleDataRetrievedSaved;
import job.DonationJob;
import job.ScheduleJob;
import job.builder.MyJobsBuilder;
import model.Donation;
import model.Schedule;
import model.dyno.Connectivity;


public class DonationFragment extends Fragment  implements DataBindingCustomListener.View {

    private FragmentDonationBinding fragmentDonationBinding;
    private JobManager jobManager;
    private int orientationChange = -1;
    private Cursor localCursor;
    private DtbCustomListenerPresenter dtbCustomListenerPresenter;


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
        fragmentDonationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_donation, container, false);

        jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getActivity().getApplicationContext()));
        if (savedInstanceState == null) {
            //do not start job if is orientation change
            //start bg job to get donation from remote server

            jobManager.addJobInBackground(new DonationJob());
        } else {
            orientationChange = 1;
        }

        //set cursor to null
        localCursor = null;

        //set presenter
        dtbCustomListenerPresenter = new DtbCustomListenerPresenter(this);

        fragmentDonationBinding.setDatabindingcustompresenter(dtbCustomListenerPresenter);

        return fragmentDonationBinding.getRoot();
    }

    @Override
    public void onNeedString(String stringData) {
    }

    @Override
    public void onSocialClick(String code, String stringData) {
        //build the web intent
        Uri webpage = Uri.parse(stringData);
        Intent webIntent = new Intent(Intent.ACTION_VIEW,webpage);

        //verify it resolves
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(webIntent,0);
                boolean isIntentSafe = activities.size() > 0;

        if(isIntentSafe)
            startActivity(webIntent);
    }

    @Override
    public void onCommentClick() {

    }

    private void getDonationFromDb() {
        if (localCursor != null) {
            localCursor.close();
            localCursor = null;
        }

        //show loader
        fragmentDonationBinding.pageloader.startProgress();

        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localCursor = cursor;

                loadDonationDataToViews();
            }
        };

        String[] projection = {
                ChurchContract.DonationEntry.COLUMN_TITLE,
                ChurchContract.DonationEntry.COLUMN_IMAGE_URL,
                ChurchContract.DonationEntry.COLUMN_DESCRIPTION,
                ChurchContract.DonationEntry.COLUMN_CONTENT,
                ChurchContract.DonationEntry.COLUMN_FACEBOOK,
                ChurchContract.DonationEntry.COLUMN_TWITTER,
                ChurchContract.DonationEntry.COLUMN_YOUTUBE,
        };


        handler.startQuery(25, null, ChurchContract.DonationEntry.CONTENT_URI, projection, null, null, null);
    }

    private void loadDonationDataToViews() {

        if (localCursor.getCount() > 0) {


            fragmentDonationBinding.pageloader.stopProgress();

            if(localCursor.moveToFirst()){

                Donation donation = new Donation();
                donation.setTitle(localCursor.getString(localCursor.getColumnIndex(ChurchContract.DonationEntry.COLUMN_TITLE)));
                donation.setImage_url(localCursor.getString(localCursor.getColumnIndex(ChurchContract.DonationEntry.COLUMN_IMAGE_URL)));
                donation.setDescription(localCursor.getString(localCursor.getColumnIndex(ChurchContract.DonationEntry.COLUMN_DESCRIPTION)));
                donation.setContent(localCursor.getString(localCursor.getColumnIndex(ChurchContract.DonationEntry.COLUMN_CONTENT)));
                donation.setFacebook_url(localCursor.getString(localCursor.getColumnIndex(ChurchContract.DonationEntry.COLUMN_FACEBOOK)));
                donation.setTwitter_url(localCursor.getString(localCursor.getColumnIndex(ChurchContract.DonationEntry.COLUMN_TWITTER)));
                donation.setYoutube_url(localCursor.getString(localCursor.getColumnIndex(ChurchContract.DonationEntry.COLUMN_YOUTUBE)));

                //ensure fb,twitter,youtube is not null to proceed
                if(donation.getFacebook_url() == null || donation.getFacebook_url().isEmpty() || donation.getFacebook_url().trim().equalsIgnoreCase("null"))
                    fragmentDonationBinding.fragFbUrl.setVisibility(View.GONE);

                if(donation.getTwitter_url() == null || donation.getTwitter_url().isEmpty() || donation.getTwitter_url().trim().equalsIgnoreCase("null"))
                    fragmentDonationBinding.fragTwitterUrl.setVisibility(View.GONE);

                if(donation.getYoutube_url() == null || donation.getYoutube_url().isEmpty() || donation.getYoutube_url().trim().equalsIgnoreCase("null"))
                    fragmentDonationBinding.fragYoutubeUrl.setVisibility(View.GONE);

                fragmentDonationBinding.setDonation(donation);
                String html = "<html><head><link href=\"bootstrap.min.css\" type=\"text/css\" /></head><body>" + donation.getContent()+"<script src=\"bootstrap.min.js\" type=\"text/javascript\"></script> </body></html>";
                fragmentDonationBinding.donationWebView.loadDataWithBaseURL("file:///android_asset/",html,"text/html", "UTF-8", "");
            }


        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("orientationChange", 1);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDonationDataRetrievedSaved(DonationDataRetrievedSaved event) {
        getDonationFromDb();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDonationFromDb();
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

        fragmentDonationBinding.donationWebView.loadUrl("about:blank");

    }


}
