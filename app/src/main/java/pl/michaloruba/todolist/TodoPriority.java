package pl.michaloruba.todolist;

public enum TodoPriority {
    HIGH("Wysoki"),
    MEDIUM("Średni"),
    LOW("Niski");

    private String value;

    TodoPriority(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }

    public static TodoPriority fromString(String text) {
        for (TodoPriority b : TodoPriority.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
