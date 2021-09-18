import java.util.Arrays;

public class Main {

    /**
     * The main method of the program
     * @param args the arguments in the command line
     */
    public static void main(String[] args) {


        selfTest();
//        if (inArgs("-h", args)) {
//            showCommandLineInfo();
//            if (args.length > 1) {
//                System.err.println("");
//            }
//        }

    }

    /**
     * @param string the string to search for
     * @param args the String array to search for the string in
     * @return whether string is in args
     */
    private static boolean inArgs(String string, String[] args) {
        for (String arg: args) {
            if (arg.equals(string))
                return true;
        }
        return false;
    }


    private static void showCommandLineInfo() {
        System.out.print("""
                
                How to use the ILOC Parser:\s
                ======================================================================
                412fe -h
                -----------
                When a -h flag is detected, 412fe will produce a list of valid command
                line arguments as well as their description. 412fe is not required to
                process command line arguments that appear after the -h flag
                ======================================================================
                412fe -s <file name>
                -----------
                When a -s flag is detected, 412fe reads the file specified and prints 
                a list of the tokens that the scanner found
                ======================================================================
                412fe -p <file name>
                -----------
                When a -p flag is detected, 412 reads the file specified, scans it and
                parses it, builds the intermediate representation, and reports either
                success or reports all of the errors that it finds in the input
                ======================================================================
                412fe -r <file name>
                -----------
                When a -r flag is detected, 412fe reads the file specified, scanst it,
                parses it, builds the intermediate representation, and prints out the 
                info in the intermediate representation
                
                """);
    }

    /**
     * For the simplest form of testing that I tried before testing thoroughly.
     */
    private static void selfTest() {
        ILOCParser ip = new ILOCParser("/Users/silen/IdeaProjects/412Lab1/src/testFile.txt");
        ILOCParser ip2 = new ILOCParser("/Users/silen/IdeaProjects/412Lab1/src/testFile.txt");


        try {
            System.out.println("Visual parsing:");
            ip2.ParseVisual();

            System.out.println("\nNow for actual parsing : \n");

            boolean ipLine = ip.Parse();
            if (ipLine) {
                System.out.println("Success");
            }
        }
        catch (Exception e) {
            System.out.println("Error in parse");
            e.printStackTrace();
        }
    }
}
