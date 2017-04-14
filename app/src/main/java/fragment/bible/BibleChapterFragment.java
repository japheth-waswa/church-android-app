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
    private int bookPosition = -1;
    private int orientationChange = -1;
    private int bibleBookCurrentVisiblePos = -1;

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




        //todo get the following bundle arguments chapter/verse position
        Bundle bundle = getArguments();
        orientationChange = bundle.getInt("orientationChange");
        //bibleBookCurrentVisiblePos = bundle.getInt("bibleBookCurrentVisiblePosition");
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

                bookPosition = position;
                //start a new fragment showing all the chapters in this book
                //launchBookChapters(bookPosition);

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
        //EventBus.getDefault().register(this);
    }

    private void setChapterTestaments() {
        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localTestamentCursor = cursor;
                //int previousBookPosition = getPrevBookPosition();

               // if (previousBookPosition != -1 && orientationChange != -1) {
                  //  bookPosition = previousBookPosition;
                   // launchBookChapters(bookPosition);
                //} else {
                    if (cursor.getCount() > 0) {
                        //set recycler cursor
                        bibleChapterRecyclerViewAdapter.setCursor(localTestamentCursor);

                        //scroll to position if set
                        //if(bibleBookCurrentVisiblePos != -1){
                        //    fragmentBibleChapterBinding.bibleChaptersRecycler.scrollToPosition(bibleBookCurrentVisiblePos);
                        //}

                    }

                //}


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

    private int getPrevBookPosition() {
        Resources res = getResources();
        String preferenceFileKey = res.getString(R.string.preference_file_key);
        SharedPreferences sharedPref = getContext().getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE);
       return sharedPref.getInt(res.getString(R.string.preference_book_position),-1);
    }

    private void launchBookChapters(int position) {

        //save the current book in preferences
        saveToPreference(bookPosition);

        localTestamentCursor.moveToPosition(position);
        BibleChapterFragment bibleChapterFragment = new BibleChapterFragment();
        String bibleBookCode = localTestamentCursor.getString(localTestamentCursor.getColumnIndex(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_CODE));

        Bundle bundle = new Bundle();
        bundle.putString("bibleBookCode", bibleBookCode);
        bundle.putInt("orientationChange",orientationChange);

        bibleChapterFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.mainBibleFragment, bibleChapterFragment, "bibleChapterFragment");
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void saveToPreference(int bookPosition) {
        Resources res = getResources();
        String preferenceFileKey = res.getString(R.string.preference_file_key);
        SharedPreferences sharedPref = getContext().getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(res.getString(R.string.preference_book_position),bookPosition);
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
        //save current recyclerview position for bible book
        /**long bibleBookCurrentVisiblePosition;
         bibleBookCurrentVisiblePosition = ((LinearLayoutManager)fragmentBibleBookBinding.bibleBooksRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

         //post event to EventBus
         EventBus.getDefault().post(new BibleBookPositionEvent((int) bibleBookCurrentVisiblePosition));**/
    }


    @Override
    public void onStop() {
        //EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
