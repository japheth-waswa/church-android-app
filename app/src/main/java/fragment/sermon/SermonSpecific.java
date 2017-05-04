package fragment.sermon;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cleveroad.play_widget.PlayLayout;
import com.cleveroad.play_widget.VisualizerShadowChanger;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.FragmentBibleVerseBinding;
import com.japhethwaswa.church.databinding.FragmentSermonSpecificBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import adapters.recyclerview.bible.BibleVerseRecyclerViewAdapter;
import app.NavActivity;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.pojo.BibleVersePositionEvent;
import event.pojo.FragConfigChange;
import event.pojo.NavActivityColor;
import event.pojo.NavActivityHideNavigation;
import model.BibleChapter;
import model.MusicItem;
import model.Sermon;
import model.dyno.ApplicationContextProvider;
import model.dyno.FragDyno;
import service.ChurchWebService;

public class SermonSpecific extends Fragment{

    private static final int MESSAGE_ID = 5;
    private FragmentSermonSpecificBinding fragmentSermonSpecificBinding;
   // public NavActivity navActivity;

    private Cursor localCursor;
    private int orientationChange = -1;
    private int sermonId = -1;
    /**
     * =====play widget======
     **/
    private MediaPlayer mediaPlayer;
    private final List<MusicItem> items = new ArrayList<>();
    private int playingIndex = -1;
    private boolean paused;
    private boolean preparing;
    private Timer timer;
    private VisualizerShadowChanger mShadowChanger;
    private static final long UPDATE_INTERVAL = 20;
    public static final String EXTRA_FILE_URIS = "EXTRA_FILE_URIS";
    public static final String EXTRA_SELECT_TRACK = "EXTRA_SELECT_TRACK";
    public static final int MY_PERMISSIONS_REQUEST_READ_AUDIO = 11;
    private boolean mediaPlayerPaused = false;
    private boolean mediaPlayerWasPlaying = false;

    /**
     * ======================
     **/


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

        //todo set sermon data here and after cursor load


        //set cursor to null
        localCursor = null;

        //navActivity = (NavActivity) getActivity();
        hideNavigation();


        /**=====play widget======
        fragmentSermonSpecificBinding.playLayout.setOnButtonsClickListener(new PlayLayout.OnButtonsClickListener() {

            @Override
            public void onPlayButtonClicked() {
                playButtonClicked();

            }

            @Override
            public void onSkipPreviousClicked() {
                onPreviousClicked();
                if (fragmentSermonSpecificBinding.playLayout.isOpen()) {
                    fragmentSermonSpecificBinding.playLayout.startRevealAnimation();
                }
            }

            @Override
            public void onSkipNextClicked() {
                onNextClicked();
                if (fragmentSermonSpecificBinding.playLayout.isOpen()) {
                    fragmentSermonSpecificBinding.playLayout.startRevealAnimation();
                }
            }


            @Override
            public void onShuffleClicked() {
                Toast.makeText(getContext(), "shuffle", Toast.LENGTH_SHORT);
            }

            @Override
            public void onRepeatClicked() {
                Toast.makeText(getContext(), "repeat", Toast.LENGTH_SHORT);
            }


        });

        fragmentSermonSpecificBinding.playLayout.setOnProgressChangedListener(new PlayLayout.OnProgressChangedListener() {
            @Override
            public void onPreSetProgress() {
                stopTrackingPosition();
            }

            @Override
            public void onProgressChanged(float progress) {
                Log.i("onProgressChanged", "Progress = " + progress);
                mediaPlayer.seekTo((int) (mediaPlayer.getDuration() * progress));
                startTrackingPosition();
            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //fragmentSermonSpecificBinding.playLayout.fastOpen();
        //selectNewTrack(getActivity().getIntent());
        selectNewTrack();

        ======================**/

        //audio player settings
        fragmentSermonSpecificBinding.audioPlayer.getSettings().setJavaScriptEnabled(true);
        fragmentSermonSpecificBinding.audioPlayer.getSettings().setLoadWithOverviewMode(true);
        fragmentSermonSpecificBinding.audioPlayer.getSettings().setUseWideViewPort(true);

        //video player settings
        fragmentSermonSpecificBinding.videoPlayer.getSettings().setJavaScriptEnabled(true);
        //fragmentSermonSpecificBinding.videoPlayer.getSettings().setLoadWithOverviewMode(true);
        //fragmentSermonSpecificBinding.videoPlayer.getSettings().setUseWideViewPort(true);
        /**fragmentSermonSpecificBinding.videoPlayer.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });**/



        return fragmentSermonSpecificBinding.getRoot();
    }

    private void hideNavigation() {
       //post event
        EventBus.getDefault().post(new NavActivityHideNavigation(false));
    }


    /**
     * =====play widget======


    //method
    private void playButtonClicked() {

        if (fragmentSermonSpecificBinding.playLayout == null) {
            return;
        }

        if (fragmentSermonSpecificBinding.playLayout.isOpen()) {
            mediaPlayer.pause();
            mediaPlayerPaused = true;
            fragmentSermonSpecificBinding.playLayout.startDismissAnimation();
        } else {
            if(mediaPlayerPaused){
                //repause
                startCurrentTrack(true);
            }else{
                //first time
                startCurrentTrack(false);
            }

            mediaPlayer.start();
            fragmentSermonSpecificBinding.playLayout.startRevealAnimation();
        }


    }

    //method
    private void onNextClicked() {
        if (items.size() == 0)
            return;
        playingIndex++;
        if (playingIndex >= items.size()) {
            playingIndex = 0;
        }

        startCurrentTrack(false);
    }

    //method
    private void onPreviousClicked() {
        if (items.size() > 0)
            return;
        playingIndex--;
        if (playingIndex < 0) {
            playingIndex = items.size() - 1;
        }

        startCurrentTrack(false);
    }

    //method
    private void startCurrentTrack(boolean firstTime) {

        setImageForItem();
        if(firstTime == false){
            //ie play,previous,next clicked
            if (mediaPlayer.isPlaying() || paused) {
                mediaPlayer.stop();
                paused = false;
            }

            mediaPlayer.reset();

            try {
                mediaPlayer.setDataSource(getContext(), items.get(playingIndex).fileUri());
                mediaPlayer.prepareAsync();
                preparing = true;


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    //method
    private void setImageForItem() {
        Glide.with(this)
                .load(items.get(playingIndex).albumArtUri())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.ic_error)
                .into(imageTarget);
    }

    //method
    private SimpleTarget<GlideDrawable> imageTarget = new SimpleTarget<GlideDrawable>() {
        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            fragmentSermonSpecificBinding.playLayout.setImageDrawable(resource);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            super.onLoadFailed(e, errorDrawable);
            fragmentSermonSpecificBinding.playLayout.setImageDrawable(errorDrawable);
        }
    };

    //method
    private void stopTrackingPosition() {
        if (timer == null)
            return;
        timer.cancel();
        timer.purge();
        timer = null;
    }

    //method
    private void startTrackingPosition() {

        //progresssTrackingThread.run();
        timer = new Timer("MainActivity Timer");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MediaPlayer tempMediaPlayer = mediaPlayer;
                        if (tempMediaPlayer != null && tempMediaPlayer != null && tempMediaPlayer.isPlaying()) {

                            fragmentSermonSpecificBinding.playLayout.setProgress((float) tempMediaPlayer.getCurrentPosition() / tempMediaPlayer.getDuration());
                        }
                    }
                });

            }
        }, UPDATE_INTERVAL, UPDATE_INTERVAL);

    }


    //todo rewrite this method
    //method(use intent from activity)
    //private void selectNewTrack(Intent intent) {
    private void selectNewTrack() {
        if (preparing) {
            return;
        }
        //AddNewTracks(intent);
        AddNewTracks();
       /** if (intent.hasExtra(EXTRA_FILE_URIS)) {
            AddNewTracks(intent);
        }

        MusicItem item = intent.getParcelableExtra(EXTRA_SELECT_TRACK);

        if (item == null && playingIndex == -1 || playingIndex != -1 && items.get(playingIndex).equals(item)) {
            if (mediaPlayer.isPlaying()) {
                fragmentSermonSpecificBinding.playLayout.startDismissAnimation();
            } else {
                fragmentSermonSpecificBinding.playLayout.startRevealAnimation();
            }

            return;
        }

        playingIndex = items.indexOf(item);**/

    /**startCurrentTrack(true);


    }

    //todo rewrite this method
    //method(use intent from activity)
    //private void AddNewTracks(Intent intent) {
    private void AddNewTracks() {

        MusicItem playingItem = null;

        if (playingIndex != -1)
            playingItem = items.get(playingIndex);
        items.clear();//todo they are sure if current playing item,then it will be available

        MusicItem musicItemed = new MusicItem();
        musicItemed.setAlbum("My Album");

        Uri albumArtUrii = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/a/aa/StateLibQld_1_121964_Steel_bridge_over_the_Brisbane_River_at_Chelmer%2C_ca._1900.jpg/800px-StateLibQld_1_121964_Steel_bridge_over_the_Brisbane_River_at_Chelmer%2C_ca._1900.jpg");
        musicItemed.setAlbumArtUri(albumArtUrii);

        musicItemed.setArtist("Artist Name");
        musicItemed.setDuration(10000);
        musicItemed.setTitle("My song title");

        Uri albumMusicUrii = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/3/37/00_Jazz_Violin_Solo.ogg");
        musicItemed.setFileUri(albumMusicUrii);

        this.items.add(musicItemed);

        if (playingItem == null) {
            playingIndex = -1;
        } else {
            playingIndex = this.items.indexOf(playingItem);
        }

        //todo i injected to 0 since it will always be 0 for 1 item
        playingIndex = 0;

        if (playingIndex == -1 && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_AUDIO) {
            boolean bothGranted = true;
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.RECORD_AUDIO.equals(permissions[i]) || Manifest.permission.MODIFY_AUDIO_SETTINGS.equals(permissions[i])) {
                    bothGranted &= grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }
            if (bothGranted) {
                startVisualiser();
            } else {
                permissionsNotGranted();
            }
        }
    }

    private void permissionsNotGranted() {

    }

    //method start visualizer
    private void startVisualiser() {

        if(mShadowChanger ==  null){
            mShadowChanger = VisualizerShadowChanger.newInstance(mediaPlayer.getAudioSessionId());
            mShadowChanger.setEnabledVisualization(true);
            fragmentSermonSpecificBinding.playLayout.setShadowProvider(mShadowChanger);
            Log.i("startVisualiser","startVisualiser " + mediaPlayer.getAudioSessionId());

        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        //completed not paused
        mediaPlayerPaused = false;

       /** if(playingIndex == -1){
            if(fragmentSermonSpecificBinding.playLayout != null){
                fragmentSermonSpecificBinding.playLayout.startDismissAnimation();
            }
            return;
        }**/

       /**if(mediaPlayerWasPlaying == true){
           if(fragmentSermonSpecificBinding.playLayout != null){
               fragmentSermonSpecificBinding.playLayout.startDismissAnimation();
               fragmentSermonSpecificBinding.playLayout.setProgress(0);
           }
           mediaPlayerWasPlaying =  false;
       }

        //play next track(not applicable in this case since we are only playing one track)

        /**if(playingIndex >= items.size()){
            playingIndex = 0;
            if(items.size() == 0){
                return;
            }
        }


        startCurrentTrack(false);**/

    /**}

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        preparing = true;
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        preparing = false;
        mediaPlayer.start();
        stopTrackingPosition();
        startTrackingPosition();


        mediaPlayerWasPlaying = true;
    }

    /**
     * ======================
     **/


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

                    /**handle the audio webview**/
                    //String AUDIO_URL = "https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/108641951?iframe=true&width=400&height=200";
                    String AUDIO_URL = sermon.getSermon_audio_url();

                    String html = "<!DOCTYPE html><html> <head> <meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <link rel=\"stylesheet\" media=\"screen and (-webkit-device-pixel-ratio:1.5)\" href=\"hdpi.css\" /></head> <body style=\"background:black;margin:0 0 0 0; padding:0 0 0 0;\"> <iframe id=\"sc-widget " +
                            "\" width=\"100%\" height=\"50%\"" + // Set Appropriate Width and Height that you want for SoundCloud Player
                            " src=\"" + AUDIO_URL   // Set Embedded url
                            + "\" frameborder=\"no\" scrolling=\"no\"></iframe>" +
                            "<script src=\"https://w.soundcloud.com/player/api.js\" type=\"text/javascript\"></script> </body> </html> ";

                    fragmentSermonSpecificBinding.audioPlayer.loadDataWithBaseURL("",html,"text/html", "UTF-8", "");
//todo center all the items in the layout view.
                    /**handle the video webview**/
                    //get the video code from end of url
                    String videoCode =  null;
                    String videoUrl = sermon.getSermon_video_url();
                    int start = videoUrl.indexOf("=");

                    if(start >= 0)
                    videoCode = videoUrl.substring(start + 1);
                    //String videoCode = "47yJ2XCRLZs";

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

    //todo subscribe to fragmentConfig change to save the current item  playing  and play position

    public int getScreenDimensions() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        /**int height = dm.heightPixels;
         int dens = dm.densityDpi;
         double wi = (double)width / (double)dens;
         double hi = (double)height / (double)dens;
         double x = Math.pow(wi,2);
         double y = Math.pow(hi,2);
         double screenInches = Math.sqrt(x+y);**/
        return width;
    }

    @Override
    public void onDestroy() {
        /**close cursors**/
        if (localCursor != null) {
            localCursor.close();
        }

        /**=====play widget======
        if(mShadowChanger != null){
            mShadowChanger.release();
        }

        stopTrackingPosition();
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer =  null;
        /**======================**/

        super.onDestroy();
    }


    @Override
    public void onStart() {
        super.onStart();

        //get this specific sermon
        getThisSermonFromDb();
        //register event
        //EventBus.getDefault().register(this);
    }


    @Override
    public void onStop() {
        //EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        //post event to change background color in activity
        EventBus.getDefault().post(new NavActivityColor(R.color.lightBlack));
        /**=====play widget======
        if(mShadowChanger != null){
            mShadowChanger.setEnabledVisualization(true);
        }
        /**======================**/



    }

    @Override
    public void onPause() {

        /**=====play widget======
        if(mShadowChanger != null){
            mShadowChanger.setEnabledVisualization(false);
        }
        /**======================**/

        super.onPause();

        fragmentSermonSpecificBinding.audioPlayer.loadUrl("about:blank");
        fragmentSermonSpecificBinding.videoPlayer.loadUrl("about:blank");
    }


}
