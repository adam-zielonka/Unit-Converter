package pro.adamzielonka.items.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import pro.adamzielonka.items.R;
import pro.adamzielonka.items.interfaces.ActionInterface;
import pro.adamzielonka.items.interfaces.TestInterface;
import pro.adamzielonka.items.tools.Test;

import static pro.adamzielonka.java.Number.stringToDouble;

public class EditDialogBuilder extends DialogBuilder {
    private Object value;
    private String error;
    private EditText editText;
    private ActionInterface.ObjectAction action;
    private DialogInterface.OnClickListener negativeAction;
    private DialogInterface.OnClickListener neutralAction;
    private List<Test> validators;
    private int neutralText;

    public EditDialogBuilder setValue(Object value) {
        this.value = value;
        return this;
    }

    public EditDialogBuilder setError(String error) {
        this.error = error;
        return this;
    }

    public EditDialogBuilder setAction(ActionInterface.ObjectAction action) {
        this.action = action;
        return this;
    }

    private ActionInterface.ObjectAction getAction() {
        return action != null ? action : object -> {
        };
    }

    public EditDialogBuilder setNegativeAction(DialogInterface.OnClickListener negativeAction) {
        this.negativeAction = negativeAction;
        return this;
    }

    private DialogInterface.OnClickListener getNegativeAction() {
        return negativeAction != null ? negativeAction : (d, i) -> {
        };
    }

    public EditDialogBuilder setNeutralAction(@StringRes int text, DialogInterface.OnClickListener neutralAction) {
        this.neutralAction = neutralAction;
        this.neutralText = text;
        return this;
    }

    public EditDialogBuilder addValidator(List<Test> validators) {
        this.validators.addAll(validators);
        return this;
    }

    public EditDialogBuilder addValidator(TestInterface.ObjectTest test, String error) {
        this.validators.add(new Test(test, error));
        return this;
    }

    public EditDialogBuilder(@NonNull Activity activity) {
        super(activity);
        validators = new ArrayList<>();
    }

    @Override
    public AlertDialog.Builder create() {
        value = value != null ? value : "";
        error = error != null ? error : "";

        editText = new EditTextBuilder(activity)
                .setValue(value)
                .setError(error)
                .create();
        return super.create();
    }

    @Override
    protected AlertDialog.Builder getAlert() {
        AlertDialog.Builder builder = super.getAlert()
                .setView(editText.getRootView())
                .setPositiveButton(R.string.dialog_save, (dialogInterface, i) -> {
                    String newText = editText.getText().toString();
                    Object newValue = value instanceof Double ? stringToDouble(newText) : newText;

                    StringBuilder errors = new StringBuilder();
                    for (Test test : validators) {
                        if (!test.isTest(newValue)) {
                            if (!errors.toString().isEmpty()) errors.append('\n');
                            errors.append(test.error);
                        }
                    }
                    if (errors.toString().isEmpty()) getAction().onAction(newValue);
                    else {
                        error = errors.toString();
                        value = newValue;
                        create().show();
                    }
                }).setCancelable(negativeAction == null)
                .setNegativeButton(R.string.dialog_cancel, getNegativeAction());
        if (neutralAction != null) builder.setNeutralButton(neutralText, neutralAction);
        return builder;
    }
}
