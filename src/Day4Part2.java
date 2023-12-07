import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class Day4Part2 {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/4"));
        List<BigInteger> cardsGenerated = new ArrayList<>();
        while (s.hasNext()) {
            s.next();
            s.next();
            String[] numbers = s.nextLine().split("\\|");
            Set<Integer> winning = Arrays.stream(numbers[0].trim().split("\\s+"))
                    .map((x) -> Integer.parseInt(x))
                    .collect(Collectors.toSet());
            Set<Integer> mine = Arrays.stream(numbers[1].trim().split("\\s+"))
                    .map((x) -> Integer.parseInt(x))
                    .collect(Collectors.toSet());
            mine.retainAll(winning);
            cardsGenerated.add(BigInteger.valueOf(mine.size()));
        }
        for (int i = cardsGenerated.size() - 1; i >= 0; i--) {
            BigInteger newGen = BigInteger.ZERO;
            for (int j = i + 1; j < cardsGenerated.size() && j < i + 1 + cardsGenerated.get(i).intValue(); j++) {
                newGen = newGen.add(cardsGenerated.get(j));
            }
            cardsGenerated.set(i, newGen.add(BigInteger.ONE));
        }
        BigInteger sum = BigInteger.ZERO;
        for (BigInteger x : cardsGenerated) {
            sum = sum.add(x);
        }
        System.out.println(sum);
    }
}
