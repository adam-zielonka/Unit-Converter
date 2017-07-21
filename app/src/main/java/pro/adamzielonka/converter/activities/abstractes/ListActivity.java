package pro.adamzielonka.converter.activities.abstractes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.components.MyListView;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static pro.adamzielonka.converter.tools.Number.doubleToString;

public abstract class ListActivity extends BaseActivity {
    protected MyListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            onLoad();
        } catch (Exception e) {
            finish();
        }
    }

    protected void onLoad() throws Exception {
        listView = findViewById(R.id.ListView);
        listView.setActivity(this);
    }

    protected void updateView(View view, String text) {
        ((TextView) view.findViewById(R.id.textSecondary)).setText(text);
        if (text.equals("")) view.findViewById(R.id.textSecondary).setVisibility(View.GONE);
        else view.findViewById(R.id.textSecondary).setVisibility(View.VISIBLE);
    }

    protected void updateView(View view, String textPrimary, String textSecondary) {
        ((TextView) view.findViewById(R.id.textPrimary)).setText(textPrimary);
        updateView(view, textSecondary);
    }

    protected void hideView(View view) {
        updateView(view, "");
    }

    protected boolean isUnderItemClick(int position, int countHeaderItems, int countUnderItems) {
        return (position - countHeaderItems >= 0 && position - countHeaderItems < countUnderItems);
    }

    //region dialog
    protected EditText getDialogEditText(String text) {
        return getDialogEditText(text, "");
    }

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

    protected EditText getDialogEditNumber(Double number) {
        View layout = getLayoutInflater().inflate(R.layout.dialog_edit_text, null);
        EditText editText = layout.findViewById(R.id.editText);
        editText.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
        editText.setText(doubleToString(number));
        editText.setSelection(editText.length());
        return editText;
    }

    protected AlertDialog.Builder getAlertDialogSave(int title, View view, DialogInterface.OnClickListener onClickListener) {
        return getAlertDialog(title, onClickListener, R.string.dialog_save).setView(view);
    }

    protected AlertDialog.Builder getAlertDialogDelete(int title, DialogInterface.OnClickListener onClickListener) {
        return getAlertDialog(title, onClickListener, R.string.dialog_delete);
    }

    private AlertDialog.Builder getAlertDialog(int dialogTitle, DialogInterface.OnClickListener onClickListener, int positiveButtonTitle) {
        return new AlertDialog.Builder(this)
                .setTitle(dialogTitle)
                .setCancelable(true)
                .setPositiveButton(positiveButtonTitle, onClickListener)
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                });
    }
    //endregion

}
