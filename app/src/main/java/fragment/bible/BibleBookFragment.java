package fragment.bible;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentBibleBookBinding;

import adapters.recyclerview.BibleBookRecyclerViewAdapter;
import app.NavActivity;
import db.ChurchContract;
import db.ChurchQueryHandler;

public class BibleBookFragment extends Fragment {
    private FragmentBibleBookBinding fragmentBibleBookBinding;
    public NavActivity navActivity;
    private FragmentManager localFragmentManager;
    private BibleBookRecyclerViewAdapter bibleBookRecyclerViewAdapter;
    private Cursor localTestamentCursor;

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
        bibleBookRecyclerViewAdapter =  new BibleBookRecyclerViewAdapter(localTestamentCursor);
        fragmentBibleBookBinding.bibleBooksRecycler.setAdapter(bibleBookRecyclerViewAdapter);
        fragmentBibleBookBinding.bibleBooksRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        /****/


        /**navActivity =  (NavActivity) getActivity();
        //code snippets come here
        localFragmentManager = navActivity.fragmentManager;
        FragmentTransaction fragmentTransaction = localFragmentManager.beginTransaction();**/

        //set bible books
        setBookTestaments();

        return fragmentBibleBookBinding.getRoot();
    }

    private void setBookTestaments() {
        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()){
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if(cursor.getCount() > 0){
                    localTestamentCursor = cursor;
                    //set recycler cursor
                    bibleBookRecyclerViewAdapter.setCursor(localTestamentCursor);
                }

            }
        };

        String[] projection = {
                ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NAME,
                ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_CODE,
                ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER,
                ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_VERSION
        };

        String orderBy = "CAST (" +ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER+ " AS INTEGER) ASC";

        handler.startQuery(21,null, ChurchContract.BibleBookEntry.CONTENT_URI,projection,null,null,orderBy);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onPause() {
        super.onPause();

        /**close cursors**/
        localTestamentCursor.close();

    }
}
