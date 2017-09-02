package pro.adamzielonka.java;

import static pro.adamzielonka.java.Symbols.COMMA;
import static pro.adamzielonka.java.Symbols.DOT;

class Separator {
    private static final Separator ourInstance = new Separator();
    private String separator;

    static Separator getInstance() {
        return ourInstance;
    }

    private Separator() {
        setDotDecimalSeparator();
    }

    void setCommaDecimalSeparator() {
        separator = COMMA;
    }

    void setDotDecimalSeparator() {
        separator = DOT;
    }

    String getDecimalSeparator() {
        return separator;
    }
}
