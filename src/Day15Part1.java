import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Day15Part1 {
    static int hash(String str) {
        int hash = 0;
        for (char c : str.toCharArray()) {
            hash += c;
            hash *= 17;
            hash %= 256;
        }
        return hash;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/15"));
        System.out.println(Arrays.stream(s.nextLine().split(",")).mapToLong(Day15Part1::hash).reduce(Long::sum).getAsLong());
    }
}
