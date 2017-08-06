package pro.adamzielonka.items.classes;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.items.components.ItemsView;
import pro.adamzielonka.items.interfaces.ActionInterface;
import pro.adamzielonka.items.interfaces.TestInterface;
import pro.adamzielonka.items.interfaces.UpdateInterface;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static pro.adamzielonka.converter.tools.Number.doubleToString;
import static pro.adamzielonka.converter.tools.Number.stringToDouble;

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
        private UpdateInterface.ObjectUpdate titleUpdate;
        private String titleHeader;
        private UpdateInterface.ObjectUpdate update;
        private UpdateInterface.ObjectUpdate elseUpdate;
        private ActionInterface.ObjectAction action;
        private ActionInterface.Action cancelAction;
        private ActionInterface.Action voidAction;
        private UpdateInterface.ObjectsUpdate objectsUpdate;
        private UpdateInterface.PositionUpdate positionUpdate;
        private TestInterface.Test test;
        private Test validator;
        private ArrayAdapter adapter;
        private UpdateInterface.ListUpdate listUpdate;

        public Builder(Activity activity) {
            this.activity = activity;
            update = () -> "";
            test = () -> true;
            validator = new Test(o -> true, "");
            positionUpdate = () -> 0;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(int title) {
            this.title = activity.getString(title);
            return this;
        }

        public Builder setTitle(UpdateInterface.ObjectUpdate title) {
            this.titleUpdate = title;
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

        public Builder setAdapter(ArrayAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

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

        public Builder setIf(TestInterface.Test test) {
            this.test = test;
            return this;
        }

        public Builder setValidator(TestInterface.ObjectTest validator, String error) {
            this.validator = new Test(validator, error);
            return this;
        }

        public Builder setArray(UpdateInterface.ObjectsUpdate objectsUpdate) {
            this.objectsUpdate = objectsUpdate;
            return this;
        }

        public Builder setPosition(UpdateInterface.PositionUpdate positionUpdate) {
            this.positionUpdate = positionUpdate;
            return this;
        }

        public void add(ItemsView itemsView) {
            if (titleHeader != null) createItemHeader(itemsView);
            if (adapter != null && listUpdate != null) {
                createItemAdapter(itemsView);
            } else if (getTitle() != null) {
                if (getUpdate() instanceof Boolean)
                    createItemSwitch(itemsView);
                else if (objectsUpdate != null)
                    createItemList(itemsView);
                else createItem(itemsView);
            }
        }

        public void show() {
            EditText editText = getDialogEditText(update.onUpdate(), validator.error);
            getAlertDialogSave(editText.getRootView(), (dialog, which) -> {
                String newText = editText.getText().toString();
                action.onAction(newText);
            }).show();
        }

        private Object getUpdate() {
            return !test.onTest() && elseUpdate != null ? elseUpdate.onUpdate() : update.onUpdate();
        }

        public String getTitle() {
            if (titleUpdate != null) {
                if (titleUpdate.onUpdate() instanceof Integer)
                    return activity.getString((Integer) titleUpdate.onUpdate());
                else titleUpdate.onUpdate();
            }
            return title;
        }

        //region create item
        private void createItemHeader(ItemsView itemsView) {
            View view = addItemHeader(titleHeader);
            itemsView.addItem(new Item(view, false));
        }

        private void createItem(ItemsView itemsView) {
            View view = addItem(getTitle(), "");
            itemsView.addItem(new Item(view,
                    () -> updateView(view, getUpdate() instanceof Double
                                    ? doubleToString((Double) getUpdate())
                                    : getUpdate().toString(),
                            (action != null || voidAction != null) && test.onTest()),
                    voidAction != null
                            ? () -> voidAction.onAction()
                            : () -> newAlertDialog(itemsView, getUpdate(), "")
            ));
        }

        private void createItemSwitch(ItemsView itemsView) {
            View view = addItemSwitch(getTitle(), "", itemsView);
            itemsView.addItem(new Item(view, () -> setSwitchState(view, (Boolean) getUpdate())));
        }

        private void createItemList(ItemsView itemsView) {
            View view = addItem(getTitle(), "");
            itemsView.addItem(new Item(view,
                    () -> updateView(view, getUpdate() instanceof Double
                                    ? doubleToString((Double) getUpdate())
                                    : getUpdate().toString(),
                            (action != null || voidAction != null) && test.onTest()),
                    voidAction != null
                            ? () -> voidAction.onAction()
                            : () -> newAlertDialogList(itemsView, (String[]) objectsUpdate.onUpdate(), positionUpdate.onUpdate())
            ));
        }

        private void createItemAdapter(ItemsView itemsView) {
            itemsView.setAdapter(adapter, listUpdate, action);
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
            getAlertDialogSave(editText.getRootView(), (dialog, which) -> {
                String newText = editText.getText().toString();
                Object newObject = object instanceof Double ? stringToDouble(newText) : newText;
                if (validator.isTest(newObject)) {
                    action.onAction(newObject);
                    itemsView.onSave();
                    itemsView.onUpdate();
                } else {
                    newAlertDialog(itemsView, newObject, validator.error);
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
                itemsView.onUpdate();
            }).show();
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
                itemsView.onSave();
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
            ((TextView) view.findViewById(R.id.textPrimary)).setText(getTitle());
            ((TextView) view.findViewById(R.id.textSecondary)).setText(textSecondary);
            if (textSecondary.equals(""))
                view.findViewById(R.id.textSecondary).setVisibility(View.GONE);
            else view.findViewById(R.id.textSecondary).setVisibility(View.VISIBLE);
            enabledView(view);
        }

        private void updateView(View view, String textSecondary, Boolean isEnabled) {
            updateView(view, textSecondary);
            if (!isEnabled) disableView(view);
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
