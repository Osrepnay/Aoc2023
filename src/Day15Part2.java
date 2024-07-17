import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15Part2 {
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
        String[] steps = s.nextLine().split(",");
        List<LinkedHashMap<String, Integer>> boxes = Stream.generate(() -> new LinkedHashMap<String, Integer>())
                .limit(256)
                .collect(Collectors.toCollection(LinkedList::new));
        for (String step : steps) {
            int opIdx = 0;
            int newLens = -1;
            for (; opIdx < step.length(); opIdx++) {
                if (step.charAt(opIdx) == '-') {
                    break;
                }
                if (step.charAt(opIdx) == '=') {
                    newLens = Integer.parseInt(step.substring(opIdx + 1));
                    break;
                }
            }
            String label = step.substring(0, opIdx);
            LinkedHashMap<String, Integer> box = boxes.get(hash(label));
            if (newLens == -1) {
                box.remove(label);
            } else {
                if (box.containsKey(label)) {
                    box.put(label, newLens);
                } else {
                    box.putLast(label, newLens);
                }
            }
        }
        long power = 0;
        for (int i = 0; i < boxes.size(); i++) {
            LinkedHashMap<String, Integer> box = boxes.get(i);
            int slotNum = 1;
            for (int focalLength : box.values()) {
                power += (long) (i + 1) * slotNum * focalLength;
                slotNum++;
            }
        }
        System.out.println(power);
    }
}
