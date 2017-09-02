package pro.adamzielonka.java;

import static pro.adamzielonka.java.Symbols.COMMA;
import static pro.adamzielonka.java.Symbols.DOT;

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
