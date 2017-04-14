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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentBibleBookBinding;
import com.japhethwaswa.church.databinding.FragmentBibleChapterBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import adapters.recyclerview.BibleBookRecyclerViewAdapter;
import adapters.recyclerview.BibleChapterRecyclerViewAdapter;
import app.NavActivity;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.ClickListener;
import event.CustomRecyclerTouchListener;
import event.pojo.BibleBookPositionEvent;
import event.pojo.BibleChapterPositionEvent;
import event.pojo.FragConfigChange;
import model.BibleBook;

public class BibleChapterFragment extends Fragment {
    private FragmentBibleChapterBinding fragmentBibleChapterBinding;
    public NavActivity navActivity;
    private FragmentManager localFragmentManager;
    private BibleChapterRecyclerViewAdapter bibleChapterRecyclerViewAdapter;
    private Cursor localTestamentCursor;
    private FragmentTransaction fragmentTransaction;
    private String bibleCode = null;
    private String bibleName = null;
    private int chapterPosition = -1;
    private int orientationChange = -1;
    private int bibleChapterCurrentVisiblePos = -1;
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

        fragmentBibleChapterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bible_chapter, container, false);

        //get the following bundle arguments chapter/verse position
        Bundle bundle = getArguments();
        orientationChange = bundle.getInt("orientationChange");
        bibleChapterCurrentVisiblePos = bundle.getInt("bibleChapterCurrentVisiblePosition");
        bibleVerseCurrentVisiblePos = bundle.getInt("bibleVerseCurrentVisiblePosition");
        bibleCode = bundle.getString("bibleBookCode");
        bibleName = bundle.getString("bibleBookName");

        //set title etc
        BibleBook bibleBook = new BibleBook();
        bibleBook.setBook_name(bibleName);
        fragmentBibleChapterBinding.setBibleBook(bibleBook);


        //set cursor to null
        localTestamentCursor = null;

        navActivity = (NavActivity) getActivity();
        //code snippets come here
        localFragmentManager = navActivity.fragmentManager;
        fragmentTransaction = localFragmentManager.beginTransaction();


        /**bible books recycler view**/
        bibleChapterRecyclerViewAdapter = new BibleChapterRecyclerViewAdapter(localTestamentCursor);
        fragmentBibleChapterBinding.bibleChaptersRecycler.setAdapter(bibleChapterRecyclerViewAdapter);
        fragmentBibleChapterBinding.bibleChaptersRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        /****/

        //add touch listener to recyclerview
        fragmentBibleChapterBinding.bibleChaptersRecycler.addOnItemTouchListener(new CustomRecyclerTouchListener(
                getActivity(), fragmentBibleChapterBinding.bibleChaptersRecycler, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                chapterPosition = position;
                //start a new fragment showing all the verses in this chapter
                launchChapterVerses(chapterPosition);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }
        ));



        return fragmentBibleChapterBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        //set bible books
        setChapterTestaments();

        //register event
        EventBus.getDefault().register(this);
    }

    private void setChapterTestaments() {
        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localTestamentCursor = cursor;
                int previousChapterPosition = getPrevChapterPosition();

                if (previousChapterPosition != -1 && orientationChange != -1) {
                    chapterPosition = previousChapterPosition;
                    launchChapterVerses(chapterPosition);
                } else {
                    if (cursor.getCount() > 0) {
                        //set recycler cursor
                        bibleChapterRecyclerViewAdapter.setCursor(localTestamentCursor);

                        //scroll to position if set
                        if(bibleChapterCurrentVisiblePos != -1){
                            fragmentBibleChapterBinding.bibleChaptersRecycler.scrollToPosition(bibleChapterCurrentVisiblePos);
                        }

                    }

                }


            }
        };

        String[] projection = {
                ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_BOOK_CODE,
                ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_CODE,
                ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_NUMBER,
        };

        String selection = ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_BOOK_CODE + "=?";
        String[] selectionArgs = {bibleCode};
        String orderBy = "CAST (" + ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_NUMBER + " AS INTEGER) ASC";

        handler.startQuery(21, null, ChurchContract.BibleChapterEntry.CONTENT_URI, projection,selection, selectionArgs, orderBy);
    }

    private int getPrevChapterPosition() {
        Resources res = getResources();
        String preferenceFileKey = res.getString(R.string.preference_file_key);
        SharedPreferences sharedPref = getContext().getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE);
       return sharedPref.getInt(res.getString(R.string.preference_chapter_position),-1);
    }

    private void launchChapterVerses(int position) {

        //save the current chapter position in preferences
        saveToPreference(position);

        localTestamentCursor.moveToPosition(position);
        BibleVerseFragment bibleVerseFragment = new BibleVerseFragment();
        String bibleChapterCode = localTestamentCursor.getString(localTestamentCursor.getColumnIndex(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_CODE));
        String bibleChapterNumber = localTestamentCursor.getString(localTestamentCursor.getColumnIndex(ChurchContract.BibleChapterEntry.COLUMN_CHAPTER_NUMBER));

        Bundle bundle = new Bundle();
        bundle.putString("bibleBookName", bibleName);
        bundle.putString("bibleChapterNumber", bibleChapterNumber);
        bundle.putString("bibleChapterCode", bibleChapterCode);
        bundle.putInt("orientationChange",orientationChange);
        bundle.putInt("bibleVerseCurrentVisiblePosition",bibleVerseCurrentVisiblePos);

        bibleVerseFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.mainBibleFragment, bibleVerseFragment, "bibleVerseFragment");
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void saveToPreference(int chapterPosition) {
        Resources res = getResources();
        String preferenceFileKey = res.getString(R.string.preference_file_key);
        SharedPreferences sharedPref = getContext().getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(res.getString(R.string.preference_chapter_position),chapterPosition);
        editor.commit();
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
        long bibleChapterCurrentVisiblePosition;
        bibleChapterCurrentVisiblePosition = ((LinearLayoutManager)fragmentBibleChapterBinding.bibleChaptersRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

         //post event to EventBus
         EventBus.getDefault().post(new BibleChapterPositionEvent((int) bibleChapterCurrentVisiblePosition));
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
