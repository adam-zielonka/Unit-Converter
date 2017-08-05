package pro.adamzielonka.items;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import pro.adamzielonka.converter.R;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Number.stringToDouble;

public class Item {

    public View view;
    private ActionInterface.Action update;
    private ActionInterface.Action action;
    final boolean isEnabled;

    void onUpdate() {
        if (update != null) update.onAction();
    }

    void onAction() {
        if (action != null) action.onAction();
    }

    private Item(View view, ActionInterface.Action update, ActionInterface.Action action) {
        this.view = view;
        this.update = update;
        this.action = action;
        this.isEnabled = true;
    }

    private Item(View view, ActionInterface.Action update) {
        this.view = view;
        this.update = update;
        this.isEnabled = true;
    }

    private Item(View view, boolean isEnabled) {
        this.view = view;
        this.isEnabled = isEnabled;
    }

    public static class Builder {
        private Activity activity;
        private String title;
        private String titleHeader;
        private UpdateInterface.ObjectUpdate update;
        private ActionInterface.ObjectAction action;
        private TestInterface.Test test;
        private Test validator;

        public Builder(Activity activity) {
            this.activity = activity;
            update = () -> "";
            test = () -> true;
            validator = new Test(o -> true, "");
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(int title) {
            this.title = activity.getString(title);
            return this;
        }

        public Builder setTitleHeader(String titleHeader) {
            this.titleHeader = titleHeader;
            return this;
        }

        public Builder setTitleHeader(int titleHeader) {
            this.titleHeader = activity.getString(titleHeader);
            return this;
        }

        public Builder setUpdate(UpdateInterface.ObjectUpdate update) {
            this.update = update;
            return this;
        }

        public Builder setAction(ActionInterface.ObjectAction action) {
            this.action = action;
            return this;
        }

        public Builder setIf(TestInterface.Test test) {
            this.test = test;
            return this;
        }

        public Builder setValidator(TestInterface.ObjectTest validator, String error) {
            this.validator = new Test(validator, error);
            return this;
        }

        public void add(ItemsView itemsView) {
            if (titleHeader != null) createItemHeader(itemsView);
            if (title != null) {
                if (update.onUpdate() instanceof Boolean)
                    createItemSwitch(itemsView);
                else createItem(itemsView);
            }
        }

        //region create item
        private void createItemHeader(ItemsView itemsView) {
            View view = addItemHeader(titleHeader);
            itemsView.addItem(new Item(view, false));
        }

        private void createItem(ItemsView itemsView) {
            View view = addItem(title, "");
            itemsView.addItem(new Item(view,
                    () -> updateView(view, update.onUpdate() instanceof Double
                                    ? doubleToString((Double) update.onUpdate())
                                    : update.onUpdate().toString(),
                            action != null && test.onTest()),
                    () -> newAlertDialog(itemsView, update.onUpdate(), "")
            ));
        }

        private void createItemSwitch(ItemsView itemsView) {
            View view = addItemSwitch(title, "", itemsView);
            itemsView.addItem(new Item(view, () -> setSwitchState(view, (Boolean) update.onUpdate())));
        }
        //endregion

        //region dialog edit text
        private EditText getDialogEditText(String text, String error) {
            View layout = activity.getLayoutInflater().inflate(R.layout.dialog_edit_text, null);
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

        private EditText getDialogEditText(Object object, String error) {
            EditText editText;
            if (object instanceof Double) {
                editText = getDialogEditText(doubleToString((Double) object), error);
                editText.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
            } else editText = getDialogEditText(object.toString(), error);
            return editText;
        }
        //endregion

        //region alert dialog
        private AlertDialog.Builder getAlertDialogSave(View view, DialogInterface.OnClickListener onClickListener) {
            return getAlertDialogCancel(onClickListener, R.string.dialog_save).setView(view);
        }

        private AlertDialog.Builder getAlertDialogDelete(DialogInterface.OnClickListener onClickListener) {
            return getAlertDialogCancel(onClickListener, R.string.dialog_delete);
        }

        private AlertDialog.Builder getAlertDialogCancel(DialogInterface.OnClickListener onClickListener, int positiveText) {
            return getAlertDialog()
                    .setPositiveButton(positiveText, onClickListener)
                    .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                    });
        }

        private AlertDialog.Builder getAlertDialog() {
            return new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setCancelable(true);
        }
        //endregion

        //region alert dialog + edit text
        private View newAlertDialog(ItemsView itemsView, Object object, String error) {
            EditText editText = getDialogEditText(object, error);
            getAlertDialogSave(editText.getRootView(), (dialog, which) -> {
                String newText = editText.getText().toString();
                Object newObject = object instanceof Double ? stringToDouble(newText) : newText;
                if (validator.isTest(newObject)) {
                    action.onAction(newObject);
                    itemsView.onUpdate();
                } else {
                    newAlertDialog(itemsView, newObject, validator.error);
                }
            }).show();
            return editText.getRootView();
        }
        //endregion

        //region add item
        private View addItemHeader(String text) {
            View view = activity.getLayoutInflater().inflate(R.layout.item_header, null);
            ((TextView) view.findViewById(R.id.textHeader)).setText(text);
            return view;
        }

        private View addItem(int layout, String textPrimary, String textSecondary) {
            View view = activity.getLayoutInflater().inflate(layout, null);
            ((TextView) view.findViewById(R.id.textPrimary)).setText(textPrimary);
            ((TextView) view.findViewById(R.id.textSecondary)).setText(textSecondary);
            if (textSecondary.equals(""))
                view.findViewById(R.id.textSecondary).setVisibility(View.GONE);
            return view;
        }

        private View addItem(String textPrimary, String textSecondary) {
            return addItem(R.layout.item_pref, textPrimary, textSecondary);
        }

        public View addItemSwitch(String textPrimary, String textSecondary, ItemsView itemsView) {
            View view = addItem(R.layout.item_switch, textPrimary, textSecondary);
            ((Switch) view.findViewById(R.id.textPrimary)).setOnCheckedChangeListener((compoundButton, b) -> {
                action.onAction(getSwitchState(view));
                itemsView.onUpdate();
            });
            return view;
        }
        //endregion

        //region update view
        private void setSwitchState(View view, boolean state) {
            ((Switch) view.findViewById(R.id.textPrimary)).setChecked(state);
        }

        private boolean getSwitchState(View view) {
            return ((Switch) view.findViewById(R.id.textPrimary)).isChecked();
        }

        private void updateView(View view, String textSecondary) {
            ((TextView) view.findViewById(R.id.textSecondary)).setText(textSecondary);
            if (textSecondary.equals(""))
                view.findViewById(R.id.textSecondary).setVisibility(View.GONE);
            else view.findViewById(R.id.textSecondary).setVisibility(View.VISIBLE);
            enabledView(view);
        }

        private void updateView(View view, String textPrimary, String textSecondary) {
            ((TextView) view.findViewById(R.id.textPrimary)).setText(textPrimary);
            updateView(view, textSecondary);
        }

        private void updateView(View view, String textSecondary, Boolean isEnabled) {
            updateView(view, textSecondary);
            if (!isEnabled) disableView(view);
        }

        private void updateView(View view, String textPrimary, String textSecondary, Boolean isEnabled) {
            updateView(view, textPrimary, textSecondary);
            if (!isEnabled) disableView(view);
        }

        private void hideView(View view) {
            updateView(view, "");
            disableView(view);
        }

        private void disableView(View view) {
            view.setEnabled(false);
            ((TextView) view.findViewById(R.id.textPrimary))
                    .setTextColor(activity.getResources().getColor(R.color.colorGreyAccent));
            ((TextView) view.findViewById(R.id.textSecondary))
                    .setTextColor(activity.getResources().getColor(R.color.colorGreyPrimary));
        }

        private void enabledView(View view) {
            view.setEnabled(true);
            ((TextView) view.findViewById(R.id.textPrimary))
                    .setTextColor(activity.getResources().getColor(R.color.black));
            ((TextView) view.findViewById(R.id.textSecondary))
                    .setTextColor(activity.getResources().getColor(R.color.colorGreyPrimaryDark));
        }
        //endregion
    }
}
