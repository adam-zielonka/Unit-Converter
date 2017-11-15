package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.PreferenceActivity;
import pro.adamzielonka.converter.database.UserAuth;
import pro.adamzielonka.converter.names.Property;
import pro.adamzielonka.converter.settings.DecimalSeparator;
import pro.adamzielonka.converter.settings.Language;
import pro.adamzielonka.items.Item;
import pro.adamzielonka.items.dialog.EditDialogBuilder;

import static pro.adamzielonka.converter.database.UserAuth.RC_SIGN_IN;
import static pro.adamzielonka.converter.settings.Language.getConverterLanguage;
import static pro.adamzielonka.converter.settings.Language.getConverterLanguageCode;
import static pro.adamzielonka.converter.settings.Language.setConverterLanguage;

public class SettingsActivity extends PreferenceActivity {

    private UserAuth userAuth;

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_settings);
//        userAuth = new UserAuth(this, () -> itemsView.onUpdate());
        DecimalSeparator decimalSeparator = new DecimalSeparator(this);
        Language language = new Language(this);

        new Item.Builder(this)
                .setTitleHeader(R.string.pref_header_appearance)
                .setTitle(R.string.pref_title_theme)
                .setUpdate(theme::get)
                .setEnabledUpdate(false)
                .setArray(theme::getArray)
                .setPosition(theme::getID)
                .setAction(theme::setID)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.pref_title_decimal_separator)
                .setUpdate(decimalSeparator::get)
                .setArray(decimalSeparator::getArray)
                .setPosition(decimalSeparator::getID)
                .setAction(decimalSeparator::setID)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.pref_title_language)
                .setUpdate(language::get)
                .setEnabledUpdate(false)
                .setArray(language::getArray)
                .setPosition(language::getID)
                .setAction(language::setID)
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.pref_title_language_converter)
                .setUpdate(() -> getConverterLanguage(this))
                .setAction(() -> new EditDialogBuilder(this)
                        .setValue(getConverterLanguageCode(this))
                        .setAction(text -> {
                            setConverterLanguage(this, text.toString());
                            itemsView.onSave();
                        })
                        .setNeutralAction(R.string.language_os, (d, i) -> {
                            setConverterLanguage(this, "");
                            itemsView.onSave();
                        })
                        .setTitle(R.string.lang_put_code)
                        .create().show())
                .add(itemsView);

//        new Item.Builder(this)
//                .setTitleHeader(R.string.pref_header_user)
//                .setTitle(() -> getUser() != null ? R.string.pref_title_sign_out : R.string.pref_title_sign_in)
//                .setUpdate(() -> getUser() != null ? getUser().getEmail() : "")
//                .setAction(() -> {
//                    if (getUser() != null) userAuth.signOut();
//                    else userAuth.signIn();
//                }).add(itemsView);
//        new Item.Builder(this)
//                .setTitle(R.string.pref_title_user_name)
//                .setIf(() -> getUser() != null)
//                .setUpdate(() -> userAuth.getUserName())
//                .setElseUpdate(() -> "")
//                .setAction(() -> userAuth.changeUserName())
//                .add(itemsView);

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
        if (s.equals(Property.THEME) || s.equals(Property.LANGUAGE)) reloadActivity(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) userAuth.getSignInResultFromIntent(data);
    }
}
