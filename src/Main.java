public class Main {

    public static void main(String[] args) {
        ILOCParser ip = new ILOCParser("C:\\Users\\silen\\IdeaProjects\\412Lab1\\src\\testFile.txt");
        ILOCParser ip2 = new ILOCParser("C:\\Users\\silen\\IdeaProjects\\412Lab1\\src\\testFile.txt");

       try {
           ip2.ParseVisual();

           System.out.println("\nNow for actual parsing : \n");

           int ipLine = ip.Parse();
           if (ipLine != -1) {
               System.out.println("Error at line: " + ipLine);
           }
           else {
               System.out.println("Parser exited successfully");
           }
       }
       catch (Exception ignored) {
           System.out.println("Error in parse visual");
           ignored.printStackTrace();
       }

    }
}
