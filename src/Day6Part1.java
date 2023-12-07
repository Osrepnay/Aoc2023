import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Day6Part1 {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/6"));
        String[] timesSplit = s.nextLine().split("\\s+");
        int[] times = Arrays.stream(Arrays.copyOfRange(timesSplit, 1, timesSplit.length))
                .mapToInt(Integer::parseInt)
                .toArray();
        String[] distSplit = s.nextLine().split("\\s+");
        int[] distance = Arrays.stream(Arrays.copyOfRange(distSplit, 1, distSplit.length))
                .mapToInt(Integer::parseInt)
                .toArray();
        long marginProd = 1;
        for (int i = 0; i < times.length; i++) {
            int numBeat = 0;
            for (int j = 1; j <= times[i] - 1; j++) {
                int timeRemaining = times[i] - j;
                long dist = (long) timeRemaining * j;
                if (dist > distance[i]) numBeat++;
            }
            marginProd *= numBeat;
        }
        System.out.println(marginProd);
    }
}
