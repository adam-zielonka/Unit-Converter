package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.PreferenceActivity;
import pro.adamzielonka.converter.database.UserAuth;
import pro.adamzielonka.converter.names.Property;
import pro.adamzielonka.items.Item;
import pro.adamzielonka.java.Number;

import static pro.adamzielonka.converter.database.UserAuth.RC_SIGN_IN;
import static pro.adamzielonka.converter.database.UserAuth.getUser;
import static pro.adamzielonka.converter.tools.Language.getDisplayLanguage;
import static pro.adamzielonka.converter.tools.Language.getDisplayLanguages;
import static pro.adamzielonka.converter.tools.Language.getLanguageFromID;
import static pro.adamzielonka.converter.tools.Language.getLanguageID;
import static pro.adamzielonka.converter.tools.Language.setLanguage;

public class SettingsActivity extends PreferenceActivity {

    private UserAuth userAuth;

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_settings);
        userAuth = new UserAuth(this, () -> itemsView.onUpdate());

        new Item.Builder(this)
                .setTitleHeader(R.string.pref_header_appearance)
                .setTitle(R.string.pref_title_theme)
                .setUpdate(() -> theme.getName())
                .setEnabledUpdate(false)
                .setArray(() -> theme.getArray())
                .setPosition(() -> theme.getID())
                .setAction((Integer id) -> theme.setID(id))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.pref_title_decimal_separator)
                .setUpdate(Number::getDecimalSeparator)
                .setArray(() -> new String[]{".", ","})
                .setPosition(() -> Number.getDecimalSeparator().equals(".") ? 0 : 1)
                .setAction((Integer id) -> {
                    if (id == 0) Number.setDotDecimalSeparator();
                    else Number.setCommaDecimalSeparator();
                }).add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.pref_title_language)
                .setUpdate(() -> getDisplayLanguage(this))
                .setEnabledUpdate(false)
                .setArray(() -> getDisplayLanguages(this))
                .setPosition(() -> getLanguageID(this))
                .setAction((Integer position) -> {
                    setLanguage(this, getLanguageFromID(this, position));
                    reloadActivity(this);
                }).add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.pref_title_language_converter)
                .setUpdate(() -> getDisplayLanguage(this))
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.pref_header_user)
                .setTitle(() -> getUser() != null ? R.string.pref_title_sign_out : R.string.pref_title_sign_in)
                .setUpdate(() -> getUser() != null ? getUser().getEmail() : "")
                .setAction(() -> {
                    if (getUser() != null) userAuth.signOut();
                    else userAuth.signIn();
                }).add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.pref_title_user_name)
                .setIf(() -> getUser() != null)
                .setUpdate(() -> userAuth.getUserName())
                .setElseUpdate(() -> "")
                .setAction(() -> userAuth.changeUserName())
                .add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.pref_header_about)
                .setTitle(R.string.pref_title_version)
                .setUpdate(() -> getString(R.string.app_version))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.pref_title_website)
                .setUpdate(() -> getString(R.string.website))
                .setAction(() -> startWebsite(R.string.uri_my_website))
                .add(itemsView);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Property.THEME)) reloadActivity(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) userAuth.getSignInResultFromIntent(data);
    }
}
