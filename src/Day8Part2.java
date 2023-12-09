import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.*;

public class Day8Part2 {

    record Crossroads(int left, int right) {
        int follow(char direction) {
            return switch (direction) {
                case 'L' -> left;
                case 'R' -> right;
                default -> throw new IllegalArgumentException("Invalid direction: " + direction);
            };
        }
    }

    record State(int placeIdx, int directionsIdx) {
    }

    record Loop(int startPlaceIdx, int period) {
    }

    static BigInteger gcd(BigInteger a, BigInteger b) {
        if (b.compareTo(a) < 0) return gcd(b, a);
        BigInteger mod = b.mod(a);
        if (mod.equals(BigInteger.ZERO)) return a;
        else return gcd(a, mod);
    }

    static BigInteger lcm(BigInteger a, BigInteger b) {
        BigInteger gcd = gcd(a, b);
        return a.multiply(b).divide(gcd);
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/8"));
        String directions = s.nextLine();
        s.nextLine();
        List<String> places = new ArrayList<>();
        Set<Integer> zs = new HashSet<>();
        Map<Integer, Crossroads> mappings = new HashMap<>();
        List<Integer> aes = new ArrayList<>();
        int idx = 0;
        List<String> lines = new ArrayList<>();
        while (s.hasNextLine()) lines.add(s.nextLine());
        for (String line : lines) places.add(line.substring(0, 3));
        for (String line : lines) {
            String[] mappingBits = line.split(" = ");
            String src = mappingBits[0];
            String[] dirs = mappingBits[1].substring(1, mappingBits[1].length() - 1).split(", ");
            mappings.put(places.indexOf(src), new Crossroads(places.indexOf(dirs[0]), places.indexOf(dirs[1])));
            if (src.charAt(2) == 'A') {
                aes.add(idx);
            } else if (src.charAt(2) == 'Z') {
                zs.add(idx);
            }
            idx++;
        }
        List<List<Integer>> zEncounters = new ArrayList<>();
        List<Loop> loops = new ArrayList<>();
        List<Map<State, Integer>> visited = new ArrayList<>();
        for (int i = 0; i < aes.size(); i++) {
            zEncounters.add(new ArrayList<>());
            visited.add(new HashMap<>());
            loops.add(null);
        }
        int dirIdx = 0;
        while (!loops.stream().allMatch(Objects::nonNull)) {
            for (int i = 0; i < aes.size(); i++) {
                if (loops.get(i) != null) continue;
                int currPos = aes.get(i);
                State currState = new State(currPos, dirIdx % directions.length());
                if (visited.get(i).containsKey(currState)) {
                    System.out.println("loop " + (dirIdx - visited.get(i).get(currState)));
                    System.out.println(dirIdx + " " + visited.get(i).get(currState));
                    System.out.println(currState);

                    loops.set(i, new Loop(visited.get(i).get(currState), dirIdx - visited.get(i).get(currState)));
                } else {
                    visited.get(i).put(currState, dirIdx);
                    if (zs.contains(currPos)) zEncounters.get(i).add(dirIdx);
                    int newPos = mappings.get(currPos).follow(directions.charAt(dirIdx % directions.length()));
                    aes.set(i, newPos);
                }
            }
            dirIdx++;
        }
        // TODO decheesify
        BigInteger l = BigInteger.ONE;
        for (int z : zEncounters.stream().map(x -> x.get(0)).toList()) {
            l = lcm(l, BigInteger.valueOf(z));
        }
        System.out.println(l);
    }
}
