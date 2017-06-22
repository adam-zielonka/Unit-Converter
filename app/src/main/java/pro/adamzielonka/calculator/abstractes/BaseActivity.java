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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import pro.adamzielonka.calculator.R;
import pro.adamzielonka.calculator.activities.CalculatorActivity;
import pro.adamzielonka.calculator.activities.ConverterActivity;
import pro.adamzielonka.calculator.activities.RomanActivity;
import pro.adamzielonka.calculator.units.Units;
import pro.adamzielonka.calculator.units.Measures;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected static String PACKAGE_NAME;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    protected NavigationView mNavigationView;
    protected int mItemId;
    protected List<Units> unitsList;

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

        loadConverters();
    }

    private void loadConverters() {
        Menu menu = mNavigationView.getMenu();
        Menu convertersMenu = menu.addSubMenu(getString(R.string.nav_converters));

        Measures measures = Measures.getInstance();
        this.unitsList = measures.getUnitsList();

        int i = 0;
        for (Units units : this.unitsList) {
            MenuItem menuItem = convertersMenu.add(0, i + 1000, 0, units.getName());
            menuItem.setCheckable(true);
            i++;
        }
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
        Log.e("MenuID", " " + id);

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
                default:
                    if (id - 1000 >= unitsList.size() && id - 1000 >= 0) break;
                    Intent converter = new Intent(this.getBaseContext(), ConverterActivity.class);
                    converter.putExtra("converterName", unitsList.get(id - 1000).getName());
                    converter.putExtra("converterNavId", id);
                    startActivity(converter);
            }

            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}
