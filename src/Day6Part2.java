import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Day6Part2 {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/6"));
        s.next();
        long time = Long.parseLong(s.nextLine().replace(" ", ""));
        s.next();
        long dist = Long.parseLong(s.nextLine().replace(" ", ""));
        double sqrtedDiscrim = Math.sqrt(time * time - 4L * dist);
        long solLow = (long) Math.ceil((-time + sqrtedDiscrim) / -2);
        long solHigh = (long) Math.floor((-time - sqrtedDiscrim) / -2);
        System.out.println(solHigh - solLow + 1);
    }
}
