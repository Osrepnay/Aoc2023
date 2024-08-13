import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day19Part2 {
    record Bounds(int lower, int upper) {
        public Optional<Bounds> intersection(Bounds bounds) {
            if (bounds.lower < this.lower) {
                return bounds.intersection(this);
            }
            if (bounds.upper < this.upper) {
                return Optional.of(bounds);
            }
            if (this.upper > bounds.lower) {
                return Optional.of(new Bounds(bounds.lower, this.upper));
            }
            return Optional.empty();
        }

        public List<Bounds> difference(Bounds bounds) {
            return this.intersection(bounds)
                    .map(intersect ->
                            {
                                if (intersect.equals(bounds)) {
                                    return Stream.of(
                                            new Bounds(this.lower, bounds.lower - 1),
                                            new Bounds(bounds.upper + 1, this.upper)
                                    );
                                } else if (intersect.equals(this)) {
                                    return Stream.<Bounds>empty();
                                } else if (this.lower < bounds.lower) {
                                    return Stream.of(new Bounds(this.lower, bounds.lower - 1));
                                } else {
                                    return Stream.of(new Bounds(bounds.upper + 1, this.upper));
                                }
                            }
                    )
                    .orElse(Stream.of(this))
                    .filter(b -> b.length() > 0)
                    .toList();
        }

        public int length() {
            return upper - lower + 1;
        }
    }

    enum ActionType {
        REJECT, ACCEPT, TO_RULE;
    }

    record Action(ActionType type, Rule target) {
        public static final Action REJECT = new Action(ActionType.REJECT, null);
        public static final Action ACCEPT = new Action(ActionType.ACCEPT, null);

        public static Action send(Rule target) {
            return new Action(ActionType.TO_RULE, target);
        }
    }

    static class Rule {
        char letter;
        Bounds validBounds;
        Action success;
        Action fail;

        public static Rule accept() {
            return new Rule('x', new Bounds(1, 4000), Action.ACCEPT, null);
        }

        public static Rule reject() {
            return new Rule('x', new Bounds(1, 4000), Action.REJECT, null);
        }

        public Rule(char letter, Bounds validBounds, Action success, Action fail) {
            this.letter = letter;
            this.validBounds = validBounds;
            this.success = success;
            this.fail = fail;
        }

        public Rule(Rule rule) {
            this.letter = rule.letter;
            this.validBounds = rule.validBounds;
            this.success = rule.success;
            this.fail = rule.fail;
        }

        @Override
        public String toString() {
            return String.format("Rule[validBounds = %s, success = %s, fail = %s]", validBounds, success, fail);
        }
    }

    record BasicRule(char letter, Bounds validBounds, String success) {
    }

    record PartBounds(Bounds x, Bounds m, Bounds a, Bounds s) {
        public Bounds byLetter(char letter) {
            return switch (letter) {
                case 'x' -> x;
                case 'm' -> m;
                case 'a' -> a;
                case 's' -> s;
                default -> throw new IllegalArgumentException("illegal letter " + letter);
            };
        }

        public PartBounds replaceLetter(char letter, Bounds replacement) {
            return switch (letter) {
                case 'x' -> new PartBounds(replacement, m, a, s);
                case 'm' -> new PartBounds(x, replacement, a, s);
                case 'a' -> new PartBounds(x, m, replacement, s);
                case 's' -> new PartBounds(x, m, a, replacement);
                default -> throw new IllegalArgumentException("illegal letter " + letter);
            };
        }
    }

    static long ruleWalk(Rule rule, PartBounds bounds) {
        Optional<Bounds> mergedBounds = bounds.byLetter(rule.letter).intersection(rule.validBounds);
        if (mergedBounds.isPresent()) {
            List<Bounds> boundedFailing = bounds.byLetter(rule.letter).difference(rule.validBounds);
            return doAction(rule.success, bounds.replaceLetter(rule.letter, mergedBounds.get())) +
                    boundedFailing
                            .stream()
                            .mapToLong(b -> doAction(rule.fail, bounds.replaceLetter(rule.letter, b)))
                            .sum();
        } else {
            return doAction(rule.fail, bounds);
        }
    }

    static long doAction(Action action, PartBounds bounds) {
        return switch (action.type) {
            case ACCEPT -> (long) bounds.x.length() * bounds.m.length() * bounds.a.length() * bounds.s.length();
            case REJECT -> 0;
            case TO_RULE -> ruleWalk(action.target, bounds);
        };
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/19"));
        String workflow = s.nextLine();
        Map<String, List<BasicRule>> workflows = new HashMap<>();
        while (!workflow.isEmpty()) {
            int cursor = 0;
            while (workflow.charAt(cursor) != '{') {
                cursor++;
            }
            String workflowName = workflow.substring(0, cursor);
            String[] ruleStrings = workflow.substring(cursor + 1, workflow.length() - 1).split(",");

            List<BasicRule> rules = new ArrayList<>();
            Pattern less = Pattern.compile("(\\w+)<(\\d+):(\\w+)");
            Pattern more = Pattern.compile("(\\w+)>(\\d+):(\\w+)");
            Pattern send = Pattern.compile("(\\w+)");
            for (String rule : ruleStrings) {
                Matcher lessMatcher = less.matcher(rule);
                if (lessMatcher.matches()) {
                    char letter = lessMatcher.group(1).charAt(0);
                    int bound = Integer.parseInt(lessMatcher.group(2));
                    String target = lessMatcher.group(3);
                    rules.add(new BasicRule(letter, new Bounds(1, bound - 1), target));
                    continue;
                }
                Matcher moreMatcher = more.matcher(rule);
                if (moreMatcher.matches()) {
                    char letter = moreMatcher.group(1).charAt(0);
                    int bound = Integer.parseInt(moreMatcher.group(2));
                    String target = moreMatcher.group(3);
                    rules.add(new BasicRule(letter, new Bounds(bound + 1, 4000), target));
                    continue;
                }
                Matcher sendMatcher = send.matcher(rule);
                if (sendMatcher.matches()) {
                    rules.add(new BasicRule('x', new Bounds(1, 4000), sendMatcher.group(1)));
                } else {
                    throw new IllegalStateException("bad rule " + rule);
                }
            }
            workflows.put(workflowName, rules);
            workflow = s.nextLine();
        }

        Map<String, Rule> workflowRules = new HashMap<>();
        for (String workflowName : workflows.keySet()) {
            workflowRules.put(workflowName, new Rule('\0', null, null, null));
        }
        Function<String, Rule> strToRule = str ->
                switch (str) {
                    case "R" -> Rule.reject();
                    case "A" -> Rule.accept();
                    default -> workflowRules.get(str);
                };
        for (String workflowName : workflowRules.keySet()) {
            List<BasicRule> ruleList = workflows.get(workflowName);
            BasicRule catchall = ruleList.removeLast();
            Rule workflowRule = workflowRules.get(workflowName);
            workflowRule.letter = catchall.letter;
            workflowRule.validBounds = catchall.validBounds;
            workflowRule.success = Action.send(strToRule.apply(catchall.success));
            for (BasicRule rule : ruleList.reversed()) {
                Rule workflowRuleCopy = new Rule(workflowRule);
                workflowRule.letter = rule.letter;
                workflowRule.validBounds = rule.validBounds;
                workflowRule.success = Action.send(strToRule.apply(rule.success));
                workflowRule.fail = Action.send(workflowRuleCopy);
            }
        }
        Rule ruleRoot = workflowRules.get("in");
        Bounds defaultBounds = new Bounds(1, 4000);
        System.out.println(
                ruleWalk(ruleRoot, new PartBounds(defaultBounds, defaultBounds, defaultBounds, defaultBounds))
        );
    }
}
