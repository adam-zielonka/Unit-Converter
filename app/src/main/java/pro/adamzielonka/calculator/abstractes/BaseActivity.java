package pro.adamzielonka.calculator.abstractes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.activities.CalculatorActivity;
import pro.adamzielonka.calculator.activities.ConverterActivity;
import pro.adamzielonka.calculator.activities.RomanActivity;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected static String PACKAGE_NAME;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    protected NavigationView mNavigationView;
    protected int mItemId;

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

        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        mNavigationView.setNavigationItemSelectedListener(this);

        PACKAGE_NAME = getApplicationContext().getPackageName();
    }

    protected int getIdResourceByName(String defType, String name) {
        return getResources().getIdentifier(name, defType, PACKAGE_NAME);
    }

    protected String prepareString(String result) {
        return convertDoubleToString(convertStringToDouble(result));
    }

    protected String convertDoubleToString(Double result) {
        NumberFormat numberFormat = new DecimalFormat("#.################################");
        NumberFormat numberFormat2 = new DecimalFormat("0.################################E0");
        if (!numberFormat.format(result).contains(",") && numberFormat.format(result).length() > 15)
            return numberFormat2.format(result);
        if (numberFormat.format(result).contains("0,0000") && numberFormat.format(result).length() > 15)
            return numberFormat2.format(result);
        return numberFormat.format(result);
    }

    protected Double convertStringToDouble(String result) {
        try {
            return Double.parseDouble(result.replaceAll("\\s+", "").replaceAll(",", "."));
        } catch (NumberFormatException e) {
            switch (result) {
                case "∞":
                    return Double.POSITIVE_INFINITY;
                case "-∞":
                    return Double.NEGATIVE_INFINITY;
                default:
                    return Double.NaN;
            }
        }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id != mItemId) {
            switch (id) {
                case R.id.nav_calculator:
                    Intent calculator = new Intent(this.getBaseContext(), CalculatorActivity.class);
                    startActivity(calculator);
                    break;
                case R.id.nav_roman_calculator:
                    Intent romanCalculator = new Intent(this.getBaseContext(), RomanActivity.class);
                    startActivity(romanCalculator);
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
                case R.id.nav_time:
                    Intent timeConverter = new Intent(this.getBaseContext(), ConverterActivity.class);
                    timeConverter.putExtra("converterName", "Time");
                    startActivity(timeConverter);
                    break;
                case R.id.nav_length:
                    Intent lengthConverter = new Intent(this.getBaseContext(), ConverterActivity.class);
                    lengthConverter.putExtra("converterName", "Length");
                    startActivity(lengthConverter);
                    break;
            }

            mDrawerLayout.closeDrawer(GravityCompat.START);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}
