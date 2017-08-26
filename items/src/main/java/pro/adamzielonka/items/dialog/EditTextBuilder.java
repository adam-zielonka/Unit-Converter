package pro.adamzielonka.items.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import pro.adamzielonka.items.R;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static pro.adamzielonka.lib.Number.doubleToString;

public class EditTextBuilder {
    private Activity activity;
    private Object value;
    private String error;

    public EditTextBuilder(Activity activity) {
        this.activity = activity;
    }

    public EditTextBuilder setValue(Object value) {
        this.value = value;
        return this;
    }

    public EditTextBuilder setError(String error) {
        this.error = error;
        return this;
    }

    public EditText create() {
        value = value != null ? value : "";
        error = error != null ? error : "";
        return getEditText(value, error);
    }

    private EditText getEditText(Object value, String error) {
        if (value instanceof Double) return getEditText((Double) value, error);
        return getEditText(value.toString(), error);
    }

    private EditText getEditText(Double number, String error) {
        EditText editText = getEditText(doubleToString(number), error);
        editText.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
        return editText;
    }

    private EditText getEditText(String text, String error) {
        View layout = activity.getLayoutInflater().inflate(R.layout.dialog_edit_text, null);

        TextView textView = layout.findViewById(R.id.textView);
        textView.setVisibility(!error.equals("") ? View.VISIBLE : View.GONE);
        textView.setText(error);

        EditText editText = layout.findViewById(R.id.editText);
        editText.setText(text);
        editText.setSelection(editText.length());
        return editText;
    }
}
