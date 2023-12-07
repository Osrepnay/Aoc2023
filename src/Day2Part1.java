import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Day2Part1 {
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
        int legalSum = 0;
        bigloop:
        while (s.hasNext()) {
            s.next();
            int gameNum = s.nextInt();
            String rounds = s.nextLine();
            int[] reds = red.matcher(rounds).results().mapToInt((x) -> Integer.parseInt(x.group(1))).toArray();
            int[] greens = green.matcher(rounds).results().mapToInt((x) -> Integer.parseInt(x.group(1))).toArray();
            int[] blues = blue.matcher(rounds).results().mapToInt((x) -> Integer.parseInt(x.group(1))).toArray();
            for (int r : reds) {
                if (r > 12) {
                    continue bigloop;
                }
            }
            for (int g : greens) {
                if (g > 13) {
                    continue bigloop;
                }
            }
            for (int b : blues) {
                if (b > 14) {
                    continue bigloop;
                }
            }
            legalSum += gameNum;
        }
        System.out.println(legalSum);
    }
}
