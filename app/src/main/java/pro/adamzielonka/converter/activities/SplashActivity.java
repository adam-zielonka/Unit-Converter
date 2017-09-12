package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pro.adamzielonka.converter.tools.Extra;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, StartActivity.class)
                .putExtra(Extra.MEASURE_FILE_NAME, getIntent().getStringExtra(Extra.MEASURE_FILE_NAME)));
        finish();
    }
}