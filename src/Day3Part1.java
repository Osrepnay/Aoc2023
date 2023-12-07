import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day3Part1 {
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
        long partSum = 0;
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
                        boolean foundSymbol = false;
                        partsLoop:
                        for (int pc = partStartCol; pc < partStartCol + part.length(); pc++) {
                            for (int[] delta : neighbors) {
                                int nr = r + delta[0];
                                int nc = pc + delta[1];
                                if (inBounds(nr, nc, schematic.size(), schematic.get(r).length())
                                        && symbol(schematic.get(nr).charAt(nc))) {
                                    foundSymbol = true;
                                    break partsLoop;
                                }
                            }
                        }
                        if (foundSymbol) {
                            partSum += Integer.parseInt(part.toString());
                        }
                        part.delete(0, part.length());
                        partStartCol = -1;
                    }
                }
            }
        }
        System.out.println(partSum);
    }
}

