import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Day11Part2 {
    record Coord(int r, int c) {
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/11"));
        List<String> space = new ArrayList<>();
        while (s.hasNextLine()) space.add(s.nextLine());
        List<Integer> emptyCols = new LinkedList<>();
        cols:
        for (int c = 0; c < space.get(0).length(); c++) {
            for (int r = 0; r < space.size(); r++) {
                if (space.get(r).charAt(c) == '#') continue cols;
            }
            emptyCols.add(c);
        }
        List<Coord> galaga = new ArrayList<>();
        int rowOffset = 0;
        for (int r = 0; r < space.size(); r++) {
            int colOffset = 0;
            boolean foundGalaga = false;
            for (int c = 0; c < space.get(r).length(); c++) {
                if (colOffset < emptyCols.size() && emptyCols.get(colOffset) == c) {
                    colOffset++;
                } else if (space.get(r).charAt(c) == '#') {
                    foundGalaga = true;
                    galaga.add(new Coord(r + rowOffset * (1000000 - 1), c + colOffset * (1000000 - 1)));
                }
            }
            if (!foundGalaga) rowOffset++;
        }
        long pathSum = 0;
        for (int i = 0; i < galaga.size() - 1; i++) {
            for (int j = i + 1; j < galaga.size(); j++) {
                Coord galaga1 = galaga.get(i);
                Coord galaga2 = galaga.get(j);
                pathSum += Math.abs(galaga2.r() - galaga1.r()) + Math.abs(galaga2.c() - galaga1.c());
            }
        }
        System.out.println(pathSum);
    }
}
