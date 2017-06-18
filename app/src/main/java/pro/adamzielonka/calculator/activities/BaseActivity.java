package pro.adamzielonka.calculator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import pro.adamzielonka.calculator.R;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String PACKAGE_NAME;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void setContentView(int layoutResID) {
        mDrawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) mDrawerLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(mDrawerLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        PACKAGE_NAME = getApplicationContext().getPackageName();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    void setListenerToButton(View.OnClickListener mButtonClickListener, String buttonName) {
        Button button = (Button) findViewById(getIdResourceByName("button" + buttonName));
        button.setOnClickListener(mButtonClickListener);
    }

    private int getIdResourceByName(String aString) {
        return getResources().getIdentifier(aString, "id", PACKAGE_NAME);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                temperatureConverter.putExtra("converterName", "Temperature");
                startActivity(temperatureConverter);
                break;
            case R.id.nav_byte:
                Intent byteConverter = new Intent(this.getBaseContext(), ConverterActivity.class);
                byteConverter.putExtra("converterName", "Byte");
                startActivity(byteConverter);
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
