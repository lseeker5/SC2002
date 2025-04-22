package Lectures;

import java.util.*;

public class Lecture_8 {
}

class Card{
    private int faceValue;
    private String name;
    public Card(int val){
        this.faceValue = val;
        this.name = String.valueOf(val);
    }
    public Card(String special){
        if (special.equals("J") || special.equals("Q") || special.equals("K")){
            this.faceValue = 10;
        } else if (special.equals("A")){
            this.faceValue = 1;
        }
        this.name = special;
    }

    public int getFaceValue() {
        return this.faceValue;
    }

    public String getName(){
        return this.name;
    }

    public int getOptimalValue(int currentSum) {
        if (this.name.equals("A") && currentSum + 11 <= 21) {
            return 11;
        }
        return this.faceValue;
    }


}

class Deck{
    private int remainingCardsNumber;
    private ArrayList<Card> allCards;
    public Deck(){
        this.remainingCardsNumber = 52;
        this.allCards = new ArrayList<>();
        initializeDeck();
    }

    public void initializeDeck(){
        for (int i = 0; i < 4; i++){
            for (int j = 2; j <= 10; j++){
                this.allCards.add(new Card(j));
            }
            this.allCards.add(new Card("J"));
            this.allCards.add(new Card("Q"));
            this.allCards.add(new Card("K"));
            this.allCards.add(new Card("A"));
        }
    }

    public void shuffle(){
        Collections.shuffle(this.allCards);
    }

    public Card drawNextCard(){
        this.remainingCardsNumber--;
        return this.allCards.remove(0);
    }

    public int getRemainingCardsNumber(){
        return this.remainingCardsNumber;
    }


}

class Hand{
    private ArrayList<Card> cardsInHand = new ArrayList<>();

    public ArrayList<Card> getCardsInHand() {
        return this.cardsInHand;
    }

    public void addCards(Card card){
        this.cardsInHand.add(card);
    }

    public int getNumOfCardsInHand(){
        return this.cardsInHand.size();
    }

    public void showHand(){
        System.out.print("The cards in the hand are(is): ");
        for (Card card : this.cardsInHand){
            System.out.print(card.getName() + ' ');
        }
    }
}

class Player {
    private Hand hand;

    public Player() {
        this.hand = new Hand();
    }

    public Hand getHand() {
        return this.hand;
    }

    public void request(String input, Dealer dealer) {
        if (input.equals("hit")) {
            dealer.giveACardToPlayer(this);
        }
    }

    public void showHand() {
        this.hand.showHand();
    }

    public int getCurrentValue() {
        int sum = 0;
        for (Card card : this.hand.getCardsInHand()) {
            sum += card.getOptimalValue(sum);  // Use optimal value for Ace
        }
        return sum;
    }

    public void showValue() {
        System.out.println("The current value is:" + this.getCurrentValue());
    }

    public boolean lost() {
        return this.getCurrentValue() > 21;
    }
}

class Dealer {
    private Deck deck;
    private Hand hand;

    public Dealer() {
        this.deck = new Deck();
        this.hand = new Hand();
    }

    public void shuffleDeck() {
        this.deck.shuffle();
    }

    public void giveACardToPlayer(Player player) {
        Card card = this.deck.drawNextCard();
        player.getHand().addCards(card);
    }

    public void giveACardToDealer() {
        Card card = this.deck.drawNextCard();
        this.hand.addCards(card);
    }

    public void showHand() {
        this.hand.showHand();
    }

    public int getCurrentValue() {
        int sum = 0;
        for (Card card : this.hand.getCardsInHand()) {
            sum += card.getOptimalValue(sum);  // Use optimal value for Ace
        }
        return sum;
    }

    public void showValue() {
        System.out.println("The current value is:" + this.getCurrentValue());
    }

    public boolean lost() {
        return this.getCurrentValue() > 21;
    }

    public void determineWinner(Player player) {
        if (this.lost()) {
            System.out.println("The player won!");
        } else if (player.lost()) {
            System.out.println("The dealer won!");
        } else if (this.getCurrentValue() > player.getCurrentValue()) {
            System.out.println("The dealer won!");
        } else if (this.getCurrentValue() < player.getCurrentValue()) {
            System.out.println("The player won!");
        } else {
            System.out.println("It's a tie!");
        }
    }
}

class BlackJack {
    private Player player;
    private Dealer dealer;

    public BlackJack() {
        this.dealer = new Dealer();
        this.player = new Player();
    }

    public void application() {
        this.dealer.shuffleDeck();

        // Deal two cards to player and dealer
        this.dealer.giveACardToPlayer(player);  // Give card to player
        this.dealer.giveACardToPlayer(player);  // Give second card to player
        this.dealer.giveACardToDealer();       // Give card to dealer
        this.dealer.giveACardToDealer();       // Give second card to dealer

        // Show player's and dealer's hands
        System.out.println("Player's hand:");
        player.showHand();
        player.showValue();

        System.out.println("\nDealer's hand:");
        dealer.showHand();
        dealer.showValue();

        // Player's turn
        Scanner scanner = new Scanner(System.in);
        while (!player.lost() && player.getCurrentValue() < 21) {
            System.out.println("\nDo you want to 'hit' or 'hold'?");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("hit")) {
                player.request("hit", dealer);  // Player asks for a card
                player.showHand();
                player.showValue();
            } else if (input.equalsIgnoreCase("hold")) {
                break;  // Player holds, ending their turn
            }
        }

        // If the player didn't bust, now the dealer's turn
        if (!player.lost()) {
            System.out.println("\nDealer's turn:");
            while (dealer.getCurrentValue() < 17) {  // Dealer must hit until their value is 17 or more
                dealer.giveACardToDealer();
                dealer.showHand();
                dealer.showValue();
            }
        }

        // Determine winner
        dealer.determineWinner(player);
    }

    public static void main(String[] args) {
        BlackJack game = new BlackJack();
        game.application();
    }
}
