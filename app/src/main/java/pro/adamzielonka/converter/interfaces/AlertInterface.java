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
        void onResult(Integer position);
    }

    interface StringArrayAlert {
        String[] onResult();
    }

    interface VoidAlert {
        void onResult();
    }

    interface Alert {
        void onResult(Object o);
    }

    interface Return {
        Object onResult();
    }

    interface ReturnNumber {
        Double onResult();
    }

    interface ReturnInteger {
        Integer onResult();
    }

    interface ReturnBoolean {
        Boolean onResult();
    }

    interface ReturnText {
        String onResult();
    }

    interface ReturnList {
        List onResult();
    }

    interface ExistTest {
        Boolean onTest(String newText, List list);
    }
}
