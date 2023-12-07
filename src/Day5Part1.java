import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day5Part1 {
    record Range(long src, long len, long dest) {
        boolean in(long x) {
            return x >= src && x < src + len;
        }

        long map(long x) {
            return dest + (x - src);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/5"));
        s.next();
        String seed;
        List<Long> seeds = new ArrayList<>();
        while (Character.isDigit((seed = s.next()).charAt(0))) {
            seeds.add(Long.valueOf(seed));
        }
        s.nextLine();
        List<List<Range>> allMappings = new ArrayList<>();
        String line;
        List<Range> mappings = new ArrayList<>();
        while (s.hasNextLine()) {
            line = s.nextLine();
            if (line.isEmpty()) {
                s.nextLine();
                allMappings.add(mappings);
                mappings = new ArrayList<>();
            } else {
                String[] split = line.split(" ");
                mappings.add(new Range(Long.parseLong(split[1]), Long.parseLong(split[2]), Long.parseLong(split[0])));
            }
        }
        allMappings.add(mappings);
        long lowestLocation = Long.MAX_VALUE;
        for (long sd : seeds) {
            long num = sd;
            mapLoop:
            for (List<Range> map : allMappings) {
                for (Range range : map) {
                    if (range.in(num)) {
                        num = range.map(num);
                        continue mapLoop;
                    }
                }
            }
            if (num < lowestLocation) lowestLocation = num;
        }
        System.out.println(lowestLocation);
    }
}
