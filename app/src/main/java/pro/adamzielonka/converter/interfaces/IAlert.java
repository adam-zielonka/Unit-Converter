package pro.adamzielonka.converter.interfaces;

import java.util.List;

public interface IAlert {

    interface ITextAlert {
        void onResult(String string);
    }

    interface INumberAlert {
        void onResult(Double number);
    }

    interface IListAlert {
        void onResult(int position);
    }

    interface IReturnNumber {
        Double onResult();
    }

    interface IReturnText {
        String onResult();
    }

    interface IReturnList {
        List onResult();
    }

    interface IVoidAlert {
        void onResult();
    }

    interface IExistTest {
        boolean onTest(String newText, List list);
    }
}
