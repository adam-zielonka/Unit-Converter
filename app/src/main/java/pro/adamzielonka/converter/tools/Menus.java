package pro.adamzielonka.converter.tools;

import android.view.Menu;
import android.view.MenuItem;

import pro.adamzielonka.lib.MyList;

public class Menus {
    public static MyList<MenuItem> getMenuItems(Menu menu) {
        MyList<MenuItem> menuItems = new MyList<>();
        for (int i = 0; i < menu.size(); i++)
            menuItems.add(menu.getItem(i));
        return menuItems;
    }
}
