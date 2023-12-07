import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Day5Part2 {
    record Range(long start, long len) {
        boolean isEmpty() {
            return len == 0;
        }

        boolean overlaps(Range range) {
            return end() >= range.start && end() <= range.end() || range.end() >= start && range.end() <= end();
        }

        long end() {
            return start + len - 1;
        }

        Range shift(long delta) {
            return new Range(start + delta, len);
        }

        Range[] overlap(Range other) {
            List<Long> endpoints = new ArrayList<>();
            endpoints.add(start);
            endpoints.add(end());
            endpoints.add(other.start);
            endpoints.add(other.end());
            endpoints.sort(Long::compare);
            Range over = new Range(endpoints.get(1), endpoints.get(2) - endpoints.get(1) + 1);
            boolean doesOverlap = overlaps(other);
            if (!doesOverlap) {
                over = new Range(over.start, 0);
            }
            int overlapShrink = doesOverlap ? 1 : 0;
            Range left = new Range(endpoints.get(0), endpoints.get(1) - endpoints.get(0) + 1 - overlapShrink);
            if (!left.overlaps(this)) {
                left = new Range(left.start, 0);
            }
            Range right = new Range(endpoints.get(2) + overlapShrink, endpoints.get(3) - endpoints.get(2) + 1 - overlapShrink);
            if (!right.overlaps(this)) {
                right = new Range(right.start, 0);
            }
            return new Range[]{left, over, right};
        }
    }

    record Mapping(Range start, long dest) {
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/5"));
        s.next();
        String seed;
        List<Range> seeds = new ArrayList<>();
        while (Character.isDigit((seed = s.next()).charAt(0))) {
            seeds.add(new Range(Long.parseLong(seed), s.nextLong()));
        }
        s.nextLine();
        List<List<Mapping>> allMappings = new ArrayList<>();
        String line;
        List<Mapping> mappings = new ArrayList<>();
        while (s.hasNextLine()) {
            line = s.nextLine();
            if (line.isEmpty()) {
                s.nextLine();
                allMappings.add(mappings);
                mappings = new ArrayList<>();
            } else {
                String[] split = line.split(" ");
                mappings.add(
                        new Mapping(
                                new Range(Long.parseLong(split[1]), Long.parseLong(split[2])),
                                Long.parseLong(split[0])
                        )
                );
            }
        }
        allMappings.add(mappings);
        long lowestLocation = Long.MAX_VALUE;
        for (Range sd : seeds) {
            LinkedList<Range> numRanges = new LinkedList<>();
            numRanges.add(sd);
            LinkedList<Integer> numRangesDepth = new LinkedList<>();
            numRangesDepth.add(0);
            while (!numRanges.isEmpty()) {
                Range numRange = numRanges.removeLast();
                int numRangeDepth = numRangesDepth.removeLast();
                LinkedHashSet<Range> unmatched = new LinkedHashSet<>();
                for (Mapping m : allMappings.get(numRangeDepth)) {
                    Range[] newRanges = numRange.overlap(m.start);
                    long delta = m.dest - m.start.start;
                    Range left = newRanges[0];
                    Range right = newRanges[2];
                    if (!left.isEmpty()) unmatched.add(left);
                    if (!right.isEmpty()) unmatched.add(right);
                    Range over = newRanges[1].shift(delta);
                    if (m.start.overlaps(numRange)) {
                        int newDepth = numRangeDepth + 1;
                        if (newDepth == allMappings.size()) {
                            if (over.start < lowestLocation) lowestLocation = over.start;
                        } else {
                            numRanges.add(over);
                            numRangesDepth.add(newDepth);
                        }
                    }
                }
                boolean perfectlyUnmatched = true;
                do {
                    Range unmatch = unmatched.removeLast();
                    for (Mapping m : allMappings.get(numRangeDepth)) {
                        if (unmatch.overlaps(m.start)) {
                            perfectlyUnmatched = false;
                            Range[] overlapped = unmatch.overlap(m.start);
                            if (!overlapped[0].isEmpty()) unmatched.add(overlapped[0]);
                            if (!overlapped[2].isEmpty()) unmatched.add(overlapped[2]);
                        }
                    }
                    if (perfectlyUnmatched) unmatched.add(unmatch);
                } while (!perfectlyUnmatched && !unmatched.isEmpty());
                if (numRangeDepth + 1 == allMappings.size()) {
                    for (Range r : unmatched) {
                        if (r.start < lowestLocation) lowestLocation = r.start;
                    }
                } else {
                    numRanges.addAll(unmatched);
                    for (int i = 0; i < unmatched.size(); i++) {
                        numRangesDepth.add(numRangeDepth + 1);
                    }
                }
            }
        }
        System.out.println(lowestLocation);
    }
}
