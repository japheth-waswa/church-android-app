package fragment.blog;

import android.animation.Animator;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentBlogSpecificBinding;
import com.japhethwaswa.church.databinding.FragmentSermonSpecificBinding;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import adapters.recyclerview.blog.BlogRecyclerViewAdapter;
import adapters.recyclerview.comment.CommentRecyclerViewAdapter;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.ClickListener;
import event.CustomRecyclerTouchListener;
import event.pojo.DownloadSermonPdf;
import event.pojo.DownloadSermonPdfStatus;
import event.pojo.NavActivityColor;
import event.pojo.NavActivityHideNavigation;
import model.Blog;
import model.CustomModel;
import model.Sermon;
import model.dyno.ApplicationContextProvider;
import service.ChurchWebService;

public class BlogSpecific extends Fragment {

    private static final int MESSAGE_ID = 7;
    private FragmentBlogSpecificBinding fragmentBlogSpecificBinding;
    private CommentRecyclerViewAdapter commentRecyclerViewAdapter;
    private Cursor localCursor;
    private Cursor commentsCursor;
    private int orientationChange = -1;
    private int blogId = -1;
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

        fragmentBlogSpecificBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_blog_specific, container, false);

        //get the following bundle arguments
        Bundle bundle = getArguments();
        orientationChange = bundle.getInt("orientationChange");
        blogId = bundle.getInt("blogId");

        //set cursor to null
        localCursor = null;
        commentsCursor = null;

        /**blog comments recycler view adapter**/
        commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(commentsCursor);

        LinearLayoutManager linearLayoutManagerRecycler = new LinearLayoutManager(getContext()) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                //Animate in the visible children
                spruceAnimator = new Spruce.SpruceBuilder(fragmentBlogSpecificBinding.blogCommentsRecycler)
                        .sortWith(new DefaultSort(100))
                        .animateWith(DefaultAnimations.spinAnimator(fragmentBlogSpecificBinding.blogCommentsRecycler, 800))
                        .start();
            }
        };

        fragmentBlogSpecificBinding.blogCommentsRecycler.setAdapter(commentRecyclerViewAdapter);
        fragmentBlogSpecificBinding.blogCommentsRecycler.setLayoutManager(linearLayoutManagerRecycler);


        //webview settings
        fragmentBlogSpecificBinding.blogWebView.getSettings().setJavaScriptEnabled(true);

        return fragmentBlogSpecificBinding.getRoot();
    }

    private void getThisBlogFromDb() {

        if (localCursor != null) {
            localCursor.close();
            localCursor = null;
        }

        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localCursor = cursor;
                if (localCursor.getCount() > 0 && localCursor != null) {
                    localCursor.moveToFirst();
                    //update UI
                    Blog blog = new Blog();
                    blog.setBlog_title(cursor.getString(cursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_TITLE)));
                    blog.setImage_url(ChurchWebService.getRootAbsoluteUrl(ApplicationContextProvider.getsContext(), cursor.getString(cursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_IMAGE_URL))));
                    blog.setBrief_description(cursor.getString(cursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_BRIEF_DESCRIPTION)));
                    blog.setContent(cursor.getString(cursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_CONTENT)));
                    blog.setAuthor_name(cursor.getString(cursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_AUTHOR_NAME)));

                    /**date format**/
                    String blogDate = "";
                    String dateString = cursor.getString(cursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_PUBLISH_DATE));
                    SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    try {
                        Date date = dtFormat.parse(dateString);
                        SimpleDateFormat dtFormatOutPut = new SimpleDateFormat("EEE, d MMM yyyy");
                        blogDate = dtFormatOutPut.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    blog.setPublish_date(blogDate);
                    /****/

                    fragmentBlogSpecificBinding.setBlog(blog);

                    updateCustomModelData();


                    String html = "<html><head><link href=\"bootstrap.min.css\" type=\"text/css\" /></head><body>" + blog.getContent() + "<script src=\"bootstrap.min.js\" type=\"text/javascript\"></script> </body></html>";
                    fragmentBlogSpecificBinding.blogWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", "");


                }


            }
        };

        String[] projection = {
                ChurchContract.BlogsEntry.COLUMN_BLOG_ID,
                ChurchContract.BlogsEntry.COLUMN_TITLE,
                ChurchContract.BlogsEntry.COLUMN_IMAGE_URL,
                ChurchContract.BlogsEntry.COLUMN_BRIEF_DESCRIPTION,
                ChurchContract.BlogsEntry.COLUMN_PUBLISH_DATE,
                ChurchContract.BlogsEntry.COLUMN_CONTENT,
                ChurchContract.BlogsEntry.COLUMN_AUTHOR_NAME,
                ChurchContract.BlogsEntry.COLUMN_BLOG_CATEGORY_ID,
                ChurchContract.BlogsEntry.COLUMN_URL_KEY,
                ChurchContract.BlogsEntry.COLUMN_VISIBLE,
                ChurchContract.BlogsEntry.COLUMN_CREATED_AT
        };

        String selection = ChurchContract.BlogsEntry.COLUMN_BLOG_ID + "=?";
        String[] selectionArgs = {String.valueOf(blogId)};

        handler.startQuery(23, null, ChurchContract.BlogsEntry.CONTENT_URI, projection, selection, selectionArgs, null);
    }

    private void updateCustomModelData() {

        final CustomModel customModel = new CustomModel();
        final String[] categoryTitle = {null};
        final String[] commentsCount = {"0"};

        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {


                if (token == 33) {
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        categoryTitle[0] = cursor.getString(cursor.getColumnIndex(ChurchContract.BlogCategoryEntry.COLUMN_URL_KEY));
                        cursor.close();
                    }

                }


                if (token == 37) {
                    if (cursor != null) {
                        commentsCount[0] = String.valueOf(cursor.getCount());
                    }

                    fragmentBlogSpecificBinding.setCustommodel(customModel);
                }

                customModel.setCategory(categoryTitle[0]);
                customModel.setComments_count(commentsCount[0]);


                if (token == 37) {
                    //populate comments recyclerview
                    commentsCursor = cursor;
                    populateCommentsRecyclerView();
                }


            }
        };

        String[] projection = {
                ChurchContract.BlogCategoryEntry.COLUMN__TITLE,
                ChurchContract.BlogCategoryEntry.COLUMN_BLOG_CATEGORY_ID,
                ChurchContract.BlogCategoryEntry.COLUMN_URL_KEY,
                ChurchContract.BlogCategoryEntry.COLUMN_VISIBLE,
                ChurchContract.BlogCategoryEntry.COLUMN_CREATED_AT
        };

        String selection = ChurchContract.BlogCategoryEntry.COLUMN_BLOG_CATEGORY_ID + "=?";
        String[] selectionArgs = {localCursor.getString(localCursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_BLOG_CATEGORY_ID))};

        handler.startQuery(33, null, ChurchContract.BlogCategoryEntry.CONTENT_URI, projection, selection, selectionArgs, null);


        String[] projectioned = {
                ChurchContract.BlogCommentsEntry.COLUMN_NAMES,
                ChurchContract.BlogCommentsEntry.COLUMN_MESSAGE,
                ChurchContract.BlogCommentsEntry.COLUMN_UPLOADED,
                ChurchContract.BlogCommentsEntry.COLUMN_VISIBLE,
                ChurchContract.BlogCommentsEntry.COLUMN_CREATED_AT
        };

        String selectioned = ChurchContract.BlogCommentsEntry.COLUMN_BLOG_ID + "=? AND " + ChurchContract.BlogCommentsEntry.COLUMN_VISIBLE+ "=?";
        String[] selectionArgsed = {localCursor.getString(localCursor.getColumnIndex(ChurchContract.BlogsEntry.COLUMN_BLOG_ID)),"1"};

        handler.startQuery(37, null, ChurchContract.BlogCommentsEntry.CONTENT_URI, projectioned, selectioned, selectionArgsed, null);


    }

    private void populateCommentsRecyclerView() {

        Log.e("jean","cursor"+commentsCursor.getCount());
        if (commentsCursor != null && commentsCursor.getCount() > 0) {
            commentRecyclerViewAdapter.setCursor(commentsCursor);
        }


    }

    @Override
    public void onDestroy() {
        /**close cursors**/
        if (localCursor != null) {
            localCursor.close();
        }

        if (commentsCursor != null) {
            commentsCursor.close();
        }

        super.onDestroy();
    }


    @Override
    public void onStart() {
        super.onStart();

        //register event
        // EventBus.getDefault().register(this);
    }


    @Override
    public void onStop() {
        //EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        //get this specific
        getThisBlogFromDb();

    }

    @Override
    public void onPause() {

        super.onPause();

        fragmentBlogSpecificBinding.blogWebView.loadUrl("about:blank");
    }

    //todo add the comments sections(both commenting and displaying comments)
    //todo allow users to comment and post to remote server and update in the UI
    //todo in web app set the image location of the user
    //todo while not validated with network set an a 3dots-if sent and returned set nothing(reduce opacity)-comments
    //todo Unable to open asset URL: file:///android_asset/

}
