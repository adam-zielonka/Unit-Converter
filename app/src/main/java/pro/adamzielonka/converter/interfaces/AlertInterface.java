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

    interface StringArrayAlert {
        String[] onResult();
    }

    interface VoidAlert {
        void onResult();
    }

    interface ReturnNumber {
        Double onResult();
    }

    interface ReturnInteger {
        int onResult();
    }

    interface ReturnBoolean {
        boolean onResult();
    }

    interface ReturnText {
        String onResult();
    }

    interface ReturnList {
        List onResult();
    }

    interface ExistTest {
        boolean onTest(String newText, List list);
    }
}
