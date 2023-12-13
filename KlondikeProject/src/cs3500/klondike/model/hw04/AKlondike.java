package cs3500.klondike.model.hw04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.CardImpl;
import cs3500.klondike.model.hw02.KlondikeModel;

/**
 * An abstract class implementing the KlondikeModel interface that defines many of the common
 * traits of KlondikeModels.
 */
public abstract class AKlondike implements KlondikeModel {
  /**
   * List for the draw cards, with index 0 being the top of the draw pile.
   */
  protected List<CardWithState> draw;
  /**
   * Cascade grid. The outer list is the cascades with the first being at index 0, and the inner
   * list is the card piles, with index 0 being the bottom of the pile.
   */
  protected List<List<CardWithState>> cascades;
  /**
   * Foundation grid. The outer list is the foundations with the first being at index 0,
   * and the inner list is the foundation piles, with index 0 being the bottom of the pile.
   */
  protected List<List<CardWithState>> foundations;
  /**
   * Number of draw cards visible.
   */
  protected int numDraw;

  /**
   * Return a valid and complete deck of cards for a game of Klondike.
   * There is no restriction imposed on the ordering of these cards in the deck.
   * The validity of the deck is determined by the rules of the specific game in
   * the classes implementing this interface.  This method may be called as often
   * as desired.
   *
   * @return the deck of cards as a list
   */
  @Override
  public List<Card> getDeck() {
    ArrayList<Card> d = new ArrayList<Card>();
    CardImpl.Value[] values = CardImpl.Value.values();
    CardImpl.Suit[] suits = CardImpl.Suit.values();
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 13; j++) {
        d.add(new CardImpl(values[j], suits[i]));
      }
    }
    return d;
  }

  /**
   * <p>Deal a new game of Klondike.
   * The cards to be used and their order are specified by the the given deck,
   * unless the {@code shuffle} parameter indicates the order should be ignored.</p>
   *
   * <p>This method first verifies that the deck is valid. It deals cards in rows
   * (left-to-right, top-to-bottom) into the characteristic cascade shape
   * with the specified number of rows, followed by (at most) the specified number of
   * draw cards. When {@code shuffle} is {@code false}, the {@code deck} must be used in
   * order and the 0th card in {@code deck} is used as the first card dealt.
   * There will be as many foundation piles as there are Aces in the deck.</p>
   *
   * <p>A valid deck must consist cards that can be grouped into equal-length,
   * consecutive runs of cards (each one starting at an Ace, and each of a single
   * suit).</p>
   *
   * <p>This method should have no side effects other than configuring this model
   * instance, and should work for any valid arguments.</p>
   *
   * @param deck     the deck to be dealt
   * @param shuffle  if {@code false}, use the order as given by {@code deck},
   *                 otherwise use a randomly shuffled order
   * @param numPiles number of piles to be dealt
   * @param numDraw  maximum number of draw cards available at a time
   * @throws IllegalStateException    if the game has already started
   * @throws IllegalArgumentException if the deck is null or invalid,
   *                                  a full cascade cannot be dealt with the given sizes,
   *                                  or another input is invalid
   */
  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
          throws IllegalArgumentException, IllegalStateException {
    List<CardWithState> deckToUse = new ArrayList<CardWithState>();
    //creates a copy of the deck with visibility states for playability
    if (cascades != null) {
      throw new IllegalStateException("game started already");
    } else if (numDraw < 0) {
      throw new IllegalArgumentException("invalid numDraw arg");
    } else if (numPiles < 0) {
      throw new IllegalArgumentException("invalid numPiles arg");
    } else if (deck == null) {
      throw new IllegalArgumentException("Deck cannot be null");
    } else if (deck.isEmpty()) {
      throw new IllegalArgumentException("Deck must have cards");
    } else {
      for (Card c : deck) {
        deckToUse.add(new CardWithState(c, false));
      }
      testDeckValidity(deckToUse);
      if (shuffle) {
        Collections.shuffle(deckToUse);
      }
      //initialize cascades
      cascades = new ArrayList<List<CardWithState>>();
      for (int i = 0; i < numPiles; i++) { //for every cascade
        cascades.add(new ArrayList<CardWithState>());
      }
      addCascades(deckToUse, numPiles);
      //initialize draw pile
      this.draw = deckToUse; //draw card of draw pile is index 0
      int i = 0;
      while (i < numDraw && i < draw.size()) {
        draw.get(i).flipVisibility();
        i++;
      }
      this.numDraw = numDraw;
      //initialize foundations
      foundations = new ArrayList<List<CardWithState>>();
      for (int j = 0; j < deck.size(); j++) {
        if (deck.get(j).toString().substring(0, 1).equals("A")) {
          foundations.add(new ArrayList<CardWithState>());
        }
      }
    }
  }

  /**
   * Helper method for the startGame method.
   *
   * @param deckToUse is the deck that the cascades are added to
   * @param numPiles  is the number of piles passed in startGame
   * @throws IllegalArgumentException if a full cascade cannot be dealt with the given sizes
   */
  protected void addCascades(List<CardWithState> deckToUse, int numPiles) {
    for (int i = 0; i < numPiles; i++) { // row
      for (int j = i; j < numPiles; j++) { //col
        if (deckToUse.isEmpty()) {
          throw new IllegalArgumentException("too many piles");
        } else {
          cascades.get(j).add(deckToUse.remove(0));
          if (j == i) {
            cascades.get(i).get(j).flipVisibility();
          }
        }
      }
    }
  }

  /**
   * Checks if the runs of the deck are valid uniform in length.
   */
  private void testDeckValidity(List<CardWithState> deck) {
    List<CardWithState> testDeck = new ArrayList<>(deck);
    List<List<CardWithState>> runs = new ArrayList<List<CardWithState>>();
    testDeck = sort(testDeck);
    for (CardWithState c : testDeck) {
      boolean moved = false;
      if (c.toString().substring(0, 1).equals("A")) {
        runs.add(new ArrayList<CardWithState>());
        runs.get(runs.size() - 1).add(c);
      } else {
        for (int i = 0; i < runs.size(); i++) {
          if (validFoundationStack(c, runs.get(i).get(runs.get(i).size() - 1))
                  && !moved) {
            runs.get(i).add(c);
            moved = true;
          }
        }
        if (!moved) {
          throw new IllegalArgumentException("runs aren't correct");
        }
      }
    }
    int lengthOfRun = runs.get(0).size();
    for (List<CardWithState> l : runs) {
      if (l.size() != lengthOfRun) {
        throw new IllegalArgumentException("runs don't match length");
      }
    }
  }

  /**
   * Sorts the list for testing.
   */
  private List<CardWithState> sort(List<CardWithState> cards) {
    ArrayList<CardWithState> sorted = new ArrayList<>();
    List<String> values =
            Arrays.asList("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");
    List<String> suits = Arrays.asList("♣", "♡", "♢", "♠");
    for (String v : values) {
      for (String s : suits) {
        for (CardWithState c : cards) {
          try {
            if (c.toString().contains(s) && c.toString().contains(v)) {
              sorted.add(c);
            }
          } catch (NullPointerException e) {
            throw new IllegalArgumentException("Cannot have null cards.");
          }
        }
      }
    }
    return sorted;
  }

  /**
   * Moves the requested number of cards from the source pile to the destination pile,
   * if allowable by the rules of the game.
   *
   * @param srcPile  the 0-based index (from the left) of the pile to be moved
   * @param numCards how many cards to be moved from that pile
   * @param destPile the 0-based index (from the left) of the destination pile for the
   *                 moved cards
   * @throws IllegalStateException    if the game hasn't been started yet
   * @throws IllegalArgumentException if either pile number is invalid, if the pile
   *                                  numbers are the same, or there are not enough cards to move
   *                                  from the srcPile to the destPile (i.e. the move is not
   *                                  physically possible)
   * @throws IllegalStateException    if the move is not allowable (i.e. the move is not
   *                                  logically possible)
   */
  @Override
  public void movePile(int srcPile, int numCards, int destPile)
          throws IllegalArgumentException, IllegalStateException {
    testGameStarted();
    if (!(-1 < srcPile && srcPile < cascades.size())) {
      throw new IllegalArgumentException("invalid indices");
    } else if (!(-1 < destPile && destPile < cascades.size())) {
      throw new IllegalArgumentException("invalid indices");
    } else if (srcPile == destPile) {
      throw new IllegalArgumentException("invalid indices");
    } else if (numCards == 0 || numCards > this.cascades.get(srcPile).size()) {
      throw new IllegalArgumentException("invalid numCards");
    } else {
      checkMove(srcPile, numCards, destPile);
      //does the move
      for (int i = 0; i < numCards; i++) {
        cascades.get(destPile).add(cascades.get(srcPile).remove(cascades.get(srcPile).size()
                - numCards + i));
      }
      if (!cascades.get(srcPile).isEmpty()) {
        if (!cascades.get(srcPile).get(cascades.get(srcPile).size() - 1).getVisibility()) {
          cascades.get(srcPile).get(cascades.get(srcPile).size() - 1).flipVisibility();
        }
      }
    }
  }

  protected void checkMove(int srcPile, int numCards, int destPile) {
    if (!cascades.get(destPile).isEmpty()) {
      if (!validStack(cascades.get(srcPile).get(cascades.get(srcPile).size() - numCards),
              cascades.get(destPile).get(cascades.get(destPile).size() - 1))) {
        throw new IllegalStateException("not valid stack");
      }
    } else if (!cascades.get(srcPile).get(cascades.get(srcPile).size()
            - numCards).toString().substring(0, 1).equals("K")) {
      throw new IllegalStateException("only king can go in empty spot");
    }
    for (int i = 0; i < numCards; i++) {
      if (!this.cascades.get(srcPile).get(cascades.get(srcPile).size() - 1 - i).getVisibility()) {
        throw new IllegalArgumentException("not visible");
      }
    }
  }

  /**
   * Throws exceptions if the parameters cannot stack according to the rules.
   *
   * @param cSource the card being moved.
   * @param cDest   the card to be moved on to.
   */
  public boolean validStack(CardWithState cSource, CardWithState cDest) {
    CardImpl.Value[] values = CardImpl.Value.values();
    int index1 = 0; //source value
    int index2 = 0; //dest value
    for (int i = 0; i < values.length; i++) {
      if (cSource.toString().substring(0, 1).equals(values[i].value().substring(0, 1))) {
        index1 = i;
      }
      if (cDest.toString().substring(0, 1).equals(values[i].value().substring(0, 1))) {
        index2 = i;
      }
    }
    return validStackHelper(cSource, cDest, index1, index2);
  }

  /**
   * returns true if the two cards are a valid build.
   *
   * @param cSource the CardWithState being moved.
   * @param cDest   the CardWithState to be moved on to.
   */
  protected boolean validStackHelper(CardWithState cSource, CardWithState cDest,
                                     int index1, int index2) {
    if (cSource.getCard().isRed() == cDest.getCard().isRed()) {
      return false;
    } else {
      return index1 == index2 - 1;
    }
  }

  /**
   * Throws exceptions if the parameters cannot stack according to the rules.
   *
   * @param cSource the CardWithState being moved.
   * @param cDest   the CardWithState to be moved on to.
   */
  public boolean validFoundationStack(CardWithState cSource, CardWithState cDest) {
    CardImpl.Value[] values = CardImpl.Value.values();
    int valueIndex1 = 0; //source value
    int valueIndex2 = 0; //dest value
    for (int i = 0; i < values.length; i++) {
      if (cSource.toString().substring(0, 1).equals(values[i].value().substring(0, 1))) {
        valueIndex1 = i;
      }
      if (cDest.toString().substring(0, 1).equals(values[i].value().substring(0, 1))) {
        valueIndex2 = i;
      }
    }
    CardImpl.Suit[] suits = CardImpl.Suit.values();
    int suitIndex1 = 0; //source value
    int suitIndex2 = 0; //dest value
    for (int i = 0; i < suits.length; i++) {
      if (cSource.toString().substring(cSource.toString().length() - 1).equals(suits[i].value())) {
        suitIndex1 = i;
      }
      if (cDest.toString().substring(cDest.toString().length() - 1).equals(suits[i].value())) {
        suitIndex2 = i;
      }
    }
    if (suitIndex1 != suitIndex2) {
      return false;
    } else {
      return valueIndex1 == valueIndex2 + 1;
    }
  }

  /**
   * Moves the topmost draw-card to the destination pile.  If no draw cards remain,
   * reveal the next available draw cards.
   *
   * @param destPile the 0-based index (from the left) of the destination pile for the
   *                 card.
   * @throws IllegalStateException    if the game hasn't been started yet.
   * @throws IllegalArgumentException if destination pile number is invalid.
   * @throws IllegalStateException    if there are no draw cards, or if the move is not
   *                                  allowable.
   */
  @Override
  public void moveDraw(int destPile) throws IllegalArgumentException, IllegalStateException {
    testGameStarted();
    if (!(-1 < destPile && destPile < cascades.size())) {
      throw new IllegalArgumentException("invalid indices");
    } else if (draw.isEmpty()) {
      throw new IllegalStateException("draw is empty");
    } else {
      if (!cascades.get(destPile).isEmpty()) {
        if (!validStack(draw.get(0),
                cascades.get(destPile).get(cascades.get(destPile).size() - 1))) {
          throw new IllegalStateException("not valid stack");
        }
      } else {
        kingHelper();
      }
      //draw card
      cascades.get(destPile).add(draw.remove(0));
      adjustDraw();
    }
  }

  /**
   * Checks if the first draw card is a king.
   */
  protected void kingHelper() {
    if (!draw.get(0).toString().substring(0, 1).equals("K")) {
      throw new IllegalStateException("only king can go in empty spot");
    }
  }


  /**
   * Helper method that changes visibility of top cards.
   */
  protected void adjustDraw() {
    if (draw.size() >= this.numDraw) {
      if (!draw.get(numDraw - 1).getVisibility()) {
        draw.get(numDraw - 1).flipVisibility();
      }
    }
  }

  /**
   * Moves the top card of the given pile to the requested foundation pile.
   *
   * @param srcPile        the 0-based index (from the left) of the pile to move a card.
   * @param foundationPile the 0-based index (from the left) of the foundation pile to
   *                       place the card.
   * @throws IllegalStateException    if the game hasn't been started yet.
   * @throws IllegalArgumentException if either pile number is invalid.
   * @throws IllegalStateException    if the source pile is empty or if the move is not
   *                                  allowable.
   */
  @Override
  public void moveToFoundation(int srcPile, int foundationPile)
          throws IllegalStateException {
    testGameStarted();
    if (!(-1 < srcPile && srcPile < cascades.size())) {
      throw new IllegalArgumentException("invalid src indices");
    } else if (!(-1 < foundationPile && foundationPile < getNumFoundations())) {
      throw new IllegalArgumentException("invalid foundation index");
    } else {
      if (!foundations.get(foundationPile).isEmpty()) { //throws exceptions for illegal move
        if (cascades.get(srcPile).isEmpty()) {
          throw new IllegalStateException("No source pile cards to move to foundation");
        } else if (!validFoundationStack(cascades.get(srcPile).get(cascades.get(srcPile).size()
                - 1), foundations.get(foundationPile).get(
                foundations.get(foundationPile).size() - 1))) {
          throw new IllegalStateException("illegal foundation move");
        }
      } else if (!cascades.get(srcPile).get(cascades.get(srcPile).size() - 1)
              .toString().substring(0, 1).equals("A")) {
        throw new IllegalStateException("not ace on empty foundation");
      }
      //do the move
      foundations.get(foundationPile).add(cascades.get(srcPile).remove(cascades.get(srcPile).size()
              - 1));
      if (!cascades.get(srcPile).isEmpty()) {
        if (!cascades.get(srcPile).get(cascades.get(srcPile).size() - 1).getVisibility()) {
          cascades.get(srcPile).get(cascades.get(srcPile).size() - 1).flipVisibility();
        }
      }
    }
  }

  /**
   * Moves the topmost draw-card directly to a foundation pile.
   *
   * @param foundationPile the 0-based index (from the left) of the foundation pile to
   *                       place the card.
   * @throws IllegalStateException    if the game hasn't been started yet.
   * @throws IllegalArgumentException if the foundation pile number is invalid.
   * @throws IllegalStateException    if there are no draw cards or if the move is not
   *                                  allowable.
   */
  @Override
  public void moveDrawToFoundation(int foundationPile)
          throws IllegalArgumentException, IllegalStateException {
    testGameStarted();
    if (draw.isEmpty()) {
      throw new IllegalStateException("draw is empty");
    }
    if (!(-1 < foundationPile && foundationPile < 4)) {
      throw new IllegalArgumentException("invalid foundation index");
    } else {
      if (!foundations.get(foundationPile).isEmpty()) { //throws exceptions for illegal move
        if (!validFoundationStack(draw.get(0),
                foundations.get(foundationPile).get(foundations.get(foundationPile).size() - 1))) {
          throw new IllegalStateException("illegal foundation move");
        }
      } else if (!draw.get(0).toString().substring(0, 1).equals("A")) {
        throw new IllegalStateException("not ace on empty foundation");
      }
      //do the move
      foundations.get(foundationPile).add(draw.remove(0));
      //flip visibility
      adjustDraw();
    }
  }

  /**
   * Discards the topmost draw-card.
   *
   * @throws IllegalStateException if the game hasn't been started yet.
   * @throws IllegalStateException if move is not allowable.
   */
  @Override
  public void discardDraw() throws IllegalStateException {
    testGameStarted();
    if (draw.isEmpty()) {
      throw new IllegalStateException("draw is empty");
    }
    if (draw.size() > this.numDraw) {
      draw.get(0).flipVisibility();
    }
    draw.add(draw.remove(0));
    adjustDraw();
  }

  /**
   * Returns the number of rows currently in the game.
   *
   * @return the height of the current table of cards.
   * @throws IllegalStateException if the game hasn't been started yet.
   */
  @Override
  public int getNumRows() {
    testGameStarted();
    int greatest = 0;
    for (int i = 0; i < cascades.size(); i++) {
      if (cascades.get(i).size() > greatest) {
        greatest = cascades.get(i).size();
      }
    }
    return greatest;
  }

  /**
   * Returns the number of piles for this game.
   *
   * @return the number of piles.
   * @throws IllegalStateException if the game hasn't been started yet.
   */
  @Override
  public int getNumPiles() {
    testGameStarted();
    return cascades.size();
  }

  /**
   * Returns the maximum number of visible cards in the draw pile.
   *
   * @return the number of visible cards in the draw pile.
   * @throws IllegalStateException if the game hasn't been started yet.
   */
  @Override
  public int getNumDraw() {
    testGameStarted();
    return numDraw;
  }

  /**
   * Signal if the game is over or not.  A game is over if there are no more
   * possible moves to be made, or draw cards to be used (or discarded).
   *
   * @return true if game is over, false otherwise.
   * @throws IllegalStateException if the game hasn't been started yet.
   */
  @Override
  public boolean isGameOver() throws IllegalStateException {
    testGameStarted();
    return draw.isEmpty() && noDrawToFoundation() && noDrawToCascade()
            && noCascadeReveal() && noCascadeToFoundation();
  }

  /**
   * checks if a draw card can be moved to a foundation.
   *
   * @return true if there is no draw to foundation move.
   */
  private boolean noDrawToFoundation() {
    for (int i = 0; i < foundations.size(); i++) {
      if (!foundations.get(i).isEmpty()) {
        if (!draw.isEmpty()) {
          if (validFoundationStack(draw.get(0), new CardWithState(getCardAt(i), true))) {
            return false;
          }
        }
      }
    }
    return true;
  }


  /**
   * checks if a draw card can be moved to a cascade pile.
   *
   * @return true if there is no draw to cascade move.
   */
  private boolean noDrawToCascade() {
    for (int i = 0; i < getNumPiles(); i++) {
      if (!cascades.get(i).isEmpty()) {
        if (!draw.isEmpty()) {
          if (validStack(draw.get(0), new CardWithState(getCardAt(i, cascades.get(i).size() - 1),
                  true))) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Checks if a cascade can be moved to reveal a card or new empty space.
   *
   * @return true if there is no cascade move to reveal a new cascade card.
   */
  private boolean noCascadeReveal() {
    boolean empty = false;
    for (int i = 0; i < getNumPiles(); i++) { //checks if there's an empty pile.
      if (cascades.get(i).isEmpty()) {
        empty = true;
      }
    }
    for (int i = 0; i < getNumPiles(); i++) {
      CardWithState c = null;
      if (!cascades.get(i).isEmpty()) {
        int j = 0;
        while (j < cascades.get(i).size() && c == null) { //gets first visible card in cascade pile.
          if (cascades.get(i).get(j).getVisibility()) {
            c = cascades.get(i).get(j);
          }
          j++;
        }
        for (int k = 0; k < getNumPiles(); k++) {
          if (k != i && !cascades.get(k).isEmpty()) { // skips the same pile and empty piles
            if (validStack(c, cascades.get(k).get(cascades.get(k).size() - 1))) { // if it can stack
              if (j - 1 != 0) { // if the card to move wasn't the first in pile
                return false;
              } else if (!empty) { // else if this is the first card to reveal an empty space
                return false;
              }
            }
          }
        }
      }
    }
    return true;
  }

  /**
   * Checks if a cascade can be moved to a foundation.
   *
   * @return true if there is no cascade move to foundation.
   */
  private boolean noCascadeToFoundation() {
    for (int i = 0; i < getNumPiles(); i++) { //checks if there's an empty pile.
      if (!cascades.get(i).isEmpty()) {
        for (int j = 0; j < getNumFoundations(); j++) {
          if (!foundations.get(j).isEmpty()) {
            if (validFoundationStack(cascades.get(i).get(cascades.get(i).size() - 1),
                    new CardWithState(getCardAt(j), true))) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  /**
   * Return the current score, which is the sum of the values of the topmost cards.
   * in the foundation piles.
   *
   * @return the score.
   * @throws IllegalStateException if the game hasn't been started yet.
   */
  @Override
  public int getScore() throws IllegalStateException {
    testGameStarted();
    int foundationTotal = 0;
    for (List<CardWithState> list : foundations) {
      foundationTotal += list.size();
    }
    return foundationTotal;
  }

  /**
   * Returns the number of cards in the specified pile.
   *
   * @param pileNum the 0-based index (from the left) of the pile.
   * @return the number of cards in the specified pile.
   * @throws IllegalStateException    if the game hasn't been started yet.
   * @throws IllegalArgumentException if pile number is invalid.
   */
  @Override
  public int getPileHeight(int pileNum) throws IllegalStateException {
    testGameStarted();
    if (!(-1 < pileNum && pileNum < cascades.size())) {
      throw new IllegalArgumentException("invalid indices");
    }
    return cascades.get(pileNum).size();
  }

  /**
   * Returns whether the card at the specified coordinates is face-up or not.
   *
   * @param pileNum column of the desired card (0-indexed from the left).
   * @param card    row of the desired card (0-indexed from the top).
   * @return whether the card at the given position is face-up or not.
   * @throws IllegalStateException    if the game hasn't been started yet.
   * @throws IllegalArgumentException if the coordinates are invalid.
   */
  @Override
  public boolean isCardVisible(int pileNum, int card) throws IllegalStateException {
    testGameStarted();
    if (!(-1 < pileNum && pileNum < cascades.size())) {
      throw new IllegalArgumentException("invalid indices");
    } else if (!(-1 < card && card < cascades.get(pileNum).size())) {
      throw new IllegalArgumentException("invalid indices");
    } else {
      return cascades.get(pileNum).get(card).getVisibility();
    }
  }

  /**
   * Returns the card at the specified coordinates, if it is visible.
   *
   * @param pileNum column of the desired card (0-indexed from the left).
   * @param card    row of the desired card (0-indexed from the top).
   * @return the card at the given position, or <code>null</code> if no card is there.
   * @throws IllegalStateException    if the game hasn't been started yet.
   * @throws IllegalArgumentException if the coordinates are invalid or the card is not visible.
   */
  @Override
  public Card getCardAt(int pileNum, int card) throws IllegalStateException {
    testGameStarted();
    if (!(-1 < pileNum && pileNum < cascades.size())) {
      throw new IllegalArgumentException("invalid indices");
    } else if (!(-1 < card && card < cascades.get(pileNum).size())) {
      throw new IllegalArgumentException("invalid indices");
    } else if (!cascades.get(pileNum).get(card).getVisibility()) {
      throw new IllegalArgumentException("not visible");
    } else {
      return cascades.get(pileNum).get(card).getCard();
    }
  }

  /**
   * Returns the card at the top of the specified foundation pile.
   *
   * @param foundationPile 0-based index (from the left) of the foundation pile.
   * @return the card at the given position, or <code>null</code> if no card is there.
   * @throws IllegalStateException    if the game hasn't been started yet.
   * @throws IllegalArgumentException if the foundation pile number is invalid.
   */
  @Override
  public Card getCardAt(int foundationPile) throws IllegalStateException {
    testGameStarted();
    if (!(-1 < foundationPile && foundationPile < 4)) {
      throw new IllegalArgumentException("invalid foundation index");
    } else if (foundations.get(foundationPile).isEmpty()) {
      return null;
    } else {
      return foundations.get(foundationPile).get(foundations.get(foundationPile).size()
              - 1).getCard();
    }
  }

  /**
   * Returns the currently available draw cards.
   * There should be at most {@link KlondikeModel#getNumDraw} cards (the number
   * specified when the game started) -- there may be fewer, if cards have been removed.
   * NOTE: Users of this method should not modify the resulting list.
   *
   * @return the ordered list of available draw cards (i.e. first element of this list
   *         is the first one to be drawn).
   * @throws IllegalStateException if the game hasn't been started yet.
   */
  @Override
  public List<Card> getDrawCards() throws IllegalStateException {
    testGameStarted();
    List<Card> cards = new ArrayList<Card>();
    for (int i = 0; i < draw.size(); i++) {
      if (draw.get(i).getVisibility()) {
        cards.add(draw.get(i).getCard());
      }
    }
    return cards;
  }

  /**
   * Return the number of foundation piles in this game.
   *
   * @return the number of foundation piles.
   * @throws IllegalStateException if the game hasn't been started yet.
   */
  @Override
  public int getNumFoundations() throws IllegalStateException {
    testGameStarted();
    return foundations.size();
  }

  /**
   * Checks if game started.
   *
   * @throws IllegalStateException if the game hasn't been started yet.
   */
  protected void testGameStarted() {
    if (cascades == null) {
      throw new IllegalStateException("game hasn't started");
    }
  }
}

