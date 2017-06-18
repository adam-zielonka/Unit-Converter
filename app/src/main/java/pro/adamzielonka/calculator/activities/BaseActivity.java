package pro.adamzielonka.calculator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import pro.adamzielonka.calculator.R;

import static pro.adamzielonka.calculator.activities.SpinnerActivity.PACKAGE_NAME;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
    }

    void setListenerToButton(View.OnClickListener mButtonClickListener, String buttonName) {
        Button button = (Button) findViewById(getIdResourceByName("button" + buttonName));
        button.setOnClickListener(mButtonClickListener);
    }

    private int getIdResourceByName(String aString) {
        return getResources().getIdentifier(aString, "id", PACKAGE_NAME);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_calculator:
                Intent calculator = new Intent(this.getBaseContext(), CalculatorActivity.class);
                startActivity(calculator);
                break;
            case R.id.nav_temperature:
                Intent temperatureConverter = new Intent(this.getBaseContext(), ConverterActivity.class);
                temperatureConverter.putExtra("converterName","Temperature");
                startActivity(temperatureConverter);
                break;
            case R.id.nav_byte:
                Intent byteConverter = new Intent(this.getBaseContext(), ConverterActivity.class);
                byteConverter.putExtra("converterName","Byte");
                startActivity(byteConverter);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
