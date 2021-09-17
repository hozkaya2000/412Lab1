import java.io.IOException;

/**
 * A class for parsing a block of ILOC code
 */
public class ILOCParser {

    /**
     * The scanner that the parser uses
     */
    private final ILOCScanner scanner;

    //                                          0         1         2         3        4        5
    String[] tokenTypeStrings = new String[] {"MEMOP", "LOADI", "ARITHOP", "OUTPUT", "NOP", "CONSTANT",
            "REG", "COMMA", "INTO", "EOF", "COMMENT", "NEWLINE", "ERROR"};
    //        6       7       8       9       10         11        12


    public ILOCParser(String fileName) {
        scanner = new ILOCScanner(fileName);
    }

    // use this for checking that the scanner returned the correct pairs to the parser
    public void ParseVisual() throws IOException {
        Integer[] next = new Integer[]{11, 12}; ;
        while (!tokenTypeStrings[next[0]].equals("EOF")) {
            System.out.print("" + next[0] + " " + tokenTypeStrings[next[0]] + ", " + next[1] + " \n");
            next = scanner.NextToken();
        }
        System.out.println("" + next[0] + " " + tokenTypeStrings[next[0]] + ", " + next[1]);
    }

    /**
     * Parses the given file and checks if it follows the correct ILOC syntax
     * @return -1 if successfully parsed, line number of failed syntax if unsuccessful
     * @throws IOException if there is a read error in the inputstream from the file
     */
    public int Parse() throws IOException {
        int lineCount = 1; // counts the line to return where the error was

        // start with "NEWLINE". So parser can check that each op starts with a newline.
        Integer[] nextToken = new Integer[]{11, 12};
        while (nextToken[0] != 9 && nextToken[0] != 12) { // keep going until error or end of file
            if (nextToken[0] != 11) { // the next ILOC statement must start on a new line 11 = "NEWLINE"
                System.out.println("The statement does not begin on next line.");
                return lineCount;
            }
            else {
                nextToken = this.scanner.NextToken();
                while(nextToken[0] == 10 || nextToken[0] == 11) { // Ignore comments and empty lines
                    lineCount ++;
                    nextToken = this.scanner.NextToken();
                }
                //System.out.println(tokenTypeStrings[nextToken[0]]); // for debugging purposes
                // add reg and constant case
                switch (nextToken[0]) {
                    // MEMOP
                    case 0 -> {
                        //System.out.println("Reading MEMOP");
                        if (!this.MemopCheck()) {
                            System.out.println("Incorrect MEMOP syntax");
                            return lineCount;
                        }
                    }
                    // LOADI
                    case 1 -> {
                        //System.out.println("Reading LOADI");
                        if (!this.LoadICheck()) {
                            System.out.println("Incorrect LOADI syntax");
                            return lineCount;
                        }
                    }
                    // ARITHOP
                    case 2 -> {
                        //System.out.println("Reading ARITHOP");
                        if (!this.ArithopCheck()) {
                            System.out.println("Incorrect ARITHOP syntax");
                            return lineCount;
                        }
                    }
                    // OUTPUT
                    case 3 -> {
                        //System.out.println("Reading OUTPUT");
                        if (!this.OutputCheck()) {
                            System.out.println("Incorrect OUTPUT syntax");
                            return lineCount;
                        }
                    }
                    // NOP
                    case 4 -> {
                        //System.out.println("READING NOP");
                        if (!this.NOPCheck()) {
                            System.out.println("Incorrect NOP syntax");
                            return lineCount;
                        }
                    }
                    // EOF
                    case 9 -> {
                        // IF EOF reached on the beginning of a newline, just ignore
                    }
                    default -> {
                        System.out.println("Statement must start with an Opcode");
                        return lineCount;
                    }
                }
            }
            lineCount ++; // keep track of the line for correct error production
            nextToken = this.scanner.NextToken();
        }
        System.out.println("End of parser. Success.");
        return -1;
    }

    /**
     * Check that the MEMOP statement follows its syntax
     * @return whether or not syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean MemopCheck() throws IOException{ // TODO IR Stuff
        Integer[] nextToken = this.scanner.NextToken(); // keep this for IR stuff
        if (nextToken[0] != 6) // check that next is REG
            return false;

        nextToken = this.scanner.NextToken();
        if (nextToken[0] != 8) // check that next is INTO
            return false;

        nextToken = this.scanner.NextToken();
        return nextToken[0] == 6; // check that next is REG
    }

    /**
     * Check that the LOADI statement follows its syntax
     * @return whether or not syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean LoadICheck() throws IOException { // TODO IR Stuff
        Integer[] nextToken = this.scanner.NextToken();
        if (nextToken[0] != 5) // check next CONSTANT
            return false;

        nextToken = this.scanner.NextToken();
        if (nextToken[0] != 8) // check next INTO
            return false;

        nextToken = this.scanner.NextToken();
        return nextToken[0] == 6; // check next REG
    }

    /**
     * Check that the ARITHOP statement follows its syntax
     * @return whether or not syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean ArithopCheck() throws IOException { // TODO IR Stuff
        Integer[] nextToken = this.scanner.NextToken();

        if (nextToken[0] != 6) // check next REG
            return false;

        nextToken = this.scanner.NextToken();
        if (nextToken[0] != 7) // check next COMMA
            return false;

        nextToken = this.scanner.NextToken();
        if (nextToken[0] != 6) // check next REG
            return false;

        nextToken = this.scanner.NextToken();
        if (nextToken[0] != 8) // check next INTO
            return false;

        nextToken = this.scanner.NextToken();
        return nextToken[0] == 6; // check next REG
    }

    /**
     * Check that the OUTPUT statement follows its syntax
     * @return whether or not syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean OutputCheck() throws IOException { // TODO IR Stuff
        Integer[] nextToken = this.scanner.NextToken();

        return nextToken[0] == 5; // check next CONSTANT
    }

    /**
     * Check that the NOP statement follows its syntax
     * @return whether or not syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean NOPCheck() throws IOException { // TODO IR Stuff
        return true;
    }




}
