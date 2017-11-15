package pro.adamzielonka.converter.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import pro.adamzielonka.java.Number;

public class EditNumber extends EditText {

    public EditNumber(Context context) {
        super(context);
    }

    public EditNumber(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void appendDigit(String digit) {
        setText(Number.appendDigit(getText().toString(), digit));
    }

    public void appendComma() {
        setText(Number.appendComma(getText().toString()));
    }

    public void changeSign() {
        setText(Number.changeSign(getText().toString()));
    }

    public void deleteLast(){
        setText(Number.deleteLast(getText().toString()));
    }
}
