package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static pro.adamzielonka.converter.tools.Code.EXTRA_MEASURE_FILE_NAME;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, StartActivity.class)
                .putExtra(EXTRA_MEASURE_FILE_NAME, getIntent().getStringExtra(EXTRA_MEASURE_FILE_NAME)));
        finish();
    }
}