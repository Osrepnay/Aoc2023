import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day13Part1 {
    static boolean isReflectHorz(List<List<Character>> grid, int r) {
        for (int ru = r - 1, rd = r; ru >= 0 && rd < grid.size(); ru--, rd++) {
            if (!grid.get(ru).equals(grid.get(rd))) return false;
        }
        return true;
    }

    static int reflectHorz(List<List<Character>> grid) {
        for (int r = 0; r < grid.size() - 1; r++) {
            if (grid.get(r).equals(grid.get(r + 1)) && isReflectHorz(grid, r + 1)) {
                return r + 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/13"));
        long sum = 0;
        while (s.hasNextLine()) {
            List<List<Character>> grid = new ArrayList<>();
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.isEmpty()) break;
                grid.add(line.chars().mapToObj(x -> (char) x).collect(Collectors.toList()));
            }
            int horz = reflectHorz(grid);
            if (horz != -1) {
                sum += horz * 100L;
            } else {
                List<List<Character>> transposed = new ArrayList<>();
                for (int c = 0; c < grid.get(0).size(); c++) {
                    List<Character> collect = new ArrayList<>();
                    for (List<Character> rows : grid) {
                        collect.add(rows.get(c));
                    }
                    transposed.add(collect);
                }
                int vert = reflectHorz(transposed);
                sum += vert;
            }
        }
        System.out.println(sum);
    }
}
