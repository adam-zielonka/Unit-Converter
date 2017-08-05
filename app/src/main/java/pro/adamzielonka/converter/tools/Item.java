package pro.adamzielonka.converter.tools;

import android.content.Intent;
import android.widget.ArrayAdapter;

import pro.adamzielonka.converter.activities.abstractes.ListActivity;
import pro.adamzielonka.converter.interfaces.AlertInterface;
import pro.adamzielonka.converter.interfaces.TestInterface;

public class Item {
    private Integer title;
    private AlertInterface.Return update;
    private AlertInterface.ReturnList listUpdate;
    private AlertInterface.Alert alert;
    private AlertInterface.VoidAlert action;
    private Test validation;
    private ArrayAdapter adapter;
    private Intent intent;
    private int requestCode;
    private TestInterface.Test condition;
    private AlertInterface.Return elseUpdate;

    private Item(Integer title) {
        this.title = title;
    }

    public static Item Builder(int title) {
        return new Item(title);
    }

    public Item update(AlertInterface.Return update) {
        this.update = update;
        return this;
    }

    public Item elseUpdate(AlertInterface.Return update) {
        this.elseUpdate = update;
        return this;
    }

    public Item update(AlertInterface.ReturnList listUpdate) {
        this.listUpdate = listUpdate;
        return this;
    }

    public Item alert(AlertInterface.Alert alert) {
        this.alert = alert;
        return this;
    }

    public Item alert(AlertInterface.VoidAlert voidAlert) {
        this.action = voidAlert;
        return this;
    }

    public Item validate(TestInterface test, int error) {
        this.validation = new Test(test, error);
        return this;
    }

    public Item adapter(ArrayAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public Item startActivityForResult(Intent intent, int requestCode) {
        this.intent = intent;
        this.requestCode = requestCode;
        return this;
    }

    public Item condition(TestInterface.Test test) {
        this.condition = test;
        return this;
    }

    public void add(ListActivity activity) {
//        MyListView listView = activity.listView;
//        View view = listView.addItem(activity.getString(title));
//        AlertInterface.Return aReturn = prepareReturn(update, elseUpdate, condition);
//        listView.addItem(view,
//                prepareUpdate(view, aReturn, alert != null),
//                prepareAlert(title, aReturn, alert, action, validation));
    }

//    private AlertInterface.Return prepareReturn(AlertInterface.Return returnValue,
//                                                AlertInterface.Return elseValue,
//                                                TestInterface.Test Test) {
//        return Test.onTest() ? returnValue : elseValue;
//    }
//
//    private AlertInterface.VoidAlert prepareUpdate(View view, AlertInterface.Return aReturn, boolean isEnabled) {
//        return aReturn != null ? () -> updateView(view, (String) aReturn.onResult(), isEnabled) : null;
//    }
//
//    private AlertInterface.VoidAlert prepareAlert(int title,
//                                                  AlertInterface.Return returnValue,
//                                                  AlertInterface.Alert alert,
//                                                  AlertInterface.VoidAlert action,
//                                                  Test validation) {
//        return alert != null ? () -> {
//            newAlertDialog(title, returnValue != null ? returnValue.onResult() : null, alert::onResult, validation);
//            action.onResult();
//        } : action;
//    }
//
//    private void newAlertDialog(int title, Object object, AlertInterface.TextAlert alert, Test test) {
//        EditText editText = object instanceof Double ? getDialogEditNumber((Double) object) : getDialogEditText(object.toString());
//        getAlertDialogSave(title, editText.getRootView(), (dialog, which) -> {
//            String newText = editText.getText().toString();
//            if (!newText.equals(object instanceof Double ? doubleToString((Double) object) : object.toString())) {
//                if (test == null || test.isTest(newText)) {
//                    alert.onResult(newText);
//                    onSave();
//                } else {
//                    showError(this, test.error);
//                }
//            }
//        }).show();
//    }
//
//    //region dialog
//    private EditText getDialogEditText(String text, String error) {
//        View layout = getLayoutInflater().inflate(R.layout.dialog_edit_text, null);
//        EditText editText = layout.findViewById(R.id.editText);
//        editText.setText(text);
//        editText.setSelection(editText.length());
//        if (!error.equals("")) {
//            TextView textView = layout.findViewById(R.id.textView);
//            textView.setVisibility(View.VISIBLE);
//            textView.setText(error);
//        }
//        return editText;
//    }
//
//    private EditText getDialogEditText(String text) {
//        return getDialogEditText(text, "");
//    }
//
//    private EditText getDialogEditNumber(Double number) {
//        EditText editText = getDialogEditText(doubleToString(number));
//        editText.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
//        return editText;
//    }
//
//    private AlertDialog.Builder getAlertDialogSave(int title, View view, DialogInterface.OnClickListener onClickListener) {
//        return getAlertDialogCancel(title, onClickListener, R.string.dialog_save).setView(view);
//    }
//
//    private AlertDialog.Builder getAlertDialogDelete(int title, DialogInterface.OnClickListener onClickListener) {
//        return getAlertDialogCancel(title, onClickListener, R.string.dialog_delete);
//    }
//
//    private AlertDialog.Builder getAlertDialogCancel(int title, DialogInterface.OnClickListener onClickListener, int positiveText) {
//        return getAlertDialog(title)
//                .setPositiveButton(positiveText, onClickListener)
//                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
//                });
//    }
//
//    private AlertDialog.Builder getAlertDialog(int title) {
//        return new AlertDialog.Builder(this)
//                .setTitle(title)
//                .setCancelable(true);
//    }
    //endregion
}
