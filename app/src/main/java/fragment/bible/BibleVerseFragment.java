package fragment.bible;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import adapters.recyclerview.bible.BibleVerseRecyclerViewAdapter;
import app.NavActivity;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.pojo.BibleUpdate;
import event.pojo.BibleVersePositionEvent;
import event.pojo.FragConfigChange;
import model.BibleChapter;

public class BibleVerseFragment extends Fragment {
    private FragmentBibleVerseBinding fragmentBibleVerseBinding;
    public NavActivity navActivity;
    private FragmentManager localFragmentManager;
    private BibleVerseRecyclerViewAdapter bibleVerseRecyclerViewAdapter;
    private Cursor localTestamentCursor;
    private FragmentTransaction fragmentTransaction;
    private String bibleChapterCode = null;
    private String bibleChapterNumber = null;
    private String bibleBookName = null;
    private int orientationChange = -1;
    private int bibleVerseCurrentVisiblePos = -1;

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

        fragmentBibleVerseBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bible_verse, container, false);

        //get the following bundle arguments chapter/verse position
        Bundle bundle = getArguments();
        orientationChange = bundle.getInt("orientationChange");
        bibleVerseCurrentVisiblePos = bundle.getInt("bibleVerseCurrentVisiblePosition");
        bibleBookName = bundle.getString("bibleBookName");
        bibleChapterNumber = bundle.getString("bibleChapterNumber");
        bibleChapterCode = bundle.getString("bibleChapterCode");

        //set title etc
        BibleChapter bibleChapter = new BibleChapter();
        bibleChapter.setChapter_number(bibleBookName +" : Chapter "+ bibleChapterNumber);
        fragmentBibleVerseBinding.setBibleChapter(bibleChapter);


        //set cursor to null
        localTestamentCursor = null;

        navActivity = (NavActivity) getActivity();
        //code snippets come here
        localFragmentManager = navActivity.fragmentManager;
        fragmentTransaction = localFragmentManager.beginTransaction();


        /**bible books recycler view**/
        bibleVerseRecyclerViewAdapter = new BibleVerseRecyclerViewAdapter(localTestamentCursor);
        fragmentBibleVerseBinding.bibleVersesRecycler.setAdapter(bibleVerseRecyclerViewAdapter);
        fragmentBibleVerseBinding.bibleVersesRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        /****/

        return fragmentBibleVerseBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        //set bible verses
        setVerseTestaments();

        //register event
        EventBus.getDefault().register(this);
    }

    private void setVerseTestaments() {
        //show loader
        fragmentBibleVerseBinding.pageloader.startProgress();

        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localTestamentCursor = cursor;

                    if (cursor.getCount() > 0) {
                        //hide loader here
                        fragmentBibleVerseBinding.pageloader.stopProgress();

                        //set recycler cursor
                        bibleVerseRecyclerViewAdapter.setCursor(localTestamentCursor);

                        //scroll to position if set
                        if(bibleVerseCurrentVisiblePos != -1){
                            fragmentBibleVerseBinding.bibleVersesRecycler.scrollToPosition(bibleVerseCurrentVisiblePos);
                        }

                    }


            }
        };

        String[] projection = {
                ChurchContract.BibleVerseEntry.COLUMN_VERSE,
                ChurchContract.BibleVerseEntry.COLUMN_VERSE_CHAPTER_CODE,
                ChurchContract.BibleVerseEntry.COLUMN_VERSE_NUMBER,
        };

        String selection = ChurchContract.BibleVerseEntry.COLUMN_VERSE_CHAPTER_CODE + "=?";
        String[] selectionArgs = {bibleChapterCode};
        String orderBy = "CAST (" + ChurchContract.BibleVerseEntry.COLUMN_VERSE_NUMBER + " AS INTEGER) ASC";

        handler.startQuery(21, null, ChurchContract.BibleVerseEntry.CONTENT_URI, projection,selection, selectionArgs, orderBy);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**close cursors**/
        if (localTestamentCursor != null) {
            localTestamentCursor.close();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFragConfigChange(FragConfigChange event){
        //save current recyclerview position
        long bibleVerseCurrentVisiblePosition;
        bibleVerseCurrentVisiblePosition = ((LinearLayoutManager)fragmentBibleVerseBinding.bibleVersesRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

         //post event to EventBus
         EventBus.getDefault().post(new BibleVersePositionEvent((int) bibleVerseCurrentVisiblePosition));

        }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBibleUpdate(BibleUpdate event){
        if(event.getBibleRank() == 2){
            //set verses
            setVerseTestaments();
        }
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
