package cs3500.klondike;

import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw04.WhiteheadKlondike;
import cs3500.klondike.model.hw04.LimitedDrawKlondike;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for the other whitehead and discard draw models.
 */
public class ExtendedModelTests {

  KlondikeModel testGame;
  List<Card> cards;

  /**
   * Tests LimitedDraw with a negative draw.
   */
  @Test(expected = IllegalArgumentException.class)
  public void checkNegativeDraw() {
    testGame = new LimitedDrawKlondike(-1);
    cards = this.sort(testGame.getDeck());
    testGame.startGame(cards, false, 7, 1);
  }

  /**
   * Tests LimitedDraw with one redraw.
   */
  @Test
  public void checkOneDraw() {
    testGame = new LimitedDrawKlondike(1);
    cards = this.sort(testGame.getDeck());
    List<Card> smallList = cards.subList(0, 32);
    testGame.startGame(smallList, false, 7, 1);
    for (int i = 0; i < 8; i++) {
      testGame.discardDraw();
    }
    Assert.assertTrue(testGame.getDrawCards().isEmpty());
  }

  /**
   * Tests LimitedDraw with zero redraw.
   */
  @Test
  public void checkZeroDraw() {
    testGame = new LimitedDrawKlondike(0);
    cards = this.sort(testGame.getDeck());
    List<Card> smallList = cards.subList(0, 32);
    testGame.startGame(smallList, false, 7, 1);
    for (int i = 0; i < 4; i++) {
      testGame.discardDraw();
    }
    Assert.assertTrue(testGame.getDrawCards().isEmpty());
  }

  /**
   * Tests LimitedDraw with one redraw and moving a draw pile card.
   */
  @Test
  public void checkMoveDraw() {
    testGame = new LimitedDrawKlondike(1);
    cards = this.sort(testGame.getDeck());
    List<Card> smallList = cards.subList(0, 32);
    Card temp = cards.get(28);
    cards.set(28, cards.get(1));
    cards.set(1, temp); // Ace of hearts with draw
    testGame.startGame(smallList, false, 7, 1);
    testGame.moveDraw(1);
    for (int i = 0; i < 6; i++) {
      testGame.discardDraw();
    }
    Assert.assertTrue(testGame.getDrawCards().isEmpty());
  }

  /**
   * Tests that LimitedDraw redraws cards when under the redraw amount.
   */
  @Test
  public void checkOneCycleDraw() {
    testGame = new LimitedDrawKlondike(5);
    cards = this.sort(testGame.getDeck());
    List<Card> smallList = cards.subList(0, 32);
    testGame.startGame(smallList, false, 7, 6);
    for (int i = 0; i < 8; i++) {
      testGame.discardDraw();
    }
    Assert.assertEquals(4, testGame.getDrawCards().size());
  }

  /**
   * Checks that whitehead cascades start visible.
   */
  @Test
  public void whiteheadVisible() {
    initWhitehead();
    testGame.startGame(cards, false, 7, 1);
    Assert.assertTrue(testGame.isCardVisible(6, 2));
  }

  /**
   * Tests whitehead valid pile move with different suits.
   */
  @Test
  public void whiteheadValidPileMoveDifferentSuits() {
    initWhitehead();
    cards = this.sort(testGame.getDeck());
    testGame.startGame(cards, false, 7, 1);
    testGame.movePile(0, 1, 1);
    Assert.assertEquals(testGame.getCardAt(1, 2).toString(), "A♣");
  }

  /**
   * Tests whitehead valid pile move with same suits.
   */
  @Test
  public void whiteheadValidPileMoveSameSuits() {
    initWhitehead();
    Card temp = cards.get(7);
    cards.set(7, cards.get(4));
    cards.set(4, temp); // 2 clubs on 2nd cascade
    testGame.startGame(cards, false, 7, 1);
    testGame.movePile(0, 1, 1);
    Assert.assertEquals(testGame.getCardAt(1, 2).toString(), "A♣");
  }

  /**
   * Tests moving 2 cards of different suits simultaneously to form an otherwise valid build.
   */
  @Test(expected = IllegalStateException.class)
  public void whiteheadInvalidPileMoveToCascade() {
    initWhitehead();
    Card temp = cards.get(13);
    cards.set(13, cards.get(8));
    cards.set(8, temp); // 3 clubs with 3rd cascade top
    testGame.startGame(cards, false, 7, 1);
    testGame.movePile(0, 1, 1);
    testGame.movePile(1, 2, 2);
  }

  /**
   * Tests whitehead invalid pile move with alternating color build.
   */
  @Test(expected = IllegalStateException.class)
  public void whiteheadInvalidPileMoveDifferentColors() {
    initWhitehead();
    Card temp = cards.get(5);
    cards.set(5, cards.get(7));
    cards.set(7, temp); // 2 hearts top of second cascade
    testGame.startGame(cards, false, 7, 1);
    testGame.movePile(0, 1, 1);
  }

  /**
   * Tests moving 2 cards of different suits simultaneously to empty cascade in whitehead.
   */
  @Test(expected = IllegalStateException.class)
  public void whiteheadInvalidPileMoveToEmptyCascade() {
    initWhitehead();
    testGame.startGame(cards, false, 7, 1);
    testGame.movePile(0, 1, 1);
    testGame.movePile(1, 2, 0);
  }

  /**
   * Tests incorrect numbered build whitehead.
   */
  @Test(expected = IllegalStateException.class)
  public void invalidBuild() {
    initWhitehead();
    testGame.startGame(cards, false, 7, 1);
    testGame.movePile(1, 1, 0);
  }

  /**
   * Tests moving a non-king from a cascade pile to an empty cascade.
   */
  @Test
  public void whiteheadValidPileMoveToEmptyCascade() {
    initWhitehead();
    Card temp = cards.get(4);
    cards.set(4, cards.get(7));
    cards.set(7, temp); // 2 clubs with 2nd cascade top
    testGame.startGame(cards, false, 7, 1);
    testGame.movePile(0, 1, 1);
    testGame.movePile(1, 2, 0);
    Assert.assertEquals(testGame.getCardAt(0, 1).toString(), "A♣");
  }

  /**
   * Tests moving a non-king from the draw pile to an empty cascade.
   */
  @Test
  public void whiteheadValidDrawToEmptyCascade() {
    initWhitehead();
    testGame.startGame(cards, false, 7, 1);
    testGame.movePile(0, 1, 1);
    testGame.moveDraw(0);
    Assert.assertEquals(testGame.getCardAt(0, 0).toString(), "8♣");
  }

  /**
   * Tests a valid move from draw to cascade.
   */
  @Test
  public void whiteheadValidDrawToCascade() {
    initWhitehead();
    Card temp = cards.get(0);
    cards.set(0, cards.get(28));
    cards.set(28, temp);
    testGame.startGame(cards, false, 7, 1);
    testGame.moveDraw(1);
    Assert.assertEquals(testGame.getCardAt(1, 2).toString(), "A♣");
  }

  /**
   * Initializes a whitehead game to be used in tests.
   */
  private void initWhitehead() {
    testGame = new WhiteheadKlondike();
    cards = this.sort(testGame.getDeck());
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
