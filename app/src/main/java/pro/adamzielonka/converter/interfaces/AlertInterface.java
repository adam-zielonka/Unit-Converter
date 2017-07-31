package pro.adamzielonka.converter.interfaces;

import java.util.List;

public interface AlertInterface {

    interface TextAlert {
        void onResult(String string);
    }

    interface NumberAlert {
        void onResult(Double number);
    }

    interface ListAlert {
        void onResult(int position);
    }

    interface ReturnNumber {
        Double onResult();
    }

    interface ReturnText {
        String onResult();
    }

    interface ReturnList {
        List onResult();
    }

    interface VoidAlert {
        void onResult();
    }

    interface ExistTest {
        boolean onTest(String newText, List list);
    }
}
