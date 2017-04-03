package app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //start home activity
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
