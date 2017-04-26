package fragment.bible;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentBibleBookBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import adapters.recyclerview.bible.BibleBookRecyclerViewAdapter;
import app.NavActivity;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.ClickListener;
import event.CustomRecyclerTouchListener;
import event.pojo.BibleBookPositionEvent;
import event.pojo.BibleUpdate;
import event.pojo.FragConfigChange;
import model.dyno.FragDyno;

public class BibleBookFragment extends Fragment {
    private FragmentBibleBookBinding fragmentBibleBookBinding;
    public NavActivity navActivity;
    private FragmentManager localFragmentManager;
    private BibleBookRecyclerViewAdapter bibleBookRecyclerViewAdapter;
    private Cursor localTestamentCursor;
    private FragmentTransaction fragmentTransaction;
    private int bookPosition = -1;
    private int orientationChange = -1;
    private int bibleBookCurrentVisiblePos = -1;
    private int bibleChapterCurrentVisiblePos = -1;
    private int bibleVerseCurrentVisiblePos = -1;
//todo subscribe to event for bible update complete

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

        fragmentBibleBookBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bible_book, container, false);


        //handle orientation change since it returns to bible books
        Bundle bundle = getArguments();
        orientationChange = bundle.getInt("orientationChange");
        bibleBookCurrentVisiblePos = bundle.getInt("bibleBookCurrentVisiblePosition");
        bibleChapterCurrentVisiblePos = bundle.getInt("bibleChapterCurrentVisiblePosition");
        bibleVerseCurrentVisiblePos = bundle.getInt("bibleVerseCurrentVisiblePosition");

        //set cursor to null
        localTestamentCursor = null;

        navActivity = (NavActivity) getActivity();
        //code snippets come here
        localFragmentManager = navActivity.fragmentManager;
        fragmentTransaction = localFragmentManager.beginTransaction();


        /**bible books recycler view**/
        bibleBookRecyclerViewAdapter = new BibleBookRecyclerViewAdapter(localTestamentCursor);
        fragmentBibleBookBinding.bibleBooksRecycler.setAdapter(bibleBookRecyclerViewAdapter);
        fragmentBibleBookBinding.bibleBooksRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        /****/

        //add touch listener to recyclerview
        fragmentBibleBookBinding.bibleBooksRecycler.addOnItemTouchListener(new CustomRecyclerTouchListener(
                getActivity(), fragmentBibleBookBinding.bibleBooksRecycler, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                bookPosition = position;
                //start a new fragment showing all the chapters in this book
                launchBookChapters(bookPosition);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }
        ));



        return fragmentBibleBookBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        //confirm if is not screen orientation change then clear all the bible,chapters,verse logs in preference file
        if(orientationChange == -1){
            //clear all preference bible related data
            String[] itemNames = {getString(R.string.preference_book_position),
                    getString(R.string.preference_chapter_position),
                    getString(R.string.preference_verse_position)};
            FragDyno.clearDataPreference(itemNames);
        }

        //set bible books
        setBookTestaments();

        //register event
        EventBus.getDefault().register(this);
    }

    private void setBookTestaments() {
        //show loader
        fragmentBibleBookBinding.pageloader.startProgress();

        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localTestamentCursor = cursor;
                int previousBookPosition = FragDyno.getPrevPosition(getString(R.string.preference_book_position));

                if (previousBookPosition != -1 && orientationChange != -1) {
                    bookPosition = previousBookPosition;
                    launchBookChapters(bookPosition);
                } else {
                    if (cursor.getCount() > 0) {

                        //hide loader here
                        fragmentBibleBookBinding.pageloader.stopProgress();

                        //set recycler cursor
                        bibleBookRecyclerViewAdapter.setCursor(localTestamentCursor);

                        //scroll to position if set
                        if(bibleBookCurrentVisiblePos != -1){
                            fragmentBibleBookBinding.bibleBooksRecycler.scrollToPosition(bibleBookCurrentVisiblePos);
                        }

                    }

                }


            }
        };

        String[] projection = {
                ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NAME,
                ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_CODE,
                ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER,
                ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_VERSION
        };

        String orderBy = "CAST (" + ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER + " AS INTEGER) ASC";

        handler.startQuery(21, null, ChurchContract.BibleBookEntry.CONTENT_URI, projection, null, null, orderBy);
    }


    private void launchBookChapters(int position) {

        //save the current book in preferences
        FragDyno.saveToPreference(getString(R.string.preference_book_position), bookPosition);

        localTestamentCursor.moveToPosition(position);
        BibleChapterFragment bibleChapterFragment = new BibleChapterFragment();
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
        fragmentTransaction.commit();
    }



    @Override
    public void onPause() {
        super.onPause();

        /**close cursors**/
        if (localTestamentCursor != null) {
            localTestamentCursor.close();
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFragConfigChange(FragConfigChange event){
        //save current recyclerview position for bible book
        long bibleBookCurrentVisiblePosition;
         bibleBookCurrentVisiblePosition = ((LinearLayoutManager)fragmentBibleBookBinding.bibleBooksRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

         //post event to EventBus
         EventBus.getDefault().post(new BibleBookPositionEvent((int) bibleBookCurrentVisiblePosition));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBibleUpdate(BibleUpdate event){
        if(event.getBibleRank() == 0){
            //set bible books
            setBookTestaments();
        }
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
