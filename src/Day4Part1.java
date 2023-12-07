import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day4Part1 {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/4"));
        long points = 0;
        while (s.hasNext()) {
            s.next();
            s.next();
            String[] numbers = s.nextLine().split("\\|");
            Set<Integer> winning = Arrays.stream(numbers[0].trim().split("\\s+"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
            Set<Integer> mine = Arrays.stream(numbers[1].trim().split("\\s+"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
            mine.retainAll(winning);
            points += 1L << (mine.size() - 1);
        }
        System.out.println(points);
    }
}
