package pro.adamzielonka.converter.settings;

public interface SettingInterface {
    String get();

    String[] getArray();

    int getID();

    void setID(Integer id);
}
