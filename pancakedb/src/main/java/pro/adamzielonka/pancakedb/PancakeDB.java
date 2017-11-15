package pro.adamzielonka.pancakedb;

public class PancakeDB {
    private static final PancakeDB ourInstance = new PancakeDB();

    public static PancakeDB getInstance() {
        return ourInstance;
    }

    private PancakeDB() {
    }

}
