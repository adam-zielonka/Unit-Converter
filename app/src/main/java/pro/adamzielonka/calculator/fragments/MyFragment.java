package pro.adamzielonka.calculator.fragments;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import static pro.adamzielonka.calculator.activities.SpinnerActivity.PACKAGE_NAME;

abstract class MyFragment extends Fragment {
    void setListenerToButton(View view, View.OnClickListener mButtonClickListener, String buttonName) {
        Button button = (Button) view.findViewById(getIdResourceByName("button"+buttonName));
        button.setOnClickListener(mButtonClickListener);
    }

    private int getIdResourceByName(String aString) {
        return getResources().getIdentifier(aString, "id", PACKAGE_NAME);
    }
}
