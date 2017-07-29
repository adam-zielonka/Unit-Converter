package pro.adamzielonka.converter.activities.abstractes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.components.MyListView;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static pro.adamzielonka.converter.tools.Number.doubleToString;

public abstract class ListActivity extends BaseActivity {
    protected MyListView listView;
    protected boolean isUserCheckedChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isUserCheckedChanged = true;

        try {
            onLoad();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    protected void onLoad() throws Exception {
        listView = findViewById(R.id.ListView);
        listView.setActivity(this);
    }

    //region views
    protected void updateView(View view, String textSecondary) {
        ((TextView) view.findViewById(R.id.textSecondary)).setText(textSecondary);
        if (textSecondary.equals(""))
            view.findViewById(R.id.textSecondary).setVisibility(View.GONE);
        else view.findViewById(R.id.textSecondary).setVisibility(View.VISIBLE);
        enabledView(view);
    }

    protected void updateView(View view, String textPrimary, String textSecondary) {
        ((TextView) view.findViewById(R.id.textPrimary)).setText(textPrimary);
        updateView(view, textSecondary);
    }

    protected void updateView(View view, String textSecondary, Boolean isEnabled) {
        updateView(view, textSecondary);
        if (!isEnabled) disableView(view);
    }

    protected void updateView(View view, String textPrimary, String textSecondary, Boolean isEnabled) {
        updateView(view, textPrimary, textSecondary);
        if (!isEnabled) disableView(view);
    }

    protected void hideView(View view) {
        updateView(view, "");
        disableView(view);
    }

    protected void setSwitchState(View view, boolean state) {
        isUserCheckedChanged = false;
        ((Switch) view.findViewById(R.id.textPrimary)).setChecked(state);
        isUserCheckedChanged = true;
    }

    protected boolean getSwitchState(View view) {
        return ((Switch) view.findViewById(R.id.textPrimary)).isChecked();
    }

    protected void disableView(View view) {
        view.setEnabled(false);
        ((TextView) view.findViewById(R.id.textPrimary))
                .setTextColor(getResources().getColor(R.color.colorGreyAccent));
        ((TextView) view.findViewById(R.id.textSecondary))
                .setTextColor(getResources().getColor(R.color.colorGreyPrimary));
    }

    protected void enabledView(View view) {
        view.setEnabled(true);
        ((TextView) view.findViewById(R.id.textPrimary))
                .setTextColor(getResources().getColor(R.color.black));
        ((TextView) view.findViewById(R.id.textSecondary))
                .setTextColor(getResources().getColor(R.color.colorGreyPrimaryDark));
    }
    //endregion

    //region adapter
    protected boolean isAdapterItemClick(int position) {
        return (position - listView.getHeaderViewsCount() >= 0 && position - listView.getHeaderViewsCount()
                < listView.getCount() - listView.getHeaderViewsCount() - listView.getFooterViewsCount());
    }

    protected int getAdapterPosition(int position) {
        return position - listView.getHeaderViewsCount();
    }
    //endregion

    //region dialog
    protected EditText getDialogEditText(String text, String error) {
        View layout = getLayoutInflater().inflate(R.layout.dialog_edit_text, null);
        EditText editText = layout.findViewById(R.id.editText);
        editText.setText(text);
        editText.setSelection(editText.length());
        if (!error.equals("")) {
            TextView textView = layout.findViewById(R.id.textView);
            textView.setVisibility(View.VISIBLE);
            textView.setText(error);
        }
        return editText;
    }

    protected EditText getDialogEditText(String text) {
        return getDialogEditText(text, "");
    }

    protected EditText getDialogEditNumber(Double number) {
        EditText editText = getDialogEditText(doubleToString(number));
        editText.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
        return editText;
    }

    protected AlertDialog.Builder getAlertDialogSave(int title, View view, DialogInterface.OnClickListener onClickListener) {
        return getAlertDialogCancel(title, onClickListener, R.string.dialog_save).setView(view);
    }

    protected AlertDialog.Builder getAlertDialogDelete(int title, DialogInterface.OnClickListener onClickListener) {
        return getAlertDialogCancel(title, onClickListener, R.string.dialog_delete);
    }

    protected AlertDialog.Builder getAlertDialogCancel(int title, DialogInterface.OnClickListener onClickListener, int positiveText) {
        return getAlertDialog(title)
                .setPositiveButton(positiveText, onClickListener)
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                });
    }

    protected AlertDialog.Builder getAlertDialog(int title) {
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setCancelable(true);
    }
    //endregion

}
