package pro.adamzielonka.converter.tools;

import pro.adamzielonka.converter.R;

public class Theme {
    public static int getStyleID(String themeID){
        switch (themeID) {
            case "1":
                return R.style.RedTheme;
            case "2":
                return R.style.GreenTheme;
            default:
                return R.style.BlueTheme;
        }
    }

    public static int getConverterStyleID(String themeID){
        switch (themeID) {
            case "1":
                return R.style.RedTheme_Converter;
            case "2":
                return R.style.GreenTheme_Converter;
            default:
                return R.style.BlueTheme_Converter;
        }
    }
}
