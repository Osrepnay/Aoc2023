import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;

public class Day14Part2 {
    record Position(int r, int c) {
    }

    static int rows;
    static int cols;
    static Set<Position> cubes;

    static void shift(Position round, Set<Position> rounds, Function<Position, Position> delta) {
        if (!rounds.contains(round)) return;
        Position newPos = round;
        Position blockerPos;
        rounds.remove(round);
        for (blockerPos = new Position(round.r, round.c);
             blockerPos.r >= 0 &&
                     !cubes.contains(blockerPos) &&
                     !rounds.contains(blockerPos);
             blockerPos = delta.apply(blockerPos)) {
            newPos = blockerPos;
        }
        rounds.add(newPos);
    }

    static void cycle(Set<Position> rounds) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                shift(new Position(r, c), rounds, p -> new Position(p.r - 1, p.c));
            }
        }
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                shift(new Position(r, c), rounds, p -> new Position(p.r, p.c - 1));
            }
        }
        for (int r = rows - 1; r >= 0; r--) {
            for (int c = 0; c < cols; c++) {
                shift(new Position(r, c), rounds, p -> new Position(p.r + 1, p.c));
            }
        }
        for (int c = cols - 1; c >= 0; c--) {
            for (int r = 0; r < rows; r++) {
                shift(new Position(r, c), rounds, p -> new Position(p.r, p.c + 1));
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/14"));
        cubes = new HashSet<>();
        Set<Position> rounds = new HashSet<>();
        rows = 0;
        cols = 0;
        while (s.hasNextLine()) {
            String line = s.nextLine();
            int c;
            for (c = 0; c < line.length(); c++) {
                if (line.charAt(c) == '#') {
                    cubes.add(new Position(rows, c));
                } else if (line.charAt(c) == 'O') {
                    rounds.add(new Position(rows, c));
                }
            }
            rows++;
            cols = c;
        }
        for (int r = 0; r < rows; r++) {
            cubes.add(new Position(r, -1));
            cubes.add(new Position(r, cols));
        }
        for (int c = 0; c < cols; c++) {
            cubes.add(new Position(-1, c));
            cubes.add(new Position(rows, c));
        }
        int cycle = 0;
        int goCycles = 1000000000;
        Map<Set<Position>, Integer> roundHistory = new HashMap<>();
        do {
            roundHistory.put(rounds, cycle++);
            cycle(rounds);
        } while (!roundHistory.containsKey(rounds) && cycle <= goCycles);

        int cyclesLeft;
        if (roundHistory.containsKey(rounds)) {
            int cycleTime = cycle - roundHistory.get(rounds);
            cyclesLeft = (goCycles - cycle) % cycleTime;
        } else {
            cyclesLeft = 0;
        }

        for (int i = 0; i < cyclesLeft; i++) {
            cycle(rounds);
        }

        int totalRows = rows;
        long sum = rounds.stream().mapToLong(p -> totalRows - p.r).sum();
        System.out.println(sum);
    }
}
