import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Day10Part1 {
    record Coord(int r, int c) {
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
                        int[][] diffs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                        String[] acceptableConns = {"|7F", "|LJ", "-LF", "-7J"};
                        for (int i = 0; i < diffs.length; i++) {
                            int[] diff = diffs[i];
                            String acc = acceptableConns[i];
                            int nr = r + diff[0];
                            int nc = c + diff[1];
                            if (nr >= 0 && nc >= 0 && nr < grid.size() && nc < grid.get(c).size() && acc.indexOf(grid.get(nr).get(nc)) != -1) {
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
        int len = 1;
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
            previousAts = old;
            len++;
        } while (!at.get(0).equals(at.get(1)));
        System.out.println(len);
    }
}
