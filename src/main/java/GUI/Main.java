package GUI;

import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        Locale.setDefault(new Locale("en", "US"));
        if (args.length > 0) {
            switch(args[0]) {
                case "text":
                    TextOnly.main(args);
                    break;
                case "fast":
                    FastMode.main(args);
                    break;
                default:
                    System.out.println("============== StIFT ==============\n" +
                            "Run StIFT with commands 'text' or 'fast'\n" +
                            "text mode prints step by step results for given input\n" +
                            "fast mode processes input file (and grid file) to export file\n" +
                            "running without commands opens the graphical interface\n" +
                            "===================================");
            }
        } else {
            StIFT.main(args);
        }
    }
}
