package cs3500.klondike;

import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.Card;

import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for Klondike Controller.
 */
public class ControllerTests {

  BasicKlondike model;
  KlondikeTextualController controller;
  StringReader r;
  StringBuilder a;
  List<Card> cards;

  /**
   * Helper for tests that initializes variables.
   */
  private void initVariables() {
    model = new BasicKlondike();
    a = new StringBuilder();
    controller = new KlondikeTextualController(r, a);
  }

  /**
   * Helper for tests checks if score is printed.
   */
  @Test
  public void containsScore() {
    r = new StringReader("q");
    initVariables();
    cards = this.sort(model.getDeck());
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertTrue(a.toString().contains("Score"));
  }

  /**
   * Checks if score contains 0.
   */
  @Test
  public void containsScore0() {
    r = new StringReader("q");
    initVariables();
    cards = this.sort(model.getDeck());
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertTrue(a.toString().contains("0"));
  }

  /**
   * Checks for quit as input.
   */
  @Test
  public void qInMiddle() {
    r = new StringReader("1 q");
    initVariables();
    cards = this.sort(model.getDeck());
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertTrue(a.toString().contains("Score"));
  }

  /**
   * Checks for quit as input.
   */
  @Test
  public void quitDisplay() {
    r = new StringReader("1 q");
    initVariables();
    cards = this.sort(model.getDeck());
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertTrue(a.toString().contains("Game quit!"));
  }

  /**
   * Checks for illogical move.
   */
  @Test
  public void illegalPileMove() {
    r = new StringReader("mpp 1 1 2 q");
    initVariables();
    cards = this.sort(model.getDeck());
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertTrue(a.toString().contains("Invalid move"));
  }

  /**
   * Checks that the controller properly does a correct move.
   */
  @Test
  public void movesPile() {
    r = new StringReader("mpp 1 1 2 q");
    initVariables();
    cards = this.sort(model.getDeck());
    Card temp = cards.get(5);
    cards.set(5, cards.get(7));
    cards.set(7, temp); // 2 of hearts second cascade
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertFalse(a.toString().contains("Invalid move"));
  }

  /**
   * Checks for skipping junk.
   */
  @Test
  public void IllegalInputPileMove() {
    r = new StringReader("mpp mpp 1 1 2 q");
    initVariables();
    cards = this.sort(model.getDeck());
    Card temp = cards.get(5);
    cards.set(5, cards.get(7));
    cards.set(7, temp); // 2 of hearts second cascade
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertFalse(a.toString().contains("Invalid move"));
  }

  /**
   * Checks legal draw move.
   */
  @Test
  public void movesDrawLegal() {
    r = new StringReader("md 2 q");
    initVariables();
    cards = this.sort(model.getDeck());
    Card temp = cards.get(28);
    cards.set(28, cards.get(1));
    cards.set(1, temp); // Ace of hearts with draw
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertFalse(a.toString().contains("Invalid move"));
  }

  /**
   * Another logic check for move draw this time.
   */
  @Test
  public void movesDrawIllegal() {
    r = new StringReader("md 1 q");
    initVariables();
    cards = this.sort(model.getDeck());
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertTrue(a.toString().contains("Invalid move"));
  }

  /**
   * Test for null model.
   */
  @Test(expected = IllegalArgumentException.class)
  public void nullModel() {
    initVariables();
    controller.playGame(null, this.sort(model.getDeck()), false, 7, 1);
  }

  /**
   * Proper foundation move.
   */
  @Test
  public void movesPileToFoundation() {
    r = new StringReader("mpf 1 1 q");
    initVariables();
    cards = this.sort(model.getDeck());
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertFalse(a.toString().contains("Invalid move"));
  }

  /**
   * Score updated correctly.
   */
  @Test
  public void movesPileToFoundationScoreDisplay() {
    r = new StringReader("mpf 1 1 q");
    initVariables();
    cards = this.sort(model.getDeck());
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertTrue(a.toString().contains("Score: 1"));
  }

  /**
   * Checks if quit ends the game.
   */
  @Test
  public void movesAfterQuit() {
    r = new StringReader("q mdf 1");
    initVariables();
    cards = this.sort(model.getDeck());
    Card temp = cards.get(28);
    cards.set(28, cards.get(1));
    cards.set(1, temp); // Ace of hearts with draw
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertFalse(a.toString().contains("Foundation: A♡"));
  }

  /**
   * Tests proper discard draw.
   */
  @Test
  public void discardDraw() {
    r = new StringReader("dd q");
    initVariables();
    cards = this.sort(model.getDeck());
    controller.playGame(model, cards, false, 7, 1);
    Assert.assertFalse(a.toString().contains("Invalid move"));
  }

  /**
   * Checks if game is won message with bad inputs.
   */
  @Test
  public void winning() {
    r = new StringReader("mpp 13 1 2 3 4 mpf 1 1 mdf 2 mdf 3 mdf 4");
    initVariables();
    cards = this.sort(model.getDeck());
    List<Card> smallList = cards.subList(0, 4);
    controller.playGame(model, smallList, false, 1, 1);
    Assert.assertTrue(a.toString().contains("You win!"));
  }

  /**
   * Checks game over with no valid moves.
   */
  @Test
  public void gameOver() {
    r = new StringReader("mdf 2 mdf 3 mdf 4 mdf 1");
    initVariables();
    cards = this.sort(model.getDeck());
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
    controller.playGame(model, smallList, false, 7, 1);
    Assert.assertTrue(a.toString().contains("Game over."));
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
