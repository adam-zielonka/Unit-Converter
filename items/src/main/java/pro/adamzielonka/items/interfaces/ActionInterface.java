package pro.adamzielonka.items.interfaces;

public interface ActionInterface {

    interface VoidAction {
        void onAction();
    }

    interface Action<T> {
        void onAction(T t);
    }
}
