package pro.adamzielonka.converter.components;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import pro.adamzielonka.converter.R;

public class MyListView extends ListView {
    private Activity activity;
    private int countHeaderItems;

    public MyListView(Context context) {
        super(context);
        countHeaderItems = 0;
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        countHeaderItems = 0;
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        countHeaderItems = 0;
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
        countHeaderItems++;
        View view = activity.getLayoutInflater().inflate(R.layout.layout_list_title, null);
        ((TextView) view.findViewById(R.id.textHeader)).setText(text);
        addHeaderView(view, false, false);
    }

    public View addHeaderItem(String textPrimary, String textSecondary) {
        return addHeaderItem(textPrimary, textSecondary, true);
    }

    public View addHeaderItem(String textPrimary, String textSecondary, boolean isSelectable) {
        countHeaderItems++;
        View view = activity.getLayoutInflater().inflate(R.layout.layout_list_item, null);
        ((TextView) view.findViewById(R.id.textPrimary)).setText(textPrimary);
        ((TextView) view.findViewById(R.id.textSecondary)).setText(textSecondary);
        if (!isSelectable) {
            ((TextView) view.findViewById(R.id.textPrimary))
                    .setTextColor(activity.getResources().getColor(R.color.colorGreyAccent));
            ((TextView) view.findViewById(R.id.textSecondary))
                    .setTextColor(activity.getResources().getColor(R.color.colorGreyPrimary));
        }
        addHeaderView(view, false, isSelectable);
        return view;
    }

    public View addFooterItem(String textPrimary) {
        View view = activity.getLayoutInflater().inflate(R.layout.layout_list_item, null);
        ((TextView) view.findViewById(R.id.textPrimary)).setText(textPrimary);
        view.findViewById(R.id.textSecondary).setVisibility(View.GONE);
        addFooterView(view, false, true);
        return view;
    }

    public int getCountHeaderItems() {
        return countHeaderItems;
    }
}
