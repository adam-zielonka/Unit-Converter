package pro.adamzielonka.converter.activities.edit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.tools.Theme;
import pro.adamzielonka.converter.units.concrete.ConcreteMeasure;
import pro.adamzielonka.converter.units.user.Measure;

import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Save.saveMeasure;

public abstract class EditActivity extends AppCompatActivity implements IEdit {
    protected Measure userMeasure;
    protected ConcreteMeasure concreteMeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setTheme(Theme.getStyleID(preferences.getString("theme", "")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            onLoad();
        } catch (Exception e) {
            finish();
        }
    }

    protected void onSave() {
        onSave(true);
    }

    protected void onSave(boolean reload) {
        try {
            saveMeasure(this, concreteMeasure, userMeasure);
            if (reload) onReload();
        } catch (Exception e) {
            showError(this, R.string.error_could_not_save_changes);
        }
    }
}
