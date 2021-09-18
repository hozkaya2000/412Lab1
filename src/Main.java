public class Main {

    public static void main(String[] args) {
        ILOCParser ip = new ILOCParser("/Users/halit/IdeaProjects/412Lab1/src/testFile.txt");
        ILOCParser ip2 = new ILOCParser("/Users/halit/IdeaProjects/412Lab1/src/testFile.txt");


        try {
            ip2.ParseVisual();

            System.out.println("\nNow for actual parsing : \n");

            int ipLine = ip.Parse();
            if (ipLine != -1) {
                System.out.println("Error at line: " + ipLine);
            }
            else {
                System.out.println("Parser exited successfully");
                ip.ShowRep();
            }
        }
        catch (Exception e) {
            System.out.println("Error in parse visual");
            e.printStackTrace();
        }

    }
}
