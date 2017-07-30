package pro.adamzielonka.converter.bool;

import android.util.Log;

import java.util.List;

import pro.adamzielonka.converter.models.user.Prefix;
import pro.adamzielonka.converter.models.user.Unit;

public class UniqueSymbol extends Unique {

    public UniqueSymbol(List list, int error) {
        super(list, error);
    }

    public boolean isUnique(String text) {
        for (Object o : list) {
            if (o instanceof Unit) {
                Log.i("UNIQUE", "UNIT ");
                if (((Unit) o).symbol.equals(text)) {
                    Log.i("UNIQUE", "UNIT :" + text);
                    return false;
                }
            } else if (o instanceof Prefix) {
                Log.i("UNIQUE", "PREFIX ");
                if (((Prefix) o).symbol.equals(text)) {
                    Log.i("UNIQUE", "PREFIX: " + text);
                    return false;
                }
            }
        }
        return true;
    }
}
