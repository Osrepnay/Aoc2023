import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Day2Part2 {
    public static void main(String[] args) {
        Scanner s;
        try {
            s = new Scanner(new File("inputs/2")).useDelimiter("[ :]+");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Pattern red = Pattern.compile("(\\d+) red");
        Pattern green = Pattern.compile("(\\d+) green");
        Pattern blue = Pattern.compile("(\\d+) blue");
        int powerSum = 0;
        while (s.hasNext()) {
            s.next();
            String rounds = s.nextLine();
            int[] reds = red.matcher(rounds).results().mapToInt((x) -> Integer.parseInt(x.group(1))).toArray();
            int[] greens = green.matcher(rounds).results().mapToInt((x) -> Integer.parseInt(x.group(1))).toArray();
            int[] blues = blue.matcher(rounds).results().mapToInt((x) -> Integer.parseInt(x.group(1))).toArray();
            int redPower = Integer.MIN_VALUE;
            int greenPower = Integer.MIN_VALUE;
            int bluePower = Integer.MIN_VALUE;
            for (int r : reds) {
                if (r > redPower) redPower = r;
            }
            for (int g : greens) {
                if (g > greenPower) greenPower = g;
            }
            for (int b : blues) {
                if (b > bluePower) bluePower = b;
            }
            powerSum += redPower * bluePower * greenPower;
        }
        System.out.println(powerSum);
    }
}
