import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Day17Part1 {
    static int[][] grid;

    record Coord(int r, int c) {
    }

    enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        Direction opposing() {
            return switch (this) {
                case UP -> DOWN;
                case DOWN -> UP;
                case LEFT -> RIGHT;
                case RIGHT -> LEFT;
            };
        }

        Coord apply(Coord coord) {
            return switch (this) {
                case UP -> new Coord(coord.r - 1, coord.c);
                case DOWN -> new Coord(coord.r + 1, coord.c);
                case LEFT -> new Coord(coord.r, coord.c - 1);
                case RIGHT -> new Coord(coord.r, coord.c + 1);
            };
        }
    }

    record Position(Coord coord, Direction last, int run) {
    }

    record State(int heatLoss, Position pos) implements Comparable<State> {
        @Override
        public int compareTo(State state) {
            return Integer.compare(this.heatLoss, state.heatLoss);
        }
    }

    static int dijkstra(Set<Position> visited, PriorityQueue<State> next) {
        State nextState = next.poll();
        while (nextState.pos.coord.r != grid.length - 1 || nextState.pos.coord.c != grid[0].length - 1) {
            Set<Direction> dirs = Arrays.stream(Direction.values()).collect(Collectors.toCollection(HashSet::new));
            dirs.remove(nextState.pos.last.opposing());
            if (nextState.pos.run == 3) {
                dirs.remove(nextState.pos.last);
            }
            for (Direction d : dirs) {
                Coord newCoord = d.apply(nextState.pos.coord);
                if (newCoord.r < 0 || newCoord.c < 0 || newCoord.r >= grid.length || newCoord.c >= grid[0].length) {
                    continue;
                }
                int newRun = d == nextState.pos.last ? nextState.pos.run + 1 : 1;
                Position newPos = new Position(newCoord, d, newRun);
                if (!visited.contains(newPos)) {
                    visited.add(newPos);
                    next.add(new State(nextState.heatLoss + grid[newCoord.r][newCoord.c], newPos));
                }
            }
            nextState = next.poll();
        }
        return nextState.heatLoss;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/17"));
        List<String> lines = new ArrayList<>();
        while (s.hasNextLine()) {
            lines.add(s.nextLine());
        }
        grid = lines.stream().map(x -> x.chars().map(c -> c - '0').toArray()).toArray(int[][]::new);
        Position startPos = new Position(new Coord(0, 0), Direction.RIGHT, 0);
        Set<Position> visited = new HashSet<>();
        visited.add(startPos);
        PriorityQueue<State> next = new PriorityQueue<>();
        next.add(new State(0, startPos));
        System.out.println(dijkstra(visited, next));
    }
}
