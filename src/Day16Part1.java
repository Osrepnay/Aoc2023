import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Day16Part1 {
    static int maxRows;
    static int maxCols;

    enum Direction {
        UP, RIGHT, DOWN, LEFT;

        private static final Direction[] vals = values();

        Direction clockwise() {
            return vals[(this.ordinal() + 1) % vals.length];
        }

        Direction widdershins() {
            return vals[(this.ordinal() - 1 + vals.length) % vals.length];
        }

        boolean horizontal() {
            return this == RIGHT || this == LEFT;
        }
    }

    enum Type {
        EMPTY, MIRROR, SPLITTER
    }

    record Position(int r, int c) {
        boolean inBounds() {
            return r >= 0 && c >= 0 && r < maxRows && c < maxCols;
        }
    }

    record At(Position pos, Direction heading) {
        At step() {
            return new At(
                    switch (heading) {
                        case UP -> new Position(pos.r() - 1, pos.c());
                        case RIGHT -> new Position(pos.r(), pos.c() + 1);
                        case DOWN -> new Position(pos.r() + 1, pos.c());
                        case LEFT -> new Position(pos.r(), pos.c() - 1);
                    },
                    heading
            );
        }
    }

    record Tile(Direction dir, Type type) {
        List<At> follow(At from) {
            switch (type) {
                case EMPTY:
                    return List.of(from.step());
                case MIRROR:
                    boolean turnClockwise = from.heading().horizontal() ^ dir.horizontal();
                    return List.of(
                            turnClockwise ?
                                    new At(from.pos(), from.heading().clockwise()).step() :
                                    new At(from.pos(), from.heading().widdershins()).step()
                    );
                case SPLITTER:
                    if (from.heading().horizontal() != dir.horizontal()) {
                        return List.of(from.step());
                    } else {
                        return List.of(
                                new At(from.pos(), from.heading().clockwise()).step(),
                                new At(from.pos(), from.heading().widdershins()).step()
                        );
                    }
                default:
                    throw new IllegalStateException("whar?");
            }
        }
    }

    static List<List<Tile>> grid = new ArrayList<>();

    static int energize(At start) {
        Queue<At> ats = new LinkedList<>(List.of(start));
        Set<At> energized = new HashSet<>();
        energized.add(start);
        At at;
        while ((at = ats.poll()) != null) {
            for (At newAt : grid.get(at.pos().r()).get(at.pos().c()).follow(at)) {
                if (newAt.pos().inBounds() && !energized.contains(newAt)) {
                    ats.offer(newAt);
                    energized.add(newAt);
                }
            }
        }
        return energized.stream().map(At::pos).collect(Collectors.toSet()).size();
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/16"));
        maxRows = 0;
        while (s.hasNextLine()) {
            String line = s.nextLine();
            List<Tile> row = new ArrayList<>();
            maxCols = line.length();
            for (char c : line.toCharArray()) {
                row.add(
                        switch (c) {
                            case '.' -> new Tile(Direction.UP, Type.EMPTY);
                            case '/' -> new Tile(Direction.RIGHT, Type.MIRROR);
                            case '\\' -> new Tile(Direction.UP, Type.MIRROR);
                            case '-' -> new Tile(Direction.UP, Type.SPLITTER);
                            case '|' -> new Tile(Direction.RIGHT, Type.SPLITTER);
                            default -> throw new IllegalArgumentException("poopy");
                        }
                );
            }
            grid.add(row);
            maxRows++;
        }
        System.out.println(energize(new At(new Position(0, 0), Direction.RIGHT)));
    }
}
