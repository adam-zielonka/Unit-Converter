package pro.adamzielonka.converter.settings;

import android.app.Activity;

import pro.adamzielonka.java.Number;

public class DecimalSeparator implements SettingInterface {

    Activity activity;

    public DecimalSeparator(Activity activity){
        this.activity = activity;
    }

    public String get() {
        return Number.getDecimalSeparator();
    }

    public String[] getArray() {
        return new String[]{".", ","};
    }

    public int getID() {
        return Number.getDecimalSeparator().equals(".") ? 0 : 1;
    }

    public void setID(Integer id) {
        if (id == 0) Number.setDotDecimalSeparator();
        else Number.setCommaDecimalSeparator();
    }
}
