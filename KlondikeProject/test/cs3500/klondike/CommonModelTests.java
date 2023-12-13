package cs3500.klondike;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.Card;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test methods on the AKlondike common functionality.
 */
public class CommonModelTests {

  BasicKlondike testGame;
  List<Card> cards;

  /**
   * Initializes a game to be used in tests.
   */
  private void initVariables1() {
    testGame = new BasicKlondike();
    cards = this.sort(testGame.getDeck());
  }

  /**
   * Checks if IllegalArgumentException is thrown for startGame with <1 cascade pile.
   */
  @Test(expected = IllegalArgumentException.class)
  public void startGamePileNumInvalid1() {
    initVariables1();
    testGame.startGame(cards, false, -1, 1);
  }

  /**
   * Checks if IllegalArgumentException is thrown for startGame with too many cascade piles.
   */
  @Test(expected = IllegalArgumentException.class)
  public void startGamePileNumInvalid2() {
    initVariables1();
    testGame.startGame(cards, false, 13, 1);
  }

  /**
   * Checks if IllegalArgumentException is thrown for startGame with draw <1.
   */
  @Test(expected = IllegalArgumentException.class)
  public void startGameDrawNumInvalid() {
    initVariables1();
    testGame.startGame(cards, false, 7, -1);
  }

  /**
   * Checks if cards are shuffled.
   */
  @Test
  public void shuffle() {
    initVariables1();
    boolean difference = false;
    ArrayList<Card> c1 = new ArrayList<>(cards.subList(1, cards.size()));
    testGame.startGame(cards, true, 1, 100);
    ArrayList<Card> c2 = new ArrayList<>(testGame.getDrawCards());
    for (int i = 0; i < c1.size(); i++) {
      if (!c1.get(i).equals(c2.get(i))) {
        difference = true;
      }
    }
    Assert.assertTrue(difference);
  }

  /**
   * Checks game over with no valid moves.
   */
  @Test
  public void gameOver() {
    initVariables1();
    cards = this.sort(testGame.getDeck());
    List<Card> smallList = cards.subList(0, 32);
    Card temp = smallList.get(28);
    smallList.set(28, smallList.get(1));
    smallList.set(1, temp); // Ace of hearts with draw
    temp = smallList.get(29);
    smallList.set(29, smallList.get(0));
    smallList.set(0, temp); // 8 hearts first pile ace clubs to draw
    temp = smallList.get(30);
    smallList.set(30, smallList.get(2));
    smallList.set(2, temp); // Ace of diamonds with draw
    temp = smallList.get(31);
    smallList.set(31, smallList.get(3));
    smallList.set(3, temp); // Ace of spades with draw
    temp = smallList.get(25);
    smallList.set(25, smallList.get(7));
    smallList.set(7, temp); // 7 heart with second pile
    //3rd pile 4 hearts
    //4th is 5 diamonds
    //5th 6 diamonds
    temp = smallList.get(25);
    smallList.set(25, smallList.get(26));
    smallList.set(26, temp); //6th 7 diamonds
    temp = smallList.get(21);
    smallList.set(21, smallList.get(27));
    smallList.set(27, temp); // 6 heart with 7th pile
    //System.out.print(smallList);
    testGame.startGame(smallList, false, 7, 1);
    testGame.moveDrawToFoundation(2);
    testGame.moveDrawToFoundation(3);
    testGame.moveDrawToFoundation(0);
    testGame.moveDrawToFoundation(1);
    Assert.assertTrue(testGame.isGameOver());
  }

  /**
   * Checks game over with valid moves.
   */
  @Test
  public void gameOver1() {
    initVariables1();
    cards = this.sort(testGame.getDeck());
    testGame.startGame(cards, false, 7, 1);
    Assert.assertFalse(testGame.isGameOver());
  }

  /**
   * Checks if exception thrown for illegal pile move.
   */
  @Test(expected = IllegalStateException.class)
  public void movePiles() {
    initVariables1();
    testGame.startGame(cards, false, 7, 1);
    testGame.movePile(1, 1, 0);
  }

  /**
   * Checks if king can move to empty space.
   */
  @Test
  public void moveKingEmptySpace() {
    initVariables1();
    Card temp = cards.get(7);
    cards.set(7, cards.get(51));
    cards.set(51, temp);
    testGame.startGame(cards, false, 7, 1);
    testGame.moveToFoundation(0, 0);
    testGame.movePile(1, 1, 0);
    Assert.assertEquals(testGame.getCardAt(0, 0).toString(), "K♠");
  }

  /**
   * Error if null card is passed.
   */
  @Test(expected = IllegalArgumentException.class)
  public void nullCard() {
    initVariables1();
    Card temp = cards.get(7);
    cards.set(7, null);
    cards.add(temp);
    testGame.startGame(cards, false, 7, 1);
  }

  /**
   * Checks proper foundation move.
   */
  @Test
  public void foundationMove() {
    initVariables1();
    testGame.startGame(cards, false, 7, 1);
    testGame.moveToFoundation(0, 0);
    Assert.assertEquals(testGame.getCardAt(0).toString(), "A♣");
  }

  /**
   * Checks illegal foundation move.
   */
  @Test(expected = IllegalStateException.class)
  public void foundationMoveDouble() {
    initVariables1();
    testGame.startGame(cards, false, 7, 1);
    Card temp = cards.get(6);
    cards.set(6, cards.get(8));
    cards.set(8, temp);
    testGame.movePile(1, 2, 0);
  }

  /**
   * checks illegal foundation move.
   */
  @Test(expected = IllegalStateException.class)
  public void illegalFoundationMove() {
    initVariables1();
    testGame.startGame(cards, false, 7, 1);
    testGame.moveToFoundation(1, 0);
  }

  /**
   * Illegal move to empty space.
   */
  @Test(expected = IllegalStateException.class)
  public void illegalEmptyMove() {
    initVariables1();
    Card temp = cards.get(5);
    cards.set(5, cards.get(7));
    cards.set(7, temp);
    testGame.startGame(cards, false, 7, 1);
    testGame.movePile(0, 1, 1);
    testGame.movePile(1, 2, 0);
    Assert.assertTrue(testGame.isCardVisible(1, 0));
  } // 2 hearts and ace clubs pile 0

  /**
   * Empty space has no visible card.
   */
  @Test(expected = IllegalArgumentException.class)
  public void emptyVisible() {
    initVariables1();
    Card temp = cards.get(5);
    cards.set(5, cards.get(7));
    cards.set(7, temp); // 2 of spades of clubs with 2 of hearts
    testGame.startGame(cards, false, 7, 1);
    testGame.movePile(0, 1, 1);
    Assert.assertFalse(testGame.isCardVisible(0, 0));
  }

  /**
   * Correct movement of draw to foundation.
   */
  @Test
  public void drawToFoundationTestCorrect() {
    initVariables1();
    Card temp = cards.get(28);
    cards.set(28, cards.get(4));
    cards.set(4, temp); // 2 of clubs with draw
    testGame.startGame(cards, false, 7, 1);
    testGame.moveToFoundation(0, 0);
    testGame.moveDrawToFoundation(0);
    Assert.assertEquals(testGame.getCardAt(0).toString(), "2♣");
  }

  /**
   * Correct movement of draw to cards.
   */
  @Test
  public void drawToCardsTestCorrect() {
    initVariables1();
    Card temp = cards.get(28);
    cards.set(28, cards.get(1));
    cards.set(1, temp); // Ace of hearts with draw
    testGame.startGame(cards, false, 7, 1);
    testGame.moveDraw(1);
    Assert.assertEquals(testGame.getCardAt(1, 2).toString(), "A♡");
  }

  /**
   * Correct max number of draw cards.
   */
  @Test
  public void drawMaxTest() {
    initVariables1();
    Card temp = cards.get(29);
    cards.set(29, cards.get(1));
    cards.set(1, temp); // Ace of hearts with 2nd draw
    testGame.startGame(cards, false, 7, 2);
    Assert.assertEquals(testGame.getNumDraw(), 2);
  }

  /**
   * Correct discard behavior.
   */
  @Test
  public void discardDrawTest() {
    initVariables1();
    Card temp = cards.get(29);
    cards.set(29, cards.get(1));
    cards.set(1, temp); // Ace of hearts with 2nd draw
    testGame.startGame(cards, false, 7, 1);
    testGame.discardDraw();
    Assert.assertEquals(testGame.getDrawCards().get(0).toString(), "A♡");
  }

  /**
   * Sorts the list for testing.
   */
  private List<Card> sort(List<Card> cards) {
    ArrayList<Card> sorted = new ArrayList<>();
    List<String> values =
            Arrays.asList("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");
    List<String> suits = Arrays.asList("♣", "♡", "♢", "♠");
    for (String v : values) {
      for (String s : suits) {
        for (Card c : cards) {
          if (c.toString().contains(s) && c.toString().contains(v)) {
            sorted.add(c);
          }
        }
      }
    }
    return sorted;
  }
}
