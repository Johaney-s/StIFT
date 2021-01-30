package GUI;

public class Main {

    public static void main(String[] args) {
        if ((args.length > 0) && args[0].equals("text")) {
            TextOnly.main(args);
        } else {
            StIFT.main(args);
        }
    }
}
