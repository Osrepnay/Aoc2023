import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Day14Part1 {
    record Position(int r, int c) {
        Position lower() {
            return new Position(r + 1, c);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/14"));
        List<List<Position>> limits = new ArrayList<>();
        List<Position> rounds = new ArrayList<>();
        int r = 0;
        while (s.hasNextLine()) {
            String line = s.nextLine();
            if (limits.isEmpty()) {
                limits.addAll(Stream.generate(() -> new LinkedList<Position>()).limit(line.length()).toList());
            }
            for (int c = 0; c < line.length(); c++) {
                if (line.charAt(c) == '#') {
                    limits.get(c).addFirst(new Position(r, c));
                } else if (line.charAt(c) == 'O') {
                    rounds.add(new Position(r, c));
                }
            }
            r++;
        }
        for (int c = 0; c < limits.size(); c++) {
            limits.get(c).add(new Position(-1, c));
        }
        for (int i = 0; i < rounds.size(); i++) {
            Position round = rounds.get(i);
            for (int j = 0; j < limits.get(round.c).size(); j++) {
                Position limit = limits.get(round.c).get(j);
                if (round.r > limit.r) {
                    limits.get(round.c).set(j, limit.lower());
                    rounds.set(i, limit.lower());
                    break;
                }
            }
        }
        int finalR = r;
        long sum = rounds.stream().mapToLong(p -> finalR - p.r).sum();
        System.out.println(sum);
    }
}
