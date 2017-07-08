package pro.adamzielonka.converter.activities.edit;

import java.io.FileNotFoundException;

public interface IEdit {
    void onLoad() throws FileNotFoundException;
    void onReload() throws FileNotFoundException;
}
