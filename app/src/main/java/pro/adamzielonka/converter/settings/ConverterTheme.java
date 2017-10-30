package pro.adamzielonka.converter.settings;

import android.app.Activity;

import pro.adamzielonka.converter.R;

public class ConverterTheme extends Theme {

    public ConverterTheme(Activity activity) {
        super(activity);
    }

    @Override
    protected int getStyleID() {
        switch (getID()) {
            case 1:
                return R.style.RedTheme_Converter;
            case 2:
                return R.style.GreenTheme_Converter;
            case 3:
                return R.style.GreyTheme_Converter;
            default:
                return R.style.BlueTheme_Converter;
        }
    }

}
