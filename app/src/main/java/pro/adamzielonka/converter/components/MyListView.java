package pro.adamzielonka.converter.components;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.interfaces.AlertInterface;

public class MyListView extends ListView {
    private Activity activity;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    private List<MyView> myViews = new ArrayList<>();
    private AlertInterface.ReturnList adapterUpdate;
    private AlertInterface.ListAlert adapterAlert;
    private ArrayAdapter adapter;

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void addItemTitle(boolean inFooter, String text) {
        View view = activity.getLayoutInflater().inflate(R.layout.item_header, null);
        ((TextView) view.findViewById(R.id.textHeader)).setText(text);
        if (!inFooter) addHeaderView(view, false, false);
        else addFooterView(view, false, false);
    }

    public View addItem(boolean inFooter, String textPrimary) {
        return addItem(inFooter, textPrimary, "");
    }

    public View addItem(boolean inFooter, int layout, String textPrimary, String textSecondary) {
        View view = activity.getLayoutInflater().inflate(layout, null);
        ((TextView) view.findViewById(R.id.textPrimary)).setText(textPrimary);
        ((TextView) view.findViewById(R.id.textSecondary)).setText(textSecondary);
        if (textSecondary.equals(""))
            view.findViewById(R.id.textSecondary).setVisibility(View.GONE);
        if (!inFooter) addHeaderView(view);
        else addFooterView(view);
        return view;
    }

    public View addItem(boolean inFooter, String textPrimary, String textSecondary) {
        return addItem(inFooter, R.layout.item_pref, textPrimary, textSecondary);
    }

    public View addItemSwitch(boolean inFooter, String textPrimary, String textSecondary) {
        View view = addItem(inFooter, R.layout.item_switch, textPrimary, textSecondary);
        ((Switch) view.findViewById(R.id.textPrimary)).setOnCheckedChangeListener(onCheckedChangeListener);
        return view;
    }

    public View addFooterItem(String textPrimary) {
        View view = activity.getLayoutInflater().inflate(R.layout.item_pref, null);
        ((TextView) view.findViewById(R.id.textPrimary)).setText(textPrimary);
        view.findViewById(R.id.textSecondary).setVisibility(View.GONE);
        addFooterView(view, false, true);
        return view;
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void addItem(View view, AlertInterface.VoidAlert update, AlertInterface.VoidAlert alert) {
        myViews.add(new MyView(view, update, alert));
    }

    public void onAlert(View view, int position) {
        if (isAdapterItemClick(position)) {
            if (adapterAlert != null) adapterAlert.onResult(getAdapterPosition(position));
        } else
            for (MyView myView : myViews) {
                if (myView.view.equals(view)) myView.onAlert();
            }
    }

    public void onUpdate() {
        for (MyView myView : myViews) {
            myView.onUpdate();
        }
        if (adapterUpdate != null && adapter != null) {
            adapter.clear();
            adapter.addAll(adapterUpdate.onResult());
            adapter.notifyDataSetChanged();
        }
    }

    public void setAdapter(ArrayAdapter adapter, AlertInterface.ReturnList update, AlertInterface.ListAlert alert) {
        super.setAdapter(adapter);
        this.adapterUpdate = update;
        this.adapterAlert = alert;
        this.adapter = adapter;
    }

    protected boolean isAdapterItemClick(int position) {
        return (position - getHeaderViewsCount() >= 0 && position - getHeaderViewsCount()
                < getCount() - getHeaderViewsCount() - getFooterViewsCount());
    }

    protected int getAdapterPosition(int position) {
        return position - getHeaderViewsCount();
    }
}

