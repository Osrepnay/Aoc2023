import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Day9Part2 {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/9"));
        long sum = 0;
        while (s.hasNextLine()) {
            List<Integer> values = Arrays.stream(s.nextLine().split(" "))
                    .map(Integer::valueOf)
                    .collect(Collectors.toCollection(LinkedList::new));
            List<List<Integer>> diffs = new ArrayList<>();
            while (true) {
                diffs.add(values);
                List<Integer> newValues = new LinkedList<>();
                int last = -1;
                boolean changed = false;
                for (int i = 0; i < values.size() - 1; i++) {
                    int diff = values.get(i + 1) - values.get(i);
                    if (last != -1 && last != diff) {
                        changed = true;
                    }
                    newValues.add(diff);
                    last = diff;
                }
                values = newValues;
                if (!changed) break;
            }
            values.addFirst(values.getFirst());
            diffs.add(values);
            for (int i = diffs.size() - 2; i >= 0; i--) {
                diffs.get(i).addFirst(diffs.get(i).getFirst() - diffs.get(i + 1).getFirst());
            }
            sum += diffs.getFirst().getFirst();
        }
        System.out.println(sum);
    }
}
