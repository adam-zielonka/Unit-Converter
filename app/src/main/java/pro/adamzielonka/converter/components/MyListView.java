package pro.adamzielonka.converter.components;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.interfaces.IAlert;

public class MyListView extends ListView {
    private Activity activity;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    private List<MyView> myViews = new ArrayList<>();

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

    public void addHeaderTitle(String text) {
        View view = activity.getLayoutInflater().inflate(R.layout.item_header, null);
        ((TextView) view.findViewById(R.id.textHeader)).setText(text);
        addHeaderView(view, false, false);
    }

    public View addHeaderItem(String textPrimary) {
        return addHeaderItem(textPrimary, "");
    }

    public View addHeaderItem(int layout, String textPrimary, String textSecondary) {
        View view = activity.getLayoutInflater().inflate(layout, null);
        ((TextView) view.findViewById(R.id.textPrimary)).setText(textPrimary);
        ((TextView) view.findViewById(R.id.textSecondary)).setText(textSecondary);
        if (textSecondary.equals(""))
            view.findViewById(R.id.textSecondary).setVisibility(View.GONE);
        addHeaderView(view);
        return view;
    }

    public View addHeaderItem(String textPrimary, String textSecondary) {
        return addHeaderItem(R.layout.item_pref, textPrimary, textSecondary);
    }

    public View addHeaderSwitch(String textPrimary, String textSecondary) {
        View view = addHeaderItem(R.layout.item_switch, textPrimary, textSecondary);
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

    public void addItem(View view, IAlert.IVoidAlert update, IAlert.IVoidAlert alert) {
        myViews.add(new MyView(view, update, alert));
    }

    public void onAlert(View view) {
        for (MyView myView : myViews) {
            if (myView.view.equals(view)) myView.onAlert();
        }
    }

    public void onUpdate() {
        for (MyView myView : myViews) {
            myView.onUpdate();
        }
    }
}

