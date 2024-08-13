import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19Part1 {
    record Part(int x, int m, int a, int s) {
        public int byLetter(char letter) {
            return switch (letter) {
                case 'x' -> x;
                case 'm' -> m;
                case 'a' -> a;
                case 's' -> s;
                default -> throw new IllegalArgumentException("illegal letter " + letter);
            };
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/19"));
        String workflow = s.nextLine();
        Map<String, List<Function<Part, Optional<String>>>> workflows = new HashMap<>();
        while (!workflow.isEmpty()) {
            int cursor = 0;
            while (workflow.charAt(cursor) != '{') {
                cursor++;
            }
            String workflowName = workflow.substring(0, cursor);
            String[] ruleStrings = workflow.substring(cursor + 1, workflow.length() - 1).split(",");

            List<Function<Part, Optional<String>>> rules = new ArrayList<>();
            Pattern less = Pattern.compile("(\\w+)<(\\d+):(\\w+)");
            Pattern more = Pattern.compile("(\\w+)>(\\d+):(\\w+)");
            Pattern send = Pattern.compile("(\\w+)");
            for (String rule : ruleStrings) {
                Matcher lessMatcher = less.matcher(rule);
                if (lessMatcher.matches()) {
                    char letter = lessMatcher.group(1).charAt(0);
                    int bound = Integer.parseInt(lessMatcher.group(2));
                    String target = lessMatcher.group(3);
                    rules.add(p -> p.byLetter(letter) < bound ? Optional.of(target) : Optional.empty());
                    continue;
                }
                Matcher moreMatcher = more.matcher(rule);
                if (moreMatcher.matches()) {
                    char letter = moreMatcher.group(1).charAt(0);
                    int bound = Integer.parseInt(moreMatcher.group(2));
                    String target = moreMatcher.group(3);
                    rules.add(p -> p.byLetter(letter) > bound ? Optional.of(target) : Optional.empty());
                    continue;
                }
                Matcher sendMatcher = send.matcher(rule);
                if (sendMatcher.matches()) {
                    rules.add(_ -> Optional.of(sendMatcher.group(1)));
                } else {
                    throw new IllegalStateException("bad rule " + rule);
                }
            }
            workflows.put(workflowName, rules);
            workflow = s.nextLine();
        }
        s.useDelimiter("\\D+");
        int sum = 0;
        while (s.hasNext()) {
            Part part = new Part(s.nextInt(), s.nextInt(), s.nextInt(), s.nextInt());
            String currWorkflow = "in";
            while (!currWorkflow.equals("R") && !currWorkflow.equals("A")) {
                List<Function<Part, Optional<String>>> workflowRules = workflows.get(currWorkflow);
                for (Function<Part, Optional<String>> rule : workflowRules) {
                    Optional<String> result = rule.apply(part);
                    if (result.isPresent()) {
                        currWorkflow = result.get();
                        break;
                    }
                }
            }
            if (currWorkflow.equals("A")) {
                sum += part.x + part.m + part.a + part.s;
            }
        }
        System.out.println(sum);
    }
}
