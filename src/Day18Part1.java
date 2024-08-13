import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Day18Part1 {
    record Pos(int r, int c) {
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/18"));
        Set<Pos> walls = new HashSet<>();
        Pos at = new Pos(0, 0);
        while (s.hasNext()) {
            char direction = s.next().charAt(0);
            int dist = s.nextInt();
            s.next();
            Pos delta = switch (direction) {
                case 'U' -> new Pos(-1, 0);
                case 'D' -> new Pos(1, 0);
                case 'L' -> new Pos(0, -1);
                case 'R' -> new Pos(0, 1);
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            };
            for (int i = 0; i < dist; i++) {
                walls.add(at);
                at = new Pos(at.r + delta.r, at.c + delta.c);
            }
        }
        Pos topLeftCorner = new Pos(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Pos bottomRightCorner = new Pos(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Pos pos : walls) {
            if (pos.r < topLeftCorner.r) {
                topLeftCorner = new Pos(pos.r, topLeftCorner.c);
            }
            if (pos.c < topLeftCorner.c) {
                topLeftCorner = new Pos(topLeftCorner.r, pos.c);
            }
            if (pos.r > bottomRightCorner.r) {
                bottomRightCorner = new Pos(pos.r, bottomRightCorner.c);
            }
            if (pos.c > bottomRightCorner.c) {
                bottomRightCorner = new Pos(bottomRightCorner.r, pos.c);
            }
        }

        Pos start = topLeftCorner;
        outer:
        for (int r = topLeftCorner.r; r <= bottomRightCorner.r; r++) {
            for (int c = topLeftCorner.c; c <= bottomRightCorner.c; c++) {
                if (!walls.contains(new Pos(r, c))) {
                    start = new Pos(r, c);
                    break outer;
                }
            }
        }

        topLeftCorner = new Pos(topLeftCorner.r - 1, topLeftCorner.c - 1);
        bottomRightCorner = new Pos(bottomRightCorner.r + 1, bottomRightCorner.c + 1);

        boolean touchedEdge = false;
        Set<Pos> visited = new HashSet<>();
        LinkedList<Pos> nextTiles = new LinkedList<>();
        nextTiles.add(start);
        do {
            Pos pos = nextTiles.poll();
            int[][] deltas = {
                    {-1, 0},
                    {1, 0},
                    {0, 1},
                    {0, -1}
            };
            for (int[] delta : deltas) {
                Pos newPos = new Pos(pos.r + delta[0], pos.c + delta[1]);
                if (newPos.r >= topLeftCorner.r && newPos.c >= topLeftCorner.c
                        && newPos.r <= bottomRightCorner.r && newPos.c <= bottomRightCorner.c) {
                    if (!visited.contains(newPos) && !walls.contains(newPos)) {
                        nextTiles.add(newPos);
                        visited.add(newPos);
                    }
                } else {
                    touchedEdge = true;
                }
            }
        } while (!nextTiles.isEmpty());

        if (!touchedEdge) {
            System.out.println(walls.size() + visited.size());
        } else {
            int totalArea = (bottomRightCorner.r - topLeftCorner.r + 1) * (bottomRightCorner.c - topLeftCorner.c + 1);
            System.out.println(totalArea - visited.size());
        }
    }
}
