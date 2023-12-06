import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day1Part2 {
    static int firstMatch(String str, String[] search) {
        for (int i = 0; i < str.length(); i++) {
            char character = str.charAt(i);
            if (Character.isDigit(character)) {
                return character - '0';
            } else {
                for (int j = 0; j < search.length; j++) {
                    String number = search[j];
                    if (number.length() <= str.length() - i
                            && str.substring(i, i + number.length()).equals(number)) {
                        return j + 1;
                    }
                }
            }
        }
        return -1;
    }
    public static void main(String[] args) throws IOException {
        Scanner reader = new Scanner(new FileReader("inputs/1"));
        String[] numbersForward = new StringBuilder("one two three four five six seven eight nine")
                .toString()
                .split(" ");
        String[] numbersReverse = new StringBuilder("nine eight seven six five four three two one")
                .reverse()
                .toString()
                .split(" ");
        long sum = 0;
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            int first = firstMatch(line, numbersForward);
            int last = firstMatch(new StringBuilder(line).reverse().toString(), numbersReverse);
            sum += first * 10L + last;
        }
        System.out.println(sum);
    }
}