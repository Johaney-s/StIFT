package GUI;

import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        Locale.setDefault(new Locale("en", "US"));
        if ((args.length > 0) && args[0].equals("text")) {
            TextOnly.main(args);
        } else {
            StIFT.main(args);
        }
    }
}
