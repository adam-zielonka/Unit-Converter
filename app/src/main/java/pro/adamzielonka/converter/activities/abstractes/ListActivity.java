package pro.adamzielonka.converter.activities.abstractes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.bool.Unique;
import pro.adamzielonka.converter.components.MyListView;
import pro.adamzielonka.converter.interfaces.IAlert;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Number.stringToDouble;

public abstract class ListActivity extends BaseActivity implements ListView.OnItemClickListener {
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

        try {
            onUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    protected void onLoad() throws Exception {
        listView = findViewById(R.id.ListView);
        listView.setActivity(this);
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);
        addItems();
    }

    protected void addItems() {
    }

    protected void onUpdate() throws Exception {
        listView.onUpdate();
    }

    protected void onSave() {
        onSave(true);
    }

    protected void onSave(boolean reload) {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        listView.onAlert(view);
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

    //region new dialog
    protected void newAlertDialogText(int title, String text, IAlert.ITextAlert alert) {
        EditText editText = getDialogEditText(text);
        getAlertDialogSave(title, editText.getRootView(), (dialog, which) -> {
            alert.onResult(editText.getText().toString());
            onSave();
        }).show();
    }

    protected void newAlertDialogTextExist(int title, String text, IAlert.IExistTest test, List list, int error, IAlert.ITextAlert alert) {
        EditText editText = getDialogEditText(text);
        getAlertDialogSave(title, editText.getRootView(), (dialog, which) -> {
            String newText = editText.getText().toString();
            if (!newText.equals(text)) {
                if (!test.onTest(newText, list)) {
                    alert.onResult(newText);
                    onSave();
                } else {
                    showError(this, error);
                }
            }
        }).show();
    }

    protected void newAlertDialogTextUnique(int title, String text, IAlert.ITextAlert alert, Unique unique) {
        EditText editText = getDialogEditText(text);
        getAlertDialogSave(title, editText.getRootView(), (dialog, which) -> {
            String newText = editText.getText().toString();
            if (!newText.equals(text)) {
                if (unique.isUnique(newText)) {
                    alert.onResult(newText);
                    onSave();
                } else {
                    showError(this, unique.error);
                }
            }
        }).show();
    }

    protected void newAlertDialogNumber(int title, Double number, IAlert.INumberAlert alert) {
        EditText editText = getDialogEditNumber(number);
        getAlertDialogSave(title, editText.getRootView(), (dialog, which) -> {
            alert.onResult(stringToDouble(editText.getText().toString()));
            onSave();
        }).show();
    }

    protected void newAlertDialogList(int title, String[] strings, int position, IAlert.IListAlert alert) {
        getAlertDialog(title).setSingleChoiceItems(strings, position, (dialogInterface, i) -> {
            int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
            alert.onResult(selectedPosition);
            dialogInterface.dismiss();
            onSave();
        }).show();
    }

    protected void newAlertDialogAdapter(int title, ListAdapter adapter, IAlert.IListAlert alert) {
        getAlertDialog(title)
                .setAdapter(adapter, (dialogInterface, i) -> {
                    alert.onResult(i);
                    onSave();
                }).show();
    }

    protected void newAlertDialogDelete(int title, IAlert.IVoidAlert alert) {
        getAlertDialogDelete(title, (dialog, which) -> {
            alert.onResult();
            onSave(false);
            onBackPressed();
        }).show();
    }
    //endregion

    //region add items
    protected void addItemTitle(int title) {
        listView.addHeaderTitle(getString(title));
    }

    protected void addItemNumber(int title, IAlert.IReturnNumber returnValue, IAlert.INumberAlert alert) {
        View view = listView.addHeaderItem(getString(title));
        listView.addItem(view,
                () -> updateView(view, doubleToString(returnValue.onResult())),
                () -> newAlertDialogNumber(title, returnValue.onResult(), alert));
    }

    protected void addItemText(int title, IAlert.IReturnText returnValue) {
        IAlert.ITextAlert alert = null;
        addItemText(title, returnValue, alert);
    }

    protected void addItemText(int title, IAlert.IReturnText returnValue, IAlert.ITextAlert alert) {
        View view = listView.addHeaderItem(getString(title));
        listView.addItem(view,
                () -> updateView(view, returnValue.onResult(), alert != null),
                alert != null ? () -> newAlertDialogText(title, returnValue.onResult(), alert) : null);
    }

    protected void addItemText(int title, IAlert.IReturnText returnValue, IAlert.IVoidAlert alert) {
        View view = listView.addHeaderItem(getString(title));
        listView.addItem(view,
                () -> updateView(view, returnValue.onResult(), alert != null),
                alert);
    }

    protected void addItemTextUnique(int title, IAlert.IReturnText returnValue, IAlert.ITextAlert alert, Unique unique) {
        View view = listView.addHeaderItem(getString(title));
        listView.addItem(view,
                () -> updateView(view, returnValue.onResult(), alert != null),
                alert != null ? () -> newAlertDialogTextUnique(title, returnValue.onResult(), alert, unique) : null);
    }
    //endregion

    //region menu
    Menu menu;
    IAlert.IVoidAlert actionDelete;

    protected void addActions() {
    }

    protected void addActionDelete(int title, IAlert.IVoidAlert alert) {
        actionDelete = () -> newAlertDialogDelete(title, alert);
        menu.findItem(R.id.menu_delete).setVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        this.menu = menu;
        addActions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            if (actionDelete != null) actionDelete.onResult();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion
}

