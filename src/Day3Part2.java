import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Day3Part2 {
    static boolean inBounds(int r, int c, int maxRows, int maxCols) {
        return r >= 0 && c >= 0 && r < maxRows && c < maxCols;
    }

    static boolean symbol(char sym) {
        return sym != '.' && !Character.isDigit(sym);
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/3"));
        List<String> schematic = new ArrayList<>();
        while (s.hasNextLine()) {
            schematic.add(s.nextLine());
        }
        Map<Integer, List<Integer>> gears = new HashMap<>();
        for (int r = 0; r < schematic.size(); r++) {
            StringBuilder part = new StringBuilder();
            int partStartCol = -1;
            for (int c = 0; c < schematic.get(r).length() + 1; c++) {
                char character;
                if (c == schematic.get(r).length()) {
                    character = '$';
                } else {
                    character = schematic.get(r).charAt(c);
                }
                if (partStartCol == -1 && c < schematic.get(r).length()) {
                    if (Character.isDigit(character)) {
                        partStartCol = c;
                        part.append(character);
                    }
                } else {
                    if (Character.isDigit(character)) {
                        part.append(character);
                    } else {
                        int[][] neighbors = {
                                {-1, -1},
                                {-1, 0},
                                {-1, 1},
                                {0, 1},
                                {1, 1},
                                {1, 0},
                                {1, -1},
                                {0, -1},
                        };
                        List<Integer> foundGearsRow = new ArrayList<>();
                        List<Integer> foundGearsCol = new ArrayList<>();
                        partsLoop:
                        for (int pc = partStartCol; pc < partStartCol + part.length(); pc++) {
                            for (int[] delta : neighbors) {
                                int nr = r + delta[0];
                                int nc = pc + delta[1];
                                if (inBounds(nr, nc, schematic.size(), schematic.get(r).length())
                                        && symbol(schematic.get(nr).charAt(nc))) {
                                    if (schematic.get(nr).charAt(nc) == '*') {
                                        foundGearsRow.add(nr);
                                        foundGearsCol.add(nc);
                                        break partsLoop;
                                    }
                                }
                            }
                        }
                        for (int i = 0; i < foundGearsCol.size(); i++) {
                            int nr = foundGearsRow.get(i);
                            int nc = foundGearsCol.get(i);
                            int index = nr * schematic.get(nr).length() + nc;
                            List<Integer> connectedParts;
                            if (gears.containsKey(index)) {
                                connectedParts = gears.get(index);
                            } else {
                                connectedParts = new ArrayList<>();
                                gears.put(index, connectedParts);
                            }
                            connectedParts.add(Integer.parseInt(part.toString()));
                        }
                        part.delete(0, part.length());
                        partStartCol = -1;
                    }
                }
            }
        }
        long gearSum = 0;
        for (List<Integer> gearParts : gears.values()) {
            if (gearParts.size() == 2) {
                gearSum += (long) gearParts.get(0) * gearParts.get(1);
            }
        }
        System.out.println(gearSum);
    }
}
