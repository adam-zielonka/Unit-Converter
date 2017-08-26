package pro.adamzielonka.items.interfaces;

public interface ActionInterface {
    interface Action {
        void onAction();
    }

    interface ObjectAction {
        void onAction(Object object);
    }

    interface LogicAction {
        boolean onAction();
    }

    interface VoidAction {
        Void onAction();
    }

    interface IntegerAction {
        void onAction(Integer integer);
    }

    interface StringAction {
        void onAction(String text);
    }

    interface DoubleAction {
        void onAction(Double number);
    }
}
