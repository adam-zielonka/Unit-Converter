package pro.adamzielonka.converter.activities.drawer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.AboutActivity;
import pro.adamzielonka.converter.activities.AddConverterActivity;
import pro.adamzielonka.converter.activities.settings.SettingsActivity;
import pro.adamzielonka.converter.tools.Theme;

public class EmptyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getConverterStyleID(preferences.getString("theme", "")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);

        switch (id) {
            case R.id.nav_about:
                Intent about = new Intent(this.getBaseContext(), AboutActivity.class);
                startActivity(about);
                break;
            case R.id.nav_settings:
                Intent settings = new Intent(this.getBaseContext(), SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.nav_add_converter:
                Intent addConverter = new Intent(this.getBaseContext(), AddConverterActivity.class);
                startActivity(addConverter);
                break;
        }

        return true;
    }
}
