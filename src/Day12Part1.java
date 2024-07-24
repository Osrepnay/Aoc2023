import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Day12Part1 {
    record Group(char type, int length) {
    }

    static long factorial(int x) {
        long res = 1;
        for (int i = 2; i <= x; i++) {
            res *= i;
        }
        return res;
    }

    static long binom(int n, int k) {
        return factorial(n) / factorial(k) / factorial(n - k);
    }

    static long fitBrokens(int space, List<Integer> brokenRuns) {
        if (brokenRuns.isEmpty()) return 1;
        int brokenRunsSpace = brokenRuns.stream().mapToInt(Integer::intValue).sum() + brokenRuns.size();
        if (brokenRunsSpace > space) {
            return 0;
        } else {
            int remainder = space - brokenRunsSpace;
            return binom(remainder + brokenRuns.size(), brokenRuns.size());
        }
    }

    static long permutations(List<Group> groups, List<Integer> brokenRuns, int brokenOwe) {
        if (groups.isEmpty()) {
            if (!brokenRuns.isEmpty() || brokenOwe > 0) {
                return 0;
            } else {
                return 1;
            }
        } else if (brokenRuns.isEmpty() && brokenOwe == 0) {
            for (Group group : groups) {
                if (group.type == '#') {
                    return 0;
                }
            }
            return 1;
        }
        Group first = groups.getFirst();
        List<Group> groupsTail = groups.subList(1, groups.size());
        if (first.length <= 0) return permutations(groupsTail, brokenRuns, brokenOwe);
        return switch (first.type) {
            case '.' -> {
                if (brokenOwe > 0) {
                    yield 0;
                } else {
                    yield permutations(groupsTail, brokenRuns, 0);
                }
            }
            case '#' -> {
                boolean owed = brokenOwe > 0;
                int brokenNeeded = owed ? brokenOwe : brokenRuns.getFirst();
                List<Integer> newBrokens = owed ? brokenRuns : brokenRuns.subList(1, brokenRuns.size());
                if (first.length == brokenNeeded) {
                    if (!groupsTail.isEmpty()) {
                        Group next = groupsTail.getFirst();
                        if (next.type == '?') {
                            groupsTail.set(0, new Group('?', next.length - 1));
                            long result = permutations(groupsTail, newBrokens, 0);
                            groupsTail.set(0, next);
                            yield result;
                        }
                    }
                    yield permutations(groupsTail, newBrokens, 0);
                } else if (first.length < brokenNeeded) {
                    int damagedRemaining = brokenNeeded - first.length;
                    yield permutations(groupsTail, newBrokens, damagedRemaining);
                } else {
                    yield 0;
                }
            }
            case '?' -> {
                if (brokenOwe > 0) {
                    if (brokenOwe > first.length) {
                        yield permutations(groupsTail, brokenRuns, brokenOwe - first.length);
                    } else if (brokenOwe == first.length) {
                        if (!groupsTail.isEmpty()) {
                            Group next = groupsTail.getFirst();
                            if (next.type == '#') {
                                yield 0;
                            }
                        }
                        yield permutations(groupsTail, brokenRuns, 0);
                    } else {
                        groups.set(0, new Group('?', first.length - brokenOwe - 1));
                        long res = permutations(groups, brokenRuns, 0);
                        groups.set(0, first);
                        yield res;
                    }
                }
                long permutations = 0;
                int brokenConsume = 0;
                while (brokenConsume <= brokenRuns.size()) {
                    List<Integer> brokenConsumed = brokenRuns.subList(0, brokenConsume);
                    List<Integer> brokenRemain = brokenRuns.subList(brokenConsume, brokenRuns.size());
                    if (!groupsTail.isEmpty() && groupsTail.getFirst().type == '#') {
                        Group next = groupsTail.getFirst();
                        boolean oneFit = false;
                        for (int merge = 0; merge <= first.length; merge++) {
                            groupsTail.set(0, new Group('#', next.length + merge));
                            long fitResult = fitBrokens(first.length - merge, brokenConsumed);
                            permutations += fitResult * permutations(groupsTail, brokenRemain, 0);
                            groupsTail.set(0, next);

                            if (fitResult == 0) {
                                break;
                            } else {
                                oneFit = true;
                            }
                        }
                        if (!oneFit) {
                            break;
                        }
                    } else {
                        long fitResult = fitBrokens(first.length + 1, brokenConsumed);
                        permutations += fitResult * permutations(groupsTail, brokenRemain, 0);
                        if (fitResult == 0) {
                            break;
                        }
                    }
                    brokenConsume++;
                }
                yield permutations;
            }
            default -> throw new IllegalArgumentException("bad group");
        };
    }


    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/12"));
        long sum = 0;
        while (s.hasNext()) {
            List<Group> groups = new ArrayList<>();
            String conditions = s.next();
            List<Integer> brokens = Arrays.stream(s.next().split(",")).map(Integer::valueOf).toList();
            char currChar = ' ';
            int currGroupLen = 0;
            for (int i = 0; i < conditions.length(); i++) {
                if (currChar != conditions.charAt(i)) {
                    if (currChar != ' ') {
                        groups.add(new Group(currChar, currGroupLen));
                    }
                    currChar = conditions.charAt(i);
                    currGroupLen = 0;
                }
                currGroupLen++;
            }
            groups.add(new Group(currChar, currGroupLen));
            sum += permutations(groups, brokens, 0);
        }
        System.out.println(sum);
    }
}