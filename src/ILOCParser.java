import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * A class for parsing a block of ILOC code
 */
public class ILOCParser {

    /**
     * The scanner that the parser uses
     */
    private final ILOCScanner scanner;

    /**
     * The translator from integer into strings to show opcodes for output
     */
    private String[] opCodeStrings;

    /**
     * The IR doubly link list only valid if Parse returns -1
     */
    private List<Integer[]> iRep;

    /**
     * The translator from integer into strings to show token types for output
     */
    String[] tokenTypeStrings;

    /**
     * Creates an ILOC Parser
     * @param filePath the absolute path to the file to parse
     */
    public ILOCParser(String filePath) {
        // create the intermediate representation as a linked list of int arrays
        iRep = new LinkedList<>();

        //                                  0         1         2         3        4        5
        tokenTypeStrings = new String[] {"MEMOP", "LOADI", "ARITHOP", "OUTPUT", "NOP", "CONSTANT",
                "REG", "COMMA", "INTO", "EOF", "COMMENT", "NEWLINE", "ERROR"};
        //        6       7       8       9       10         11        12

        //                              0        1        2       3      4      5        6
        opCodeStrings = new String[] { "load", "loadI", "store", "add", "sub", "mult", "lshift",
                "rshift", "output", "nop", ",", "=>", "NOT IN LEXEME"};
        //         7         8        9    10    11        12

        scanner = new ILOCScanner(filePath);
    }

    /**
     * a visual parser for checking parsing on a simple file
     * @throws IOException in case the inputstream reader fails
     */
    public void ParseVisual() throws IOException {
        System.out.println("Visual parsing: \n ");
        Integer[] next = new Integer[]{11, 12};
        while (!tokenTypeStrings[next[0]].equals("EOF")) {
            System.out.print("" + next[0] + " " + tokenTypeStrings[next[0]] + ", " + next[1] + " \n");
            next = scanner.NextToken();
        }
        System.out.println("" + next[0] + " " + tokenTypeStrings[next[0]] + ", " + next[1]);
    }

    /**
     * Parses the given file and checks if it follows the correct ILOC syntax
     * @return -1 if successfully parsed, line number of failed syntax if unsuccessful
     * @throws IOException if there is a read error in the input stream from the file
     */
    public int Parse() throws IOException {
        int lineCount = 1; // counts the line to return where the error was

        // start with "NEWLINE". So parser can check that each op starts with a newline.
        Integer[] nextToken = new Integer[]{11, 12};
        while (nextToken[0] != 9) { // keep going until end of file
            // keep track of the line for correct error production
            if (nextToken[0] != 11 && nextToken[0] != 12) {
                // the next ILOC statement must start on a new line 11 = "NEWLINE" (ignore errors)
                System.out.println("" + lineCount + ": The statement does not begin on next line.");
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
                        if (!this.MemopCheck(nextToken[1])) {
                            System.out.println("" + lineCount + ": Incorrect MEMOP syntax");
                        }
                    }
                    // LOADI
                    case 1 -> {
                        //System.out.println("Reading LOADI");
                        if (!this.LoadICheck(nextToken[1])) {
                            System.out.println("" + lineCount + ": Incorrect LOADI syntax");
                        }
                    }
                    // ARITHOP
                    case 2 -> {
                        //System.out.println("Reading ARITHOP");
                        if (!this.ArithopCheck(nextToken[1])) {
                            System.out.println("" + lineCount + ": Incorrect ARITHOP syntax");
                        }
                    }
                    // OUTPUT
                    case 3 -> {
                        //System.out.println("Reading OUTPUT");
                        if (!this.OutputCheck(nextToken[1])) {
                            System.out.println("" + lineCount + ": Incorrect OUTPUT syntax");
                        }
                    }
                    // NOP
                    case 4 -> {
                        //System.out.println("READING NOP");
                        if (!this.NOPCheck(nextToken[1])) {
                            System.out.println("" + lineCount + ": Incorrect NOP syntax");
                        }
                    }
                    // EOF
                    case 9 -> {
                        // IF EOF reached on the beginning of a newline, just ignore
                    }
                    default -> System.out.println("" + lineCount + ": Statement must start with an Opcode");
                }
            }
            nextToken = this.scanner.NextToken();
            if (nextToken[0] == 12) {// there has been an error word at this line
                System.out.println("" + lineCount + ": Unknown word at end of line");
            }
            else if(nextToken[0] == 11)
                lineCount ++;
        }
        System.out.println("End of parsing.");
        return -1;
    }

    /**
     * Prints out the representation
     */
    public void ShowRep() {
        for (Integer[] rep : iRep) {
            System.out.println(" " + opCodeStrings[rep[0]] + " " + rep[1] + " " + rep[5] + " " + rep[9]);
        }
    }

    /**
     * Check that the MEMOP statement follows its syntax
     * @return whether or not syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean MemopCheck(int opCode) throws IOException{
        Integer[] nextToken = this.scanner.NextToken(); // keep this for IR stuff
        Integer[] iRepElement = new Integer[13];
        iRepElement[0] = opCode; // store the opCode as the first element

        iRepElement[1] = nextToken[1]; // store the register number at index one
        if (nextToken[0] != 6) // check that next is REG
            return false;


        nextToken = this.scanner.NextToken();
        // no need to store INTO
        if (nextToken[0] != 8) // check that next is INTO
            return false;

        nextToken = this.scanner.NextToken();
        iRepElement[9] = nextToken[1]; // store next reg number at index 9
        this.iRep.add(iRepElement); // add the block to the IR
        return nextToken[0] == 6; // check that next is REG
    }

    /**
     * Check that the LOADI statement follows its syntax
     * @return whether or not syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean LoadICheck(int opCode) throws IOException {
        Integer[] nextToken = this.scanner.NextToken();
        Integer[] iRepElement = new Integer[13];
        iRepElement[0] = opCode; // store the opCode as the first element

        iRepElement[1] = nextToken[1]; // store the constant
        if (nextToken[0] != 5) // check next CONSTANT
            return false;

        nextToken = this.scanner.NextToken();
        // don't store into
        if (nextToken[0] != 8) // check next INTO
            return false;

        nextToken = this.scanner.NextToken();
        iRepElement[9] = nextToken[1]; //store the register
        this.iRep.add(iRepElement);
        return nextToken[0] == 6; // check next REG
    }

    /**
     * Check that the ARITHOP statement follows its syntax
     * @return whether the syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean ArithopCheck(int opCode) throws IOException {
        Integer[] nextToken = this.scanner.NextToken();
        Integer[] iRepElement = new Integer[13];
        iRepElement[0] = opCode; // store the opCode as the first element

        iRepElement[1] = nextToken[1]; // store the reg
        if (nextToken[0] != 6) // check next REG
            return false;

        nextToken = this.scanner.NextToken();
        // don't store comma
        if (nextToken[0] != 7) // check next COMMA
            return false;

        nextToken = this.scanner.NextToken();
        iRepElement[5] = nextToken[1]; // store the next reg
        if (nextToken[0] != 6) // check next REG
            return false;

        nextToken = this.scanner.NextToken();
        // don't store INTO
        if (nextToken[0] != 8) // check next INTO
            return false;

        nextToken = this.scanner.NextToken();
        iRepElement[9] = nextToken[1]; // store the final reg
        this.iRep.add(iRepElement);
        return nextToken[0] == 6; // check next REG
    }

    /**
     * Check that the OUTPUT statement follows its syntax
     * @return whether or not syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean OutputCheck(int opCode) throws IOException {
        Integer[] nextToken = this.scanner.NextToken();
        Integer[] iRepElement = new Integer[13];
        iRepElement[0] = opCode; // store the opCode as the first element

        iRepElement[1] = nextToken[1]; // store the constant
        iRep.add(iRepElement);
        return nextToken[0] == 5; // check next CONSTANT
    }

    /**
     * Check that the NOP statement follows its syntax
     * @return whether or not syntax is correct
     */
    private boolean NOPCheck(int opCode) {
        Integer[] iRepElement = new Integer[13];
        iRepElement[0] = opCode; // store the opCode as the first element

        this.iRep.add(iRepElement);
        return true; // for nop to be true the next state must be a NEWLINE. Done in Parse()
    }



}
