package app;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.birbit.android.jobqueue.JobManager;

import db.ChurchContract;
import db.ChurchQueryHandler;
import job.GetBibleData;
import job.builder.MyJobsBuilder;

public class LaunchActivity extends AppCompatActivity {
    private JobManager jobManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //StrictMode
        StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setVmPolicy(vmPolicy);
        /**==============**/

        super.onCreate(savedInstanceState);

        //todo before starting bg job to insert bible data ensure that the data does not exist.
        dbPreparation(this);
        //instantiate job manager todo remove this job after getting data
        jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getApplicationContext()));
        jobManager.addJobInBackground(new GetBibleData());


        //start home activity
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private static void dbPreparation(LaunchActivity launchActivity) {
        //deletions
        ChurchQueryHandler handler = new ChurchQueryHandler(launchActivity.getContentResolver());
        handler.startDelete(2,null, ChurchContract.BibleBookEntry.CONTENT_URI,null,null);
        handler.startDelete(3,null, ChurchContract.BibleChapterEntry.CONTENT_URI,null,null);
         handler.startDelete(4,null, ChurchContract.BibleVerseEntry.CONTENT_URI,null,null);
    }
}
