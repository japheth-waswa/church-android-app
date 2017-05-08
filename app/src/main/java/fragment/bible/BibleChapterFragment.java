package fragment.bible;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentBibleChapterBinding;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import adapters.recyclerview.bible.BibleChapterRecyclerViewAdapter;
import app.NavActivity;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.ClickListener;
import event.CustomRecyclerTouchListener;
import event.pojo.BibleChapterPositionEvent;
import event.pojo.BibleUpdate;
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
    private Animator spruceAnimator;
    private int dualPane = -1;

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
        dualPane = bundle.getInt("dualPane");

        //set title etc
        BibleBook bibleBook = new BibleBook();
        bibleBook.setBook_name(bibleName);
        fragmentBibleChapterBinding.setBibleBook(bibleBook);


        //set cursor to null
        localTestamentCursor = null;

        navActivity = (NavActivity) getActivity();
        //code snippets come here
        localFragmentManager = navActivity.fragmentManager;
        //fragmentTransaction = localFragmentManager.beginTransaction();


        /**bible books recycler view**/
        bibleChapterRecyclerViewAdapter = new BibleChapterRecyclerViewAdapter(localTestamentCursor);
        LinearLayoutManager linearLayoutManagerRecycler = new LinearLayoutManager(getContext()){
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                //Animate in the visible children
                spruceAnimator = new Spruce.SpruceBuilder(fragmentBibleChapterBinding.bibleChaptersRecycler)
                        .sortWith(new DefaultSort(100))
                        .animateWith(DefaultAnimations.shrinkAnimator(fragmentBibleChapterBinding.bibleChaptersRecycler,800),
                                ObjectAnimator.ofFloat(fragmentBibleChapterBinding.bibleChaptersRecycler,
                                        "translationX",-fragmentBibleChapterBinding.bibleChaptersRecycler.getWidth(),0f)
                                        .setDuration(800)).start();
            }
        };
        fragmentBibleChapterBinding.bibleChaptersRecycler.setAdapter(bibleChapterRecyclerViewAdapter);
        //fragmentBibleChapterBinding.bibleChaptersRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        fragmentBibleChapterBinding.bibleChaptersRecycler.setLayoutManager(linearLayoutManagerRecycler);
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

        //set bible chapters
        setChapterTestaments();

        //register event
        EventBus.getDefault().register(this);
    }

    private void setChapterTestaments() {
        //show loader
        fragmentBibleChapterBinding.pageloader.startProgress();

        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localTestamentCursor = cursor;
                int previousChapterPosition = getPrevChapterPosition();

                if (previousChapterPosition != -1 && orientationChange != -1) {
                    chapterPosition = previousChapterPosition;
                    if (dualPane != -1)
                        populateChapterRecycler();
                    launchChapterVerses(chapterPosition);
                } else {
                    populateChapterRecycler();

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

    private void populateChapterRecycler() {
        if (localTestamentCursor.getCount() > 0) {

            //hide loader here
            fragmentBibleChapterBinding.pageloader.stopProgress();

            //set recycler cursor
            bibleChapterRecyclerViewAdapter.setCursor(localTestamentCursor);

            //scroll to position if set
            if(bibleChapterCurrentVisiblePos != -1){
                fragmentBibleChapterBinding.bibleChaptersRecycler.scrollToPosition(bibleChapterCurrentVisiblePos);
            }

        }
    }

    private int getPrevChapterPosition() {
        Resources res = getResources();
        String preferenceFileKey = res.getString(R.string.preference_file_key);
        SharedPreferences sharedPref = getContext().getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE);
       return sharedPref.getInt(res.getString(R.string.preference_chapter_position),-1);
    }

    private void launchChapterVerses(int position) {

        //reset the fragment transaction
        fragmentTransaction = localFragmentManager.beginTransaction();

        //save the current chapter position in preferences
        saveToPreference(position);

        if(localTestamentCursor.moveToPosition(position)){

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
            if (dualPane == -1) {
                fragmentTransaction.replace(R.id.mainBibleFragment, bibleVerseFragment, "bibleVerseFragment");
            } else {
                fragmentTransaction.replace(R.id.mainBibleFragmentSpecs, bibleVerseFragment, "bibleChapterFragment");
            }
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

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

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBibleUpdate(BibleUpdate event){
        if(event.getBibleRank() == 1){
            //set chapters
            setChapterTestaments();
        }
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
