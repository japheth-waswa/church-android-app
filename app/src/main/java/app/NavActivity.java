package app;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cleveroad.loopbar.widget.OnItemClickListener;
import com.japhethwaswa.church.R;
import com.japhethwaswa.church.databinding.ActivityNavBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import db.ChurchContract;
import db.ChurchQueryHandler;

public class NavActivity extends AppCompatActivity {
    //todo start fragment to load each specific navigation and manage it
    //todo in each fragment handle screen orientation appropriately
    private ActivityNavBinding activityNavBinding;

    private int navPosition;


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

        //get data from HomeActivity
        Intent intent = getIntent();
        navPosition = intent.getIntExtra("navPosition", 0);

        //inflate activity
        activityNavBinding = DataBindingUtil.setContentView(this, R.layout.activity_nav);

        //determine if orientation change or activity creation
        if (savedInstanceState == null) {
            //set the default selected menu item
            int newNavPosition = navPosition + 1;
            activityNavBinding.mainNavigationView.setCurrentItem(newNavPosition);

            //start fragment
            handleMenuClicks(newNavPosition);

        }

        //work with the items
        activityNavBinding.mainNavigationView.addOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                handleMenuClicks(position);
            }
        });

        //todo delete this after getting bible data
        checkbibleDb();

    }

    private void checkbibleDb() {
        ChurchQueryHandler handler = new ChurchQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if(cursor.getCount() > 0){
                    JSONObject jsonObj = new JSONObject();
                    int i = -1 ;
                    while (cursor.moveToNext()) {
                        i++;
                        String bible_book = cursor.getString(cursor.getColumnIndex(ChurchContract.BibleCollectionEntry.COLUMN_BIBLE_BOOK));

                        try {
                            jsonObj.put(String.valueOf(i),bible_book);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Log.e("jeff-bible-book", bible_book);
                    }

                    //save to collection file
                    File fileName = new File(NavActivity.this.getFilesDir().getPath() + "/bible_collection.txt");
                    FileOutputStream fOut = null;
                    try {
                        fOut = new FileOutputStream(fileName, true);
                        fOut.write(jsonObj.toString().getBytes());
                        fOut.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                cursor.close();
            }
        };
        String[] projection = {ChurchContract.BibleCollectionEntry.COLUMN_BIBLE_BOOK};
        handler.startQuery(12, null, ChurchContract.BibleCollectionEntry.CONTENT_URI, projection, null, null, null);
    }

    //handle menu clicks
    private void handleMenuClicks(int position) {
        //ensure if home clicked you load HomeActivity ie super.backpressed
        switch (position) {
            case 0:
                super.onBackPressed();
                finish();
                break;
            case 1:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 2:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 3:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 4:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 5:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 6:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 7:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 8:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 9:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 10:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 11:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            case 12:
                Log.e("jeff-waswa", String.valueOf(position) + "-fragment load here");
                break;
            default:
                super.onBackPressed();
                break;
        }
        //todo use switch statement to load each different fragment
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("customOrientationChange", 1);
    }
}
