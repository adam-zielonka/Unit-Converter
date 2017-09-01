package pro.adamzielonka.lib;

import static pro.adamzielonka.lib.Symbols.COMMA;
import static pro.adamzielonka.lib.Symbols.DOT;

class Separator {
    private static final Separator ourInstance = new Separator();
    private String decimalSeparator;

    static Separator getInstance() {
        return ourInstance;
    }

    private Separator() {
        decimalSeparator = DOT;
    }

    void setCommaDecimalSeparator() {
        decimalSeparator = COMMA;
    }

    void setDotDecimalSeparator() {
        decimalSeparator = DOT;
    }

    String getDecimalSeparator() {
        return decimalSeparator;
    }
}
