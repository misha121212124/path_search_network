import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        dots();
    }

    private static void dots() {
        GraphicForm f = new GraphicForm();
        new Thread(f).start();
    }
}