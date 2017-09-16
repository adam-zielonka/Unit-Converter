package pro.adamzielonka.items;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.List;

import pro.adamzielonka.items.interfaces.ActionInterface;
import pro.adamzielonka.items.interfaces.UpdateInterface;
import pro.adamzielonka.java.MyList;

public class ItemsView extends ListView {

    MyList<Item> items;
    ArrayAdapter adapter;
    ActionInterface.Action<Integer> listAction;
    UpdateInterface.Update<List> listUpdate;
    OnItemsUpdate onItemsUpdate;
    OnItemsSave onItemsSave;
    public boolean isUpdateProcess;

    //region interface
    public interface OnItemsUpdate {
        void onUpdate();
    }

    public interface OnItemsSave {
        void onSave();
    }
    //endregion

    //region constructors
    public ItemsView(Context context) {
        this(context, null);
    }

    public ItemsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        isUpdateProcess = false;
        items = new MyList<>();
        setOnItemClickListener(null);
        setEmptyAdapter();
    }
    //endregion

    //region sets
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

    private void setEmptyAdapter() {
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

    public void setAdapter(ArrayAdapter adapter, UpdateInterface.Update<List> listUpdate,
                           ActionInterface.Action<Integer> listAction) {
        super.setAdapter(adapter);
        this.adapter = adapter;
        this.listAction = listAction;
        this.listUpdate = listUpdate;
    }
    //endregion

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
        items.myForEach(Item::onUpdate);
        if (listUpdate != null && adapter != null) {
            adapter.clear();
            adapter.addAll(listUpdate.onUpdate());
            adapter.notifyDataSetChanged();
        }

        isUpdateProcess = false;
    }

    //region adapter position
    private boolean isAdapterItemClick(int position) {
        int adapterPosition = getAdapterPosition(position);
        return (adapterPosition >= 0 && adapterPosition < getAdapterItemsCount());
    }

    private int getAdapterPosition(int position) {
        return position - getHeaderViewsCount();
    }

    private int getAdapterItemsCount() {
        return getCount() - getHeaderViewsCount() - getFooterViewsCount();
    }
    //endregion
}
