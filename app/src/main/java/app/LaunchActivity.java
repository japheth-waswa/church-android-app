package app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.birbit.android.jobqueue.JobManager;

import db.ChurchContract;
import db.ChurchQueryHandler;
import job.GetBibleData;
import job.SaveBibleDataToDb;
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

        //confirm and run bg job to handle bible data
        startBibleBgJob();

        //start home activity
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    //start bg job
    private void startBibleBgJob() {
        //start job only if bible db is empty
        ChurchQueryHandler handler = new ChurchQueryHandler(LaunchActivity.this.getContentResolver()){
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                boolean runBg = true;
                if(cursor.getCount() > 0){
                    runBg = false;
                }
                cursor.close();

                if(runBg == true){
                    //todo this job does not require internet connection therefore remove network persisit in job
                    dbPreparation();
                }
            }
        };
        String[] projection = {ChurchContract.BibleBookEntry.COLUMN_BIBLE_BOOK_NUMBER};
        handler.startQuery(23, null, ChurchContract.BibleBookEntry.CONTENT_URI, projection, null, null, null);
    }

    private void dbPreparation() {

        //clear bible database
        ChurchQueryHandler handler = new ChurchQueryHandler(LaunchActivity.this.getContentResolver());
        handler.startDelete(2,null, ChurchContract.BibleBookEntry.CONTENT_URI,null,null);
        handler.startDelete(3,null, ChurchContract.BibleChapterEntry.CONTENT_URI,null,null);
        handler.startDelete(4,null, ChurchContract.BibleVerseEntry.CONTENT_URI,null,null);

        //start background job to save the bible books in database
        //instantiate job manager
        jobManager = new JobManager(MyJobsBuilder.getConfigBuilder(getApplicationContext()));
        jobManager.addJobInBackground(new SaveBibleDataToDb());
    }
}
