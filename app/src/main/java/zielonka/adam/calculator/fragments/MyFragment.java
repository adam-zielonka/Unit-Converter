package zielonka.adam.calculator.fragments;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import static zielonka.adam.calculator.activities.TabbedActivity.PACKAGE_NAME;

abstract class MyFragment extends Fragment {
    void setListenerToButton(View view, View.OnClickListener mButtonClickListener, String buttonName) {
        Button button = (Button) view.findViewById(getIdResourceByName("button"+buttonName));
        button.setOnClickListener(mButtonClickListener);
    }

    private int getIdResourceByName(String aString) {
        return getResources().getIdentifier(aString, "id", PACKAGE_NAME);
    }
}
