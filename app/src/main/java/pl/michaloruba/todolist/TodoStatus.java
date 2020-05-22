package pl.michaloruba.todolist;

public enum TodoStatus {
    NEW ("Nowe"),
    DONE ("Zrealizowane");

    private String status;


    TodoStatus(String status) {
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    @Override
    public String toString() {
        return getStatus();
    }

    public static TodoStatus fromString(String text) {
        for (TodoStatus b : TodoStatus.values()) {
            if (b.status.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
