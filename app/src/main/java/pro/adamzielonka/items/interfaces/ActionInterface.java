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

    interface ListAction {
        void onAction(Integer position);
    }
}
