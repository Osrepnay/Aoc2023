import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Day18Part2 {
    enum Axis {
        X, Y;

        public Axis other() {
            return this == X ? Y : X;
        }
    }

    record Pos(long r, long c) {
        public long get(Axis axis) {
            return axis == Axis.X ? c : r;
        }
    }

    enum Direction {
        UP(new Pos(-1, 0)),
        RIGHT(new Pos(0, 1)),
        DOWN(new Pos(1, 0)),
        LEFT(new Pos(0, -1));
        final Pos delta;

        Direction(Pos delta) {
            this.delta = delta;
        }

        public int difference(Direction otherDir) {
            int result = this.ordinal() - otherDir.ordinal();
            if (result >= 3) {
                return result - 4;
            } else if (result <= -3) {
                return result + 4;
            } else {
                return result;
            }
        }

        public Direction applyDifference(int diff) {
            Direction[] values = Direction.values();
            int newIdx = (this.ordinal() + diff) % values.length;
            if (newIdx < 0) {
                newIdx += values.length;
            }
            return values[newIdx];
        }

        public Axis axis() {
            return this == UP || this == DOWN ? Axis.Y : Axis.X;
        }
    }

    record Range(long begin, long end) {
        public static Range sorted(long begin, long end) {
            if (begin > end) {
                return new Range(end, begin);
            } else {
                return new Range(begin, end);
            }
        }

        public boolean contains(long x) {
            return x >= begin && x <= end;
        }

        public boolean overlaps(Range other) {
            if (other.begin < this.begin) {
                return other.overlaps(this);
            }
            return this.contains(other.begin);
        }

        public Optional<Range> intersection(Range other) {
            if (!this.overlaps(other)) {
                return Optional.empty();
            }
            if (other.begin < this.begin) {
                return other.intersection(this);
            }
            if (this.contains(other.end)) {
                return Optional.of(other);
            } else {
                return Optional.of(new Range(other.begin, this.end));
            }
        }

        public long length() {
            return end - begin + 1;
        }
    }

    record Border(Axis axis, long coord, Range range) {
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/18"));
        List<Border> borders = new ArrayList<>();
        List<Direction> borderDirection = new ArrayList<>();
        Pos at = new Pos(0, 0);
        while (s.hasNext()) {
            s.next();
            s.next();
            String hex = s.next().substring(2, 8);
            Direction direction = switch (hex.charAt(5)) {
                case '0' -> Direction.RIGHT;
                case '1' -> Direction.DOWN;
                case '2' -> Direction.LEFT;
                case '3' -> Direction.UP;
                default -> throw new IllegalStateException("Unexpected value: " + hex.charAt(5));
            };
            Axis axis = direction.axis();
            long dist = Long.parseLong(hex.substring(0, 5), 16);
            Pos newAt = new Pos(at.r + direction.delta.r * dist, at.c + direction.delta.c * dist);
            borders.add(new Border(axis, at.get(axis.other()), Range.sorted(at.get(axis), newAt.get(axis))));
            borderDirection.add(direction);
            at = newAt;
        }

        Map<Border, Direction> inwardFace = new HashMap<>();
        int setIdx = -1;
        outer:
        for (int i = 0; i < borders.size(); i++) {
            Border border = borders.get(i);
            boolean intersectsGreater = false;
            boolean intersectsLesser = false;
            for (Border potentialBlock : borders) {
                if (intersectsGreater && intersectsLesser) {
                    continue outer;
                }
                if (potentialBlock == border) {
                    continue;
                }
                if (border.axis == potentialBlock.axis && border.range.overlaps(potentialBlock.range)) {
                    if (border.coord < potentialBlock.coord) {
                        intersectsGreater = true;
                    } else {
                        intersectsLesser = true;
                    }
                }
            }
            setIdx = i;
            if (!intersectsGreater) {
                inwardFace.put(border, border.axis == Axis.X ? Direction.UP : Direction.LEFT);
            } else if (!intersectsLesser) {
                inwardFace.put(border, border.axis == Axis.X ? Direction.DOWN : Direction.RIGHT);
            } else {
                continue;
            }
            break;
        }

        for (int i = setIdx + 1; i < borders.size(); i++) {
            Border prev = borders.get(i - 1);
            Border border = borders.get(i);
            inwardFace.put(
                    border,
                    inwardFace.get(prev)
                            .applyDifference(borderDirection.get(i).difference(borderDirection.get(i - 1)))
            );
        }
        for (int i = setIdx - 1; i >= 0; i--) {
            Border prev = borders.get(i + 1);
            Border border = borders.get(i);
            inwardFace.put(
                    border,
                    inwardFace.get(prev)
                            .applyDifference(borderDirection.get(i).difference(borderDirection.get(i + 1)))
            );
        }

        long area = 0;
        while (borders.size() != 4) {
            for (int i = 0; i < borderDirection.size() - 2; i++) {
                int diff1 = borderDirection.get(i + 1).difference(borderDirection.get(i));
                int diff2 = borderDirection.get(i + 2).difference(borderDirection.get(i + 1));
                if (diff1 == diff2 && diff1 != 0) {
                    Border endBorder = borders.get(i + 1);
                    Border leftBorder = borders.get(i);
                    Border rightBorder = borders.get(i + 2);
                    Direction moveDirection = borderDirection.get(i + 2);

                    long dist1 = borders.get(i).range.length() - 1;
                    long dist2 = borders.get(i + 2).range.length() - 1;
                    long collapseDist = Math.min(dist1, dist2);
                    for (Border block : borders) {
                        if (block == endBorder || block == leftBorder || block == rightBorder) {
                            continue;
                        }
                        long dist = collapseDist;
                        if (block.axis == endBorder.axis) {
                            long intersectionLen = endBorder
                                    .range
                                    .intersection(block.range)
                                    .map(Range::length)
                                    .orElse(0L);
                            if (intersectionLen > 1) {
                                if (moveDirection == Direction.DOWN || moveDirection == Direction.RIGHT) {
                                    dist = block.coord - endBorder.coord - 1;
                                } else {
                                    dist = endBorder.coord - block.coord - 1;
                                }
                            }
                        }
                        if (dist >= 0) {
                            collapseDist = Math.min(collapseDist, dist);
                        }
                    }
                    if (collapseDist == 0) {
                        continue;
                    }

                    long leftStationaryPoint = leftBorder.range.begin == endBorder.coord
                            ? leftBorder.range.end
                            : leftBorder.range.begin;
                    long rightStationaryPoint = rightBorder.range.begin == endBorder.coord
                            ? rightBorder.range.end
                            : rightBorder.range.begin;

                    long newCoord = moveDirection == Direction.DOWN || moveDirection == Direction.RIGHT
                            ? endBorder.coord + collapseDist
                            : endBorder.coord - collapseDist;

                    Border newEnd = new Border(endBorder.axis, newCoord, endBorder.range);
                    Border newLeft = new Border(
                            leftBorder.axis,
                            leftBorder.coord,
                            Range.sorted(leftStationaryPoint, newCoord)
                    );
                    Border newRight = new Border(
                            rightBorder.axis,
                            rightBorder.coord,
                            Range.sorted(rightStationaryPoint, newCoord)
                    );
                    borders.set(i + 1, newEnd);
                    inwardFace.put(newEnd, inwardFace.get(endBorder));
                    inwardFace.remove(endBorder);
                    borders.set(i, newLeft);
                    inwardFace.put(newLeft, inwardFace.get(leftBorder));
                    inwardFace.remove(leftBorder);
                    borders.set(i + 2, newRight);
                    inwardFace.put(newRight, inwardFace.get(rightBorder));
                    inwardFace.remove(rightBorder);

                    if (newRight.range.length() <= 1) {
                        borders.remove(i + 2);
                        borderDirection.remove(i + 2);
                        inwardFace.remove(newRight);
                    }
                    if (borders.get(i).range.length() <= 1) {
                        borders.remove(i);
                        borderDirection.remove(i);
                        inwardFace.remove(newLeft);
                    }

                    if (moveDirection == inwardFace.get(newEnd)) {
                        area += endBorder.range.length() * collapseDist;
                    } else {
                        area -= (endBorder.range.length() - 2) * collapseDist;
                    }
                    break;
                }
            }
            for (int i = 0; i < borders.size() - 1; i++) {
                Border borderA = borders.get(i);
                Border borderB = borders.get(i + 1);
                if (borderA.axis == borderB.axis) {
                    List<Long> points = new ArrayList<>(
                            List.of(borderA.range.begin, borderA.range.end, borderB.range.begin, borderB.range.end)
                    );
                    Collections.sort(points);
                    borders.set(
                            i,
                            new Border(borderA.axis, borderA.coord, Range.sorted(points.getFirst(), points.getLast()))
                    );
                    borders.remove(i + 1);
                    borderDirection.remove(i + 1);
                    inwardFace.put(borders.get(i), inwardFace.get(borderA));
                    inwardFace.remove(borderA);
                    inwardFace.remove(borderB);
                    i--;
                }
            }
        }
        System.out.println(area + borders.get(0).range.length() * borders.get(1).range.length());
    }
}
