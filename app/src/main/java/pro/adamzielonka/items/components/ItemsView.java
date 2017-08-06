package pro.adamzielonka.items.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.items.classes.Item;
import pro.adamzielonka.items.interfaces.ActionInterface;
import pro.adamzielonka.items.interfaces.UpdateInterface;

public class ItemsView extends ListView {

    List<Item> items = new ArrayList<>();
    ArrayAdapter adapter;
    ActionInterface.ObjectAction listAction;
    UpdateInterface.ListUpdate listUpdate;
    OnItemsUpdate onItemsUpdate;
    OnItemsSave onItemsSave;
    public boolean isUpdateProcess;

    public interface OnItemsUpdate {
        void onUpdate();
    }

    public interface OnItemsSave {
        void onSave();
    }

    public void setOnItemsUpdate(OnItemsUpdate onItemsUpdate) {
        this.onItemsUpdate = onItemsUpdate;
    }

    public void setOnItemsSave(OnItemsSave onItemsSave) {
        this.onItemsSave = onItemsSave;
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        super.setOnItemClickListener((adapterView, view, i, l) -> {
            onAction(view, i);
            if (listener != null) listener.onItemClick(adapterView, view, i, l);
        });
    }

    private void onCreate() {
        isUpdateProcess = false;
        setOnItemClickListener(null);
    }

    public ItemsView(Context context) {
        super(context);
        onCreate();
    }

    public ItemsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public ItemsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate();
    }

    public void setEmptyAdapter() {
        setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                return null;
            }
        });
    }

    public void setAdapter(ArrayAdapter adapter, UpdateInterface.ListUpdate listUpdate,
                           ActionInterface.ObjectAction listAction) {
        super.setAdapter(adapter);
        this.adapter = adapter;
        this.listAction = listAction;
        this.listUpdate = listUpdate;
    }

    public void addItem(Item item) {
        if (adapter == null) addHeaderView(item.view, false, item.isEnabled);
        else addFooterView(item.view, false, item.isEnabled);
        items.add(item);
    }

    public void onAction(View view, int position) {
        if (isAdapterItemClick(position) && listAction != null)
            listAction.onAction(getAdapterPosition(position));
        else for (Item item : items)
            if (item.view.equals(view) && view.isEnabled()) item.onAction();
    }

    public void onSave() {
        if (onItemsSave != null) onItemsSave.onSave();
        onUpdate();
    }

    public void onUpdate() {
        if (isUpdateProcess) return;
        isUpdateProcess = true;
        if (onItemsUpdate != null) onItemsUpdate.onUpdate();
        for (Item item : items) item.onUpdate();
        if (listUpdate != null && adapter != null) {
            adapter.clear();
            adapter.addAll(listUpdate.onUpdate());
            adapter.notifyDataSetChanged();
        }
        isUpdateProcess = false;
    }

    //region adapter position
    protected boolean isAdapterItemClick(int position) {
        return (position - getHeaderViewsCount() >= 0 && position - getHeaderViewsCount()
                < getCount() - getHeaderViewsCount() - getFooterViewsCount());
    }

    protected int getAdapterPosition(int position) {
        return position - getHeaderViewsCount();
    }
    //endregion
}
