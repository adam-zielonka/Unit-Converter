package pro.adamzielonka.items;

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

public class ItemsView extends ListView {

    List<Item> items = new ArrayList<>();
    ActionInterface.ListAction listAction;
    UpdateInterface.ListUpdate listUpdate;
    OnItemsUpdate onItemsUpdate;
    public boolean isUpdateProcess;

    public interface OnItemsUpdate {
        void onUpdate();
    }

    public void setOnItemsUpdate(OnItemsUpdate onItemsUpdate) {
        this.onItemsUpdate = onItemsUpdate;
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

    public void addItem(Item item) {
        if (getAdapter() == null) addHeaderView(item.view, false, item.isEnabled);
        else addFooterView(item.view, false, item.isEnabled);
        items.add(item);
    }

    public void onAction(View view, int position) {
        if (isAdapterItemClick(position) && listAction != null)
            listAction.onAction(getAdapterPosition(position));
        else for (Item item : items)
            if (item.view.equals(view) && view.isEnabled()) item.onAction();
    }

    public void onUpdate() {
        if (isUpdateProcess) return;
        isUpdateProcess = true;
        for (Item item : items) item.onUpdate();
        if (listUpdate != null && getAdapter() != null) {
            if (getAdapter() instanceof ArrayAdapter) {
                ((ArrayAdapter) getAdapter()).clear();
                ((ArrayAdapter) getAdapter()).addAll(listUpdate.onUpdate());
                ((ArrayAdapter) getAdapter()).notifyDataSetChanged();
            }
        }
        if (onItemsUpdate != null) onItemsUpdate.onUpdate();
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
