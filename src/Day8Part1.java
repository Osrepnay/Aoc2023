import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Day8Part1 {

    record Crossroads(String left, String right) {
        String follow(char direction) {
            return switch (direction) {
                case 'L' -> left;
                case 'R' -> right;
                default -> throw new IllegalArgumentException("Invalid direction: " + direction);
            };
        }
    }
    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/8"));
        String directions = s.nextLine();
        s.nextLine();
        Map<String, Crossroads> mappings = new HashMap<>();
        while (s.hasNextLine()) {
            String[] mappingBits = s.nextLine().split(" = ");
            String src = mappingBits[0];
            String[] dirs = mappingBits[1].substring(1, mappingBits[1].length() - 1).split(", ");
            mappings.put(src, new Crossroads(dirs[0], dirs[1]));
        }
        int dirIdx = 0;
        String currPos = "AAA";
        while (!currPos.equals("ZZZ")) {
            currPos = mappings.get(currPos).follow(directions.charAt(dirIdx % directions.length()));
            dirIdx++;
        }
        System.out.println(dirIdx);
    }
}
