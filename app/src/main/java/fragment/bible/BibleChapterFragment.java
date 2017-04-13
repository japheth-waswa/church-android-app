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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentBibleBookBinding;

import adapters.recyclerview.BibleBookRecyclerViewAdapter;
import app.NavActivity;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.ClickListener;
import event.CustomRecyclerTouchListener;

public class BibleChapterFragment extends Fragment {
    private FragmentBibleBookBinding fragmentBibleBookBinding;
    public NavActivity navActivity;
    private FragmentManager localFragmentManager;
    private BibleBookRecyclerViewAdapter bibleBookRecyclerViewAdapter;
    private Cursor localTestamentCursor;
    private FragmentTransaction fragmentTransaction;
    private int bookPosition = -1;

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

        //todo handle orientation change since it returns to bible books
        /**Bundle bundle = getArguments();
        bookPosition = bundle.getInt()**/

        //set cursor to null
        localTestamentCursor = null;

        /**navActivity = (NavActivity) getActivity();
        //code snippets come here
        localFragmentManager = navActivity.fragmentManager;
        fragmentTransaction = localFragmentManager.beginTransaction();


        bible books recycler view
        bibleBookRecyclerViewAdapter = new BibleBookRecyclerViewAdapter(localTestamentCursor);
        fragmentBibleBookBinding.bibleBooksRecycler.setAdapter(bibleBookRecyclerViewAdapter);
        fragmentBibleBookBinding.bibleBooksRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

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

        //set bible books
        setBookTestaments();**/

        return fragmentBibleBookBinding.getRoot();
    }

    private void setBookTestaments() {
        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localTestamentCursor = cursor;

                if (bookPosition != -1) {
                    launchBookChapters(bookPosition);
                } else {
                    if (cursor.getCount() > 0) {
                        //set recycler cursor
                        bibleBookRecyclerViewAdapter.setCursor(localTestamentCursor);
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

        /**localTestamentCursor.moveToPosition(position);
        BibleChapterFragment bibleChapterFragment = new BibleChapterFragment();
        String bibleBookCode = localTestamentCursor.getString(localTestamentCursor.getColumnIndex(ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_CODE));
        Bundle bundle = new Bundle();
        bundle.putString("bibleBookCode", bibleBookCode);

        bibleChapterFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.mainBibleFragment, bibleChapterFragment, "bibleChapterFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();**/
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        /**close cursors**/
        if (localTestamentCursor != null) {
            localTestamentCursor.close();
        }

    }
}
