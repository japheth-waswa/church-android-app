package fragment.sermon;

import android.Manifest;
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
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import adapters.recyclerview.bible.BibleVerseRecyclerViewAdapter;
import app.NavActivity;
import db.ChurchContract;
import db.ChurchQueryHandler;
import event.pojo.BibleVersePositionEvent;
import event.pojo.FragConfigChange;
import model.BibleChapter;
import model.MusicItem;
import model.dyno.FragDyno;

public class SermonSpecific extends Fragment implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {



    private FragmentSermonSpecificBinding fragmentSermonSpecificBinding;
    //public NavActivity navActivity;

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

        /**=====play widget======**/
        /**======================**/

        /**=====play widget======**/
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
        fragmentSermonSpecificBinding.playLayout.fastOpen();
        selectNewTrack(getActivity().getIntent());

        /**======================**/


        return fragmentSermonSpecificBinding.getRoot();
    }


    /**
     * =====play widget======
     **/

    //method
    private void playButtonClicked() {

        if (fragmentSermonSpecificBinding.playLayout == null) {
            return;
        }

        if (fragmentSermonSpecificBinding.playLayout.isOpen()) {
            mediaPlayer.pause();
            fragmentSermonSpecificBinding.playLayout.startDismissAnimation();
        } else {
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

        startCurrentTrack();
    }

    //method
    private void onPreviousClicked() {
        if (items.size() > 0)
            return;
        playingIndex--;
        if (playingIndex < 0) {
            playingIndex = items.size() - 1;
        }

        startCurrentTrack();
    }

    //method
    private void startCurrentTrack() {

        setImageForItem();
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

        timer = new Timer("Sermon Player Timer");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MediaPlayer tempMediaPlayer = mediaPlayer;
                if (tempMediaPlayer != null && tempMediaPlayer.isPlaying()) {
                    fragmentSermonSpecificBinding.playLayout.setProgress((float) tempMediaPlayer.getCurrentPosition() / tempMediaPlayer.getDuration());
                }

            }
        }, UPDATE_INTERVAL, UPDATE_INTERVAL);

    }


    //todo rewrite this method
    //method(use intent from activity)
    private void selectNewTrack(Intent intent) {
        if (preparing) {
            return;
        }
        AddNewTracks(intent);
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
        startCurrentTrack();


    }

    //todo rewrite this method
    //method(use intent from activity)
    private void AddNewTracks(Intent intent) {

        MusicItem playingItem = null;

        if (playingIndex != -1)
            playingItem = items.get(playingIndex);
        items.clear();//todo they are sure if current playing item,then it will be available

        //get the parcelable
        /**Parcelable[] items = intent.getParcelableArrayExtra(EXTRA_FILE_URIS);

        for (Parcelable item : items) {
            if (item instanceof MusicItem)
                this.items.add((MusicItem) item);
        }**/
        MusicItem musicItem = new MusicItem();
        musicItem.setAlbum("My Album");

        Uri albumArtUri = Uri.parse("http://www.mulierchile.com/images/free-images/free-images-1.jpg");
        musicItem.setAlbumArtUri(albumArtUri);

        musicItem.setArtist("Artist Name");
        musicItem.setDuration(10000);
        musicItem.setTitle("My song title");

        Uri albumMusicUri = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg");
        musicItem.setFileUri(albumMusicUri);

        this.items.add(musicItem);

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

    //method(not necessary if permissions set in AndroidManifest)
    private void checkVisualiserPermissions() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
            startVisualiser();
        } else {

            //ask for these permissions in 2 ways
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.RECORD_AUDIO) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.MODIFY_AUDIO_SETTINGS)){

                AlertDialog.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            requestPermissions();
                        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                            permissionsNotGranted();
                        }
                    }
                };
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.title_permissions))
                        .setMessage(Html.fromHtml(getString(R.string.message_permissions)))
                        .setPositiveButton(getString(R.string.btn_next), onClickListener)
                        .setNegativeButton(getString(R.string.btn_cancel), onClickListener)
                        .show();

            }else{
                requestPermissions();
            }

        }

    }

    //method
    //todo do it with fragment instead
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS},
                MY_PERMISSIONS_REQUEST_READ_AUDIO
        );
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

        if(playingIndex == -1){
            if(fragmentSermonSpecificBinding.playLayout != null){
                fragmentSermonSpecificBinding.playLayout.startDismissAnimation();
            }
            return;
        }

        //play next track
        playingIndex++;
        if(playingIndex >= items.size()){
            playingIndex = 0;
            if(items.size() == 0){
                return;
            }
        }

        startCurrentTrack();

    }

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
        checkVisualiserPermissions();
    }


    /**
     * ======================
     **/


    private void getThisSermonFromDb() {

        if (localCursor != null) {
            localCursor.close();
            localCursor = null;
        }

        //todo show loader
        //fragmentSermonSpecificBinding.pageloader.startProgress();
        ChurchQueryHandler handler = new ChurchQueryHandler(getContext().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                localCursor = cursor;
                if (cursor.getCount() > 0) {
                    //todo hide loader here
                    //fragmentSermonSpecificBinding.pageloader.stopProgress();
                    //todo update UI
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


    //todo subscribe to event and update data

    @Override
    public void onDestroy() {
        /**close cursors**/
        if (localCursor != null) {
            localCursor.close();
        }

        /**=====play widget======**/
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
        /**=====play widget======**/
        if(mShadowChanger != null){
            mShadowChanger.setEnabledVisualization(true);
        }
        /**======================**/

    }

    @Override
    public void onPause() {

        /**=====play widget======**/
        if(mShadowChanger != null){
            mShadowChanger.setEnabledVisualization(false);
        }
        /**======================**/

        super.onPause();
    }
}
