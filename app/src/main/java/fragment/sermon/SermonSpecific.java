package fragment.sermon;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentBibleVerseBinding;
import com.japhethwaswa.church.databinding.FragmentSermonSpecificBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import adapters.recyclerview.bible.BibleVerseRecyclerViewAdapter;
import app.NavActivity;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.pojo.BibleVersePositionEvent;
import event.pojo.FragConfigChange;
import model.BibleChapter;
import model.dyno.FragDyno;

public class SermonSpecific extends Fragment {
    private FragmentSermonSpecificBinding fragmentSermonSpecificBinding;
    //public NavActivity navActivity;

    private Cursor localCursor;
    private int orientationChange = -1;
    private int sermonId = -1;

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

        fragmentSermonSpecificBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sermon_specific, container, false);

        //get the following bundle arguments
        Bundle bundle = getArguments();
        orientationChange = bundle.getInt("orientationChange");
        sermonId = bundle.getInt("sermonId");

        //todo set sermon data here and after cursor load



        //set cursor to null
        localCursor = null;

        //navActivity = (NavActivity) getActivity();

        return fragmentSermonSpecificBinding.getRoot();
    }

    private void getThisSermonFromDb() {

        if (localCursor != null) {
            localCursor.close();
            localCursor =  null;
        }

        //show loader
        fragmentSermonSpecificBinding.pageloader.startProgress();
        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localCursor = cursor;
                    if (cursor.getCount() > 0) {
                        //hide loader here
                        fragmentSermonSpecificBinding.pageloader.stopProgress();
                        //todo update UI
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

        String selection =  ChurchContract.SermonEntry.COLUMN_SERMON_ID + "=?";
        String[] selectionArgs = {String.valueOf(sermonId)};

        handler.startQuery(23, null, ChurchContract.SermonEntry.CONTENT_URI, projection, selection, selectionArgs, null);
    }


    //todo subscribe to event and update data

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**close cursors**/
        if (localCursor != null) {
            localCursor.close();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        //get this specific sermon
        getThisSermonFromDb();

        //register event
        //EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        //EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
