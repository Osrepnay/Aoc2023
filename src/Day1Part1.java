import java.io.*;

public class Day1Part1 {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("inputs/1"));
        int first = -1;
        int last = -1;
        int character;
        long sum = 0;
        while ((character = reader.read()) != -1) {
            if (character == '\n') {
                sum += first * 10L + last;
                first = -1;
                last = -1;
            } else if (Character.isDigit(character)) {
                last = character - '0';
                if (first == -1) {
                    first = character - '0';
                }
            }
        }
        sum += first * 10L + last;
        System.out.println(sum);
        reader.close();
    }
}