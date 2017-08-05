package pro.adamzielonka.items;

public interface ActionInterface {
    interface Action {
        void onAction();
    }

    interface ObjectAction {
        void onAction(Object object);
    }

    interface ListAction {
        void onAction(Integer position);
    }
}
