package fragment.sermon;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.japhethwaswa.church.databinding.FragmentSermonSpecificBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import db.ChurchContract;
import db.ChurchQueryHandler;
import event.pojo.DownloadSermonPdf;
import event.pojo.DownloadSermonPdfStatus;
import event.pojo.NavActivityColor;
import event.pojo.NavActivityHideNavigation;
import model.Sermon;
import model.dyno.ApplicationContextProvider;
import service.ChurchWebService;

public class SermonSpecific extends Fragment{

    private static final int MESSAGE_ID = 5;
    private FragmentSermonSpecificBinding fragmentSermonSpecificBinding;

    private Cursor localCursor;
    private int orientationChange = -1;
    private int sermonId = -1;


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

        fragmentSermonSpecificBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sermon_specific, container, false);

        //get the following bundle arguments
        Bundle bundle = getArguments();
        orientationChange = bundle.getInt("orientationChange");
        sermonId = bundle.getInt("sermonId");

        //set cursor to null
        localCursor = null;

        hideNavigation();


        //audio player settings
        fragmentSermonSpecificBinding.audioPlayer.getSettings().setJavaScriptEnabled(true);
        fragmentSermonSpecificBinding.audioPlayer.getSettings().setLoadWithOverviewMode(true);
        fragmentSermonSpecificBinding.audioPlayer.getSettings().setUseWideViewPort(true);

        //video player settings
        fragmentSermonSpecificBinding.videoPlayer.getSettings().setJavaScriptEnabled(true);

        return fragmentSermonSpecificBinding.getRoot();
    }

    private void hideNavigation() {
       //post event
        EventBus.getDefault().post(new NavActivityHideNavigation(false));
    }

    private void getThisSermonFromDb() {

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
                    Sermon sermon = new Sermon();
                    sermon.setSermon_title(localCursor.getString(localCursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_TITLE)));
                    sermon.setSermon_image_url(ChurchWebService.getRootAbsoluteUrl(ApplicationContextProvider.getsContext(),localCursor.getString(localCursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_IMAGE_URL))));
                    sermon.setSermon_brief_description(localCursor.getString(localCursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_BRIEF_DESCRIPTION)));
                    sermon.setSermon_audio_url(localCursor.getString(localCursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_AUDIO_URL)));
                    sermon.setSermon_video_url(localCursor.getString(localCursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_VIDEO_URL)));
                    sermon.setSermon_pdf_url(localCursor.getString(localCursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_PDF_URL)));
                    /**date format**/
                    String sermonDate = "";
                    String dateString = cursor.getString(cursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_DATE));
                    SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    try {
                        Date date = dtFormat.parse(dateString);
                        SimpleDateFormat dtFormatOutPut = new SimpleDateFormat("EEE, d MMM yyyy");
                        sermonDate =  dtFormatOutPut.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    sermon.setSermon_date(sermonDate);
                    /****/

                    fragmentSermonSpecificBinding.setSermon(sermon);

                    String pdfUrl = sermon.getSermon_pdf_url();
                    if(pdfUrl == null && pdfUrl.isEmpty()){
                        fragmentSermonSpecificBinding.pdfUrlBtn.setVisibility(View.GONE);
                    }

                    /**handle the audio webview**/
                    String AUDIO_URL = sermon.getSermon_audio_url();
                    if(AUDIO_URL != null){

                        //String AUDIO_URL = "https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/108641951?iframe=true&width=400&height=200";


                        String html = "<!DOCTYPE html><html> <head> <meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <link rel=\"stylesheet\" media=\"screen and (-webkit-device-pixel-ratio:1.5)\" href=\"hdpi.css\" /></head> <body style=\"background:black;margin:0 0 0 0; padding:0 0 0 0;\"> <iframe id=\"sc-widget " +
                                "\" width=\"100%\" height=\"50%\"" + // Set Appropriate Width and Height that you want for SoundCloud Player
                                " src=\"" + AUDIO_URL   // Set Embedded url
                                + "\" frameborder=\"no\" scrolling=\"no\"></iframe>" +
                                "<script src=\"https://w.soundcloud.com/player/api.js\" type=\"text/javascript\"></script> </body> </html> ";

                        fragmentSermonSpecificBinding.audioPlayer.loadDataWithBaseURL("",html,"text/html", "UTF-8", "");
                    }else{
                        //hide
                        fragmentSermonSpecificBinding.audioPlayerTitle.setVisibility(View.GONE);
                        fragmentSermonSpecificBinding.audioPlayer.setVisibility(View.GONE);
                    }

                    /**handle the video webview**/
                    //get the video code from end of url
                    String videoCode =  null;
                    String videoUrl = sermon.getSermon_video_url();
                    int start = videoUrl.indexOf("=");

                    if(start >= 0)
                    videoCode = videoUrl.substring(start + 1);

                    if(videoCode != null){
                        int screenWidth = getScreenDimensions();
                        int videoWidth = 420;
                        int videoHeight = 315;

                        if(screenWidth < 420){

                            //calculate new video height
                            videoHeight = (videoHeight*screenWidth) / videoWidth;
                            //videoHeight = Math.round(floatVidHeight);
                            videoWidth = screenWidth;
                        }

                        String frameVideo = "<html><body><iframe width=\""+ videoWidth +"\" height=\""+ videoHeight +"\" src=\"https://www.youtube.com/embed/"+ videoCode +"\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
                        fragmentSermonSpecificBinding.videoPlayer.loadData(frameVideo, "text/html", "utf-8");
                    }else {
                        //hide
                        fragmentSermonSpecificBinding.videoPlayerTitle.setVisibility(View.GONE);
                        fragmentSermonSpecificBinding.videoPlayer.setVisibility(View.GONE);
                    }

                }


            }
        };

        String[] projection = {
                ChurchContract.SermonEntry.COLUMN_SERMON_ID,
                ChurchContract.SermonEntry.COLUMN_SERMON_TITLE,
                ChurchContract.SermonEntry.COLUMN_SERMON_IMAGE_URL,
                ChurchContract.SermonEntry.COLUMN_SERMON_BRIEF_DESCRIPTION,
                ChurchContract.SermonEntry.COLUMN_SERMON_AUDIO_URL,
                ChurchContract.SermonEntry.COLUMN_SERMON_VIDEO_URL,
                ChurchContract.SermonEntry.COLUMN_SERMON_PDF_URL,
                ChurchContract.SermonEntry.COLUMN_SERMON_DATE,
                ChurchContract.SermonEntry.COLUMN_SERMON_VISIBLE,
                ChurchContract.SermonEntry.COLUMN_SERMON_CREATED_AT,
                ChurchContract.SermonEntry.COLUMN_SERMON_UPDATED_AT
        };

        String selection = ChurchContract.SermonEntry.COLUMN_SERMON_ID + "=?";
        String[] selectionArgs = {String.valueOf(sermonId)};

        handler.startQuery(23, null, ChurchContract.SermonEntry.CONTENT_URI, projection, selection, selectionArgs, null);
    }

    public int getScreenDimensions() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        return width;
    }

    @Override
    public void onDestroy() {
        /**close cursors**/
        if (localCursor != null) {
            localCursor.close();
        }

        super.onDestroy();
    }


    @Override
    public void onStart() {
        super.onStart();

        //register event
        EventBus.getDefault().register(this);
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        //get this specific sermon
        getThisSermonFromDb();

        //post event to change background color in activity
        EventBus.getDefault().post(new NavActivityColor(R.color.lightBlack));


    }

    @Override
    public void onPause() {

        super.onPause();

        fragmentSermonSpecificBinding.audioPlayer.loadUrl("about:blank");
        fragmentSermonSpecificBinding.videoPlayer.loadUrl("about:blank");
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDownloadSermonPdf(DownloadSermonPdf event) {
        //post event with 3 statuses-0(started)-1(success)-2(error)
        EventBus.getDefault().post(new DownloadSermonPdfStatus(0));

        File downloadDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        String url = localCursor.getString(localCursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_PDF_URL));
        String fileName = localCursor.getString(localCursor.getColumnIndex(ChurchContract.SermonEntry.COLUMN_SERMON_TITLE));
        //String fileName = "file_name_comes_here";
        String dirPath = downloadDir.toString();

        AndroidNetworking.download(url,downloadDir.toString(),fileName)
                .setTag("downloadSermon")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        EventBus.getDefault().post(new DownloadSermonPdfStatus(1));
                    }
                    @Override
                    public void onError(ANError error) {
                        EventBus.getDefault().post(new DownloadSermonPdfStatus(2));
                    }
                });

    }


}
