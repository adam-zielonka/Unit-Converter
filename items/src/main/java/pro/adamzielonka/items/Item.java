package pro.adamzielonka.items;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.items.dialog.DialogBuilder;
import pro.adamzielonka.items.dialog.EditDialogBuilder;
import pro.adamzielonka.items.interfaces.ActionInterface;
import pro.adamzielonka.items.interfaces.TestInterface;
import pro.adamzielonka.items.interfaces.UpdateInterface;
import pro.adamzielonka.items.tools.Test;

import static pro.adamzielonka.java.Number.doubleToString;

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
        private String alertTitle;

        //Value
        private UpdateInterface.ObjectUpdate update;
        private UpdateInterface.ObjectUpdate elseUpdate;
        private boolean enabledUpdate;

        //Action
        private ActionInterface.ObjectAction action;
        private ActionInterface.Action voidAction;

        //Switcher
        private UpdateInterface.ObjectUpdate switcherUpdate;
        private ActionInterface.ObjectAction switcherAction;

        //Check list
        private UpdateInterface.ObjectsUpdate objectsUpdate;
        private UpdateInterface.PositionUpdate positionUpdate;

        //Tests
        private TestInterface.Test actionEnabled;
        private List<Test> validators;

        //Adapter
        private ArrayAdapter adapter;
        private UpdateInterface.ListUpdate listUpdate;

        public Builder(Activity activity) {
            this.activity = activity;
            update = () -> "";
            actionEnabled = () -> true;
            positionUpdate = () -> 0;
            validators = new ArrayList<>();
            enabledUpdate = true;
        }

        //region Title
        public Builder setTitle(@StringRes int title) {
            return setTitle(activity.getString(title));
        }

        public Builder setTitle(String title) {
            return setTitle(() -> title);
        }

        public Builder setTitle(UpdateInterface.StringResUpdate title) {
            return setTitle(() -> activity.getString(title.onUpdate()));
        }

        public Builder setTitle(UpdateInterface.ObjectUpdate title) {
            this.title = title;
            return this;
        }

        public Builder setAlertTitle(@StringRes int alertTitle) {
            return setAlertTitle(activity.getString(alertTitle));
        }

        public Builder setAlertTitle(String alertTitle) {
            this.alertTitle = alertTitle;
            return this;
        }

        public String getAlertTitle() {
            return alertTitle != null ? alertTitle : getTitle();
        }

        public String getTitle() {
            return title != null ? title.onUpdate().toString() : "";
        }
        //endregion

        //region Title Header
        public Builder setTitleHeader(@StringRes int title) {
            return setTitleHeader(activity.getString(title));
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

        public Builder setEnabledUpdate(boolean enabled) {
            this.enabledUpdate = enabled;
            return this;
        }

        private Object getUpdate() {
            return !actionEnabled.onTest() && elseUpdate != null ? elseUpdate.onUpdate() : update.onUpdate();
        }
        //endregion

        //region Action
        private Builder setAction(ActionInterface.ObjectAction action) {
            this.action = action;
            return this;
        }

        public Builder setAction(ActionInterface.IntegerAction action) {
            return setAction((Object object) -> action.onAction((Integer) object));
        }

        public Builder setAction(ActionInterface.DoubleAction action) {
            return setAction((Object object) -> action.onAction((Double) object));
        }

        public Builder setAction(ActionInterface.StringAction action) {
            return setAction((Object object) -> action.onAction((String) object));
        }

        public Builder setAction(ActionInterface.Action action) {
            this.voidAction = action;
            return this;
        }

        public Builder setAction(ActionInterface.LogicAction action) {
            this.voidAction = action::onAction;
            return this;
        }

        public Builder setAction(ActionInterface.VoidAction action) {
            this.voidAction = action::onAction;
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

        public Builder addValidator(TestInterface.ObjectTest validator, @StringRes int error) {
            return addValidator(validator, activity.getString(error));
        }

        public Builder setAdapter(ArrayAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public void add(ItemsView itemsView) {
            if (titleHeader != null) createItemHeader(itemsView);
            if (adapter != null && listUpdate != null) {
                createItemAdapter(itemsView);
            } else if (getTitle() != null) {
                if (objectsUpdate != null)
                    createItem(itemsView, action != null ? () -> newListDialog(itemsView,
                            (String[]) objectsUpdate.onUpdate(), positionUpdate.onUpdate()) : null);
                else createItem(itemsView, action != null ? () -> newEditDialog(itemsView,
                        getUpdate()) : null);
            }
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

        //region new dialogs
        private void newEditDialog(ItemsView itemsView, Object value) {
            new EditDialogBuilder(activity)
                    .setValue(value)
                    .addValidator(validators)
                    .setAction(newValue -> {
                        action.onAction(newValue);
                        if (enabledUpdate) itemsView.onSave();
                    }).setTitle(getAlertTitle())
                    .create().show();
        }

        private void newListDialog(ItemsView itemsView, String[] strings, int position) {
            new DialogBuilder(activity).setTitle(getTitle()).create()
                    .setSingleChoiceItems(strings, position, (dialogInterface, i) -> {
                        int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                        action.onAction(selectedPosition);
                        dialogInterface.dismiss();
                        if (enabledUpdate) itemsView.onSave();
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
                    if (enabledUpdate) itemsView.onSave();
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
