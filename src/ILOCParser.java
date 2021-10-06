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
     * The IR -- a doubly linked list of arrays
     */
    private List<Integer[]> iRep;

    /**
     * The translator from integer into strings to show token types for output
     */
    String[] tokenTypeStrings;

    /**
     * Keeps track of the linecount
     */
    Integer lineCount;

    /**
     * Keeps track of if a line ended with a new line incorrectly
     *
     * To prevent line miscounting when this happens.
     */
    boolean errNlEnd;

    /**
     * Whether or not to print the IR
     */
    boolean printIR;

    /**
     * Stores the line of the last printed error
     * To prevent duplicate error printing.
     */
    int lastErrorLine;

    /**
     * Total ILOC Operations
     */
    int totalOps;

    /**
     * Creates an ILOC Parser
     *
     * @param filePath the absolute path to the file to parse
     */
    public ILOCParser(String filePath, boolean printTokens, boolean printIR) {
        // create the intermediate representation as a linked list of int arrays
        this.iRep = new LinkedList<>();
        this.errNlEnd = false;
        this.printIR = printIR;
        this.totalOps = 0;

        //                                  0         1         2         3        4        5
        this.tokenTypeStrings = new String[]{"MEMOP", "LOADI", "ARITHOP", "OUTPUT", "NOP", "CONSTANT",
                "REG", "COMMA", "INTO", "EOF", "COMMENT", "NEWLINE", "ERROR"};
        //        6       7       8       9       10         11        12

        //                              0        1        2       3      4      5        6
        this.opCodeStrings = new String[]{"load", "loadI", "store", "add", "sub", "mult", "lshift",
                "rshift", "output", "nop", ",", "=>", "NOT IN LEXEME"};
        //         7         8        9    10    11        12

        this.scanner = new ILOCScanner(filePath, printTokens);
    }

    /**
     * Parses and gets the intermediate representation
     * Either Parse or ParseException method must have been run before to use this.
     * @return the intermediate representation created by this parser.
     */
    public List<Integer[]> ParseAndGetRep() {
        return iRep;
    }

    /**
     * Parses cleanly without an exception to stop the program
     */
    public void Parse() {
        try {
            ParseException();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Parses the given file and checks if it follows the correct ILOC syntax
     *\
     * @throws IOException if there is a read error in the input stream from the file
     */
    private void ParseException() throws IOException {
        boolean success = true;
        this.lineCount = 1; // counts the line to return where the error was

        // start with "NEWLINE". So parser can check that each op starts with a newline.
        Integer[] nextToken = new Integer[]{-1, -1};
        while (nextToken[0] != 9) { // keep going until end of file
            // keep track of the line for correct error product

            nextToken = this.scanner.NextToken();
            //System.out.println(tokenTypeStrings[nextToken[0]]); // for debugging purposes
            // add reg and constant case
            switch (nextToken[0]) {
                // MEMOP
                case 0 :
                    if (!this.MemopCheck(nextToken[1])) {
                        if (this.lastErrorLine != this.lineCount)
                            System.err.println("" + this.lineCount + ": Incorrect MEMOP syntax");
                        this.lastErrorLine = this.lineCount;
                        success = false;
                    } else {
                        this.lineCount++;
                        this.totalOps++;
                    }

                    break;
                // LOADI
                case 1 :
                    if (!this.LoadICheck(nextToken[1])) {
                        if (this.lastErrorLine != this.lineCount)
                            System.err.println("" + this.lineCount + ": Incorrect LOADI syntax");
                        this.lastErrorLine = this.lineCount;
                        success = false;
                    } else {
                        this.lineCount++;
                        this.totalOps++;
                    }
                    break;
                // ARITHOP
                case 2 :
                    if (!this.ArithopCheck(nextToken[1])) {
                        if (this.lastErrorLine != this.lineCount)
                            System.err.println("" + this.lineCount + ": Incorrect ARITHOP syntax");
                        this.lastErrorLine = this.lineCount;
                        success = false;
                    } else {
                        this.lineCount++;
                        this.totalOps++;
                    }
                    break;
                // OUTPUT
                case 3 :
                    if (!this.OutputCheck(nextToken[1])) {
                        if (this.lastErrorLine != this.lineCount)
                            System.err.println("" + this.lineCount + ": Incorrect OUTPUT syntax");
                        this.lastErrorLine = this.lineCount;
                        success = false;
                    } else {
                        this.lineCount++;
                        this.totalOps++;
                    }
                    break;
                // NOP
                case 4 :
                    if (!this.NOPCheck(nextToken[1])) {
                        if (this.lastErrorLine != this.lineCount)
                            System.err.println("" + this.lineCount + ": Incorrect NOP syntax");
                        this.lastErrorLine = this.lineCount;
                        success = false;
                    } else {
                        this.lineCount++;
                        this.totalOps++;
                    }
                    break;
                // EOF
                case 9 :
                    // IF EOF reached on the beginning of a newline, just ignore
                    break;
                case 10:
                    this.lineCount++;
                    break;
                case 11:
                    this.lineCount++;
                    break;
                default :
                    if (errNlEnd) {
                        lineCount ++;
                        errNlEnd = false;
                    }
                    if (this.lastErrorLine != this.lineCount)
                        System.err.println("" + this.lineCount + ": Statement must start with an Opcode");
                    this.lastErrorLine = this.lineCount;
                    success = false;
                    break;
            }
        }
        if (this.printIR)
            this.ShowRep();
        if (success)
            System.out.println("Parse success with " + this.totalOps + " operations total.");
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
     *
     * @return whether or not syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean MemopCheck(int opCode) throws IOException {
        Integer[] nextToken = this.scanner.NextToken(); // keep this for IR stuff
        Integer[] iRepElement = new Integer[13];
        iRepElement[0] = opCode; // store the opCode as the first element

        iRepElement[1] = nextToken[1]; // store the register number at index one
        if (nextToken[0] != 6) {// check that next is REG
            if (nextToken[0] == 11 || nextToken[0] == 10)
                errNlEnd = true;
            return false;
        }

        nextToken = this.scanner.NextToken();
        // no need to store INTO
        if (nextToken[0] != 8) { // check that next is INTO
            if (nextToken[0] == 11 || nextToken[0] == 10)
                errNlEnd = true;
            return false;
        }

        nextToken = this.scanner.NextToken();
        iRepElement[9] = nextToken[1]; // store next reg number at index 9
        this.iRep.add(iRepElement); // add the block to the IR
        if (nextToken[0] != 6) { // check that next is REG
            if (nextToken[0] == 11 || nextToken[0] == 10)
                errNlEnd = true;
            return false;
        }

        nextToken = this.scanner.NextToken();
        return nextToken[0] == 11 || nextToken[0] == 9 || nextToken[0] == 10; // check ends with nextline
    }

    /**
     * Check that the LOADI statement follows its syntax
     *
     * @return whether or not syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean LoadICheck(int opCode) throws IOException {
        Integer[] nextToken = this.scanner.NextToken();
        Integer[] iRepElement = new Integer[13];
        iRepElement[0] = opCode; // store the opCode as the first element

        iRepElement[1] = nextToken[1]; // store the constant
        if (nextToken[0] != 5) {// check next CONSTANT
            if (nextToken[0] == 11 || nextToken[0] == 10)
                errNlEnd = true;
            return false;
        }

        nextToken = this.scanner.NextToken();
        // don't store into
        if (nextToken[0] != 8) { // check next INTO
            if (nextToken[0] == 11 || nextToken[0] == 10)
                errNlEnd = true;
            return false;
        }

        nextToken = this.scanner.NextToken();
        iRepElement[9] = nextToken[1]; //store the register
        this.iRep.add(iRepElement);
        if(nextToken[0] != 6) { // check next REG
            if (nextToken[0] == 11 || nextToken[0] == 10)
                errNlEnd = true;
            return false;
        }

        nextToken = this.scanner.NextToken();
        return nextToken[0] == 11 || nextToken[0] == 9 || nextToken[0] == 10; // check ends with nextline
    }

    /**
     * Check that the ARITHOP statement follows its syntax
     *
     * @return whether the syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean ArithopCheck(int opCode) throws IOException {
        Integer[] nextToken = this.scanner.NextToken();
        Integer[] iRepElement = new Integer[13];
        iRepElement[0] = opCode; // store the opCode as the first element

        iRepElement[1] = nextToken[1]; // store the reg
        if (nextToken[0] != 6) {// check next REG
            if (nextToken[0] == 11 || nextToken[0] == 10)
                errNlEnd = true;
            return false;
        }

        nextToken = this.scanner.NextToken();
        // don't store comma
        if (nextToken[0] != 7) { // check next COMMA
            if (nextToken[0] == 11 || nextToken[0] == 10)
                errNlEnd = true;
            return false;
        }


        nextToken = this.scanner.NextToken();
        iRepElement[5] = nextToken[1]; // store the next reg
        if (nextToken[0] != 6) {// check next REG
            if (nextToken[0] == 11 || nextToken[0] == 10)
                errNlEnd = true;
            return false;
        }

        nextToken = this.scanner.NextToken();
        // don't store INTO
        if (nextToken[0] != 8){ // check next INTO
            if (nextToken[0] == 11 || nextToken[0] == 10)
                errNlEnd = true;
            return false;
        }

        nextToken = this.scanner.NextToken();
        iRepElement[9] = nextToken[1]; // store the final reg
        this.iRep.add(iRepElement);
        if (nextToken[0] != 6) { // check next REG
            if (nextToken[0] == 11 || nextToken[0] == 10)
                errNlEnd = true;
            return false;
        }

        nextToken = this.scanner.NextToken();
        return nextToken[0] == 11 || nextToken[0] == 9 || nextToken[0] == 10; // check ends with nextline
    }

    /**
     * Check that the OUTPUT statement follows its syntax
     *
     * @return whether or not syntax is correct
     * @throws IOException if the scanner's input stream throws an exception
     */
    private boolean OutputCheck(int opCode) throws IOException {
        Integer[] nextToken = this.scanner.NextToken();
        Integer[] iRepElement = new Integer[13];
        iRepElement[0] = opCode; // store the opCode as the first element

        iRepElement[1] = nextToken[1]; // store the constant
        iRep.add(iRepElement);
        if (nextToken[0] != 5) {// check next CONSTANT
            if (nextToken[0] == 11 || nextToken[0] == 10)
                errNlEnd = true;
            return false;
        }

        nextToken = this.scanner.NextToken();
        return nextToken[0] == 11 || nextToken[0] == 9 || nextToken[0] == 10; // check ends with nextline
    }

    /**
     * Check that the NOP statement follows its syntax
     *
     * @return whether or not syntax is correct
     * @throws IOException when inputstream in scanner fails ot read
     */
    private boolean NOPCheck(int opCode) throws IOException {
        Integer[] iRepElement = new Integer[13];
        iRepElement[0] = opCode; // store the opCode as the first element
        this.iRep.add(iRepElement);

        Integer[] nextToken = this.scanner.NextToken();
        return nextToken[0] == 11 || nextToken[0] == 9 || nextToken[0] == 10; // check ends with nextline
    }


}
