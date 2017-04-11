package fragment.bible;

import android.database.Cursor;
import android.database.DatabaseUtils;
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
import com.japhethwaswa.church.databinding.FragmentBibleBinding;
import com.japhethwaswa.church.databinding.FragmentBibleBookBinding;

import adapters.recyclerview.BibleBookOldRecyclerViewAdapter;
import app.NavActivity;
import db.ChurchContract;
import db.ChurchQueryHandler;

public class BibleBookFragment extends Fragment {
    private FragmentBibleBookBinding fragmentBibleBookBinding;
    public NavActivity navActivity;
    private FragmentManager localFragmentManager;
    private BibleBookOldRecyclerViewAdapter bibleBookOldRecyclerViewAdapter;
    private Cursor localOldTestamentCursor;

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

        fragmentBibleBookBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bible_book,container,false);

        /**old testament recycler view**/
        bibleBookOldRecyclerViewAdapter =  new BibleBookOldRecyclerViewAdapter(localOldTestamentCursor);
        fragmentBibleBookBinding.bibleBooksOldRecycler.setAdapter(bibleBookOldRecyclerViewAdapter);
        fragmentBibleBookBinding.bibleBooksOldRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        /****/

        /**navActivity =  (NavActivity) getActivity();
        //code snippets come here
        localFragmentManager = navActivity.fragmentManager;
        FragmentTransaction fragmentTransaction = localFragmentManager.beginTransaction();**/

        //set old testament
        setOldTestament();
        return fragmentBibleBookBinding.getRoot();
    }

    private void setOldTestament() {
        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()){
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if(cursor.getCount() > 0){
                    localOldTestamentCursor = cursor;
                    //set recycler cursor
                    bibleBookOldRecyclerViewAdapter.setCursor(localOldTestamentCursor);
                }

            }
        };

        String[] projection = {
                ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NAME,
                ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_CODE,
                ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER,
                ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_VERSION
        };
        String selection = ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_VERSION+"=?";
        String[] selectionArgs = {"old"};
        //String orderBy = ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER+ " ASC";
        String orderBy = "CAST (" +ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER+ " AS INTEGER) ASC";

        handler.startQuery(21,null, ChurchContract.BibleBookEntry.CONTENT_URI,projection,selection,selectionArgs,orderBy);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /**close cursors**/
        localOldTestamentCursor.close();
    }
}
