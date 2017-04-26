package fragment.sermon;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
    private static final String EXTRA_FILE_URIS = "EXTRA_FILE_URIS";

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


    //method(use intent from activity)
    private void selectNewTrack(Intent intent) {
        if (preparing) {
            return;
        }

        if (intent.hasExtra(EXTRA_FILE_URIS)) {
            AddNewTracks(intent);
        }

    }

    //method(use intent from activity)
    private void AddNewTracks(Intent intent) {

        MusicItem playingItem = null;

        if (playingIndex != -1)
            playingItem = items.get(playingIndex);
        items.clear();

        //get the parcelable


    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

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
        super.onDestroy();
        /**close cursors**/
        if (localCursor != null) {
            localCursor.close();
        }
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

}
