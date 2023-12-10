import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day10Part2 {
    record Coord(int r, int c) {
        Coord apply(int[] delta) {
            return new Coord(r + delta[0], c + delta[1]);
        }
    }

    static int[][] diffs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    static boolean flood(Coord bounds, Coord at, Set<Coord> visited) {
        if (visited.contains(at)) return true;
        visited.add(at);
        for (int[] diff : diffs) {
            Coord newAt = at.apply(diff);
            if (newAt.r >= 0 && newAt.c >= 0 && newAt.r < bounds.r && newAt.c < bounds.c) {
                boolean in = flood(bounds, newAt, visited);
                if (!in) return false;
            } else return false;
        }
        return true;
    }

    static Stream<Coord> fatten(Coord c, List<List<Character>> grid) {
        Coord nc = new Coord(c.r * 2 + 1, c.c * 2 + 1);
        return Stream.concat(
                Stream.of(nc),
                switch (grid.get(c.r).get(c.c)) {
                    case '|', '7' -> Stream.of(nc.apply(new int[]{1, 0}));
                    case '-', 'L' -> Stream.of(nc.apply(new int[]{0, 1}));
                    case 'J' -> Stream.<Coord>empty();
                    case 'F', 'S' -> Stream.of(nc.apply(new int[]{1, 0}), nc.apply(new int[]{0, 1}));
                    default -> throw new IllegalStateException("fattening non-pipe " + grid.get(c.r).get(c.c));
                }
        );
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/10"));
        List<List<Character>> grid = new ArrayList<>();
        while (s.hasNextLine()) {
            grid.add(s.nextLine().chars().mapToObj(x -> (char) x).toList());
        }
        Map<Coord, List<Coord>> conns = new HashMap<>();
        Coord sCoord = null;
        for (int r = 0; r < grid.size(); r++) {
            for (int c = 0; c < grid.get(r).size(); c++) {
                List<Coord> connectedTo = new ArrayList<>();
                switch (grid.get(r).get(c)) {
                    case '.':
                        break;
                    case '|':
                        connectedTo.add(new Coord(r - 1, c));
                        connectedTo.add(new Coord(r + 1, c));
                        break;
                    case '-':
                        connectedTo.add(new Coord(r, c - 1));
                        connectedTo.add(new Coord(r, c + 1));
                        break;
                    case 'L':
                        connectedTo.add(new Coord(r - 1, c));
                        connectedTo.add(new Coord(r, c + 1));
                        break;
                    case 'J':
                        connectedTo.add(new Coord(r - 1, c));
                        connectedTo.add(new Coord(r, c - 1));
                        break;
                    case '7':
                        connectedTo.add(new Coord(r, c - 1));
                        connectedTo.add(new Coord(r + 1, c));
                        break;
                    case 'F':
                        connectedTo.add(new Coord(r, c + 1));
                        connectedTo.add(new Coord(r + 1, c));
                        break;
                    case 'S':
                        String[] acceptableConns = {"|7F", "|LJ", "-LF", "-7J"};
                        for (int i = 0; i < diffs.length; i++) {
                            int[] diff = diffs[i];
                            String acc = acceptableConns[i];
                            int nr = r + diff[0];
                            int nc = c + diff[1];
                            if (nr >= 0 && nc >= 0 && nr < grid.size() && nc < grid.get(r).size() && acc.indexOf(grid.get(nr).get(nc)) != -1) {
                                connectedTo.add(new Coord(nr, nc));
                            }
                        }
                        sCoord = new Coord(r, c);
                        break;
                }
                conns.put(new Coord(r, c), connectedTo);
            }
        }
        List<Coord> at = new ArrayList<>(conns.get(sCoord));
        List<Coord> previousAts = new ArrayList<>(Collections.nCopies(2, sCoord));
        Set<Coord> loop = new HashSet<>();
        loop.add(sCoord);
        loop.addAll(at);
        do {
            List<Coord> old = new ArrayList<>(at);
            for (int i = 0; i < at.size(); i++) {
                Coord dont = previousAts.get(i);
                at.set(
                        i,
                        conns.get(at.get(i))
                                .stream()
                                .filter(c -> !c.equals(dont))
                                .findFirst()
                                .get()
                );
            }
            loop.addAll(at);
            previousAts = old;
        } while (!at.get(0).equals(at.get(1)));
        loop = loop.stream()
                .flatMap(c -> fatten(c, grid))
                .collect(Collectors.toSet());

        Set<Coord> visited = new HashSet<>(loop);
        Coord bounds = new Coord(grid.size() * 2 + 1, grid.get(0).size() * 2 + 1);
        search:
        for (Coord c : loop) {
            for (int[] diff : diffs) {
                Coord f = c.apply(diff);
                Set<Coord> newVisited = new HashSet<>(visited);
                if (flood(bounds, f, newVisited)) {
                    if (visited.size() != newVisited.size()) {
                        visited = newVisited;
                        break search;
                    }
                    visited = newVisited;
                }
            }
        }
        visited.removeAll(loop);
        System.out.println(visited.stream().filter(c -> c.r % 2 == 1 && c.c % 2 == 1).count());
    }
}
