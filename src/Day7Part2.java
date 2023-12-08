import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Day7Part2 {
    enum Card {
        J, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, T, Q, K, A;

        public static Card valueOf(char card) {
            return switch (card) {
                case 'A' -> A;
                case 'K' -> K;
                case 'Q' -> Q;
                case 'T' -> T;
                case '9' -> NINE;
                case '8' -> EIGHT;
                case '7' -> SEVEN;
                case '6' -> SIX;
                case '5' -> FIVE;
                case '4' -> FOUR;
                case '3' -> THREE;
                case '2' -> TWO;
                case 'J' -> J;
                default -> throw new IllegalArgumentException("Unexpected value: " + card);
            };
        }
    }

    static Map<Card, Integer> countCards(List<Card> cards) {
        Map<Card, Integer> cardCount = new HashMap<>();
        for (Card card : cards) {
            if (cardCount.containsKey(card)) {
                cardCount.put(card, cardCount.get(card) + 1);
            } else {
                cardCount.put(card, 1);
            }
        }
        return cardCount;
    }

    enum Type {
        HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_KIND, FULL_HOUSE, FOUR_KIND, FIVE_KIND;

        private static List<List<Card>> newJokerConfig(int numJokers, List<Card> switchables) {
            if (numJokers == 0) return Collections.singletonList(new LinkedList<>());
            List<List<Card>> firstJokerSwitched = new LinkedList<>();
            for (Card c : switchables) {
                for (List<Card> rest : newJokerConfig(numJokers - 1, switchables)) {
                    List<Card> newConfig = new LinkedList<>();
                    newConfig.add(c);
                    newConfig.addAll(rest);
                    firstJokerSwitched.add(newConfig);
                }
            }
            return firstJokerSwitched;
        }

        static Type cardsType(List<Card> cards) {
            Type bestType = Type.cardsTypeBasic(cards);
            Map<Card, Integer> cardCount = countCards(cards);
            if (cardCount.get(Card.J) == null) return bestType;
            List<Card> cardsUniq = cardCount.keySet().stream().toList();
            int numJ = Objects.requireNonNull(cardCount.put(Card.J, 0));
            for (List<Card> jokers : newJokerConfig(numJ, cardsUniq)) {
                List<Card> newCards = new ArrayList<>(cards);
                int jokerIdx = 0;
                for (int i = 0; i < newCards.size(); i++) {
                    if (newCards.get(i) == Card.J) {
                        newCards.set(i, jokers.get(jokerIdx++));
                    }
                }
                Type newType = Type.cardsTypeBasic(newCards);
                if (newType.compareTo(bestType) > 0) bestType = newType;
            }
            return bestType;
        }

        private static Type cardsTypeBasic(List<Card> cards) {
            Map<Card, Integer> cardCount = countCards(cards);
            Type type;
            switch (cardCount.size()) {
                case 1 -> type = Type.FIVE_KIND;
                case 2 -> {
                    int count = cardCount.get(cardCount.keySet().stream().findFirst().get());
                    if (count == 1 || count == 4) type = Type.FOUR_KIND;
                    else type = Type.FULL_HOUSE;
                }
                case 3 -> {
                    boolean foundTriple = false;
                    for (Card card : cardCount.keySet()) {
                        if (cardCount.get(card) == 3) {
                            foundTriple = true;
                            break;
                        }
                    }
                    type = foundTriple ? Type.THREE_KIND : Type.TWO_PAIR;
                }
                case 4 -> type = Type.ONE_PAIR;
                case 5 -> type = Type.HIGH_CARD;
                default -> throw new IllegalStateException("Unexpected value: " + cardCount.size());
            }
            return type;
        }
    }

    record Hand(List<Card> cards, Type type) implements Comparable<Hand> {
        public Hand(List<Card> cards) {
            this(cards, Type.cardsType(cards));
        }

        @Override
        public int compareTo(Hand hand) {
            if (type.compareTo(hand.type) == 0) {
                for (int i = 0; i < cards.size(); i++) {
                    int comp = cards.get(i).compareTo(hand.cards.get(i));
                    if (comp != 0) return comp;
                }
                return 0;
            }
            return type.compareTo(hand.type);
        }
    }

    record Bid(Hand hand, int amount) {
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner s = new Scanner(new File("inputs/7"));
        List<Bid> bids = new ArrayList<>();
        while (s.hasNext()) {
            String hand = s.next();
            int bid = s.nextInt();
            bids.add(
                    new Bid(
                            new Hand(hand.chars().mapToObj((x) -> Card.valueOf((char) x)).collect(Collectors.toList())),
                            bid
                    )
            );
        }
        bids.sort(Comparator.comparing(b -> b.hand));
        long winnings = 0;
        for (int i = 0; i < bids.size(); i++) {
            winnings += (long) bids.get(i).amount * (i + 1);
        }
        System.out.println(winnings);
    }
}
