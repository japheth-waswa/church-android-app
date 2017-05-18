package fragment.blog;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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

import com.birbit.android.jobqueue.JobManager;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentBlogsAllBinding;
import com.japhethwaswa.church.databinding.FragmentSermonsAllBinding;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import adapters.recyclerview.blog.BlogRecyclerViewAdapter;
import adapters.recyclerview.sermon.SermonRecyclerViewAdapter;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.ClickListener;
import event.CustomRecyclerTouchListener;
import event.pojo.BlogDataRetrievedSaved;
import event.pojo.SermonDataRetrievedSaved;
import fragment.sermon.SermonSpecific;
import model.dyno.FragDyno;

public class BlogAllFragment extends Fragment {

    private FragmentBlogsAllBinding fragmentBlogsAllBinding;

    private JobManager jobManager;
    private FragmentManager localFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BlogRecyclerViewAdapter blogRecyclerViewAdapter;
    private Cursor localCursor;
    private int blogPosition = -1;
    private int orientationChange = -1;
    private int currVisiblePosition = -1;
    private int previousPosition = -1;
    private int dualPane = -1;
    private Animator spruceAnimator;

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
        fragmentBlogsAllBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_blogs_all, container, false);

        //handle orientation change since it returns to bible books
        Bundle bundle = getArguments();
        orientationChange = bundle.getInt("orientationChange");
        currVisiblePosition = bundle.getInt("positionCurrentlyVisible");
        dualPane = bundle.getInt("dualPane");


        localFragmentManager = getActivity().getSupportFragmentManager();

        //set cursor to null
        localCursor = null;


        /**blog recycler view adapter**/
        blogRecyclerViewAdapter = new BlogRecyclerViewAdapter(localCursor);

        LinearLayoutManager linearLayoutManagerRecycler = new LinearLayoutManager(getContext()) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                //Animate in the visible children
                spruceAnimator = new Spruce.SpruceBuilder(fragmentBlogsAllBinding.blogsRecycler)
                        .sortWith(new DefaultSort(100))
                        .animateWith(DefaultAnimations.spinAnimator(fragmentBlogsAllBinding.blogsRecycler, 800))
                        .start();
            }
        };

        fragmentBlogsAllBinding.blogsRecycler.setAdapter(blogRecyclerViewAdapter);
        fragmentBlogsAllBinding.blogsRecycler.setLayoutManager(linearLayoutManagerRecycler);


        //add touch listener to recyclerview
        fragmentBlogsAllBinding.blogsRecycler.addOnItemTouchListener(new CustomRecyclerTouchListener(
                getActivity(), fragmentBlogsAllBinding.blogsRecycler, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                //start a new fragment showing specific sermon
                showSpecificBlog(position);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }
        ));


        return fragmentBlogsAllBinding.getRoot();
    }

    //fetch all blogs from db.
    private void getBlogsFromDb() {
        if (localCursor != null) {
            localCursor.close();
            localCursor = null;
        }
        //show loader
        fragmentBlogsAllBinding.pageloader.startProgress();

        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localCursor = cursor;
                previousPosition = FragDyno.getPrevPosition(getString(R.string.preference_blog_position));

                if (previousPosition != -1 && orientationChange != -1) {
                    blogPosition = previousPosition;
                    if (dualPane != -1)
                        loadBlogListToRecyclerView();
                    showSpecificBlog(blogPosition);
                } else {
                    loadBlogListToRecyclerView();

                }


            }
        };

        String[] projection = {
                ChurchContract.BlogsEntry.COLUMN_BLOG_ID,
                ChurchContract.BlogsEntry.COLUMN_TITLE,
                ChurchContract.BlogsEntry.COLUMN_IMAGE_URL,
                ChurchContract.BlogsEntry.COLUMN_BRIEF_DESCRIPTION,
                ChurchContract.BlogsEntry.COLUMN_PUBLISH_DATE,
                ChurchContract.BlogsEntry.COLUMN_AUTHOR_NAME,
                ChurchContract.BlogsEntry.COLUMN_BLOG_CATEGORY_ID,
                ChurchContract.BlogsEntry.COLUMN_URL_KEY,
                ChurchContract.BlogsEntry.COLUMN_VISIBLE,
                ChurchContract.BlogsEntry.COLUMN_CREATED_AT
        };

        String orderBy = ChurchContract.BlogsEntry.COLUMN_PUBLISH_DATE + " DESC";
        //String orderBy = null;

        handler.startQuery(23, null, ChurchContract.BlogsEntry.CONTENT_URI, projection, null, null, orderBy);
    }
//todo for small devices change the way item are displayed in the recyclerview
    //todo change the animation of items in the recyclerview
    private void loadBlogListToRecyclerView() {
        if (localCursor != null && localCursor.getCount() > 0) {
            //hide loader here
            fragmentBlogsAllBinding.pageloader.stopProgress();
            //set recycler cursor
            blogRecyclerViewAdapter.setCursor(localCursor);

            //scroll to position if set
            if (currVisiblePosition != -1) {
                fragmentBlogsAllBinding.blogsRecycler.scrollToPosition(currVisiblePosition);
            }

        }
    }


    private void showSpecificBlog(int position) {

        //reset the fragment transaction
        fragmentTransaction = localFragmentManager.beginTransaction();

        //save the current position in preferences
        FragDyno.saveToPreference(getString(R.string.preference_blog_position), position);

        if (localCursor != null) {
            if (localCursor.moveToPosition(position)) {


                BlogSpecific blogSpecific = new BlogSpecific();
                String blogId = localCursor.getString(localCursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_BLOG_ID));

                Bundle bundle = new Bundle();
                bundle.putInt("orientationChange", orientationChange);
                bundle.putInt("blogId", Integer.valueOf(blogId));

                blogSpecific.setArguments(bundle);

                if (dualPane == -1) {
                    fragmentTransaction.replace(R.id.mainBlogFragment, blogSpecific, "blogSpecificFragment");
                } else {
                    fragmentTransaction.replace(R.id.mainBlogSpecific, blogSpecific, "blogSpecificFragment");
                }


                fragmentTransaction.commit();
            }

        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBlogDataRetrievedSaved(BlogDataRetrievedSaved event) {
        //parse the sermon item and display
        getBlogsFromDb();
    }


    @Override
    public void onPause() {
        super.onPause();
        /**close cursors**/
        if (localCursor != null && localCursor.isClosed() == false) {
            localCursor.close();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //confirm if is not screen orientation change then clear all the bible,chapters,verse logs in preference file
        if (orientationChange == -1) {
            //clear all preference related data
            String[] itemNames = {getString(R.string.preference_blog_position)};
            FragDyno.clearDataPreference(itemNames);
        }

        getBlogsFromDb();

        //register event
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        //unregister event
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
