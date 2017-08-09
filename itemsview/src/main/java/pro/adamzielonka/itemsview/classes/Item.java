package pro.adamzielonka.itemsview.classes;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.itemsview.R;
import pro.adamzielonka.itemsview.ItemsView;
import pro.adamzielonka.itemsview.interfaces.ActionInterface;
import pro.adamzielonka.itemsview.interfaces.TestInterface;
import pro.adamzielonka.itemsview.interfaces.UpdateInterface;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static pro.adamzielonka.lib.Number.doubleToString;
import static pro.adamzielonka.lib.Number.stringToDouble;

public class Item {

    public View view;
    private ActionInterface.Action update;
    private ActionInterface.Action action;
    final public boolean isEnabled;

    public void onUpdate() {
        if (update != null) update.onAction();
    }

    public void onAction() {
        if (action != null) action.onAction();
    }

    private Item(View view, ActionInterface.Action update, ActionInterface.Action action) {
        this.view = view;
        this.update = update;
        this.action = action;
        this.isEnabled = true;
    }

    private Item(View view) {
        this.view = view;
        this.isEnabled = false;
    }

    public static class Builder {
        private Activity activity;

        //Title
        private UpdateInterface.ObjectUpdate title;
        private String titleHeader;

        //Value
        private UpdateInterface.ObjectUpdate update;
        private UpdateInterface.ObjectUpdate elseUpdate;

        //Action
        private ActionInterface.ObjectAction action;
        private ActionInterface.Action voidAction;
        private ActionInterface.Action cancelAction;

        //Switcher
        private UpdateInterface.ObjectUpdate switcherUpdate;
        private ActionInterface.ObjectAction switcherAction;

        //Check list
        private UpdateInterface.ObjectsUpdate objectsUpdate;
        private UpdateInterface.PositionUpdate positionUpdate;

        //Tests
        private TestInterface.Test actionEnabled;
        private List<Test> validators;
        private String error;

        //Adapter
        private ArrayAdapter adapter;
        private UpdateInterface.ListUpdate listUpdate;

        public Builder(Activity activity) {
            this.activity = activity;
            update = () -> "";
            actionEnabled = () -> true;
            error = "";
            positionUpdate = () -> 0;
            validators = new ArrayList<>();
        }

        //region Title
        public Builder setTitle(@StringRes int title) {
            setTitle(activity.getString(title));
            return this;
        }

        public Builder setTitle(String title) {
            setTitle(() -> title);
            return this;
        }

        public Builder setTitle(UpdateInterface.StringResUpdate title) {
            setTitle(() -> activity.getString(title.onUpdate()));
            return this;
        }

        public Builder setTitle(UpdateInterface.ObjectUpdate title) {
            this.title = title;
            return this;
        }

        public String getTitle() {
            return title != null ? title.onUpdate().toString() : "";
        }
        //endregion

        //region Title Header
        public Builder setTitleHeader(@StringRes int title) {
            setTitleHeader(activity.getString(title));
            return this;
        }

        public Builder setTitleHeader(String title) {
            this.titleHeader = title;
            return this;
        }
        //endregion

        //region Update
        public Builder setUpdate(UpdateInterface.ObjectUpdate update) {
            this.update = update;
            return this;
        }

        public Builder setUpdate(UpdateInterface.ListUpdate update) {
            this.listUpdate = update;
            return this;
        }

        public Builder setElseUpdate(UpdateInterface.ObjectUpdate update) {
            this.elseUpdate = update;
            return this;
        }

        private Object getUpdate() {
            return !actionEnabled.onTest() && elseUpdate != null ? elseUpdate.onUpdate() : update.onUpdate();
        }
        //endregion

        //region Action
        public Builder setAction(ActionInterface.ObjectAction action) {
            this.action = action;
            return this;
        }

        public Builder setAction(ActionInterface.Action action) {
            this.voidAction = action;
            return this;
        }

        public Builder setAction(ActionInterface.LogicAction action) {
            this.voidAction = action::onAction;
            return this;
        }

        public Builder setCancelAction(ActionInterface.Action cancelAction) {
            this.cancelAction = cancelAction;
            return this;
        }
        //endregion

        //region Switcher
        public Builder setSwitcherUpdate(UpdateInterface.ObjectUpdate switcherUpdate) {
            this.switcherUpdate = switcherUpdate;
            return this;
        }

        public Builder setSwitcherAction(ActionInterface.ObjectAction switcherAction) {
            this.switcherAction = switcherAction;
            return this;
        }
        //endregion

        //region Array
        public Builder setArray(UpdateInterface.ObjectsUpdate objectsUpdate) {
            this.objectsUpdate = objectsUpdate;
            return this;
        }

        public Builder setPosition(UpdateInterface.PositionUpdate positionUpdate) {
            this.positionUpdate = positionUpdate;
            return this;
        }
        //endregion

        public Builder setIf(TestInterface.Test test) {
            this.actionEnabled = test;
            return this;
        }

        public Builder addValidator(TestInterface.ObjectTest validator, String error) {
            this.validators.add(new Test(validator, error));
            return this;
        }

        public Builder setAdapter(ArrayAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder setError(String error) {
            this.error = error;
            return this;
        }

        public void add(ItemsView itemsView) {
            if (titleHeader != null) createItemHeader(itemsView);
            if (adapter != null && listUpdate != null) {
                createItemAdapter(itemsView);
            } else if (getTitle() != null) {
                if (objectsUpdate != null)
                    createItem(itemsView, action != null ? () -> newAlertDialogList(itemsView,
                            (String[]) objectsUpdate.onUpdate(), positionUpdate.onUpdate()) : null);
                else createItem(itemsView, action != null ? () -> newAlertDialog(itemsView,
                        getUpdate(), "") : null);
            }
        }

        public void show() {
            EditText editText = getDialogEditText(update.onUpdate(), error);
            getAlertDialog(editText.getRootView(), (dialog, which) -> {
                String newText = editText.getText().toString();
                action.onAction(newText);
            }).show();
        }

        private String getValue() {
            return getUpdate() instanceof Double ? doubleToString((Double) getUpdate()) : getUpdate().toString();
        }

        private boolean isEnabled() {
            return (action != null || voidAction != null) && actionEnabled.onTest();
        }

        //region create item
        private void createItemHeader(ItemsView itemsView) {
            View view = addItemHeader(titleHeader);
            itemsView.addItem(new Item(view));
        }

        private void createItem(ItemsView itemsView, ActionInterface.Action action) {
            View view = addItem(itemsView);
            itemsView.addItem(new Item(view, () -> updateView(view),
                    voidAction != null ? () -> voidAction.onAction() : action
            ));
        }

        private void createItemAdapter(ItemsView itemsView) {
            itemsView.setAdapter(adapter, listUpdate, action);
        }
        //endregion

        //region dialog edit text
        private EditText getDialogEditText(String text, String error) {
            View layout = activity.getLayoutInflater().inflate(R.layout.dialog_edit_text, null);

            TextView textView = layout.findViewById(R.id.textView);
            textView.setVisibility(!error.equals("") ? View.VISIBLE : View.GONE);
            textView.setText(error);

            EditText editText = layout.findViewById(R.id.editText);
            editText.setText(text);
            editText.setSelection(editText.length());
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
        private AlertDialog.Builder getAlertDialog(View view, DialogInterface.OnClickListener onClickListener) {
            return getAlertDialog()
                    .setView(view)
                    .setPositiveButton(R.string.dialog_save, onClickListener)
                    .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                        if (cancelAction != null) cancelAction.onAction();
                    });
        }

        private AlertDialog.Builder getAlertDialog() {
            return new AlertDialog.Builder(activity)
                    .setTitle(getTitle())
                    .setCancelable(cancelAction == null);
        }
        //endregion

        //region alert dialog + edit text
        private View newAlertDialog(ItemsView itemsView, Object object, String error) {
            EditText editText = getDialogEditText(object, error);
            getAlertDialog(editText.getRootView(), (dialog, which) -> {
                String newText = editText.getText().toString();
                Object newObject = object instanceof Double ? stringToDouble(newText) : newText;

                StringBuilder errors = new StringBuilder();
                for (Test test : validators) {
                    if (!test.isTest(newObject)) {
                        if (!errors.toString().isEmpty()) errors.append('\n');
                        errors.append(test.error);
                    }
                }
                if (errors.toString().isEmpty()) {
                    action.onAction(newObject);
                    itemsView.onSave();
                } else {
                    newAlertDialog(itemsView, newObject, errors.toString());
                }
            }).show();
            return editText.getRootView();
        }

        private void newAlertDialogList(ItemsView itemsView, String[] strings, int position) {
            getAlertDialog().setSingleChoiceItems(strings, position, (dialogInterface, i) -> {
                int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                action.onAction(selectedPosition);
                dialogInterface.dismiss();
                itemsView.onSave();
            }).show();
        }
        //endregion

        //region add item
        private View addItemHeader(String text) {
            View view = activity.getLayoutInflater().inflate(R.layout.item_header, null);
            ((TextView) view.findViewById(R.id.textHeader)).setText(text);
            return view;
        }

        private View addItem(ItemsView itemsView) {
            View view = activity.getLayoutInflater().inflate(R.layout.item_pref, null);
            if (switcherAction != null) {
                ((Switch) view.findViewById(R.id.switcher)).setOnCheckedChangeListener((compoundButton, b) -> {
                    switcherAction.onAction(getSwitchState(view));
                    itemsView.onSave();
                });
            } else
                view.findViewById(R.id.switcher).setVisibility(View.GONE);
            return view;
        }
        //endregion

        //region update view
        private void updateView(View view) {
            ((TextView) view.findViewById(R.id.textPrimary)).setText(getTitle());
            ((TextView) view.findViewById(R.id.textSecondary)).setText(getValue());
            view.findViewById(R.id.textSecondary).setVisibility(
                    getValue().equals("") ? View.GONE : View.VISIBLE);
            if (switcherAction != null) setSwitchState(view, (Boolean) switcherUpdate.onUpdate());
            if (isEnabled()) enabledView(view);
            else disableView(view);
        }

        private void disableView(View view) {
            view.setEnabled(false);
            ((TextView) view.findViewById(R.id.textPrimary))
                    .setTextColor(activity.getResources().getColor(R.color.primaryTextDisable));
            ((TextView) view.findViewById(R.id.textSecondary))
                    .setTextColor(activity.getResources().getColor(R.color.secondaryTextDisable));
        }

        private void enabledView(View view) {
            view.setEnabled(true);
            ((TextView) view.findViewById(R.id.textPrimary))
                    .setTextColor(activity.getResources().getColor(R.color.primaryColor));
            ((TextView) view.findViewById(R.id.textSecondary))
                    .setTextColor(activity.getResources().getColor(R.color.secondaryColor));
        }

        private void setSwitchState(View view, boolean state) {
            ((Switch) view.findViewById(R.id.switcher)).setChecked(state);
        }

        private boolean getSwitchState(View view) {
            return ((Switch) view.findViewById(R.id.switcher)).isChecked();
        }
        //endregion
    }
}
