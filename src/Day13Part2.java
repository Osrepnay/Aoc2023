import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day13Part2 {
    static boolean isReflectHorz(List<List<Boolean>> grid, int r) {
        for (int ru = r - 1, rd = r; ru >= 0 && rd < grid.size(); ru--, rd++) {
            if (!grid.get(ru).equals(grid.get(rd))) return false;
        }
        return true;
    }

    static int reflectHorz(List<List<Boolean>> grid, int dontcheck) {
        for (int r = 0; r < grid.size() - 1; r++) {
            if (grid.get(r).equals(grid.get(r + 1)) && r + 1 != dontcheck && isReflectHorz(grid, r + 1)) {
                return r + 1;
            }
        }
        return -1;
    }

    record Match(int i, boolean horz) {
    }

    static Match reflectAll(List<List<Boolean>> grid, List<List<Boolean>> transposed, Match dontcheck) {
        int horz = reflectHorz(grid, dontcheck != null && dontcheck.horz() ? dontcheck.i() : -1);
        if (horz != -1) {
            return new Match(horz, true);
        } else {
            int vert = reflectHorz(transposed, dontcheck != null && !dontcheck.horz() ? dontcheck.i() : -1);
            if (vert != -1) {
                return new Match(vert, false);
            }
        }
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/13"));
        long sum = 0;
        tileLoop:
        while (s.hasNextLine()) {
            List<List<Boolean>> grid = new ArrayList<>();
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.isEmpty()) break;
                grid.add(line.chars().mapToObj(x -> (char) x == '#').collect(Collectors.toList()));
            }
            List<List<Boolean>> transposed = new ArrayList<>();
            for (int c = 0; c < grid.get(0).size(); c++) {
                List<Boolean> collect = new ArrayList<>();
                for (List<Boolean> rows : grid) {
                    collect.add(rows.get(c));
                }
                transposed.add(collect);
            }
            Match reflectDefault = reflectAll(grid, transposed, null);
            for (int rc = 0; rc < grid.size(); rc++) {
                for (int cc = 0; cc < grid.getFirst().size(); cc++) {
                    grid.get(rc).set(cc, !grid.get(rc).get(cc));
                    transposed.get(cc).set(rc, !transposed.get(cc).get(rc));

                    Match reflected = reflectAll(grid, transposed, reflectDefault);
                    if (reflected != null) {
                        sum += (long) reflected.i() * (reflected.horz() ? 100 : 1);
                        continue tileLoop;
                    }

                    grid.get(rc).set(cc, !grid.get(rc).get(cc));
                    transposed.get(cc).set(rc, !transposed.get(cc).get(rc));
                }
            }
            throw new IllegalStateException("no reflect");
        }
        System.out.println(sum);
    }
}
